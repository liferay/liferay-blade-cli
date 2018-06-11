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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
		File projectDir = new File(projectPath);

		GradleRunner gradleRunner = GradleRunner.create();

		gradleRunner.withProjectDir(projectDir);
		gradleRunner.withArguments(taskPath);

		BuildResult buildResult = gradleRunner.build();

		BuildTask buildTask = null;

		for (BuildTask task : buildResult.getTasks()) {
			if (task.getPath().endsWith(taskPath[taskPath.length - 1])) {
				buildTask = task;

				break;
			}
		}

		return buildTask;
	}

	public static void verifyBuildOutput(String projectPath, String fileName) throws IOException {
		final Path[] projectFilePath = new Path[1];

		Files.walkFileTree(Paths.get(projectPath),
			new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path pathFileName = file.getFileName();

					if (fileName.equals(pathFileName.toString())) {
						projectFilePath[0] = file;

						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}
			});

		Assert.assertNotNull("Unable to find project file " + fileName + " in " + projectPath, projectFilePath[0]);
	}

	public static void verifyGradleRunnerOutput(BuildTask buildTask) {
		Assert.assertNotNull(buildTask);

		Assert.assertEquals(TaskOutcome.SUCCESS, buildTask.getOutcome());
	}

}