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
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand {

	public DeployCommand(BladeCLI blade, DeployCommandArgs options) throws Exception {
		_blade = blade;
		_options = options;
		_host = "localhost";
		_port = 11311;
	}

	public void deploy(GradleExec gradle, Set<File> outputFiles) throws Exception {
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
				} catch (Exception e) {
					PrintStream err = _blade.err();

					err.println(e.getMessage());

					e.printStackTrace(err);
				}
			}
		);
	}

	public void deployWatch(final GradleExec gradleExec, final Set<File> outputFiles) throws Exception {
		deploy(gradleExec, outputFiles);

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
			deployWatch(gradleExec, outputFiles);
		}
		else {
			deploy(gradleExec, outputFiles);
		}
	}

	private static long _getBundleId(List<BundleDTO> bundles, String bsn) throws IOException {
		long existingId = -1;

		if (bsn == null) {
			return existingId;
		}

		if (Util.isNotEmpty(bundles)) {
			for (BundleDTO bundle : bundles) {
				if (bundle.symbolicName.equals(bsn)) {
					existingId = bundle.id;

					break;
				}
			}
		}

		return existingId;
	}

	private static List<BundleDTO> _getBundles(GogoTelnetClient client) throws IOException {
		List<BundleDTO> bundles = new ArrayList<>();

		String output = client.send("lb -s -u");

		String[] lines = output.split("\\r?\\n");

		for (String line : lines) {
			try {
				String[] fields = line.split("\\|");

				//ID|State|Level|Symbolic name
				BundleDTO bundle = new BundleDTO();

				bundle.id = Long.parseLong(fields[0].trim());
				bundle.state = _getState(fields[1].trim());
				bundle.symbolicName = fields[3];

				bundles.add(bundle);
			}
			catch (Exception e) {
			}
		}

		return bundles;
	}

	private static int _getState(String state) {
		String bundleState = state.toUpperCase();

		if ("ACTIVE".equals(bundleState)) {
			return Bundle.ACTIVE;
		}
		else if ("INSTALLED".equals(bundleState)) {
			return Bundle.INSTALLED;
		}
		else if ("RESOLVED".equals(bundleState)) {
			return Bundle.RESOLVED;
		}
		else if ("STARTING".equals(bundleState)) {
			return Bundle.STARTING;
		}
		else if ("STOPPING".equals(bundleState)) {
			return Bundle.STOPPING;
		}
		else if ("UNINSTALLED".equals(bundleState)) {
			return Bundle.UNINSTALLED;
		}

		return 0;
	}

	private void _addError(String msg) {
		_blade.addErrors("deploy", Collections.singleton(msg));
	}

	private void _addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void _installOrUpdate(File file) throws Exception {
		file = file.getAbsoluteFile();

		try (GogoTelnetClient client = new GogoTelnetClient(_host, _port)) {
			String name = file.getName();

			name = name.toLowerCase();

			Domain bundle = Domain.domain(file);

			PrintStream out = _blade.out();

			Entry<String, Attrs> bsn = bundle.getBundleSymbolicName();

			if (bsn != null) {
				Entry<String, Attrs> fragmentHost = bundle.getFragmentHost();

				String hostBsn = null;

				if (fragmentHost != null) {
					hostBsn = fragmentHost.getKey();
				}

				List<BundleDTO> bundles = _getBundles(client);

				long existingId = _getBundleId(bundles, bsn.getKey());

				long hostId = _getBundleId(bundles, hostBsn);

				URI uri = file.toURI();

				String bundleURL = uri.toASCIIString();

				if (existingId > 0) {
					if (fragmentHost != null && (hostId > 0)) {
						out.println(client.send("update " + existingId + " " + bundleURL));
						out.println(client.send("refresh " + hostId));
					}
					else {
						out.println(client.send("stop " + existingId));
						out.println(client.send("update " + existingId + " " + bundleURL));
						out.println(client.send("start " + existingId));
					}

					out.println("Updated bundle " + existingId);
				}
				else {
					String install = client.send("install " + bundleURL);

					out.println(install);

					if ((fragmentHost != null) && (hostId > 0)) {
						out.println(client.send("refresh " + hostId));
					}
					else {
						existingId = _getBundleId(_getBundles(client), bsn.getKey());

						if (existingId > 1) {
							out.println(client.send("start " + existingId));
						}
						else {
							out.println("Error: fail to install " + bsn);
						}
					}
				}
			}
			else if (name.endsWith(".war")) {
		 		String webContextPath = name.substring(0, name.lastIndexOf('.'));

		 		URI uri = file.toURI();

		 		String webbundle = uri.toASCIIString();

		 		String command = "install webbundle:" + webbundle + "?Web-ContextPath=/" + webContextPath;

		 		String install = client.send(command);

				out.println(install);

		 		out.println(client.send("start"));
			}
		}
	}

	private final BladeCLI _blade;
	private final String _host;
	private final DeployCommandArgs _options;
	private final int _port;

}