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

import aQute.lib.io.IO;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
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

		Assert.assertEquals(testDir1.list().length, testDir2.list().length);
	}

	@Test
	public void testFindParentFile() throws Exception {
		File tempTestFile = null;

		try {
			File parentDirectory = new File(".").getAbsoluteFile().getParentFile();

			File parentParentDirectory = parentDirectory.getParentFile();

			tempTestFile = new File(parentParentDirectory, "test.file");

			if (tempTestFile.exists()) {
				Assert.assertTrue(tempTestFile.delete());
			}

			Assert.assertTrue(tempTestFile.createNewFile());

			File fileRelative = new File(".");

			File foundFile = BladeUtil.findParentFile(fileRelative, new String[] {"test.file"}, true);

			Assert.assertTrue(Objects.nonNull(foundFile));
		}
		finally {
			if (tempTestFile != null) {
				IO.delete(tempTestFile);
			}
		}
	}

	@Test
	public void testIsWorkspace1() throws Exception {
		File workspace = new File(temporaryFolder.getRoot(), "workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: \"com.liferay.workspace\"";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		Assert.assertTrue(BladeUtil.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace2() throws Exception {
		File workspace = new File(temporaryFolder.getRoot(), "workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: 'com.liferay.workspace'";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		Assert.assertTrue(BladeUtil.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace3() throws Exception {
		File workspace = new File(temporaryFolder.getRoot(), "workspace");

		workspace.mkdirs();

		File buildFile = new File(workspace, "build.gradle");

		File settingsFile = new File(workspace, "settings.gradle");

		settingsFile.createNewFile();

		String plugin = "\napply   plugin:   \n\"com.liferay.workspace\"";

		Files.write(buildFile.toPath(), plugin.getBytes());

		Assert.assertTrue(BladeUtil.isWorkspace(workspace));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}