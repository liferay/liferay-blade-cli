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

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Jar;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import com.liferay.blade.cli.FileWatcher.Consumer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand {

	public static final String DESCRIPTION =
		"Builds and deploys bundles to the Liferay module framework.";

	public DeployCommand(blade blade, DeployOptions options) throws Exception {
		_blade = blade;
		_options = options;
		_host = options.host() != null ? options.host() : "localhost";
		_port = options.port() != 0 ? options.port() : 11311;
	}

	public void deploy(GradleExec gradle, Set<File> outputFiles)
		throws Exception {

		int retcode = gradle.executeGradleCommand("build -x check");

		if (retcode > 0) {
			addError("Gradle jar task failed.");
			return;
		}

		for (File outputFile : outputFiles) {
			installOrUpdate(outputFile);
		}
	}

	public void deployWatch(
			final GradleExec gradleExec, final Set<File> outputFiles)
		throws Exception {

		deploy(gradleExec, outputFiles);

		new Thread() {

			public void run() {
				try {
					gradleExec.executeGradleCommand("build -x check -t");
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

						installOrUpdate(modifiedFile);
					}
				}
				catch (Exception e) {
				}
			}

		};

		new FileWatcher(_blade.getBase().toPath(), true, consumer);
	}

	public void execute() throws Exception {
		if (!Util.canConnect("localhost", _port)) {
			addError(
				"deploy",
				"Unable to connect to remote agent on port " + _port + ". " +
					"To install the agent bundle run the command \"blade " +
						"agent install\".");
			return;
		}

		GradleExec gradleExec = new GradleExec(_blade);

		Set<File> outputFiles = GradleTooling.getOutputFiles(
			_blade.getCacheDir(), _blade.getBase());

		if (_options.watch()) {
			deployWatch(gradleExec, outputFiles);
		}
		else {
			deploy(gradleExec, outputFiles);
		}
	}

	@Description(DESCRIPTION)
	public interface DeployOptions extends Options {

		@Description("The host to use to connect to gogo shell")
		public String host();

		@Description("The port to use to connect to gogo shell")
		public int port();

		@Description(
			"Watches the deployed file for changes and will automatically " +
				"redeploy"
		)
		public boolean watch();

	}


	private void addError(String msg) {
		_blade.addErrors("deploy", Collections.singleton(msg));
	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void installOrUpdate(File outputFile) throws Exception {
		boolean isFragment = false;
		String fragmentHost = null;
		String bsn = null;

		try(Jar bundle = new Jar(outputFile)) {
			Manifest manifest = bundle.getManifest();
			Attributes mainAttributes = manifest.getMainAttributes();

			fragmentHost = mainAttributes.getValue("Fragment-Host");

			isFragment = fragmentHost != null;

			bsn = bundle.getBsn();
		}

		GogoTelnetClient client = new GogoTelnetClient(_host, _port);

		long existingId = -1;

		List<BundleDTO> bundles = getBundles(client);

		for (BundleDTO bundle : bundles) {
			if (bundle.symbolicName.equals(bsn)) {
				existingId = bundle.id;
				break;
			}
		}

		String bundleURL = outputFile.toURI().toASCIIString();

		if (existingId > 0) {
			String response = client.send("stop " + existingId);

			_blade.out().println(response);

			response = client.send("update " + existingId + " " + bundleURL);

			_blade.out().println(response);

			_blade.out().println("Updated bundle " + existingId);
		}
		else {
			_blade.out().println("install " + bundleURL);

			String response = client.send("install " + bundleURL);

			_blade.out().println(response);

			if (!isFragment) {
				bundles = getBundles(client);

				for (BundleDTO bundle : bundles) {
					if (bundle.symbolicName.equals(bsn)) {
						existingId = bundle.id;
						break;
					}
				}
			}
		}

		String response = client.send("start " + existingId);

		_blade.out().println(response);

		if (isFragment) {
			String hostBSN = new Parameters(
				fragmentHost).keySet().iterator().next();

			long hostId = -1;

			for (BundleDTO bundle : bundles) {
				if (bundle.symbolicName.equals(hostBSN)) {
					hostId = bundle.id;
					break;
				}
			}

			if (hostId > 0) {
				_blade.out().println("refreshing host " + hostId);

				response = client.send("refresh " + hostId);

				_blade.out().println(response);
			}
		}

		client.close();
	}

	private List<BundleDTO> getBundles(GogoTelnetClient client)
		throws IOException {

		List<BundleDTO> bundles = new ArrayList<>();

		String output = client.send("lb -s -u");

		String lines[] = output.split("\\r?\\n");

		for (String line : lines) {
			try {
				String[] fields = line.split("\\|");

				//ID|State|Level|Symbolic name
				BundleDTO bundle = new BundleDTO();

				bundle.id = Long.parseLong(fields[0].trim());
				bundle.state = getState(fields[1].trim());
				bundle.symbolicName = fields[3];

				bundles.add(bundle);
			}
			catch (Exception e) {
			}
		}

		return bundles;
	}

	private int getState(String state) {
		String bundleState = state.toUpperCase();

		if ("ACTIVE".equals(bundleState)) {
			return Bundle.ACTIVE;
		}
		else if ("INSTALLED".equals(Bundle.INSTALLED)) {
			return Bundle.INSTALLED;
		}
		else if ("RESOLVED".equals(Bundle.RESOLVED)) {
			return Bundle.RESOLVED;
		}
		else if ("STARTING".equals(Bundle.STARTING)) {
			return Bundle.STARTING;
		}
		else if ("STOPPING".equals(Bundle.STOPPING)) {
			return Bundle.STOPPING;
		}
		else if ("UNINSTALLED".equals(Bundle.UNINSTALLED)) {
			return Bundle.UNINSTALLED;
		}

		return 0;
	}

	private final blade _blade;
	private final String _host;
	private final DeployOptions _options;
	private final int _port;

}