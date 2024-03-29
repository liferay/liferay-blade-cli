/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.client.extension.internal;

import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.ReleaseUtil;
import com.liferay.project.templates.extensions.ProjectTemplateCustomizer;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.maven.archetype.ArchetypeGenerationRequest;
import org.apache.maven.archetype.ArchetypeGenerationResult;

import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProjectTemplateCustomizer implements ProjectTemplateCustomizer {

	public static Optional<String> getExtensionMetadataFile(File destinationDir) throws IOException {
		GradleWorkspaceProvider gradleWorkspaceProvider = new GradleWorkspaceProvider();

		if (gradleWorkspaceProvider.isWorkspace(destinationDir)) {
			File workspaceDir = gradleWorkspaceProvider.getWorkspaceDir(destinationDir);

			String product = gradleWorkspaceProvider.getProduct(workspaceDir);

			return Optional.ofNullable(
				gradleWorkspaceProvider.getGradleProperties(workspaceDir)
			).map(
				properties -> properties.getProperty(WorkspaceConstants.DEFAULT_WORKSPACE_PRODUCT_PROPERTY)
			).map(
				ClientExtensionProjectTemplateCustomizer::_getTargetPlatformVersionFromProduct
			).map(
				targetPlatformVersion ->
					_BASE_BOM_URL + "release." + product + ".bom/" + targetPlatformVersion + "/release." + product +
						".bom-" + targetPlatformVersion + ".pom"
			).map(
				ClientExtensionProjectTemplateCustomizer::_getCETAPIJarFromBom
			);
		}

		return Optional.empty();
	}

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

		String projectName = projectTemplatesArgs.getName();

		List<String> args = new ArrayList<>();

		args.add("generate");
		args.add("-i");
		args.add(projectName);

		ClientExtensionProjectTemplatesArgsExt clientExtensionTemplateExt =
			(ClientExtensionProjectTemplatesArgsExt)projectTemplatesArgs.getProjectTemplatesArgsExt();

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

		File destinationDir = projectTemplatesArgs.getDestinationDir();

		Optional<String> extensionMetadataFile = getExtensionMetadataFile(destinationDir);

		LXCUtil.run(
			destinationDir.toPath(), args.toArray(new String[0]),
			extensionMetadataFile.map(
				value -> Collections.singletonMap("EXTENSION_METADATA_FILE", value)
			).orElse(
				Collections.emptyMap()
			),
			false);
	}

	private static String _getCETAPIJarFromBom(String bomUrl) {
		try {
			String version = Jsoup.connect(
				bomUrl
			).parser(
				Parser.xmlParser()
			).get(
			).select(
				"artifactId:contains(com.liferay.client.extension.type.api)"
			).last(
			).parent(
			).select(
				"version"
			).last(
			).text();

			return "https://repository-cdn.liferay.com/nexus/service/local/repositories/liferay-public-releases" +
				"/content/com/liferay/com.liferay.client.extension.type.api/" + version +
					"/com.liferay.client.extension.type.api-" + version + ".jar";
		}
		catch (Exception exception) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static String _getTargetPlatformVersionFromProduct(String productKey) {
		try {
			File userHomeDir = new File(System.getProperty("user.home"));

			userHomeDir = userHomeDir.getCanonicalFile();

			Path userHomePath = userHomeDir.toPath();

			Path productInfoPath = userHomePath.resolve(".liferay/workspace/.product_info.json");

			if (!Files.exists(productInfoPath)) {
				ReleaseUtil.ReleaseEntry releaseEntry = ReleaseUtil.getReleaseEntry(productKey);

				return releaseEntry.getTargetPlatformVersion();
			}

			JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(productInfoPath.normalize())));

			return Optional.ofNullable(
				jsonObject.get(productKey)
			).map(
				JSONObject.class::cast
			).map(
				info -> info.get("targetPlatformVersion")
			).map(
				Object::toString
			).orElse(
				null
			);
		}
		catch (Exception exception) {
		}

		return null;
	}

	private static final String _BASE_BOM_URL =
		"https://repository.liferay.com/nexus/content/groups/public/com/liferay/portal/";

}