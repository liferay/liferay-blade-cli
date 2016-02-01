package com.liferay.blade.cli;

import java.io.File;

/**
 * @author David Truong
 */
public class GradleExec {

	public GradleExec(blade blade) {
		_blade = blade;

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
		Process process = Util.startProcess(
			_blade, "\"" + _executable + "\" " + cmd);

		return process.waitFor();
	}

	private blade _blade;
	private String _executable;

}