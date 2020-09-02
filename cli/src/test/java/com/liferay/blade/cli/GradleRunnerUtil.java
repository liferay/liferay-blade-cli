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

import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.internal.impldep.com.google.common.base.Objects;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;

import org.junit.Assert;

/**
 * @author Lawrence Lee
 */
public class GradleRunnerUtil {

	public static BuildTask executeGradleRunner(String projectPath, String... taskPath) {
		Path projectDir = Paths.get(projectPath);

		GradleRunner gradleRunner = GradleRunner.create();

		gradleRunner.withProjectDir(projectDir.toFile());
		gradleRunner.withArguments(taskPath);

		BuildResult buildResult = gradleRunner.build();

		BuildTask buildTask = null;

		for (BuildTask task : buildResult.getTasks()) {
			String taskPathString = task.getPath();

			if (taskPathString.endsWith(taskPath[taskPath.length - 1])) {
				buildTask = task;

				break;
			}
		}

		return buildTask;
	}

	public static Path verifyBuildOutput(String projectPath, String fileName) throws IOException {
		return verifyBuildOutput(projectPath, fileName, false);
	}

	public static Path verifyBuildOutput(String projectPath, String fileName, boolean regex) throws IOException {
		final Path[] projectFilePath = new Path[1];

		Files.walkFileTree(
			Paths.get(projectPath),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String pathFileNameString = String.valueOf(file.getFileName());

					if (Objects.equal(pathFileNameString, fileName)) {
						projectFilePath[0] = file;

						return FileVisitResult.TERMINATE;
					}
					else if (regex) {
						Pattern fileNamePattern = Pattern.compile(fileName, Pattern.DOTALL);

						Matcher matcher = fileNamePattern.matcher(pathFileNameString);

						if (matcher.find()) {
							projectFilePath[0] = file;

							return FileVisitResult.TERMINATE;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		Assert.assertNotNull("Unable to find project file " + fileName + " in " + projectPath, projectFilePath[0]);

		return projectFilePath[0];
	}

	public static void verifyGradleRunnerOutput(BuildTask buildTask) {
		Assert.assertNotNull(buildTask);

		Assert.assertEquals(TaskOutcome.SUCCESS, buildTask.getOutcome());
	}

}