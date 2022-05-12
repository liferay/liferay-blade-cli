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

			try (JarFile jarFile = new JarFile(templateFile)) {
				JarEntry entry = jarFile.getJarEntry("config.json");

				try (InputStream input = jarFile.getInputStream(entry)) {
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

					String newConfigValue = packageJSONObject.toString();

					Files.write(batchConfigFile.toPath(), newConfigValue.getBytes());

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