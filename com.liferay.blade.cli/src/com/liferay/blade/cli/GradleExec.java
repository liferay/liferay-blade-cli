package com.liferay.blade.cli;

import java.io.File;

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

		Util.useShell(processBuilder, "\"" + _executable + "\" " + cmd);

		processBuilder.inheritIO();

		Process process = processBuilder.start();

		return process.waitFor();
	}

	private String _executable;

}