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

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author David Truong
 */
public class ServerCommand {

	public static final String DESCRIPTION =
		"Start or stop server defined by your Liferay project";

	public static final String DESCRIPTION_START =
		"Start server defined by your Liferay project";

	public static final String DESCRIPTION_STOP =
		"Stop server defined by your Liferay project";

	public ServerCommand(blade blade, ServerOptions options) {
		_blade = blade;
	}

	@Description(DESCRIPTION_START)
	public void _start(ServerStartOptions options) throws Exception {
		executeCommand("start", options);
	}

	@Description(DESCRIPTION_STOP)
	public void _stop(ServerStopOptions options) throws Exception {
		executeCommand("stop", options);
	}

	@Description(DESCRIPTION)
	public interface ServerOptions extends Options {
	}

	@Description(DESCRIPTION_START)
	public interface ServerStartOptions extends ServerOptions {

		@Description("Start server in background")
		public boolean background();

		@Description("Start server in debug mode")
		public boolean debug();

		@Description("Tail a running server")
		public boolean tail();

	}

	@Description(DESCRIPTION_STOP)
	public interface ServerStopOptions extends ServerOptions {
	}

	private void commandServer(
			String cmd, File dir, String serverType, ServerOptions options)
		throws Exception {

		if (!dir.exists() || dir.listFiles() == null) {
			_blade.error(
				" bundles folder does not exist in Liferay Workspace, execute 'gradlew initBundle' in order to create it.");

			return;
		}

		for (File file : dir.listFiles()) {
			String fileName = file.getName();

			if (fileName.startsWith(serverType) && file.isDirectory()) {
				if (serverType.equals("tomcat")) {
					commmandTomcat(cmd, file, options);

					return;
				}
				else if (serverType.equals("jboss") ||
						 serverType.equals("wildfly")) {

					commmandJBossWildfly(cmd, file, options);

					return;
				}
			}
		}

		_blade.error(serverType + " not supported");
	}

	private void commmandJBossWildfly(
			String cmd, File dir, ServerOptions options)
		throws Exception {

		Map<String, String> enviroment = new HashMap<>();

		String executable = "./standalone.sh";

		if (Util.isWindows()) {
			executable = "standalone.bat";
		}

		if (cmd.equals("start")) {
			ServerStartOptions startOptions = (ServerStartOptions)options;

			String debug = "";

			if (startOptions.debug()) {
				debug = " --debug";
			}

			Process process = Util.startProcess(
				_blade, executable + debug, new File(dir, "bin"), enviroment);

			process.waitFor();
		}
		else {
			_blade.error("JBoss/Wildfly supports start command and debug flag");
		}
	}

	private void commmandTomcat(String cmd, File dir, ServerOptions options)
		throws Exception {

		Map<String, String> enviroment = new HashMap<>();

		enviroment.put("CATALINA_PID", "catalina.pid");

		String executable = "./catalina.sh";

		if (Util.isWindows()) {
			executable = "catalina.bat";
		}

		if (cmd.equals("start")) {
			ServerStartOptions startOptions = (ServerStartOptions)options;

			String startCommand = " run";

			if (startOptions.background()) {
				startCommand = " start";
			}
			else if (startOptions.debug()) {
				startCommand = " jpda " + startCommand;
			}

			File logs = new File(dir, "logs");
			logs.mkdirs();

			File catalinaOut = new File(logs, "catalina.out");
			catalinaOut.createNewFile();

			final Process process = Util.startProcess(
				_blade, executable + startCommand, new File(dir, "bin"),
				enviroment);

			Runtime runtime = Runtime.getRuntime();

			runtime.addShutdownHook(new Thread() {
				public void run() {
					try {
						process.waitFor();
					} catch (InterruptedException e) {
						_blade.error("Could not wait for process to end " +
							"before shutting down");
					}
				}
			});

			if (startOptions.background() && startOptions.tail()) {
				Process tailProcess = Util.startProcess(
					_blade, "tail -f catalina.out", logs, enviroment);

				tailProcess.waitFor();
			}
		}
		else if (cmd.equals("stop")) {
			Process process = Util.startProcess(
				_blade, executable + " stop 60 -force", new File(dir, "bin"),
				enviroment);

			process.waitFor();
		}
	}

	private void executeCommand(String cmd, ServerOptions options)
		throws Exception {

		File gradleWrapper = Util.getGradleWrapper(_blade.getBase());

		File rootDir = gradleWrapper.getParentFile();

		String serverType = null;

		if (Util.isWorkspace(rootDir)) {
			Properties properties = Util.getGradleProperties(rootDir);

			String liferayHomePath = properties.getProperty(
				Workspace.DEFAULT_LIFERAY_HOME_DIR_PROPERTY);

			if ((liferayHomePath == null) || liferayHomePath.equals("")) {
				liferayHomePath = Workspace.DEFAULT_LIFERAY_HOME_DIR;
			}

			serverType = properties.getProperty(
				Workspace.DEFAULT_BUNDLE_ARTIFACT_NAME_PROPERTY);

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

			File tempLiferayHome = new File(liferayHomePath);
			File liferayHomeDir = null;

			if (tempLiferayHome.isAbsolute()) {
				liferayHomeDir = tempLiferayHome.getCanonicalFile();
			}
			else {
				File tempFile = new File(rootDir, liferayHomePath);
				liferayHomeDir = tempFile.getCanonicalFile();
			}

			commandServer(cmd, liferayHomeDir, serverType, options);
		}
		else {
			try {
				List<Properties> propertiesList = Util.getAppServerProperties(
					rootDir);

				String appServerParentDir = "";

				for (Properties properties : propertiesList) {
					if (appServerParentDir.equals("")) {
						String appServerParentDirTemp = properties.getProperty(
							Util.APP_SERVER_PARENT_DIR_PROPERTY);

						if ((appServerParentDirTemp != null) &&
							!appServerParentDirTemp.equals("")) {

							appServerParentDirTemp =
								appServerParentDirTemp.replace(
									"${project.dir}",
									rootDir.getCanonicalPath());

							appServerParentDir = appServerParentDirTemp;
						}
					}

					if ((serverType == null) || serverType.equals("")) {
						String serverTypeTemp = properties.getProperty(
							Util.APP_SERVER_TYPE_PROPERTY);

						if ((serverTypeTemp != null) &&
							!serverTypeTemp.equals("")) {

							serverType = serverTypeTemp;
						}
					}
				}

				if (appServerParentDir.startsWith("/") ||
					appServerParentDir.contains(":")) {

					commandServer(
						cmd, new File(appServerParentDir), serverType, options);
				}
				else {
					commandServer(
						cmd, new File(rootDir, appServerParentDir), serverType,
						options);
				}
			}
			catch (Exception e) {
				_blade.error(
					"Please execute this command from a Liferay project");
			}
		}
	}

	private blade _blade;

}