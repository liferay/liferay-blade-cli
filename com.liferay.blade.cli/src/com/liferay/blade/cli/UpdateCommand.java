package com.liferay.blade.cli;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public class UpdateCommand {
	public UpdateCommand(blade blade, UpdateOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String jarPath = args.size() > 0 ? args.get(0) : _JAR_PATH;

		ProcessBuilder processBuilder =
			new ProcessBuilder("jpm", "install", "-f", jarPath);

		processBuilder.redirectOutput(Redirect.INHERIT);
		processBuilder.redirectError(Redirect.INHERIT);

		Process process = processBuilder.start();

		int errCode = process.waitFor();

		if (errCode == 0) {
			System.out.println("Update completed successfully");
		}
	}

	private blade _blade;
	private UpdateOptions _options;
	private static final String _JAR_PATH =
		"https://liferay-test-01.ci.cloudbees.com/job/blade.tools/" +
		"lastSuccessfulBuild/artifact/com.liferay.blade.cli/generated/" +
		"com.liferay.blade.cli.jar";


}
