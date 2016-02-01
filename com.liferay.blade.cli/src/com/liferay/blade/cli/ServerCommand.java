package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
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

	public ServerCommand(blade blade, ServerOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		File gradleWrapper = Util.getGradleWrapper(_blade.getBase());

		File rootDir = gradleWrapper.getParentFile();

		executeCommand(rootDir);
	}

	@Arguments(arg = {"[serverType]"})
	public interface ServerOptions extends Options {

		@Description("Start server in debug mode")
		public boolean debug();

		@Description("Restart server")
		public boolean restart();

		@Description("Run server in current window")
		public boolean run();

		@Description("Start server in background")
		public boolean start();

		@Description("Stop server")
		public boolean stop();

		@Description("Tail server")
		public boolean tail();

	}

	private void commandServer(File dir, String serverType) throws Exception {
		for (File file : dir.listFiles()) {
			String fileName = file.getName();

			if (fileName.startsWith(serverType) && file.isDirectory()) {
				if (serverType.equals("tomcat")) {
					commmandTomcat(file);

					return;
				}
				else if (serverType.equals("jboss") ||
						 serverType.equals("wildfly")) {

					commmandJBossWildfly(file);

					return;
				}
			}
		}
	}

	private void commmandJBossWildfly(File dir) throws Exception {
		Map<String, String> enviroment = new HashMap<>();

		String executable = "./standalone.sh";

		if (Util.isWindows()) {
			executable = "standalone.bat";
		}

		if (_options.debug()) {
			Process process = Util.startProcess(
				_blade, executable + " --debug", new File(dir, "bin"),
				enviroment);

			process.waitFor();
		}
		else if (_options.run()) {
			Process process = Util.startProcess(
				_blade, executable, new File(dir, "bin"), enviroment);

			process.waitFor();
		}
		else {
			_blade.error("JBoss/Wildfly only supports debug and run");
		}
	}

	private void commmandTomcat(File dir) throws Exception {
		Map<String, String> enviroment = new HashMap<>();

		enviroment.put("CATALINA_PID", "catalina.pid");

		String executable = "./catalina.sh";

		if (Util.isWindows()) {
			executable = "catalina.bat";
		}

		if (_options.debug()) {
			Process process = Util.startProcess(
				_blade, executable + " jpda run", new File(dir, "bin"),
				enviroment);

			process.waitFor();
		}
		else if (_options.restart()) {
			Process process = Util.startProcess(
				_blade, executable + " stop 60 -force", new File(dir, "bin"),
				enviroment);

			if (process.waitFor() == 0) {
				process = Util.startProcess(
					_blade, executable + " start", new File(dir, "bin"),
					enviroment);

				process.waitFor();
			}
		}
		else if (_options.run()) {
			Process process = Util.startProcess(
				_blade, executable + " run", new File(dir, "bin"), enviroment);

			process.waitFor();
		}
		else if (_options.start()) {
			Process process = Util.startProcess(
				_blade, executable + " start", new File(dir, "bin"),
				enviroment);

			process.waitFor();
		}
		else if (_options.stop()) {
			Process process = Util.startProcess(
				_blade, executable + " stop 60 -force", new File(dir, "bin"),
				enviroment);

			process.waitFor();
		}

		if (_options.tail()) {
			Process process = Util.startProcess(
				_blade, "tail -f catalina.out", new File(dir, "logs"),
				enviroment);

			process.waitFor();
		}
	}

	private void executeCommand(File rootDir) throws Exception {
		List<String> args = _options._arguments();

		String serverType = args.size() > 0 ? args.get(0) : null;

		if (Util.isWorkspace(rootDir)) {
			Properties properties = Util.getGradleProperties(rootDir);

			String liferayHomePath = properties.getProperty(
				Workspace.DEFAULT_LIFERAY_HOME_DIR_PROPERTY);

			if (liferayHomePath == null || liferayHomePath.equals("")) {
				liferayHomePath = Workspace.DEFAULT_LIFERAY_HOME_DIR;
			}

			if (serverType == null) {
				serverType = properties.getProperty(
					Workspace.DEFAULT_BUNDLE_ARTIFACT_NAME_PROPERTY);

				if (serverType == null) {
					serverType = Workspace.DEFAULT_BUNDLE_ARTIFACT_NAME;
				}

				serverType = serverType.replace("portal-", "");

				serverType = serverType.replace("-bundle", "");
			}

			commandServer(new File(rootDir, liferayHomePath), serverType);
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

				if (appServerParentDir.startsWith(".") ||
					(Character.isLowerCase(appServerParentDir.charAt(0)))) {

					commandServer(
						new File(rootDir, appServerParentDir), serverType);
				}
				else {
					commandServer(new File(appServerParentDir), serverType);
				}
			}
			catch (Exception e) {
				_blade.error(
					"Please execute this command from a Liferay project");
			}
		}
	}

	private final blade _blade;
	private final ServerOptions _options;

}