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

import com.liferay.blade.cli.FileWatcher.Consumer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
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

	public DeployCommand(BladeCLI blade, DeployCommandArgs options) throws Exception {
		_blade = blade;
		_options = options;
		_host = "localhost";
		_port = 11311;
	}

	public void deploy(GradleExec gradle, Set<File> outputFiles) throws Exception {
		int retcode = gradle.executeGradleCommand("build -x check");

		if (retcode > 0) {
			_addError("Gradle jar task failed.");
			return;
		}

		for (File outputFile : outputFiles) {
			_installOrUpdate(outputFile);
		}
	}

	public void deployWatch(final GradleExec gradleExec, final Set<File> outputFiles) throws Exception {
		deploy(gradleExec, outputFiles);

		new Thread() {

			@Override
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

	private void _installOrUpdate(File outputFile) throws Exception {
		if (outputFile.getName().endsWith(".war")) {
 			String printFileName = outputFile.getName();
 
 			printFileName = printFileName.substring(
 				0, printFileName.lastIndexOf('.'));
 			
 			ShellCommandArgs options = new ShellCommandArgs();
 			options.getArgs().addAll(Arrays.asList (
 					"install",
 					"webbundle:" + outputFile.getAbsoluteFile().toURI().toASCIIString() + "?Web-ContextPath=/" +
 						printFileName ));
 			new ShellCommand(_blade, options).execute();
 		
  		}
		else
		{
			boolean fragment = false;
			String fragmentHost = null;
			String bsn = null;
			String hostBSN = null;
	
			try (Jar bundle = new Jar(outputFile)) {
				Manifest manifest = bundle.getManifest();
	
				Attributes mainAttributes = manifest.getMainAttributes();
	
				fragmentHost = mainAttributes.getValue("Fragment-Host");
	
				fragment = fragmentHost != null;
	
				bsn = bundle.getBsn();
	
				if (fragment) {
					Set<String> keySet = new Parameters(fragmentHost).keySet();
	
					hostBSN = keySet.iterator().next();
				}
			}
	
			GogoTelnetClient client = new GogoTelnetClient(_host, _port);
	
			List<BundleDTO> bundles = _getBundles(client);
	
			long hostId = _getBundleId(bundles, hostBSN);
	
			long existingId = _getBundleId(bundles, bsn);
	
			String bundleURL = outputFile.toURI().toASCIIString();
	
			if (existingId > 0) {
				if (fragment && (hostId > 0)) {
					String response = client.send("update " + existingId + " " + bundleURL);
	
					_blade.out().println(response);
	
					response = client.send("refresh " + hostId);
	
					_blade.out().println(response);
				}
				else {
					String response = client.send("stop " + existingId);
	
					_blade.out().println(response);
	
					response = client.send("update " + existingId + " " + bundleURL);
	
					_blade.out().println(response);
	
					response = client.send("start " + existingId);
	
					_blade.out().println(response);
				}
	
				_blade.out().println("Updated bundle " + existingId);
			}
			else {
				String response = client.send("install " + bundleURL);
	
				_blade.out().println(response);
	
				if (fragment && (hostId > 0)) {
					response = client.send("refresh " + hostId);
	
					_blade.out().println(response);
				}
				else {
					existingId = _getBundleId(_getBundles(client), bsn);
	
					if (existingId > 1) {
						response = client.send("start " + existingId);
	
						_blade.out().println(response);
					}
					else {
						_blade.out().println("Error: fail to install " + bsn);
					}
				}
			}
	
			client.close();
		}
		
	}

	private final BladeCLI _blade;
	private final String _host;
	private final DeployCommandArgs _options;
	private final int _port;

}