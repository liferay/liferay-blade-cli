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
import java.util.Scanner;

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

		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

		PrintStream errorPrintStream = new PrintStream(errorStream);

		BladeTest bladeTest = new BladeTest(outputPrintStream, errorPrintStream, in, userHomeDir);

		return runBlade(bladeTest, outputPrintStream, errorPrintStream);
	}

	public static String runBlade(BladeTest bladeTest, PrintStream outputStream, PrintStream errorStream, String... args) throws Exception {
		return runBlade(bladeTest, outputStream, errorStream, true, args);
	}

	public static String runBlade(BladeTest bladeTest, PrintStream outputStream, PrintStream errorStream, boolean assertErrors, String... args) throws Exception {
		bladeTest.run(args);

		String error = errorStream.toString();

		try (Scanner scanner = new Scanner(error)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.startsWith("SLF4J:")) {
					continue;
				}

				if (assertErrors) {
					Assert.fail("Encountered error at line: " + line + "\n" + error);
				}
			}
		}

		String content = outputStream.toString();

		return content;
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