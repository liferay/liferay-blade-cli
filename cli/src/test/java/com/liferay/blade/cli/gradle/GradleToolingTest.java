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

import java.io.File;

import java.nio.file.Files;
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

		Files.copy(new File("deps.zip").toPath(), _DEPS_ZIP.toPath(), StandardCopyOption.REPLACE_EXISTING);

		FileUtil.copyDir(new File("test-resources/projects/testws1").toPath(), wsDir.toPath());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Files.delete(_DEPS_ZIP.toPath());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		Map<String, Set<File>> projectOutputFiles = GradleTooling.getProjectOutputFiles(
			new File(temporaryFolder.getRoot(), "build/testws1"));

		Assert.assertNotNull(projectOutputFiles);

		Assert.assertEquals(
			projectOutputFiles.toString(), true, projectOutputFiles.containsKey(":modules:testportlet"));

		Set<File> files = projectOutputFiles.get(":modules:testportlet");

		Assert.assertEquals(files.toString(), 1, files.size());
	}

	@Test
	public void testGetPluginClassNames() throws Exception {
		Set<String> pluginClassNames = GradleTooling.getPluginClassNames(
			new File(temporaryFolder.getRoot(), "build/testws1/modules/testportlet"));

		Assert.assertNotNull(pluginClassNames);
		Assert.assertTrue(pluginClassNames.contains("com.liferay.gradle.plugins.LiferayOSGiPlugin"));
	}

	@Test
	public void testIsLiferayModule() throws Exception {
		boolean liferayModule = GradleTooling.isLiferayModule(
			new File(temporaryFolder.getRoot(), "build/testws1/modules/testportlet"));

		Assert.assertTrue(liferayModule);
	}

	@Test
	public void testIsNotLiferayModule() throws Exception {
		boolean liferayModule = GradleTooling.isLiferayModule(
			new File(temporaryFolder.getRoot(), "build/testws1/modules"));

		Assert.assertFalse(liferayModule);
	}

	private static final File _DEPS_ZIP = new File("build/classes/java/test/deps.zip");

}