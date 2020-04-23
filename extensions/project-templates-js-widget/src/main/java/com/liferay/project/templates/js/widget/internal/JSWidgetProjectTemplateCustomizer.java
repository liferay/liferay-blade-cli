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

package com.liferay.project.templates.js.widget.internal;

import com.liferay.blade.cli.util.NodeUtil;
import com.liferay.blade.cli.util.OSDetector;
import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

/**
 * @author David Truong
 * @author Christopher Bryan Boyd
 */
public class JSWidgetProjectTemplateCustomizer implements ProjectTemplateCustomizer {

	@Override
	public String getTemplateName() {
		return "js-widget";
	}

	@Override
	public void onAfterGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, File destinationDir,
			ArchetypeGenerationResult archetypeGenerationResult)
		throws Exception {

		Path configPath = Paths.get(destinationDir.getAbsolutePath(), projectTemplatesArgs.getName() + "/config.json");

		String config = read(configPath);

		config = _replace(config, "[$TARGET$]", "react-widget");

		config = _replace(config, "[$OUTPUT_PATH$]", projectTemplatesArgs.getName());
		config = _replace(config, "[$DESCRIPTION$]", projectTemplatesArgs.getName());

		JSWidgetProjectTemplatesArgsExt ext =
			(JSWidgetProjectTemplatesArgsExt)projectTemplatesArgs.getProjectTemplatesArgsExt();

		String workspaceLocation = ext.getWorkspaceLocation();

		String modulesLocation = ext.getModulesLocation();

		if (workspaceLocation != null) {
			Path liferayLocationPath = Paths.get(workspaceLocation);

			liferayLocationPath = liferayLocationPath.resolve("bundles");

			liferayLocationPath = liferayLocationPath.normalize();

			String liferayLocation = liferayLocationPath.toString();

			if (OSDetector.isWindows()) {
				liferayLocation = liferayLocation.replace("\\", "\\\\");
			}

			config = _replace(config, "[$LIFERAY_DIR$]", liferayLocation);
			config = _replace(config, "[$LIFERAY_PRESENT$]", "true");

			modulesLocation = new File(
				modulesLocation, projectTemplatesArgs.getName()
			).getAbsolutePath();

			if (OSDetector.isWindows()) {
				modulesLocation = modulesLocation.replace("\\", "\\\\");
			}

			Path modulesPath = Paths.get(modulesLocation);

			Path workspacePath = Paths.get(workspaceLocation);

			workspacePath = workspacePath.normalize();

			Path relativePath = workspacePath.relativize(modulesPath);

			String relativePathString = relativePath.toString();

			if (OSDetector.isWindows()) {
				relativePathString = relativePathString.replace("\\", "\\\\");
			}

			config = _replace(config, "[$FOLDER$]", relativePathString);
		}
		else {
			config = _replace(config, "[$LIFERAY_DIR$]", "/liferay");
			config = _replace(config, "[$LIFERAY_PRESENT$]", "false");
		}

		write(configPath, config);

		NodeUtil.runYo(
			projectTemplatesArgs.getLiferayVersion(), new File(workspaceLocation),
			new String[] {"liferay-js", "--config", configPath.toString(), "--skip-install"});
	}

	@Override
	public void onBeforeGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, ArchetypeGenerationRequest archetypeGenerationRequest)
		throws Exception {
	}

	public String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path));
	}

	public void write(Path destination, String content) throws Exception {
		Files.write(destination, content.getBytes());
	}

	private String _replace(String s, String key, Object value) {
		if (value == null) {
			value = "";
		}

		return s.replace(key, value.toString());
	}

}