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
import com.liferay.blade.cli.StringPrintStream;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class GradleExec {

	public GradleExec(BladeCLI blade) {
		_blade = blade;
	}

	public ProcessResult executeTask(String task) throws Exception {
		BaseArgs args = _blade.getArgs();

		File baseDir = args.getBase();

		return executeTask(task, baseDir, true);
	}

	public ProcessResult executeTask(String task, boolean captureOutput) throws Exception {
		BaseArgs args = _blade.getArgs();

		File baseDir = args.getBase();

		return executeTask(task, baseDir, captureOutput);
	}

	public ProcessResult executeTask(String task, File baseDir) throws Exception {
		return executeTask(task, baseDir, true);
	}

	public ProcessResult executeTask(String task, File baseDir, boolean captureOutput) throws Exception {
		String executable = _getGradleExecutable(baseDir);

		if (captureOutput) {
			StringPrintStream outputStream = StringPrintStream.newInstance();

			StringPrintStream errorStream = StringPrintStream.newInstance();

			Process process = BladeUtil.startProcess(
				"\"" + executable + "\" " + task, baseDir, outputStream, errorStream);

			int returnCode = process.waitFor();

			String output = outputStream.get();

			String error = errorStream.get();

			if (returnCode > 0) {
				throw new GradleExecutionException(error, returnCode);
			}

			return new ProcessResult(returnCode, output, error);
		}

		Process process = BladeUtil.startProcess("\"" + executable + "\" " + task, baseDir);

		int returnCode = process.waitFor();

		if (returnCode > 0) {
			throw new GradleExecutionException(
				"Gradle error executing task '" + task + "' in " + baseDir.getAbsolutePath(), returnCode);
		}

		return new ProcessResult(returnCode, null, null);
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

			builder.directory(new File(System.getProperty("user.home")));

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

			String result = output.toString();

			if ((result != null) && result.contains("version")) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}

	private String _getGradleExecutable(File dir) throws NoSuchElementException {
		File gradlew = BladeUtil.getGradleWrapper(dir);

		String executable = "gradle";

		BaseArgs baseArgs = _blade.getArgs();

		if ((gradlew == null) || !gradlew.exists()) {
			File baseDir = baseArgs.getBase();

			gradlew = BladeUtil.getGradleWrapper(baseDir);
		}

		if (gradlew != null) {
			try {
				if (!gradlew.canExecute()) {
					gradlew.setExecutable(true);
				}

				executable = gradlew.getCanonicalPath();
			}
			catch (Throwable th) {
			}
		}

		if (Objects.equals("gradle", executable)) {
			if (_isGradleInstalled()) {
				if (!baseArgs.isQuiet()) {
					_blade.out("Could not find gradle wrapper, using gradle");
				}
			}
			else {
				throw new NoSuchElementException("Gradle wrapper not found and Gradle is not installed");
			}
		}

		return executable;
	}

	private BladeCLI _blade;

}