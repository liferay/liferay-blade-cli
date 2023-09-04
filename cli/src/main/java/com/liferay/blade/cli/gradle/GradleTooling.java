/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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