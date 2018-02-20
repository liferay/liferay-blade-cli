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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author David Truong
 */
public class ServerStartCommand {

	public ServerStartCommand(BladeCLI blade, ServerStartCommandArgs options) {
		_blade = blade;
		_args = options;
	}

	public void execute() throws Exception {
		Path gradleWrapper = Util.getGradleWrapper(_blade.getBase()).toPath();

		File rootDir = gradleWrapper.getParent().toFile();

		String serverType = null;

		Path rootDirPath = rootDir.toPath();

		if (Util.isWorkspace(rootDir)) {
			Properties properties = Util.getGradleProperties(rootDir);

			String liferayHomePath = properties.getProperty(Workspace.DEFAULT_LIFERAY_HOME_DIR_PROPERTY);

			if ((liferayHomePath == null) || liferayHomePath.equals("")) {
				liferayHomePath = Workspace.DEFAULT_LIFERAY_HOME_DIR;
			}

			serverType = properties.getProperty(Workspace.DEFAULT_BUNDLE_ARTIFACT_NAME_PROPERTY);

			if (serverType == null) {
				serverType = Workspace.DEFAULT_BUNDLE_ARTIFACT_NAME;
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
				List<Properties> propertiesList = Util.getAppServerProperties(rootDir);

				String appServerParentDir = "";

				for (Properties properties : propertiesList) {
					if (appServerParentDir.equals("")) {
						String appServerParentDirTemp = properties.getProperty(Util.APP_SERVER_PARENT_DIR_PROPERTY);

						if ((appServerParentDirTemp != null) && !appServerParentDirTemp.equals("")) {
							Path rootDirRealPath = rootDirPath.toRealPath();

							appServerParentDirTemp = appServerParentDirTemp.replace(
								"${project.dir}", rootDirRealPath.toString());

							appServerParentDir = appServerParentDirTemp;
						}
					}

					if ((serverType == null) || serverType.equals("")) {
						String serverTypeTemp = properties.getProperty(Util.APP_SERVER_TYPE_PROPERTY);

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
				_blade.error("Please execute this command from a Liferay project");
			}
		}
	}

	private void _commandServer(Path dir, String serverType) throws Exception {
		if (Files.notExists(dir) || Util.isDirEmpty(dir)) {
			_blade.error(
				" bundles folder does not exist in Liferay Workspace, execute 'gradlew initBundle' in order to " +
					"create it.");

			return;
		}

		Optional<Path> server = Files.find(dir, Integer.MAX_VALUE, (file, bbfa) -> {
				Path fileName = file.getFileName();
				String fileNameString = String.valueOf(fileName);

				return fileNameString.startsWith(serverType) && Files.isDirectory(file);
			}).findFirst();

		boolean success = false;

		if (server.isPresent()) {
			Path file = server.get();

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
			_blade.error(serverType + " not supported");
		}

	}

	private void _commmandJBossWildfly(Path dir) throws Exception {
		Map<String, String> enviroment = new HashMap<>();

		String executable = "./standalone.sh";

		if (Util.isWindows()) {
			executable = "standalone.bat";
		}

		String debug = "";

		if (_args.isDebug()) {
			debug = " --debug";
		}

		Process process = Util.startProcess(_blade, executable + debug, dir.resolve("bin").toFile(), enviroment);

		process.waitFor();
	}

	private void _commmandTomcat(Path dir) throws Exception {
		Map<String, String> enviroment = new HashMap<>();

		enviroment.put("CATALINA_PID", "catalina.pid");

		String executable = "./catalina.sh";

		if (Util.isWindows()) {
			executable = "catalina.bat";
		}

		String startCommand = " run";

		if (_args.isBackground()) {
			startCommand = " start";
		}
		else if (_args.isDebug()) {
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

		final Process process = Util.startProcess(
			_blade, executable + startCommand, dir.resolve("bin").toFile(), enviroment);

		Runtime runtime = Runtime.getRuntime();

		runtime.addShutdownHook(
			new Thread() {

				@Override
				public void run() {
					try {
						process.waitFor();
					}
					catch (InterruptedException ie) {
						_blade.error("Could not wait for process to end before shutting down");
					}
				}

			});

		if (_args.isBackground() && _args.isTail()) {
			Process tailProcess = Util.startProcess(_blade, "tail -f catalina.out", logsPath.toFile(), enviroment);

			tailProcess.waitFor();
		}
	}

	private ServerStartCommandArgs _args;
	private BladeCLI _blade;

}