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

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class UninstallExtensionCommandTest {

	@Before
	public void setUpTestExtensions() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		Path extensionsPath = _extensionsDir.toPath();

		_setupTestExtension(extensionsPath, System.getProperty("sampleCommandJarFile"));
	}

	@Test
	public void testUninstallCustomExtension() throws Exception {
		File sampleCommandJarFile = new File(System.getProperty("sampleCommandJarFile"));

		String jarName = sampleCommandJarFile.getName();

		String[] args = {"extension", "uninstall", jarName};

		File extensionsDir = new File(temporaryFolder.getRoot(), "extensions");

		extensionsDir.mkdirs();

		File testJar = new File(extensionsDir, jarName);

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output.contains(" successful") && output.contains(jarName));

		Assert.assertTrue(!testJar.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _setupTestExtension(Path extensionsPath, String jarPath) throws Exception {
		File sampleJarFile = new File(jarPath);

		Assert.assertTrue(sampleJarFile.getAbsolutePath() + " does not exist.", sampleJarFile.exists());

		Path sampleJarPath = extensionsPath.resolve(sampleJarFile.getName());

		Files.copy(sampleJarFile.toPath(), sampleJarPath, StandardCopyOption.REPLACE_EXISTING);

		Assert.assertTrue(Files.exists(sampleJarPath));
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}