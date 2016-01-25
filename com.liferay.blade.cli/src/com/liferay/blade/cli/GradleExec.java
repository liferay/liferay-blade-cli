package com.liferay.blade.cli;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author David Truong
 */
public class GradleExec {

	public GradleExec(blade blade) {
		File gradlew = Util.getGradleWrapper(blade.getBase());

		if (gradlew != null) {
			try {
				_executable = gradlew.getCanonicalPath();
			}
			catch (Exception e) {
				blade.out().println(
					"Could not find gradle wrapper, using gradle");

				_executable = "gradle";
			}
		}
		else {
			blade.out().println("Could not find gradle wrapper, using gradle");

			_executable = "gradle";
		}
	}

	public int executeGradleCommand(String cmd) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder();

		List<String> commands = new ArrayList<>();

		Map<String, String> env = processBuilder.environment();

		if (Util.isWindows()) {
			commands.add("cmd.exe");
			commands.add("/c");
		}
		else {
			env.put("PATH", env.get("PATH") + ":/usr/local/bin");

			commands.add("sh");
			commands.add("-c");
		}

		commands.add(_executable + " " + cmd);

		processBuilder.command(commands);

		processBuilder.inheritIO();

		Process process = processBuilder.start();

		return process.waitFor();
	}

	private String _executable;

}