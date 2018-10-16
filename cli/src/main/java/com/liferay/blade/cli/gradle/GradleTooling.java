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
import com.liferay.blade.gradle.model.CustomModel;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;
import java.util.Set;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 */
public class GradleTooling {

	public static Set<String> getPluginClassNames(File buildDir) throws Exception {
		final CustomModel model = _getModel(CustomModel.class, buildDir);

		return model.getPluginClassNames();
	}

	public static Map<String, Set<File>> getProjectOutputFiles(File buildDir) throws Exception {
		final CustomModel model = _getModel(CustomModel.class, buildDir);

		return model.getProjectOutputFiles();
	}

	public static boolean isLiferayModule(File buildDir) throws Exception {
		final CustomModel model = _getModel(CustomModel.class, buildDir);

		return model.isLiferayModule();
	}

	private static <T> T _getModel(Class<T> modelClass, File projectDir) throws Exception {
		T retval = null;

		GradleConnector connector = GradleConnector.newConnector();

		connector.forProjectDirectory(projectDir);

		ProjectConnection connection = null;

		try {
			connection = connector.connect();

			ModelBuilder<T> modelBuilder = connection.model(modelClass);

			Path tempPath = Files.createTempDirectory("blade-tooling-model");

			InputStream in = GradleTooling.class.getResourceAsStream("/deps.zip");

			FileUtil.unzip(in, tempPath.toFile());

			String initScriptTemplate = FileUtil.collect(GradleTooling.class.getResourceAsStream("init.gradle"));

			String libsPath = tempPath.toString();

			libsPath = libsPath.replaceAll("\\\\", "/");

			String initScriptContents = initScriptTemplate.replaceAll("%libsPath%", libsPath);

			Path initPath = tempPath.resolve("init.gradle");

			FileUtil.write(initScriptContents.getBytes(), initPath.toFile());

			modelBuilder.withArguments("--init-script", initPath.toString(), "--stacktrace");

			retval = modelBuilder.get();
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		return retval;
	}

}