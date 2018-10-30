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

import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class GradleToolingTest {

	@ClassRule
	public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpClass() throws Exception {
		File wsDir = temporaryFolder.newFolder("build", "testws1");

		Path toolingZipPath = Paths.get("tooling.zip");

		Assert.assertTrue(Files.exists(toolingZipPath));

		Files.copy(toolingZipPath, _TOOLING_ZIP.toPath(), StandardCopyOption.REPLACE_EXISTING);

		FileUtil.copyDir(Paths.get("test-resources/projects/testws1"), wsDir.toPath());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Files.delete(_TOOLING_ZIP.toPath());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(
			new File(temporaryFolder.getRoot(), "build/testws1").toPath());

		Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

		Assert.assertNotNull(projectOutputFiles);

		Assert.assertEquals(
			projectOutputFiles.toString(), true, projectOutputFiles.containsKey(":modules:testportlet"));

		Set<File> files = projectOutputFiles.get(":modules:testportlet");

		Assert.assertEquals(files.toString(), 1, files.size());
	}

	@Test
	public void testGetPluginClassNames() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(
			new File(temporaryFolder.getRoot(), "build/testws1/modules/testportlet").toPath());

		Set<String> pluginClassNames = projectInfo.getPluginClassNames();

		Assert.assertNotNull(pluginClassNames);
		Assert.assertTrue(pluginClassNames.contains("com.liferay.gradle.plugins.LiferayOSGiPlugin"));
	}

	@Test
	public void testIsLiferayModule() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(
			new File(temporaryFolder.getRoot(), "build/testws1/modules/testportlet").toPath());

		Assert.assertTrue(projectInfo.isLiferayProject());
	}

	@Test
	public void testIsNotLiferayModule() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(
			new File(temporaryFolder.getRoot(), "build/testws1/modules").toPath());

		Assert.assertFalse(projectInfo.isLiferayProject());
	}

	private static final String _TEST_OUTPUT_PATH = System.getProperty("testOutputPath");

	private static final File _TOOLING_ZIP = new File(_TEST_OUTPUT_PATH + "/tooling.zip");

}