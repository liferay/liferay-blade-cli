/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.js.widget.internal;

import com.liferay.blade.cli.util.Constants;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.NodeUtil;
import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

import org.gradle.internal.impldep.com.google.common.base.Objects;

import org.json.JSONObject;

/**
 * @author David Truong
 * @author Christopher Bryan Boyd
 * @author Simon Jiang
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
	}

	@Override
	public void onBeforeGenerateProject(
			ProjectTemplatesArgs projectTemplatesArgs, ArchetypeGenerationRequest archetypeGenerationRequest)
		throws Exception {

		File destinationDir = projectTemplatesArgs.getDestinationDir();

		String projectName = projectTemplatesArgs.getName();

		JSWidgetProjectTemplatesArgsExt jsWidgetTemplateExt =
			(JSWidgetProjectTemplatesArgsExt)projectTemplatesArgs.getProjectTemplatesArgsExt();

		if (jsWidgetTemplateExt.isBatchModel()) {
			File batchConfigFile = File.createTempFile("config_batch", ".json");

			File templateFile = ProjectTemplatesUtil.getTemplateFile(projectTemplatesArgs);

			if (!FileUtil.exists(templateFile.toString())) {
				throw new Exception("Can not find JS widget project template");
			}

			try (JarFile templateJarFile = new JarFile(templateFile)) {
				JarEntry entry = templateJarFile.getJarEntry("config.json");

				try (InputStream input = templateJarFile.getInputStream(entry)) {
					String config = FileUtil.readStreamToString(input);

					config = _replace(config, "[$TARGET$]", jsWidgetTemplateExt.getTarget());
					config = _replace(config, "[$PLATFORM$]", jsWidgetTemplateExt.getPlatform());
					config = _replace(config, "[$PROJECT_TYPE$]", jsWidgetTemplateExt.getProjectType());
					config = _replace(config, "[$DESCRIPTION$]", projectName);
					config = _replace(config, "[$CATEGORY$]", "category.sample");
					config = _replace(config, "[$ADD_CONFIGURATION_SUPPORT$]", "true");
					config = _replace(config, "[$ADD_LOCALIZATION_SUPPORT$]", "true");
					config = _replace(config, "[$CUSTOM_ELEMENT_NAME$]", projectName);
					config = _replace(config, "[$USE_SHADOW_DOM$]", "true");
					config = _replace(config, "[$CREATE_INITIALIZER$]", "true");

					JSONObject packageJSONObject = new JSONObject(config);

					if (!Objects.equal(
							jsWidgetTemplateExt.getProjectType(), Constants.DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES[3])) {

						packageJSONObject.remove("addLocalizationSupport");
						packageJSONObject.remove("addConfigurationSupport");
						packageJSONObject.remove("createInitializer");
					}

					if (!Objects.equal(jsWidgetTemplateExt.getTarget(), Constants.DEFAULT_POSSIBLE_TARGET_VALUES[0])) {
						packageJSONObject.remove("category");
					}

					if (!Objects.equal(jsWidgetTemplateExt.getTarget(), Constants.DEFAULT_POSSIBLE_TARGET_VALUES[1])) {
						packageJSONObject.remove("customElementName");
						packageJSONObject.remove("useShadowDOM");
					}

					String newConfig = packageJSONObject.toString();

					Files.write(batchConfigFile.toPath(), newConfig.getBytes());

					NodeUtil.runLiferayCli(
						projectTemplatesArgs.getLiferayVersion(), destinationDir,
						new String[] {"new", projectName, "--batch", "--options", batchConfigFile.toString()});
				}
			}
		}
		else {
			NodeUtil.runLiferayCli(
				projectTemplatesArgs.getLiferayVersion(), destinationDir, new String[] {"new", projectName});
		}
	}

	private String _replace(String s, String key, Object value) {
		if (value == null) {
			value = "";
		}

		return s.replace(key, value.toString());
	}

}