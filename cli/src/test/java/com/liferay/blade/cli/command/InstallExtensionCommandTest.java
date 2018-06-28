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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.PathChangeWatcher;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.concurrent.CompletableFuture;

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
public class InstallExtensionCommandTest {

	@Before
	public void setUp() throws Exception {
		Whitebox.setInternalState(BladeCLI.class, "USER_HOME_DIR", temporaryFolder.getRoot());

		BladeTest bladeTest = new BladeTest();

		File cacheDir = bladeTest.getCacheDir();

		if (cacheDir.exists()) {
			FileUtil.deleteDir(cacheDir.toPath());
		}
	}

	@Test
	public void testInstallCustomExtension() throws Exception {
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		String output = TestUtil.runBlade(args);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		File root = temporaryFolder.getRoot();

		File extensionJar = new File(root, ".blade/extensions/" + _sampleCommandJarFile.getName());

		Assert.assertTrue(extensionJar.getAbsolutePath() + " does not exist", extensionJar.exists());
	}

	@Test
	public void testInstallCustomExtensionTwice() throws Exception {
		String jarName = _sampleCommandJarFile.getName();

		File extensionsFolder = temporaryFolder.newFolder(".blade", "extensions");

		File extensionJar = new File(extensionsFolder, jarName);

		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		String output;

		try (PathChangeWatcher watcher = new PathChangeWatcher(extensionJar.toPath())) {
			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());

			output = TestUtil.runBlade(args);

			Assert.assertTrue("Existing extension \"" + jarName + "\" should have been modified", watcher.get());
		}

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(jarName));

		output = TestUtil.runBlade(args);

		String data = "y";

		try (PathChangeWatcher watcher = new PathChangeWatcher(extensionJar.toPath())) {
			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());

			output = _testBladeWithInteractive(args, data);

			Assert.assertTrue("Existing extension \"" + jarName + "\" should have been modified", watcher.get());
		}

		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
		Assert.assertTrue("Expected output to contain \"Overwriting\"\n" + output, output.contains("Overwriting"));
		Assert.assertTrue(
			"Expected output to contain \"installed successfully\"\n" + output,
			output.contains(" installed successfully"));

		data = "n";

		try (PathChangeWatcher watcher = new PathChangeWatcher(extensionJar.toPath())) {
			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());

			output = _testBladeWithInteractive(args, data);

			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());
		}

		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
		Assert.assertFalse("Expected output to not contain \"Overwriting\"\n" + output, output.contains("Overwriting"));
		Assert.assertFalse(
			"Expected output to not contain \"installed successfully\"\n" + output,
			output.contains(" installed successfully"));

		data = "foobar";

		try (PathChangeWatcher watcher = new PathChangeWatcher(extensionJar.toPath())) {
			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());

			output = _testBladeWithInteractive(args, data);

			Assert.assertFalse("Existing extension \"" + jarName + "\" should not have been modified", watcher.get());
		}

		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
		Assert.assertFalse("Expected output to not contain \"Overwriting\"\n" + output, output.contains("Overwriting"));
		Assert.assertFalse(
			"Expected output to not contain \"installed successfully\"\n" + output,
			output.contains(" installed successfully"));
	}

	@Test
	public void testInstallCustomGithubExtension() throws Exception {
		String[] args = {"extension", "install", "https://github.com/gamerson/blade-sample-command"};

		String output = TestUtil.runBlade(args);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		File root = temporaryFolder.getRoot();

		Path rootPath = root.toPath();

		Path extensionJarPath = rootPath.resolve(Paths.get(".blade", "extensions", "blade-sample-command-master.jar"));

		boolean pathExists = Files.exists(extensionJarPath);

		Assert.assertTrue(extensionJarPath.toAbsolutePath() + " does not exist", pathExists);
	}

	@Test
	public void testInstallUninstallCustomExtension() throws Exception {
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		String output = TestUtil.runBlade(args);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		args = new String[] {"extension uninstall", _sampleCommandJarFile.getName()};

		output = TestUtil.runBlade(args);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _testBladeWithInteractive(String[] args, String data) throws Exception {
		String output;
		InputStream testInput = new ByteArrayInputStream(data.getBytes("UTF-8"));
		InputStream old = System.in;

		try {
			System.setIn(testInput);

			CompletableFuture<String> futureString = CompletableFuture.supplyAsync(
				() -> {
					try {
						return TestUtil.runBlade(args);
					}
					catch (Exception e) {
						throw new RuntimeException(e);
					}
				});

			output = futureString.get();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			System.setIn(old);
		}

		return output;
	}

	private static final File _sampleCommandJarFile = new File(System.getProperty("sampleCommandJarFile"));

}