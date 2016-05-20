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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class GradleToolingTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("deps.zip"), new File("bin_test/deps.zip"));
	}

	@Test
	public void testCheckLatestArtifactVersionOnline() throws Exception {
		File file = GradleTooling.findLatestAvailableArtifact(
			"group: 'com.liferay', name: 'com.liferay.gradle.plugins.workspace', version: '1+', classifier: 'sources', ext: 'jar'");

		String name = file.getName();

		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(name.startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(name.endsWith("sources.jar"));
		assertEquals(name, true, name.matches(".*-1\\.0\\.[0-9]+-.*"));
	}

	@Test
	public void testGetOutputFile() throws Exception {
		Set<File> files = GradleTooling.getOutputFiles(
			new File("bin_test"), new File("."));

		assertNotNull(files);
		assertEquals(2, files.size());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		Set<File> files = GradleTooling.getOutputFiles(
			new File("bin_test"), new File(".."));

		assertNotNull(files);
		assertEquals(17, files.size());
	}

	@Test
	public void testGetPluginClassNames() throws Exception {
		Set<String> pluginClassNames = GradleTooling.getPluginClassNames (
			new File("bin_test"), new File("."));

		assertNotNull(pluginClassNames);
		assertTrue(pluginClassNames.contains("aQute.bnd.gradle.BndPlugin"));
	}

	@Test
	public void testIsLiferayModule() throws Exception {
		boolean isModule = GradleTooling.isLiferayModule (
			new File("bin_test"), new File("."));

		assertFalse(isModule);
	}
}
