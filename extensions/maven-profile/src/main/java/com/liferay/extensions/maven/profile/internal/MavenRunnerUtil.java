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

package com.liferay.extensions.maven.profile.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.util.Objects;

/**
 * @author Andy Wu
 * @author Christopher Bryan Boyd
 */
public class MavenRunnerUtil {

	public static void executeGoals(String projectPath, String[] goals) {
		Objects.requireNonNull(goals, "Goals must be specified");

		if (!(goals.length > 0)) {
			throw new RuntimeException("Goals must be specified");
		}

		String os = System.getProperty("os.name");

		boolean windows = false;

		os = os.toLowerCase();

		if (os.startsWith("win")) {
			windows = true;
		}

		boolean buildSuccess = false;
		int exitValue = 1;

		StringBuilder commandBuilder = new StringBuilder();

		for (String goal : goals) {
			commandBuilder.append(goal + " ");
		}

		StringBuilder output = new StringBuilder();

		try {
			Runtime runtime = Runtime.getRuntime();

			Process process = runtime.exec(
				(windows ? "cmd.exe /c .\\mvnw.cmd" : "./mvnw") + " " + commandBuilder.toString(), null,
				new File(projectPath));

			BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String line = null;

			while ((line = processOutput.readLine()) != null) {
				output.append(line);
				output.append(System.lineSeparator());

				if (line.contains("BUILD SUCCESS")) {
					buildSuccess = true;
				}
			}

			while ((line = processError.readLine()) != null) {
				output.append(line);
				output.append(System.lineSeparator());
			}

			exitValue = process.waitFor();
		}
		catch (Exception e) {
			throw new RuntimeException(
				"Maven goals " + goals + " encountered an error in project path " + projectPath + ", " + e.getMessage(),
				e);
		}

		boolean exitValueCorrect = false;

		if (exitValue == 0) {
			exitValueCorrect = true;
		}

		if (!exitValueCorrect) {
			throw new RuntimeException(
				"Maven goals " + goals + " returned incorrect exit value in project path " + projectPath);
		}

		if (!buildSuccess) {
			throw new RuntimeException("Maven goals " + goals + " failed in project path " + projectPath);
		}
	}

	public static void verifyBuildOutput(String projectPath, String fileName) {
		File file = new File(projectPath, "/target/" + fileName);

		if (!file.exists()) {
			throw new RuntimeException("Maven file " + fileName + " doses not exist in project path " + projectPath);
		}
	}

}