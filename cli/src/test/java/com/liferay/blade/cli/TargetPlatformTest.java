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
 */
public class TargetPlatformTest {

	@Before
	public void setUp() throws Exception {
		_gradleWorkspaceDir = temporaryFolder.newFolder("build", "test", "gradle-workspace");

		_notWorkspaceDir = temporaryFolder.newFolder("build", "test", "not-workspace");

		String[] args = {"init", "--base", _gradleWorkspaceDir.getAbsolutePath()};

		new BladeTest().run(args);

		_settingGradle = BladeUtil.getSettingGradleFile(_gradleWorkspaceDir);
		_gradlePorperties = BladeUtil.getGradlePropertiesFile(_gradleWorkspaceDir);
	}

	@Test
	public void testCreateTargetPlatformActivator() throws Exception {
		_targetPlatformSettingConfigure(true);
		_targetPlatformPropertiesConfigure(true);

		String[] args =
			{"--base", _gradleWorkspaceDir.getAbsolutePath(), "create", "-t", "activator", "target-platform-activator"};

		new BladeTest().run(args);

		File modules = new File(_gradleWorkspaceDir, "modules");

		File project = new File(modules, "target-platform-activator");

		File buildFile = new File(project, "build.gradle");

		String buildScript = BladeUtil.read(buildFile);

		boolean containsVersion = buildScript.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertFalse("Not expect compileOnly dependencies have version", containsVersion);
	}

	@Test
	public void testOnlyPropertiesNotSupportTargetPlatform() throws Exception {
		_targetPlatformSettingConfigure(true);
		_targetPlatformPropertiesConfigure(false);

		String[] args =
			{"--base", _gradleWorkspaceDir.getAbsolutePath(), "create", "-t", "activator", "properties-not-support"};

		new BladeTest().run(args);

		File modules = new File(_gradleWorkspaceDir, "modules");

		File project = new File(modules, "properties-not-support");

		File buildFile = new File(project, "build.gradle");

		String buildScript = BladeUtil.read(buildFile);

		boolean containsVersion = buildScript.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertTrue("Expect compileOnly dependencies have version", containsVersion);
	}

	@Test
	public void testOnlySettingNotSupportTargetPlatform() throws Exception {
		_targetPlatformSettingConfigure(false);
		_targetPlatformPropertiesConfigure(true);

		String[] args =
			{"--base", _gradleWorkspaceDir.getAbsolutePath(), "create", "-t", "activator", "setting-not-support"};

		new BladeTest().run(args);

		File modules = new File(_gradleWorkspaceDir, "modules");

		File project = new File(modules, "setting-not-support");

		File buildFile = new File(project, "build.gradle");

		String buildScript = BladeUtil.read(buildFile);

		boolean containsVersion = buildScript.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertTrue("Expect compileOnly dependencies have version", containsVersion);
	}

	@Test
	public void testWithoutWorkspace() throws Exception {
		String[] args =
			{"--base", _notWorkspaceDir.getAbsolutePath(), "create", "-t", "activator", "without-workspace-activator"};

		new BladeTest().run(args);

		File project = new File(_notWorkspaceDir, "without-workspace-activator");

		File buildFile = new File(project, "build.gradle");

		String buildScript = BladeUtil.read(buildFile);

		boolean containsVersion = buildScript.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertTrue("Expect compileOnly dependencies have version", containsVersion);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _targetPlatformPropertiesConfigure(boolean support) throws IOException {
		Properties properties = BladeUtil.getGradleProperties(_gradlePorperties);

		String newPorperties = "";

		if (support) {
			if (!properties.containsKey("liferay.workspace.target.platform.version")) {
				newPorperties = "liferay.workspace.target.platform.version = 7.0-GA7";
			}
		}

		byte[] bytes = newPorperties.getBytes();

		try (OutputStream outputStream = Files.newOutputStream(_gradlePorperties.toPath())) {
			outputStream.write(bytes);
		}
	}

	private void _targetPlatformSettingConfigure(boolean support) throws IOException {
		String settingScript = BladeUtil.read(_settingGradle);

		Matcher matcher = WorkspaceConstants.patternGradleWorkspacePlugin.matcher(settingScript);

		if (!matcher.find()) {
			return;
		}

		String pluginVersion = matcher.group(1);

		String newSettingScript = null;

		if (support) {
			newSettingScript = settingScript.replaceFirst(pluginVersion, "1.10.2");
		}
		else {
			newSettingScript = settingScript.replaceFirst(pluginVersion, "1.9.0");
		}

		byte[] bytes = newSettingScript.getBytes();

		try (OutputStream outputStream = Files.newOutputStream(_settingGradle.toPath())) {
			outputStream.write(bytes);
		}
	}

	private File _gradlePorperties = null;
	private File _gradleWorkspaceDir = null;
	private File _notWorkspaceDir = null;
	private File _settingGradle = null;

}