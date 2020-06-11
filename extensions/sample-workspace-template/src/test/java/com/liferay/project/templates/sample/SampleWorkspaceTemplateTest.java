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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class SampleWorkspaceTemplateTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testProjectTemplatesWithCustom() throws Exception {
		_setupTestExtensions();

		File tempDir = temporaryFolder.newFolder();

		String basePath = tempDir.getAbsolutePath();

		String[] args = {"--base", basePath, "init", "-P", "sample", "-v", BladeTest.PRODUCT_VERSION_PORTAL_73};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File settingsFile = new File(tempDir, "settings.gradle");

		Assert.assertTrue(settingsFile.exists());

		File bladePropertiesFile = new File(tempDir, ".blade.properties");

		Properties props = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(bladePropertiesFile)) {
			props.load(fileInputStream);

			String profileName = props.getProperty("profile.name");

			Assert.assertEquals("sample", profileName);
		}

		File gradleLocalPropertiesFile = new File(tempDir, "gradle-local.properties");

		Assert.assertTrue(gradleLocalPropertiesFile.exists());

		props = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(gradleLocalPropertiesFile)) {
			props.load(fileInputStream);

			String sampleSetting = props.getProperty("sample.setting");

			Assert.assertEquals("sample", sampleSetting);
		}
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static void _setupTestExtension(Path extensionsPath, String jarPath) throws IOException {
		File sampleJarFile = new File(jarPath);

		Assert.assertTrue(sampleJarFile.getAbsolutePath() + " does not exist.", sampleJarFile.exists());

		Path sampleJarPath = extensionsPath.resolve(sampleJarFile.getName());

		Files.copy(sampleJarFile.toPath(), sampleJarPath, StandardCopyOption.REPLACE_EXISTING);

		Assert.assertTrue(Files.exists(sampleJarPath));
	}

	private void _setupTestExtensions() throws Exception {
		File extensionsDir = new File(temporaryFolder.getRoot(), ".blade/extensions");

		extensionsDir.mkdirs();

		Assert.assertTrue("Unable to create test extensions dir.", extensionsDir.exists());

		Path extensionsPath = extensionsDir.toPath();

		_setupTestExtension(extensionsPath, System.getProperty("sampleTemplateJarFile"));
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}