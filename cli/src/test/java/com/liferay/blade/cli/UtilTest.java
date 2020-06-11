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
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class UtilTest {

	@Test
	public void testAppServerProperties() throws Exception {
		File dir = temporaryFolder.getRoot();

		File appServerProperty1 = new File(dir, "app.server." + System.getProperty("user.name") + ".properties");

		appServerProperty1.createNewFile();

		File appServerProperty2 = new File(dir, "app.server.properties");

		appServerProperty2.createNewFile();

		List<Properties> propertiesList = BladeUtil.getAppServerProperties(dir);

		Assert.assertTrue(propertiesList.size() == 2);
	}

	@Test
	public void testCopyEntireDirectory() throws Exception {
		File testDir1 = temporaryFolder.newFolder("dir1");

		File testFile1 = new File(testDir1, "1");
		File testFile2 = new File(testDir1, "2");
		File testFile3 = new File(testDir1, "3");

		testFile1.createNewFile();
		testFile2.createNewFile();
		testFile3.createNewFile();

		File testDir2 = new File(temporaryFolder.getRoot(), "dir2");

		FileUtil.copyDir(testDir1.toPath(), testDir2.toPath());
		Assert.assertTrue(testDir2.exists());

		String[] testDir1List = testDir1.list();
		String[] testDir2List = testDir2.list();

		Assert.assertEquals(Arrays.toString(testDir2List), testDir1List.length, testDir2List.length);
	}

	@Test
	public void testFindParentFile() throws Exception {
		File tempTestFile = null;

		try {
			File dir = new File(".");

			dir = dir.getAbsoluteFile();

			File parentDir = dir.getParentFile();

			File parentParentDir = parentDir.getParentFile();

			tempTestFile = new File(parentParentDir, "test.file");

			if (tempTestFile.exists()) {
				Assert.assertTrue(tempTestFile.delete());
			}

			Assert.assertTrue(tempTestFile.createNewFile());

			File fileRelative = dir;

			File foundFile = BladeUtil.findParentFile(fileRelative, new String[] {"test.file"}, true);

			Assert.assertTrue(Objects.nonNull(foundFile));
		}
		finally {
			if (tempTestFile != null) {
				Files.delete(tempTestFile.toPath());
			}
		}
	}

	@Test
	public void testMigrateBladeSettings() throws Exception {
		File rootWorkspaceDir = temporaryFolder.newFolder("workspace");

		File extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		String[] args = {"--base", rootWorkspaceDir.getAbsolutePath(), "init", "-f", "foo", "-v", "dxp-7.2-ga1"};

		File workspaceDirectory = new File(rootWorkspaceDir, "foo");

		TestUtil.runBlade(workspaceDirectory, extensionsDir, args);

		GradleWorkspaceProvider provider = new GradleWorkspaceProvider();

		boolean workspace = provider.isWorkspace(workspaceDirectory);

		Assert.assertTrue(workspace);

		File bladeSettings = new File(workspaceDirectory, ".blade.properties");

		Path bladeSettingsNewPath = bladeSettings.toPath();

		Path workspacePath = workspaceDirectory.toPath();

		Path bladeDirPath = workspacePath.resolve(".blade");

		if (!Files.exists(bladeDirPath)) {
			Files.createDirectory(bladeDirPath);
		}

		Path bladeSettingsOldPath = bladeDirPath.resolve("settings.properties");

		Files.move(bladeSettingsNewPath, bladeSettingsOldPath);

		boolean bladeSettingsOldPathExists = Files.exists(bladeSettingsOldPath);

		Assert.assertTrue(bladeSettingsOldPathExists);

		args = new String[] {"--base", workspaceDirectory.getPath(), "help"};

		TestUtil.runBlade(workspaceDirectory, extensionsDir, args);

		bladeSettingsOldPathExists = Files.exists(bladeSettingsOldPath);

		Assert.assertFalse(bladeSettingsOldPathExists);

		boolean bladeSettingsPathExists = Files.exists(bladeSettingsNewPath);

		Assert.assertTrue(bladeSettingsPathExists);
	}

	@Test
	public void testNewBladeSettings() throws Exception {
		File rootWorkspaceDir = temporaryFolder.newFolder("workspace");

		File extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		String[] args = {"--base", rootWorkspaceDir.getAbsolutePath(), "init", "-f", "foo", "-v", "dxp-7.2-ga1"};

		File workspaceDirectory = new File(rootWorkspaceDir, "foo");

		TestUtil.runBlade(workspaceDirectory, extensionsDir, args);

		GradleWorkspaceProvider provider = new GradleWorkspaceProvider();

		boolean workspace = provider.isWorkspace(workspaceDirectory);

		Assert.assertTrue(workspace);

		File bladeSettings = new File(workspaceDirectory, ".blade.properties");

		Assert.assertTrue(bladeSettings.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}