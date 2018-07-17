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

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Truong
 */
public class ServerStopCommand extends BaseCommand<ServerStopArgs> {

	public ServerStopCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		File gradleWrapperFile = BladeUtil.getGradleWrapper(bladeCLI.getBase());

		if (gradleWrapperFile == null) {
			throw new NoSuchElementException("Unable to locate Gradle Wrapper, cannot process command");
		}

		Path gradleWrapperPath = gradleWrapperFile.toPath();

		Path parent = gradleWrapperPath.getParent();

		File rootDir = parent.toFile();

		String serverType = null;

		Path rootDirPath = rootDir.toPath();

		if (BladeUtil.isWorkspace(rootDir)) {
			Properties properties = BladeUtil.getGradleProperties(rootDir);

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

			Path tempLiferayHome = Paths.get(liferayHomePath);
			Path liferayHomeDir = null;

			if (tempLiferayHome.isAbsolute()) {
				liferayHomeDir = tempLiferayHome.normalize();
			}
			else {
				Path tempFile = rootDirPath.resolve(tempLiferayHome);

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
	public Class<ServerStopArgs> getArgsClass() {
		return ServerStopArgs.class;
	}

	private void _commandServer(Path dir, String serverType) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		try (Stream<Path> list = Files.list(dir)) {
			Collection<Path> paths = list.collect(Collectors.toList());

			if (Files.notExists(dir) || paths.isEmpty()) {
				bladeCLI.error(
					" bundles folder does not exist in Liferay Workspace, execute 'gradlew initBundle' in order to " +
						"create it.");

				return;
			}

			for (Path file : paths) {
				Path fileName = file.getFileName();

				if (fileName.startsWith(serverType) && Files.isDirectory(file)) {
					if (serverType.equals("tomcat")) {
						_commmandTomcat(file);

						return;
					}
					else if (serverType.equals("jboss") || serverType.equals("wildfly")) {
						_commmandJBossWildfly();

						return;
					}
				}
			}

			bladeCLI.error(serverType + " not supported");
		}
	}

	private void _commmandJBossWildfly() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.error("JBoss/Wildfly supports start command and debug flag");
	}

	private void _commmandTomcat(Path dir) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Map<String, String> enviroment = new HashMap<>();

		enviroment.put("CATALINA_PID", "catalina.pid");

		String executable = "./catalina.sh";

		if (BladeUtil.isWindows()) {
			executable = "catalina.bat";
		}

		Path binPath = dir.resolve("bin");

		Process process = BladeUtil.startProcess(
			bladeCLI, executable + " stop 60 -force", binPath.toFile(), enviroment);

		process.waitFor();
	}

}