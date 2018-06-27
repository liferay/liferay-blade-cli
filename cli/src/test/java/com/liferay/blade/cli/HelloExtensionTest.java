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

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.powermock.reflect.Whitebox;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class HelloExtensionTest {

	@Before
	public void setUp() throws Exception {
		temporaryFolder.newFolder(".blade", "extensions");

		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		Whitebox.setInternalState(BladeCLI.class, "USER_HOME_DIR", temporaryFolder.getRoot());

		_setupTestExtensions();
	}

	@Test
	public void testCustomProfileExtension() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		BladeSettings bladeSettings = bladeTest.getSettings();

		Assert.assertEquals("maven", bladeSettings.getProfileName());

		args = new String[] {"--base", _workspaceDir.getPath() + "/newproject", "hello", "--name", "foobar"};

		String content = TestUtil.runBlade(args);

		Assert.assertTrue(content.contains("maven"));
	}

	@Test
	public void testHelp() throws Exception {
		String[] args = {"hello", "--name", "foo"};

		String output = TestUtil.runBlade(args);

		Assert.assertEquals("Hello foo!", output.trim());
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

		_setupTestExtension(extensionsPath, System.getProperty("sampleCommandJarFile"));
	}

	private File _workspaceDir = null;

}