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

package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.StringConverter;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.util.NoSuchElementException;

/**
 * @author David Truong
 */
public class GradleExec {

	public GradleExec(BladeCLI blade) {
		_blade = blade;
	}

	public ProcessResult executeCommand(String cmd, File dir) throws Exception {
		String executable = _getGradleExecutable(dir);

		Process process = BladeUtil.startProcess(_blade, "\"" + executable + "\" " + cmd, dir, true);

		int returnCode = process.waitFor();

		String output = StringConverter.frommInputStream(process.getInputStream());

		String error = StringConverter.frommInputStream(process.getErrorStream());

		return new ProcessResult(returnCode, output, error);
	}

	public ProcessResult executeGradleCommand(String cmd) throws Exception {
		return executeCommand(cmd, _blade.getBase());
	}

	private static boolean _isGradleInstalled() {
		try {
			ProcessBuilder builder = new ProcessBuilder();

			if (BladeUtil.isWindows()) {
				builder.command("cmd.exe", "/c", "gradle -version");
			}
			else {
				builder.command("sh", "-c", "gradle -version");
			}

			builder.directory(BladeCLI.USER_HOME_DIR);

			Process process = builder.start();

			StringBuilder output = new StringBuilder();

			String stdOutString = StringConverter.frommInputStream(process.getInputStream());
			String stdErrString = StringConverter.frommInputStream(process.getErrorStream());

			output.append(stdOutString);
			output.append(System.lineSeparator());
			output.append(stdErrString);

			int code = process.waitFor();

			if (code != 0) {
				return false;
			}
			else {
				String result = output.toString();

				if ((result != null) && result.contains("version")) {
					return true;
				}

				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	private String _getGradleExecutable(File dir) throws NoSuchElementException {
		File gradlew = BladeUtil.getGradleWrapper(dir);
		String executable = "gradle";

		if (gradlew == null) {
			gradlew = BladeUtil.getGradleWrapper(_blade.getBase());
		}

		if ((gradlew != null) && gradlew.exists()) {
			try {
				if (!gradlew.canExecute()) {
					gradlew.setExecutable(true);
				}

				executable = gradlew.getCanonicalPath();
			}
			catch (Throwable th) {
			}
		}

		if ("gradle".equals(executable)) {
			if (_isGradleInstalled()) {
				_blade.out("Could not find gradle wrapper, using gradle");
			}
			else {
				throw new NoSuchElementException("Gradle wrapper not found and Gradle is not installed");
			}
		}

		return executable;
	}

	private BladeCLI _blade;

}