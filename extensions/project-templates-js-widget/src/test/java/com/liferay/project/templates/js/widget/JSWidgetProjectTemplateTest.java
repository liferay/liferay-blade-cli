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

package com.liferay.project.templates.js.widget;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class JSWidgetProjectTemplateTest {

	@Before
	public void setUp() throws Exception {
		File extensionsDir = _getExtensionsDir();

		_extensionsDirPath = extensionsDir.toPath();

		File settingsDir = temporaryFolder.getRoot();

		Path settingsDirPath = settingsDir.toPath();

		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setSettingsDir(settingsDirPath);

		bladeTestBuilder.setExtensionsDir(_extensionsDirPath);

		_bladeTest = bladeTestBuilder.build();

		_rootDir = temporaryFolder.getRoot();
	}

	@Test
	public void testJSWidgetProjectTemplate() throws Exception {
		_setupTestExtensions();

		File workspaceDir = new File(_rootDir, "workspace");

		_makeWorkspace(workspaceDir);

		String[] args = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-t", "js-widget", "js-widget-test", "--jsBatch",
			"--jsTarget", "Liferay Platform Project", "--jsPlatform", "dxp-7.4", "--jsType", "Shared bundle"
		};

		_bladeTest.run(args);

		File projectDir = new File(workspaceDir, "modules/js-widget-test");

		Assert.assertTrue("Expected project dir to exist " + projectDir, projectDir.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _getExtensionsDir() {
		return new File(temporaryFolder.getRoot(), ".blade/extensions");
	}

	private void _makeWorkspace(File workspace) throws Exception {
		File parentFile = workspace.getParentFile();

		String[] args = {
			"--base", parentFile.getPath(), "init", workspace.getName(), "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		TestUtil.runBlade(workspace, _extensionsDirPath.toFile(), args);
	}

	private void _setupTestExtension(Path extensionsPath, String jarPath) throws Exception {
		File sampleJarFile = new File(jarPath);

		Assert.assertTrue(sampleJarFile.getAbsolutePath() + " does not exist.", sampleJarFile.exists());

		Path sampleJarPath = extensionsPath.resolve(sampleJarFile.getName());

		Files.copy(sampleJarFile.toPath(), sampleJarPath, StandardCopyOption.REPLACE_EXISTING);

		Assert.assertTrue(Files.exists(sampleJarPath));
	}

	private void _setupTestExtensions() throws Exception {
		File extensionsDir = _getExtensionsDir();

		extensionsDir.mkdirs();

		Assert.assertTrue("Unable to create test extensions dir.", extensionsDir.exists());

		Path extensionsPath = extensionsDir.toPath();

		_setupTestExtension(extensionsPath, System.getProperty("jsWidgetTemplateJarFile"));
	}

	private BladeTest _bladeTest;
	private Path _extensionsDirPath;
	private File _rootDir;

}