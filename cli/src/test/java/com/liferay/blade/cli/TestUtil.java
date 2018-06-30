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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;

/**
 * @author Christopher Bryan Boyd
 */
public class TestUtil {

	public static void deleteDir(Path dirPath) throws IOException {
		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dirPath, IOException ioe) throws IOException {
					Files.delete(dirPath);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Files.delete(path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static String runBlade(File userHomeDir, InputStream in, String... args) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		PrintStream outputPrintStream = new PrintStream(outputStream);

		boolean errors = blade.runBlade(args);

		StringBuilder sb = new StringBuilder();
		String output = blade.getOutput();

		BladeTest bladeTest = new BladeTest(outputPrintStream, errorPrintStream, in, userHomeDir);

		bladeTest.run(args);

		String error = errorStream.toString();

		String error = blade.getError();

		if (error != null) {
			if (errors && checkAssert) {
				Assert.fail("Errors were encountered while running blade: " + System.lineSeparator() + error);
			}

			if (sb.length() > 0) {
				sb.append(System.lineSeparator());
			}

			sb.append(error);
		}

		return sb.toString();
	}

	public static String runBlade(String... args) throws Exception {
		return runBlade(true, args);
	}

	public static String runBlade(File userHomeDir, String... args) throws Exception {
		return runBlade(userHomeDir, System.in, args);
	}

	public static String runBlade(String... args) throws Exception {
		return runBlade(new File(System.getProperty("user.home")), System.in, args);
	}

	public static void verifyBuild(String projectPath, String outputFileName) throws Exception {
		verifyBuild(projectPath, "build", outputFileName);
	}

	public static void verifyBuild(String projectPath, String taskPath, String outputFileName) throws Exception {
		Path path = Paths.get(projectPath);

		Path buildGradlePath = path.resolve("build.gradle");

		String content = "\nbuildscript { repositories { mavenLocal() } }";

		if (Files.exists(buildGradlePath)) {
			Files.write(buildGradlePath, content.getBytes(), StandardOpenOption.APPEND);
		}

		Path settingsGradlePath = path.resolve("settings.gradle");

		if (Files.exists(settingsGradlePath)) {
			Files.write(settingsGradlePath, content.getBytes(), StandardOpenOption.APPEND);
		}

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, taskPath);

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, outputFileName);
	}

}