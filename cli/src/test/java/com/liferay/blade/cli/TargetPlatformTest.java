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
import com.liferay.blade.cli.util.WorkspaceUtil;

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
		_gradleWorkspaceDir = temporaryFolder.newFolder("gradle-workspace");

		_nonGradleWorkspaceDir = temporaryFolder.newFolder("non-gradle-workspace");

		String[] args = {"init", "--base", _gradleWorkspaceDir.getAbsolutePath()};

		TestUtil.runBlade(args);

		_gradlePropertiesFile = WorkspaceUtil.getGradlePropertiesFile(_gradleWorkspaceDir);
		_settingsGradleFile = WorkspaceUtil.getSettingGradleFile(_gradleWorkspaceDir);
	}

	@Test
	public void testCreateTargetPlatformActivator() throws Exception {
		_setWorkspacePluginVersion("1.10.2");
		_setTargetPlatformVersionProperty("7.1.0");

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
		_setWorkspacePluginVersion("1.10.2");

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
		_setWorkspacePluginVersion("1.9.0");
		_setTargetPlatformVersionProperty("7.1.0");

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
		String[] args = {
			"--base", _nonGradleWorkspaceDir.getAbsolutePath(), "create", "-t", "activator",
			"without-workspace-activator"
		};

		new BladeTest().run(args);

		File project = new File(_nonGradleWorkspaceDir, "without-workspace-activator");

		File buildFile = new File(project, "build.gradle");

		String buildScript = BladeUtil.read(buildFile);

		boolean containsVersion = buildScript.contains(
			"compileOnly group: \"org.osgi\", name: \"org.osgi.core\", version:");

		Assert.assertTrue("Expect compileOnly dependencies have version", containsVersion);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _setTargetPlatformVersionProperty(String version) throws IOException {
		Properties properties = WorkspaceUtil.getGradleProperties(_gradlePropertiesFile);

		properties.setProperty("liferay.workspace.target.platform.version", version);

		try (OutputStream outputStream = Files.newOutputStream(_gradlePropertiesFile.toPath())) {
			properties.store(outputStream, "");
		}
	}

	private void _setWorkspacePluginVersion(String version) throws IOException {
		String settingsScript = BladeUtil.read(_settingsGradleFile);

		Matcher matcher = WorkspaceUtil.patternWorkspacePluginVersion.matcher(settingsScript);

		Assert.assertTrue(settingsScript, matcher.matches());

		String pluginVersion = matcher.group(1);

		String newSettingsScript = settingsScript.replaceFirst(pluginVersion, version);

		byte[] bytes = newSettingsScript.getBytes();

		try (OutputStream outputStream = Files.newOutputStream(_settingsGradleFile.toPath())) {
			outputStream.write(bytes);
		}
	}

	private File _gradlePropertiesFile = null;
	private File _gradleWorkspaceDir = null;
	private File _nonGradleWorkspaceDir = null;
	private File _settingsGradleFile = null;

}