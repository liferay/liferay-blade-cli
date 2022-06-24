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

package com.liferay.project.templates.client.extension;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.portal.tools.bundle.support.internal.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProjectTemplateTest {

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
	public void testClientExtensionCustomElementProjectTemplate() throws Exception {
		_setupTestExtensions();

		File workspaceDir = new File(_rootDir, "workspace");

		_makeWorkspace(workspaceDir);

		String[] args = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-t", "client-extension", "-d",
			workspaceDir.getAbsolutePath(), "--extension-name", "test123", "--extension-type", "customElement",
			"customelementtest"
		};

		_bladeTest.run(args);

		File projectDir = new File(workspaceDir, "customelementtest");

		Assert.assertTrue("Expected project dir to exist " + projectDir, projectDir.exists());

		File clientExtensionFile = new File(projectDir, "client-extension.yaml");

		Assert.assertTrue(
			"Expected client-extension.yaml file to exist " + clientExtensionFile, clientExtensionFile.exists());

		String content = FileUtil.read(clientExtensionFile);

		Assert.assertTrue("Expected client-extension.yaml to contain test123\n" + content, content.contains("test123"));
	}

	@Test
	public void testClientExtensionMetadataURL() throws Exception {
		_setupTestExtensions();

		File workspaceDir = new File(_rootDir, "workspace");

		_makeWorkspace(workspaceDir);

		String[] args = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-t", "client-extension", "-d",
			workspaceDir.getAbsolutePath(), "--extension-name", "test456", "--extension-type", "themeCSS", "test456"
		};

		_bladeTest.run(args);

		File projectDir = new File(workspaceDir, "test456");

		Assert.assertTrue("Expected project dir to exist " + projectDir, projectDir.exists());

		File clientExtensionFile = new File(projectDir, "client-extension.yaml");

		Assert.assertTrue(
			"Expected client-extension.yaml file to exist " + clientExtensionFile, clientExtensionFile.exists());

		List<String> lines = Files.readAllLines(clientExtensionFile.toPath());

		Assert.assertEquals("  clayURL: css/clay.css", lines.get(1));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _getExtensionsDir() {
		return new File(temporaryFolder.getRoot(), ".blade/extensions");
	}

	private void _makeWorkspace(File workspace) throws Exception {
		File parentFile = workspace.getParentFile();

		String[] args = {
			"--base", parentFile.getPath(), "init", workspace.getName(), "-v", BladeTest.PRODUCT_VERSION_DXP_74
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

		_setupTestExtension(extensionsPath, System.getProperty("clientExtensionTemplateJarFile"));
	}

	private BladeTest _bladeTest;
	private Path _extensionsDirPath;
	private File _rootDir;

}