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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.codehaus.plexus.util.FileUtils;

/**
 * @author Andy Wu
 */
public class MavenRunnerUtil
{
    public static void executeMavenPackage (String projectPath , String[] phases) {
		assertNotNull(phases);
		assertTrue(phases.length > 0);

		String os = System.getProperty("os.name");
		boolean isWindows = false;

		if (os.toLowerCase().startsWith("win")) {
			isWindows = true;
		}

		boolean isBuildSuccess = false;
		int exitValue = 1;

		StringBuilder commandBuilder = new StringBuilder();

		for (String phase : phases) {
			commandBuilder.append(phase + " ");
		}

		try {
			Runtime runTime = Runtime.getRuntime();

			if(isWindows) {
				File mvnw = new File("mvnw.cmd");
				FileUtils.copyFile(mvnw, new File(projectPath+"/mvnw.cmd"));
			}

			Process process = runTime.exec(
				(isWindows ? ".\\mvnw.cmd" : "./mvnw") + " " + commandBuilder.toString(),
				null, new File(projectPath));

			BufferedReader input = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				if (line.contains("BUILD SUCCESS")) {
					isBuildSuccess = true;
				}
			}

			exitValue = process.waitFor();
		}
		catch (Exception e) {
		}

		assertEquals(0, exitValue);
		assertTrue(isBuildSuccess);
    }

    public static void verifyBuildOutput (String projectPath, String fileName) {
		File file = IO.getFile(projectPath + "/target/" + fileName);

		assertTrue(file.exists());
	}
}
