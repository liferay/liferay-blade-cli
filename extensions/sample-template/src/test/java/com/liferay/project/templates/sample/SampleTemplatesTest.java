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

package com.liferay.project.templates.sample;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.text.MessageFormat;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class SampleTemplatesTest {

	@Before
	public void setUp() throws Exception {
		File extensionsDir = _getExtensionsDir();

		Path extensionsDirPath = extensionsDir.toPath();

		File settingsDir = temporaryFolder.getRoot();

		Path settingsDirPath = settingsDir.toPath();

		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setSettingsDir(settingsDirPath);

		bladeTestBuilder.setExtensionsDir(extensionsDirPath);

		_bladeTest = bladeTestBuilder.build();

		_rootDir = temporaryFolder.getRoot();
	}

	@Test
	public void testProjectTemplatesBuiltIn() throws Exception {
		Map<String, String> templates = BladeUtil.getTemplates(_bladeTest);

		Assert.assertNotNull(templates);

		Assert.assertEquals(templates.toString(), _NUM_BUILTIN_TEMPLATES, templates.size());
	}

	@Test
	public void testProjectTemplatesWithCustom() throws Exception {
		_setupTestExtensions();

		Map<String, String> templates = BladeUtil.getTemplates(_bladeTest);

		Assert.assertNotNull(templates);

		Assert.assertEquals(templates.toString(), _NUM_BUILTIN_TEMPLATES + 1, templates.size());
	}

	@Test
	public void testSampleProjectTemplate() throws Exception {
		_setupTestExtensions();

		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "sample", "foo-sample"};

		_bladeTest.run(args);

		File projectDir = new File(workspace, "modules/foo-sample");

		Assert.assertTrue("Expected project dir to exist " + projectDir, projectDir.exists());

		File buildGradle = new File(projectDir, "build.gradle");

		Assert.assertTrue("Expected build.gradle to exist " + buildGradle, buildGradle.exists());

		String contents = new String(Files.readAllBytes(buildGradle.toPath()));

		String line =
			"compileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"";

		Assert.assertTrue(
			MessageFormat.format("Expected contents to contain {0}\n{1}", line, contents), contents.contains(line));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _getExtensionsDir() {
		return new File(temporaryFolder.getRoot(), ".blade/extensions");
	}

	private void _makeWorkspace(File workspace) throws Exception {
		File parentFile = workspace.getParentFile();

		String[] args = {
			"--base", parentFile.getPath(), "init", workspace.getName(), "-v", BladeTest.PRODUCT_VERSION_PORTAL_73
		};

		TestUtil.runBlade(workspace, _getExtensionsDir(), args);
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

		_setupTestExtension(extensionsPath, System.getProperty("sampleTemplateJarFile"));
	}

	private static final int _NUM_BUILTIN_TEMPLATES = 29;

	private BladeTest _bladeTest;
	private File _rootDir;

}