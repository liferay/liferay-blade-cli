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

import java.io.File;

import java.nio.file.Files;

import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David Truong
 */
public class UtilTest {

	@After
	public void cleanUp() throws Exception {
		_testdir = IO.getFile("build/test");

		if (_testdir.exists()) {
			IO.delete(_testdir);
			Assert.assertFalse(_testdir.exists());
		}
	}

	@Test
	public void testAppServerProperties() throws Exception {
		File dir = new File("build/test");

		dir.mkdirs();

		File appServerProperty1 = new File(dir, "app.server." + System.getProperty("user.name") + ".properties");

		appServerProperty1.createNewFile();

		File appServerProperty2 = new File(dir, "app.server.properties");

		appServerProperty2.createNewFile();

		List<Properties> propertiesList = Util.getAppServerProperties(dir);

		Assert.assertTrue(propertiesList.size() == 2);
	}

	@Test
	public void testIsWorkspace1() throws Exception {
		File workspace = new File("build/test/workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: \"com.liferay.workspace\"";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		Assert.assertTrue(Util.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace2() throws Exception {
		File workspace = new File("build/test/workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: 'com.liferay.workspace'";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		Assert.assertTrue(Util.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace3() throws Exception {
		File workspace = new File("build/test/workspace");

		workspace.mkdirs();

		File buildFile = new File(workspace, "build.gradle");

		File settingsFile = new File(workspace, "settings.gradle");

		settingsFile.createNewFile();

		String plugin = "\napply   plugin:   \n\"com.liferay.workspace\"";

		Files.write(buildFile.toPath(), plugin.getBytes());

		Assert.assertTrue(Util.isWorkspace(workspace));
	}

	private File _testdir;

}