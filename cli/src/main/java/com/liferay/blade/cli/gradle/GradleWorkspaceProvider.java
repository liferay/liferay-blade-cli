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

import aQute.bnd.version.Version;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductInfo;

import java.io.File;
import java.io.FilenameFilter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christopher Bryan Boyd
 */
public class GradleWorkspaceProvider implements WorkspaceProvider {

	public static final Pattern patternDockerImageLiferayVersion = Pattern.compile(
		".*(?<=liferay/(?:dxp|portal):)(.{3}).*", Pattern.DOTALL | Pattern.MULTILINE);
	public static final Pattern patternWorkspacePlugin = Pattern.compile(
		".*apply\\s*plugin\\s*:\\s*[\'\"]com\\.liferay\\.workspace[\'\"]\\s*$", Pattern.MULTILINE | Pattern.DOTALL);
	public static final Pattern patternWorkspacePluginLatestRelease = Pattern.compile(
		".*name:\\s*\"com\\.liferay\\.gradle\\.plugins\\.workspace\",\\s*version:" +
			"\\s*\"([latest\\.release|latest\\.integration]+)\".*",
		Pattern.MULTILINE | Pattern.DOTALL);
	public static final Pattern patternWorkspacePluginVersion = Pattern.compile(
		".*name:\\s*\"com\\.liferay\\.gradle\\.plugins\\.workspace\",\\s*version:\\s*\"([0-9\\.]+)\".*",
		Pattern.MULTILINE | Pattern.DOTALL);

	public Properties getGradleProperties(File dir) {
		File file = getGradlePropertiesFile(dir);

		return BladeUtil.getProperties(file);
	}

	public File getGradlePropertiesFile(File dir) {
		return new File(getWorkspaceDir(dir), _GRADLE_PROPERTIES_FILE_NAME);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getLiferayVersion(File workspaceDir) {
		try {
			Properties gradleProperties = getGradleProperties(workspaceDir);

			Optional<String> baseLiferayVersion = Optional.ofNullable(
				gradleProperties.getProperty(WorkspaceConstants.DEFAULT_TARGET_PLATFORM_VERSION_PROPERTY)
			).filter(
				BladeUtil::isNotEmpty
			);

			if (!baseLiferayVersion.isPresent()) {
				String productKey = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WORKSPACE_PRODUCT_PROPERTY);

				Map<String, Object> productInfoMap = BladeUtil.getProductInfos();

				ProductInfo productInfo = new ProductInfo((Map<String, String>)productInfoMap.get(productKey));

				if (productInfo != null) {
					baseLiferayVersion = Optional.ofNullable(
						productInfo.getTargetPlatformVersion()
					).filter(
						BladeUtil::isNotEmpty
					);
				}
			}

			if (!baseLiferayVersion.isPresent()) {
				String dockerImageProperty = gradleProperties.getProperty(
					WorkspaceConstants.DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY);

				if (BladeUtil.isEmpty(dockerImageProperty)) {
					return null;
				}

				Matcher matcher = patternDockerImageLiferayVersion.matcher(dockerImageProperty);

				if (matcher.find()) {
					baseLiferayVersion = Optional.of(matcher.group(1));
				}
			}

			if (baseLiferayVersion.isPresent()) {
				String presentLiferayVersion = baseLiferayVersion.get();

				int dashPostion = presentLiferayVersion.indexOf("-");

				Version liferayVersion = null;

				if (dashPostion != -1) {
					liferayVersion = Version.parseVersion(presentLiferayVersion.substring(0, dashPostion));
				}
				else {
					liferayVersion = Version.parseVersion(presentLiferayVersion);
				}

				return new String(liferayVersion.getMajor() + "." + liferayVersion.getMinor());
			}
		}
		catch (Exception exception) {
			BladeCLI.instance.error(exception);
		}

		return null;
	}

	@Override
	public String getProduct(File workspaceDir) {
		try {
			Properties gradleProperties = getGradleProperties(workspaceDir);

			String productKey = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WORKSPACE_PRODUCT_PROPERTY);

			if (productKey == null) {
				String targetPlatformVersion = gradleProperties.getProperty(
					WorkspaceConstants.DEFAULT_TARGET_PLATFORM_VERSION_PROPERTY);

				if (targetPlatformVersion == null) {
					String dockerImageProperty = gradleProperties.getProperty(
						WorkspaceConstants.DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY);

					if (dockerImageProperty == null) {
						return "portal";
					}
					else if (dockerImageProperty.contains("dxp")) {
						return "dxp";
					}
				}
				else {
					Version version = Version.parseVersion(targetPlatformVersion.replaceAll("-", "."));

					int microVersion = version.getMicro();

					if (microVersion >= 10) {
						return "dxp";
					}
				}
			}
			else if (productKey.contains("dxp")) {
				return "dxp";
			}
		}
		catch (Exception exception) {
		}

		return "portal";
	}

	public File getSettingGradleFile(File dir) {
		return new File(getWorkspaceDir(dir), _SETTINGS_GRADLE_FILE_NAME);
	}

	@Override
	public File getWorkspaceDir(BladeCLI blade) {
		BaseArgs args = blade.getArgs();

		return getWorkspaceDir(args.getBase());
	}

	@Override
	public File getWorkspaceDir(File dir) {
		File gradleParent = BladeUtil.findParentFile(
			dir, new String[] {_SETTINGS_GRADLE_FILE_NAME, _GRADLE_PROPERTIES_FILE_NAME}, true);

		if ((gradleParent != null) && gradleParent.exists()) {
			return gradleParent;
		}

		FilenameFilter gradleFilter =
			(file, name) -> _SETTINGS_GRADLE_FILE_NAME.equals(name) || _GRADLE_PROPERTIES_FILE_NAME.equals(name);

		File[] matches = dir.listFiles(gradleFilter);

		if (Objects.nonNull(matches) && (matches.length > 0)) {
			return dir;
		}

		return null;
	}

	@Override
	public boolean isDependencyManagementEnabled(File dir) {
		if (!isWorkspace(dir)) {
			return false;
		}

		Properties properties = getGradleProperties(dir);
		String targetPlatformVersionKey = "liferay.workspace.target.platform.version";

		boolean targetPlatformEnabled = properties.containsKey(targetPlatformVersionKey);

		try {
			if (!targetPlatformEnabled) {
				return false;
			}

			String settingsGradleFileContent = BladeUtil.read(getSettingGradleFile(dir));

			Matcher matcher = patternWorkspacePluginVersion.matcher(settingsGradleFileContent);

			if (matcher.find()) {
				Version minVersion = new Version(1, 9, 0);

				Version pluginVersion = new Version(matcher.group(1));

				int result = pluginVersion.compareTo(minVersion);

				if (result >= 0) {
					return true;
				}
			}
			else {
				matcher = patternWorkspacePluginLatestRelease.matcher(settingsGradleFileContent);

				return matcher.find();
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	@Override
	public boolean isWorkspace(File dir) {
		File workspaceDir = getWorkspaceDir(dir);

		if (Objects.isNull(dir) || Objects.isNull(workspaceDir)) {
			return false;
		}

		File gradleFile = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);

		if (!gradleFile.exists()) {
			return false;
		}

		try {
			String script = BladeUtil.read(gradleFile);

			Matcher matcher = patternWorkspacePlugin.matcher(script);

			if (matcher.find()) {
				return true;
			}

			//For workspace plugin < 1.0.5

			gradleFile = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);

			script = BladeUtil.read(gradleFile);

			matcher = patternWorkspacePlugin.matcher(script);

			return matcher.find();
		}
		catch (Exception e) {
			return false;
		}
	}

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

}