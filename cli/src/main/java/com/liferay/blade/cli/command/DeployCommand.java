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

package com.liferay.blade.cli.command;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.LiferayBundleDeployer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileWatcher;
import com.liferay.blade.cli.util.FileWatcher.Consumer;

import java.io.File;
import java.io.PrintStream;

import java.net.ConnectException;
import java.net.URI;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand extends BaseCommand<DeployArgs> {

	public DeployCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		String host = "localhost";
		int port = 11311;

		if (!BladeUtil.canConnect(host, port)) {
			StringBuilder sb = new StringBuilder();

			sb.append("Unable to connect to gogo shell on " + host + ":" + port);
			sb.append(System.lineSeparator());
			sb.append("Liferay may not be running, or the gogo shell may need to be enabled. ");
			sb.append("Please see this link for more details: ");
			sb.append("https://dev.liferay.com/en/develop/reference/-/knowledge_base/7-1/using-the-felix-gogo-shell");
			sb.append(System.lineSeparator());

			_addError(sb.toString());

			PrintStream err = bladeCLI.err();

			new ConnectException(sb.toString()).printStackTrace(err);

			return;
		}

		GradleExec gradleExec = new GradleExec(bladeCLI);

		Path cachePath = bladeCLI.getCachePath();

		DeployArgs deployArgs = getArgs();

		File baseDir = new File(deployArgs.getBase());

		Set<File> outputFiles = GradleTooling.getOutputFiles(cachePath.toFile(), baseDir);

		if (deployArgs.isWatch()) {
			_deployWatch(gradleExec, outputFiles, host, port);
		}
		else {
			_deploy(gradleExec, outputFiles, host, port);
		}
	}

	@Override
	public Class<DeployArgs> getArgsClass() {
		return DeployArgs.class;
	}

	private static void _deployWar(File file, LiferayBundleDeployer deployer) throws Exception {
		URI uri = file.toURI();

		long bundleId = deployer.install(uri);

		if (bundleId > 0) {
			BundleDTO bundle = deployer.getBundle(bundleId);

			if (bundle.state == Bundle.INSTALLED) {
				deployer.start(bundleId);
			}
			else if (bundle.state == Bundle.ACTIVE) {
				deployer.update(bundleId, uri);
			}
		}
		else {
			throw new Exception("Failed to deploy war: " + file.getAbsolutePath());
		}
	}

	private void _addError(String msg) {
		getBladeCLI().addErrors("deploy", Collections.singleton(msg));
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _deploy(GradleExec gradle, Set<File> outputFiles, String host, int port) throws Exception {
		ProcessResult processResult = gradle.executeTask("assemble -x check");

		int resultCode = processResult.getResultCode();

		BladeCLI bladeCLI = getBladeCLI();

		if (resultCode > 0) {
			String errorMessage = "Gradle assemble task failed.";

			_addError(errorMessage);

			PrintStream err = bladeCLI.err();

			_addError(processResult.getError());

			new ConnectException(errorMessage).printStackTrace(err);

			return;
		}

		Stream<File> stream = outputFiles.stream();

		stream.filter(
			File::exists
		).forEach(
			outputFile -> {
				try {
					_installOrUpdate(outputFile, host, port);
				}
				catch (Exception e) {
					String message = e.getMessage();

					Class<?> exceptionClass = e.getClass();

					if (message == null) {
						message = "DeployCommand._deploy threw " + exceptionClass.getSimpleName();
					}

					_addError(message);

					PrintStream err = bladeCLI.err();

					e.printStackTrace(err);
				}
			}
		);
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

	private void _deployWatch(final GradleExec gradleExec, final Set<File> outputFiles, String host, int port)
		throws Exception {

		_deploy(gradleExec, outputFiles, host, port);

		Stream<File> stream = outputFiles.stream();

		Collection<Path> outputPaths = stream.map(
			File::toPath
		).collect(
			Collectors.toSet()
		);

		BladeCLI bladeCLI = getBladeCLI();

		new Thread() {

			@Override
			public void run() {
				try {
					gradleExec.executeTask("assemble -x check -t");
				}
				catch (Exception e) {
					String message = e.getMessage();

					if (message == null) {
						message = "Gradle build task failed.";
					}

					_addError("deploy watch", message);

					PrintStream err = bladeCLI.err();

					e.printStackTrace(err);
				}
			}

		}.start();

		Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				try {
					File file = modified.toFile();

					File modifiedFile = file.getAbsoluteFile();

					if (outputPaths.contains(modifiedFile.toPath())) {
						bladeCLI.out("installOrUpdate " + modifiedFile);

						_installOrUpdate(modifiedFile, host, port);
					}
				}
				catch (Exception e) {
					String exceptionMessage = e.getMessage() == null ? "" : (System.lineSeparator() + e.getMessage());

					String message = "Error: Bundle Insatllation failed: " + modified + exceptionMessage;

					_addError(message);

					PrintStream err = bladeCLI.err();

					e.printStackTrace(err);
				}
			}

		};

		BaseArgs args = bladeCLI.getBladeArgs();

		File base = new File(args.getBase());

		new FileWatcher(base.toPath(), true, consumer);
	}

	private void _installNewBundle(
			LiferayBundleDeployer client, Entry<String, Attrs> bsn, Entry<String, Attrs> fragmentHost, long hostId,
			URI uri)
		throws Exception {

		BladeCLI bladeCLI = getBladeCLI();

		PrintStream out = bladeCLI.out();

		long installedId = client.install(uri);

		out.println("Installed bundle " + installedId);

		if ((fragmentHost != null) && (hostId > 0)) {
			client.refresh(hostId);

			out.println("Deployed fragment bundle " + installedId);
		}
		else {
			long existingId = client.getBundleId(bsn.getKey());

			try {
				if (!Objects.equals(installedId, existingId)) {
					out.println("Error: Bundle IDs do not match.");
				}
				else {
					if (existingId > 1) {
						client.start(existingId);

						out.println("Started bundle " + installedId);
					}
					else {
						out.println("Error: bundle failed to start: " + bsn);
					}
				}
			}
			catch (Exception e) {
				String exceptionMessage = e.getMessage() == null ? "" : (System.lineSeparator() + e.getMessage());

				String message = "Error: Bundle Deployment failed: " + bsn + exceptionMessage;

				_addError("deploy watch", message);

				PrintStream err = bladeCLI.err();

				e.printStackTrace(err);
			}
		}
	}

	private void _installOrUpdate(File file, String host, int port) throws Exception {
		file = file.getAbsoluteFile();

		try (LiferayBundleDeployer client = LiferayBundleDeployer.newInstance(host, port)) {
			String name = file.getName();

			name = name.toLowerCase();

			Domain bundle = Domain.domain(file);

			Entry<String, Attrs> bsn = bundle.getBundleSymbolicName();

			if (name.endsWith(".war")) {
				_deployWar(file, client);
			}
			else if (bsn != null) {
				_deployBundle(file, client, bundle, bsn);
			}
		}
	}

	private final void _reloadExistingBundle(
			LiferayBundleDeployer client, Entry<String, Attrs> fragmentHost, long existingId, long hostId, URI uri)
		throws Exception {

		if ((fragmentHost != null) && (hostId > 0)) {
			client.reloadFragment(existingId, hostId, uri);
		}
		else {
			client.reloadBundle(existingId, uri);
		}

		PrintStream out = getBladeCLI().out();

		out.println("Updated bundle " + existingId);
	}

}