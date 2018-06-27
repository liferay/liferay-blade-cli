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
import com.liferay.blade.cli.TestUtil;
import com.liferay.project.templates.internal.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		String output = TestUtil.runBlade(args);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		output = TestUtil.runBlade(args);

		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
	}

	@Test
	public void testInstallCustomGithubExtension() throws Exception {
		String[] args = {"extension install", _SAMPLE_COMMAND_STRING};

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

	private static final String _SAMPLE_COMMAND_STRING = "https://github.com/gamerson/blade-sample-command";

	private static final File _sampleCommandJarFile = new File(System.getProperty("sampleCommandJarFile"));

}