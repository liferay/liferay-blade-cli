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

package com.liferay.blade.cli;

import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.Properties;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Haoyi Sun
 * @author Gregory Amerson
 */
public class TargetPlatformTest {

	@Before
	public void setUp() throws Exception {
		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		_gradleWorkspaceDir = temporaryFolder.newFolder("gradle-workspace");

		String[] args = {
			"init", "--base", _gradleWorkspaceDir.getAbsolutePath(), "-v", BladeTest.PRODUCT_VERSION_PORTAL_73
		};

		TestUtil.runBlade(_gradleWorkspaceDir, _extensionsDir, args);

		GradleWorkspaceProvider workspaceProviderGradle = new GradleWorkspaceProvider();

		_gradlePropertiesFile = workspaceProviderGradle.getGradlePropertiesFile(_gradleWorkspaceDir);
		_settingsGradleFile = workspaceProviderGradle.getSettingGradleFile(_gradleWorkspaceDir);
	}

	@Test
	public void testTargetPlatformEnabled() throws Exception {
		_setTargetPlatformVersionProperty("7.1.0");
		_setWorkspacePluginVersion("1.10.2");

		String[] args = {"--base", _gradleWorkspaceDir.getAbsolutePath(), "create", "-t", "api", "test-api"};

		TestUtil.runBlade(_gradleWorkspaceDir, _extensionsDir, args);

		File modulesDir = new File(_gradleWorkspaceDir, "modules");

		File projectDir = new File(modulesDir, "test-api");

		File buildGradleFile = new File(projectDir, "build.gradle");

		String buildScriptContents = BladeUtil.read(buildGradleFile);

		boolean containsVersion = buildScriptContents.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertFalse("osgi.core dependency should not have a version", containsVersion);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _setTargetPlatformVersionProperty(String version) throws IOException {
		GradleWorkspaceProvider workspaceProviderGradle = new GradleWorkspaceProvider();

		Properties properties = workspaceProviderGradle.getGradleProperties(_gradlePropertiesFile);

		properties.setProperty("liferay.workspace.target.platform.version", version);

		try (OutputStream outputStream = Files.newOutputStream(_gradlePropertiesFile.toPath())) {
			properties.store(outputStream, "");
		}
	}

	private void _setWorkspacePluginVersion(String version) throws IOException {
		String settingsScript = BladeUtil.read(_settingsGradleFile);

		Matcher matcher = GradleWorkspaceProvider.patternWorkspacePluginVersion.matcher(settingsScript);

		Assert.assertTrue(settingsScript, matcher.matches());

		String pluginVersion = matcher.group(1);

		String newSettingsScript = settingsScript.replaceFirst(pluginVersion, version);

		byte[] bytes = newSettingsScript.getBytes();

		try (OutputStream outputStream = Files.newOutputStream(_settingsGradleFile.toPath())) {
			outputStream.write(bytes);
		}
	}

	private File _extensionsDir = null;
	private File _gradlePropertiesFile = null;
	private File _gradleWorkspaceDir = null;
	private File _settingsGradleFile = null;

}