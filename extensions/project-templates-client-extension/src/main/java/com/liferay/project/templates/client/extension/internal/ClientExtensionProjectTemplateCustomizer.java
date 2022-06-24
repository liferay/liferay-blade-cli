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

package com.liferay.project.templates.client.extension.internal;

import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProjectTemplateCustomizer implements ProjectTemplateCustomizer {

	@Override
	public String getTemplateName() {
		return "client-extension";
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

		ClientExtensionProjectTemplatesArgsExt clientExtensionTemplateExt =
			(ClientExtensionProjectTemplatesArgsExt)projectTemplatesArgs.getProjectTemplatesArgsExt();

		List<String> args = new ArrayList<>();

		args.add("generate");
		args.add("-i");
		args.add(projectName);

		String extensionName = clientExtensionTemplateExt.getExtensionName();

		if (extensionName != null) {
			args.add("-n");
			args.add(extensionName);
		}

		String extensionType = clientExtensionTemplateExt.getExtensionType();

		if (extensionType != null) {
			args.add("-t");
			args.add(extensionType);
		}

		Map<String, String> env = Collections.singletonMap(
			"EXTENSION_METADATA_FILE",
			"https://github.com/gamerson/liferay-portal/raw/1ffe92d4a987ad90f1faf6cb042567077240b8b5/modules/apps" +
				"/client-extension/client-extension-type-api/com.liferay.client.extension.type.api-5.0.1.jar");

		LXCUtil.run(destinationDir.toPath(), args.toArray(new String[0]), env, false);
	}

}