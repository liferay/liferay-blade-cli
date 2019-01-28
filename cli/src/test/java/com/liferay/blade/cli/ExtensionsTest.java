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

import com.liferay.blade.cli.BladeTest.BladeTestBuilder;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ExtensionsTest {

	@Before
	public void setUp() throws Exception {
		BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		_extensionClassLoaderSupplier = new ExtensionClassLoaderSupplier(_extensionsDir.toPath());

		bladeTestBuilder.setExtensionsDir(_extensionsDir.toPath());

		bladeTestBuilder.setSettingsDir(_rootDir.toPath());

		_bladeTest = bladeTestBuilder.build();
	}

	@After
	public void tearDown() throws Exception {
		if (_extensionClassLoaderSupplier != null) {
			_extensionClassLoaderSupplier.close();
		}
	}

	@Test
	public void testArgsSort() throws Exception {
		String[] args = {"--base", "/foo/bar/dir/", "--flag1", "extension", "install", "/path/to/jar.jar", "--flag2"};

		ClassLoader classLoader = _extensionClassLoaderSupplier.get();

		Extensions extensions = new Extensions(classLoader);

		Map<String, BaseCommand<? extends BaseArgs>> commands = extensions.getCommands();

		String[] sortedArgs = Extensions.sortArgs(commands, args);

		boolean correctSort = false;

		for (String arg : sortedArgs) {
			if (Objects.equals(arg, "extension install")) {
				correctSort = true;
			}
		}

		Assert.assertTrue(correctSort);
	}

	@Test
	public void testLoadCommandsBuiltIn() throws Exception {
		ClassLoader classLoader = _extensionClassLoaderSupplier.get();

		Extensions extensions = new Extensions(classLoader);

		Map<String, BaseCommand<? extends BaseArgs>> commands = extensions.getCommands();

		Assert.assertNotNull(commands);

		Assert.assertEquals(commands.toString(), _BUILT_IN_COMMANDS_COUNT, commands.size());
	}

	@Test
	public void testLoadCommandsWithCustomExtension() throws Exception {
		_setupTestExtensions();

		ClassLoader classLoader = _extensionClassLoaderSupplier.get();

		Extensions extensions = new Extensions(classLoader);

		Map<String, BaseCommand<? extends BaseArgs>> commands = extensions.getCommands();

		Assert.assertNotNull(commands);

		Assert.assertEquals(commands.toString(), _BUILT_IN_COMMANDS_COUNT + 1, commands.size());
	}

	@Test
	public void testLoadCommandsWithCustomExtensionInWorkspace() throws Exception {
		_setupTestExtensions();

		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		String[] args = {"--base", workspaceDir.getPath(), "init", "-P", "foo"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		BladeSettings settings = _bladeTest.getBladeSettings();

		settings.setProfileName("foo");

		ClassLoader classLoader = _extensionClassLoaderSupplier.get();

		Extensions extensions = new Extensions(classLoader);

		Map<String, BaseCommand<? extends BaseArgs>> commands = extensions.getCommands("foo");

		Assert.assertNotNull(commands);

		Assert.assertEquals(commands.toString(), _BUILT_IN_COMMANDS_COUNT + 2, commands.size());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static int _getBuiltInCommandsCount() {
		ClassLoader classLoader = ExtensionsTest.class.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"META-INF/services/com.liferay.blade.cli.command.BaseCommand");

		try (Scanner scanner = new Scanner(inputStream)) {
			int numLines = 0;

			while (scanner.hasNextLine()) {
				scanner.nextLine();
				numLines++;
			}

			return numLines;
		}
	}

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
		_setupTestExtension(extensionsPath, System.getProperty("sampleProfileJarFile"));
		_setupTestExtension(extensionsPath, System.getProperty("sampleTemplateJarFile"));
	}

	private static final int _BUILT_IN_COMMANDS_COUNT = _getBuiltInCommandsCount();

	private BladeTest _bladeTest;
	private ExtensionClassLoaderSupplier _extensionClassLoaderSupplier = null;
	private File _extensionsDir = null;
	private File _rootDir = null;

}