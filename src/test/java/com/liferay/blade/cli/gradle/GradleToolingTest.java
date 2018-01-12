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

import aQute.lib.io.IO;

import java.io.File;

import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class GradleToolingTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("deps.zip"), new File("build/classes/java/test/deps.zip"));
		IO.copy(new File("test-resources/projects/testws1"), new File("build/testws1"));
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		Set<File> files = GradleTooling.getOutputFiles(new File("build"), new File("build/testws1"));

		Assert.assertNotNull(files);
		Assert.assertEquals(1, files.size());
	}

	@Test
	public void testGetPluginClassNames() throws Exception {
		Set<String> pluginClassNames = GradleTooling.getPluginClassNames(
			new File("build"), new File("build/testws1/modules/testportlet"));

		Assert.assertNotNull(pluginClassNames);
		Assert.assertTrue(pluginClassNames.contains("com.liferay.gradle.plugins.LiferayOSGiPlugin"));
	}

	@Test
	public void testIsLiferayModule() throws Exception {
		boolean isModule = GradleTooling.isLiferayModule (
			new File("build"), new File("build/testws1/modules/testportlet"));

		Assert.assertTrue(isModule);
	}

	@Test
	public void testIsNotLiferayModule() throws Exception {
		boolean isModule = GradleTooling.isLiferayModule (new File("build"), new File("build/testws1/modules"));

		Assert.assertFalse(isModule);
	}

}