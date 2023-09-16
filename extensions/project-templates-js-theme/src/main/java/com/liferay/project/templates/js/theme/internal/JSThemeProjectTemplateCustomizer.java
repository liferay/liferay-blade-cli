/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.js.theme.internal;

import com.liferay.blade.cli.util.NodeUtil;
import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.VersionUtil;

import java.io.File;

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

		String config = new String(Files.readAllBytes(configPath));

		String liferayVersion = projectTemplatesArgs.getLiferayVersion();

		liferayVersion =
			String.valueOf(VersionUtil.getMajorVersion(liferayVersion)) + "." +
				String.valueOf(VersionUtil.getMinorVersion(liferayVersion));

		config = _replace(config, "[$LIFERAY_VERSION$]", liferayVersion);

		String name = projectTemplatesArgs.getName();

		config = _replace(config, "[$THEME_ID$]", _getThemeId(name));
		config = _replace(config, "[$THEME_NAME$]", _getThemeName(name));

		Files.write(configPath, config.getBytes());

		NodeUtil.runYo(
			liferayVersion, new File(basePath),
			new String[] {
				"liferay-theme", "--config", configPath.toString(), "--skip-install", "--scripts-prepend-node-path"
			});

		File liferayThemeJsonFile = new File(projectPath, "liferay-theme.json");

		if (liferayThemeJsonFile.exists()) {
			liferayThemeJsonFile.delete();
		}

		File configFile = configPath.toFile();

		if (configFile.exists()) {
			configFile.delete();
		}
	}

	@Override
	public void onBeforeGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, ArchetypeGenerationRequest archetypeGenerationRequest)
		throws Exception {

		String name = projectTemplatesArgs.getName();

		if (!name.endsWith("-theme")) {
			projectTemplatesArgs.setName(name + "-theme");
		}

		String artifactId = archetypeGenerationRequest.getArtifactId();

		if (!artifactId.endsWith("-theme")) {
			archetypeGenerationRequest.setArtifactId(artifactId + "-theme");
		}
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