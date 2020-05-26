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

package com.liferay.extensions.sample.command;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.StringPrintStream;

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

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class SampleCommandsTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();
		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testCommandExtension() throws Exception {
		_setupTestExtensions();

		String rootPathString = _rootDir.getAbsolutePath();

		String[] args = {"--base", rootPathString, "hello", "--name", "foobar"};

		StringPrintStream outputStream = StringPrintStream.newInstance();
		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setExtensionsDir(_extensionsDir.toPath());
		bladeTestBuilder.setSettingsDir(_rootDir.toPath());
		bladeTestBuilder.setStdOut(outputStream);

		BladeTest bladeTest = bladeTestBuilder.build();

		bladeTest.run(args);

		String output = outputStream.get();

		Assert.assertTrue(output, output.contains("foobar"));

		Assert.assertFalse(output, output.contains("maven"));

		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		args = new String[] {
			"--base", workspaceDir.getPath(), "init", "-P", "maven", "-v", BladeTest.PRODUCT_VERSION_PORTAL_72
		};

		outputStream = StringPrintStream.newInstance();

		bladeTestBuilder.setStdOut(outputStream);

		bladeTest = bladeTestBuilder.build();

		bladeTest.run(args);

		args = new String[] {"--base", workspaceDir.getPath(), "hello", "--name", "foobar"};

		outputStream = StringPrintStream.newInstance();

		bladeTestBuilder.setStdOut(outputStream);

		bladeTest = bladeTestBuilder.build();

		bladeTest.run(args);

		output = outputStream.get();

		Assert.assertTrue(output, output.contains("foobar"));

		Assert.assertTrue(output, output.contains("maven"));
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
		Path extensionsPath = _extensionsDir.toPath();

		_setupTestExtension(extensionsPath, System.getProperty("sampleCommandJarFile"));
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}