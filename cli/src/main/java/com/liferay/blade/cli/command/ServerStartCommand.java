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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ServerUtil;
import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author David Truong
 * @author Simon Jiang
 */
public class ServerStartCommand extends BaseCommand<ServerStartArgs> {

	public ServerStartCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getBladeArgs();

		File baseDir = new File(baseArgs.getBase());

		File gradleWrapperFile = BladeUtil.getGradleWrapper(baseDir);

		Path gradleWrapperPath = gradleWrapperFile.toPath();

		Path parent = gradleWrapperPath.getParent();

		File rootDir = parent.toFile();

		String serverType = null;

		Path rootDirPath = rootDir.toPath();

		if (WorkspaceUtil.isWorkspace(rootDir)) {
			Properties properties = WorkspaceUtil.getGradleProperties(rootDir);

			String liferayHomePath = properties.getProperty(WorkspaceConstants.DEFAULT_LIFERAY_HOME_DIR_PROPERTY);

			if ((liferayHomePath == null) || liferayHomePath.equals("")) {
				liferayHomePath = WorkspaceConstants.DEFAULT_LIFERAY_HOME_DIR;
			}

			serverType = properties.getProperty(WorkspaceConstants.DEFAULT_BUNDLE_ARTIFACT_NAME_PROPERTY);

			if (serverType == null) {
				serverType = WorkspaceConstants.DEFAULT_BUNDLE_ARTIFACT_NAME;
			}

			if (serverType.contains("jboss")) {
				serverType = "jboss";
			}
			else if (serverType.contains("wildfly")) {
				serverType = "wildfly";
			}
			else if (serverType.contains("tomcat")) {
				serverType = "tomcat";
			}

			Path liferayHomeDir = null;
			Path tempLiferayHome = Paths.get(liferayHomePath);

			if (tempLiferayHome.isAbsolute()) {
				liferayHomeDir = tempLiferayHome.normalize();
			}
			else {
				Path tempFile = rootDirPath.resolve(liferayHomePath);

				liferayHomeDir = tempFile.normalize();
			}

			_commandServer(liferayHomeDir, serverType);
		}
		else {
			try {
				List<Properties> propertiesList = BladeUtil.getAppServerProperties(rootDir);

				String appServerParentDir = "";

				for (Properties properties : propertiesList) {
					if (appServerParentDir.equals("")) {
						String appServerParentDirTemp = properties.getProperty(
							BladeUtil.APP_SERVER_PARENT_DIR_PROPERTY);

						if ((appServerParentDirTemp != null) && !appServerParentDirTemp.equals("")) {
							Path rootDirRealPath = rootDirPath.toRealPath();

							appServerParentDirTemp = appServerParentDirTemp.replace(
								"${project.dir}", rootDirRealPath.toString());

							appServerParentDir = appServerParentDirTemp;
						}
					}

					if ((serverType == null) || serverType.equals("")) {
						String serverTypeTemp = properties.getProperty(BladeUtil.APP_SERVER_TYPE_PROPERTY);

						if ((serverTypeTemp != null) && !serverTypeTemp.equals("")) {
							serverType = serverTypeTemp;
						}
					}
				}

				if (appServerParentDir.startsWith("/") || appServerParentDir.contains(":")) {
					_commandServer(Paths.get(appServerParentDir), serverType);
				}
				else {
					_commandServer(rootDirPath.resolve(appServerParentDir), serverType);
				}
			}
			catch (Exception e) {
				bladeCLI.error("Please execute this command from a Liferay project");
			}
		}
	}

	@Override
	public Class<ServerStartArgs> getArgsClass() {
		return ServerStartArgs.class;
	}

	public Collection<Process> getProcesses() {
		return _processes;
	}

	private void _commandServer(Path dir, String serverType) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		if (Files.notExists(dir) || BladeUtil.isDirEmpty(dir)) {
			bladeCLI.error(
				" bundles folder does not exist in Liferay Workspace, execute 'gradlew initBundle' in order to " +
					"create it.");

			return;
		}

		Optional<Path> serverFolder = ServerUtil.findServerFolder(dir, serverType);

		boolean success = false;

		if (serverFolder.isPresent()) {
			Path file = serverFolder.get();

			if (serverType.equals("tomcat")) {
				_commmandTomcat(file);

				success = true;
			}
			else if (serverType.equals("jboss") || serverType.equals("wildfly")) {
				_commmandJBossWildfly(file);

				success = true;
			}
		}

		if (!success) {
			bladeCLI.error(serverType + " not supported");
		}
	}

	private void _commmandJBossWildfly(Path dir) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		ServerStartArgs serverStartArgs = getArgs();

		Map<String, String> enviroment = new HashMap<>();

		String executable = ServerUtil.getJBossWildflyExecutable();

		String debug = "";

		if (serverStartArgs.isDebug()) {
			debug = " --debug";
		}

		Path binPath = dir.resolve("bin");

		Process process = BladeUtil.startProcess(
			executable + debug, binPath.toFile(), enviroment, bladeCLI.out(), bladeCLI.err());

		_processes.add(process);

		process.waitFor();
	}

	private void _commmandTomcat(Path dir) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		ServerStartArgs serverStartArgs = getArgs();

		Map<String, String> enviroment = new HashMap<>();

		enviroment.put("CATALINA_PID", "catalina.pid");

		String executable = ServerUtil.getTomcatExecutable();

		String startCommand = " run";

		if (serverStartArgs.isBackground()) {
			startCommand = " start";
		}

		if (serverStartArgs.isDebug()) {
			startCommand = " jpda " + startCommand;
		}

		Path logsPath = dir.resolve("logs");

		if (!Files.exists(logsPath)) {
			Files.createDirectory(logsPath);
		}

		Path catalinaOutPath = logsPath.resolve("catalina.out");

		if (!Files.exists(catalinaOutPath)) {
			Files.createFile(catalinaOutPath);
		}

		Path binPath = dir.resolve("bin");

		final Process process = BladeUtil.startProcess(
			executable + startCommand, binPath.toFile(), enviroment, bladeCLI.out(), bladeCLI.err());

		_processes.add(process);

		Runtime runtime = Runtime.getRuntime();

		runtime.addShutdownHook(
			new Thread() {

				@Override
				public void run() {
					try {
						process.waitFor();
					}
					catch (InterruptedException ie) {
						bladeCLI.error("Could not wait for process to end before shutting down");
					}
				}

			});

		if (serverStartArgs.isBackground() && serverStartArgs.isTail()) {
			Process tailProcess = BladeUtil.startProcess("tail -f catalina.out", logsPath.toFile(), enviroment);

			_processes.add(tailProcess);

			tailProcess.waitFor();
		}
	}

	private Collection<Process> _processes = new HashSet<>();

}