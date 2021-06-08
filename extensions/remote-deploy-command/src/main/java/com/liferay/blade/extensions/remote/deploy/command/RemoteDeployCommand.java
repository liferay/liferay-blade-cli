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

package com.liferay.blade.extensions.remote.deploy.command;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.LiferayBundleDeployer;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileWatcher;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.File;
import java.io.PrintStream;

import java.net.ConnectException;
import java.net.URI;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class RemoteDeployCommand extends BaseCommand<RemoteDeployArgs> {

	public RemoteDeployCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		RemoteDeployArgs deployArgs = getArgs();

		File baseDir = deployArgs.getBase();

		String host = "localhost";
		int port = 11311;

		if (!BladeUtil.canConnect(host, port)) {
			StringBuilder sb = new StringBuilder();

			sb.append("Unable to connect to gogo shell on " + host + ":" + port);
			sb.append(System.lineSeparator());
			sb.append("Liferay may not be running, or the gogo shell may need to be enabled. ");
			sb.append("Please see this link for more details: ");
			sb.append("https://dev.liferay.com/en/develop/reference/");
			sb.append("-/knowledge_base/7-1/using-the-felix-gogo-shell");
			sb.append(System.lineSeparator());

			_addError(sb.toString());

			PrintStream error = bladeCLI.error();

			ConnectException connectException = new ConnectException(sb.toString());

			connectException.printStackTrace(error);

			return;
		}

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(baseDir.toPath());

		Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

		if (deployArgs.isWatch()) {
			_deployWatch(gradleExec, projectOutputFiles, host, port);
		}
		else {
			_deploy(gradleExec, projectOutputFiles, host, port);
		}
	}

	@Override
	public Class<RemoteDeployArgs> getArgsClass() {
		return RemoteDeployArgs.class;
	}

	private void _addError(String msg) {
		getBladeCLI().addErrors("deploy", Collections.singleton(msg));
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _deploy(GradleExec gradle, Map<String, Set<File>> projectOutputFiles, String host, int port)
		throws Exception {

		ProcessResult processResult = gradle.executeTask("assemble -x check");

		int resultCode = processResult.getResultCode();

		BladeCLI bladeCLI = getBladeCLI();

		if (resultCode > 0) {
			String errorMessage = "Gradle assemble task failed.";

			_addError(errorMessage);

			PrintStream err = bladeCLI.error();

			_addError(processResult.getError());

			ConnectException connectException = new ConnectException(errorMessage);

			connectException.printStackTrace(err);

			return;
		}

		Collection<Set<File>> values = projectOutputFiles.values();

		Stream<Set<File>> stream = values.stream();

		stream.flatMap(
			files -> files.stream()
		).filter(
			File::exists
		).forEach(
			outputFile -> {
				try {
					_installOrUpdate(outputFile, host, port);
				}
				catch (Exception exception) {
					String message = exception.getMessage();

					Class<?> exceptionClass = exception.getClass();

					if (message == null) {
						message = "DeployCommand._deploy threw " + exceptionClass.getSimpleName();
					}

					_addError(message);

					PrintStream error = bladeCLI.error();

					exception.printStackTrace(error);
				}
			}
		);
	}

	private void _deployBundle(File file, LiferayBundleDeployer client, Domain bundle, Map.Entry<String, Attrs> bsn)
		throws Exception {

		Map.Entry<String, Attrs> fragmentHost = bundle.getFragmentHost();

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

	private void _deployWar(File file, LiferayBundleDeployer liferayBundleDeployer) throws Exception {
		URI uri = file.toURI();

		long bundleId = liferayBundleDeployer.install(uri);

		if (bundleId > 0) {
			BladeCLI bladeCLI = getBladeCLI();

			PrintStream out = bladeCLI.out();

			out.println("Installed bundle " + bundleId);

			BundleDTO bundle = liferayBundleDeployer.getBundle(bundleId);

			if (bundle.state == Bundle.INSTALLED) {
				liferayBundleDeployer.start(bundleId);

				out.println("Started bundle " + bundleId);
			}
			else if (bundle.state == Bundle.ACTIVE) {
				liferayBundleDeployer.update(bundleId, uri);

				out.println("Updated bundle " + bundleId);
			}
		}
		else {
			throw new Exception("Failed to deploy war: " + file.getAbsolutePath());
		}
	}

	private void _deployWatch(
			final GradleExec gradleExec, final Map<String, Set<File>> projectOutputFiles, String host, int port)
		throws Exception {

		_deploy(gradleExec, projectOutputFiles, host, port);

		Collection<Set<File>> values = projectOutputFiles.values();

		Stream<Set<File>> stream = values.stream();

		Collection<Path> outputPaths = stream.flatMap(
			files -> files.stream()
		).map(
			File::toPath
		).collect(
			Collectors.toSet()
		);

		BladeCLI bladeCLI = getBladeCLI();

		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					gradleExec.executeTask("assemble -x check -t");
				}
				catch (Exception exception) {
					String message = exception.getMessage();

					if (message == null) {
						message = "Gradle build task failed.";
					}

					_addError("deploy watch", message);

					PrintStream error = bladeCLI.error();

					exception.printStackTrace(error);
				}
			}

		};

		thread.start();

		FileWatcher.Consumer<Path> consumer = new FileWatcher.Consumer<Path>() {

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
				catch (Exception exception) {
					String exceptionMessage = (exception.getMessage() == null) ? "" : (System.lineSeparator() + exception.getMessage());

					String message = "Error: Bundle Insatllation failed: " + modified + exceptionMessage;

					_addError(message);

					PrintStream error = bladeCLI.error();

					exception.printStackTrace(error);
				}
			}

		};

		BaseArgs args = bladeCLI.getArgs();

		File baseDir = args.getBase();

		new FileWatcher(baseDir.toPath(), true, consumer);
	}

	private void _installNewBundle(
			LiferayBundleDeployer client, Map.Entry<String, Attrs> bsn, Map.Entry<String, Attrs> fragmentHost,
			long hostId, URI uri)
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
			catch (Exception exception) {
				String exceptionMessage = (exception.getMessage() == null) ? "" : (System.lineSeparator() + exception.getMessage());

				String message = "Error: Bundle Deployment failed: " + bsn + exceptionMessage;

				_addError("deploy watch", message);

				PrintStream error = bladeCLI.error();

				exception.printStackTrace(error);
			}
		}
	}

	private void _installOrUpdate(File file, String host, int port) throws Exception {
		file = file.getAbsoluteFile();

		try (LiferayBundleDeployer client = LiferayBundleDeployer.newInstance(host, port)) {
			String name = file.getName();

			name = name.toLowerCase();

			if (name.endsWith(".war")) {
				_deployWar(file, client);
			}
			else {
				Domain bundle = Domain.domain(file);

				Map.Entry<String, Attrs> bsn = bundle.getBundleSymbolicName();

				if (bsn != null) {
					_deployBundle(file, client, bundle, bsn);
				}
				else {
					getBladeCLI().error("Unable to install or update " + file.getName() + "as it is not a bundle.");
				}
			}
		}
	}

	private final void _reloadExistingBundle(
			LiferayBundleDeployer client, Map.Entry<String, Attrs> fragmentHost, long existingId, long hostId, URI uri)
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