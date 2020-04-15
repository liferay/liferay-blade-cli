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

package com.liferay.project.templates.js.theme.internal;

import com.liferay.blade.cli.util.NodeUtil;
import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.stream.Stream;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

/**
 * @author David Truong
 */
public class JSThemeProjectTemplateCustomizer implements ProjectTemplateCustomizer {

	@Override
	public String getTemplateName() {
		return "js-theme";
	}

	@Override
	public void onAfterGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, File destinationDir,
			ArchetypeGenerationResult archetypeGenerationResult)
		throws Exception {

		String basePath = destinationDir.getAbsolutePath();

		String projectPath = basePath + "/" + projectTemplatesArgs.getName();

		Path configPath = Paths.get(projectPath + "/config.json");

		String config = read(configPath);

		String name = projectTemplatesArgs.getName();

		config = _replace(config, "[$LIFERAY_VERSION$]", projectTemplatesArgs.getLiferayVersion());
		config = _replace(config, "[$THEME_ID$]", _getThemeId(name));
		config = _replace(config, "[$THEME_NAME$]", _getThemeName(name));

		write(configPath, config);

		NodeUtil.runYo(
			new File(basePath), new String[] {"liferay-theme", "--config", configPath.toString(), "--skip-install"});

		File liferayThemeJsonFile = new File(projectPath, "liferay-theme.json");

		if (liferayThemeJsonFile.exists()) {
			liferayThemeJsonFile.delete();
		}
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

	private String _getThemeId(String name) {
		String themeId = name.replaceAll("(.)(\\p{Upper})", "$1-$2");

		return themeId.toLowerCase();
	}

	private String _getThemeName(String name) {
		if ((name == null) || name.equals("")) {
			return "";
		}

		if (name.length() == 1) {
			return name.toUpperCase();
		}

		name = name.replaceAll("[-|_|\\.]", " ");

		StringBuilder sb = new StringBuilder(name.length());

		Stream.of(
			name.split(" ")
		).forEach(
			s -> {
				if (s.length() > 1) {
					sb.append(Character.toTitleCase(s.charAt(0)));
					sb.append(s.substring(1));
				}
				else {
					sb.append(s);
				}

				sb.append(" ");
			}
		);

		String themeName = sb.toString();

		return themeName.trim();
	}

	private String _replace(String s, String key, Object value) {
		if (value == null) {
			value = "";
		}

		return s.replace(key, value.toString());
	}

}