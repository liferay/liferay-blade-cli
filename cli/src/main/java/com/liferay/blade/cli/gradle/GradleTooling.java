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

import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 */
public class GradleTooling {

	public static ProjectInfo loadProjectInfo(Path projectPath) throws Exception {
		ProjectInfo projectInfo = null;

		GradleConnector connector = GradleConnector.newConnector();

		connector.forProjectDirectory(projectPath.toFile());

		ProjectConnection connection = null;

		try {
			connection = connector.connect();

			ModelBuilder<ProjectInfo> modelBuilder = connection.model(ProjectInfo.class);

			Path tempPath = Files.createTempDirectory("tooling");

			InputStream in = GradleTooling.class.getResourceAsStream("/tooling.zip");

			FileUtil.unzip(in, tempPath.toFile());

			try (Stream<Path> toolingFiles = Files.list(tempPath)) {
				String files = toolingFiles.map(
					Path::toAbsolutePath
				).map(
					Path::toString
				).map(
					path -> "\"" + path.replaceAll("\\\\", "/") + "\""
				).collect(
					Collectors.joining(", ")
				);

				String initScriptTemplate = FileUtil.collect(GradleTooling.class.getResourceAsStream("init.gradle"));

				String initScriptContents = initScriptTemplate.replaceAll("%files%", files);

				Path initPath = tempPath.resolve("init.gradle");

				Files.write(initPath, initScriptContents.getBytes());

				modelBuilder.withArguments("--init-script", initPath.toString(), "--stacktrace");

				projectInfo = modelBuilder.get();
			}
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		return projectInfo;
	}

}