/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.FileWatcher.Consumer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.PrintStream;

import java.net.URI;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand {

	public DeployCommand(BladeCLI blade, DeployCommandArgs args) throws Exception {
		_blade = blade;
		_options = args;
		_host = "localhost";
		_port = 11311;
	}

	private void _deploy(GradleExec gradle, Set<File> outputFiles) throws Exception {
		int retcode = gradle.executeGradleCommand("assemble -x check");

		if (retcode > 0) {
			_addError("Gradle assemble task failed.");
			return;
		}

		Stream<File> stream = outputFiles.stream();

		stream.filter(
			File::exists
		).forEach(
			outputFile -> {
				try {
					_installOrUpdate(outputFile);
				}
				catch (Exception e) {
					PrintStream err = _blade.err();

					err.println(e.getMessage());

					e.printStackTrace(err);
				}
			}
		);
	}

	private void _deployWatch(final GradleExec gradleExec, final Set<File> outputFiles) throws Exception {
		_deploy(gradleExec, outputFiles);

		new Thread() {

			@Override
			public void run() {
				try {
					gradleExec.executeGradleCommand("assemble -x check -t");
				}
				catch (Exception e) {
				}
			}

		}.start();

		Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				try {
					File modifiedFile = modified.toFile();

					if (outputFiles.contains(modifiedFile)) {
						_blade.out().println("installOrUpdate " + modifiedFile);

						_installOrUpdate(modifiedFile);
					}
				}
				catch (Exception e) {
				}
			}

		};

		new FileWatcher(_blade.getBase().toPath(), true, consumer);
	}

	public void execute() throws Exception {
		if (!Util.canConnect(_host, _port)) {
			_addError("deploy", "Unable to connect to gogo shell on " + _host + ":" + _port);
			return;
		}

		GradleExec gradleExec = new GradleExec(_blade);

		Set<File> outputFiles = GradleTooling.getOutputFiles(_blade.getCacheDir(), _blade.getBase());

		if (_options.isWatch()) {
			_deployWatch(gradleExec, outputFiles);
		}
		else {
			_deploy(gradleExec, outputFiles);
		}
	}

	private static void deployWar(File file, LiferayBundleDeployer deployer) throws Exception {
		URI uri = file.toURI();

		long bundleId = deployer.install(uri);

		if (bundleId > 0) {
			deployer.start(bundleId);

		}
		else {

			throw new Exception("Failed to deploy war: " + file.toURI().toASCIIString());
		}
	}

	private void _addError(String msg) {
		_blade.addErrors("deploy", Collections.singleton(msg));
	}

	private void _addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void _installOrUpdate(File file) throws Exception {
		file = file.getAbsoluteFile();

		try (LiferayBundleDeployer client = LiferayBundleDeployer.newInstance(_host, _port)) {
			String name = file.getName();

			name = name.toLowerCase();

			Domain bundle = Domain.domain(file);

			Entry<String, Attrs> bsn = bundle.getBundleSymbolicName();

			if (bsn != null) {
				_deployBundle(file, client, bundle, bsn);
			}
			else if (name.endsWith(".war")) {
				deployWar(file, client);
			}
		}
	}

	private void _deployBundle(File file, LiferayBundleDeployer client, Domain bundle, Entry<String, Attrs> bsn)
		throws Exception {

		Entry<String, Attrs> fragmentHost = bundle.getFragmentHost();

		String hostBsn = null;

		if (fragmentHost != null) {
			hostBsn = fragmentHost.getKey();
		}

		Collection<BundleDTO> bundles = client.getBundles();

		long existingId = client.getBundleId(bundles, bsn.getKey());

		long hostId = client.getBundleId(bundles, hostBsn);

		URI uri = file.toURI();

		if (existingId > 0) {
			_reloadExistingBundle(client, fragmentHost, existingId, hostId, uri);
		}
		else {
			_installNewBundle(client, bsn, fragmentHost, hostId, uri);
		}
	}

	private void _installNewBundle(LiferayBundleDeployer client, Entry<String, Attrs> bsn,
			Entry<String, Attrs> fragmentHost, long hostId, URI uri) throws Exception {

		PrintStream out = _blade.out();

		long existingId = client.install(uri);

		if ((fragmentHost != null) && (hostId > 0)) {
			client.refresh(hostId);

			_blade.out().println("Installed fragment bundle " + existingId);
		}
		else {
			long checkedExistingId = client.getBundleId(bsn.getKey());

			try {
				if (!Objects.equals(existingId, checkedExistingId)) {
					out.print("Error: Bundle IDs do not match.");

				} else {

					if (checkedExistingId > 1) {
						client.start(checkedExistingId);

						_blade.out().println("Installed bundle " + existingId);
					}
					else {
						out.println("Error: Bundle failed to install: " + bsn);
					}
				}
			} catch (Exception e) {

				out.println("Error: Bundle failed to install: " + bsn);
				e.printStackTrace(out);
			}
		}
	}

	private final void _reloadExistingBundle(LiferayBundleDeployer client, Entry<String, Attrs> fragmentHost,
			long existingId, long hostId, URI uri) throws Exception {

		if (fragmentHost != null && (hostId > 0)) {
			client.reloadFragment(existingId, hostId, uri);
		}
		else {
			client.reloadBundle(existingId, uri);
		}

		_blade.out().println("Updated bundle " + existingId);
	}

	private final BladeCLI _blade;
	private final String _host;
	private final DeployCommandArgs _options;
	private final int _port;

}