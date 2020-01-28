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

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.StringTestUtil;
import com.liferay.blade.cli.TestUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.diibadaaba.zipdiff.DifferenceCalculator;
import net.diibadaaba.zipdiff.Differences;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class InstallExtensionCommandTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testInstallCustomExtension() throws Exception {
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		File root = temporaryFolder.getRoot();

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		File extensionJar = new File(root, ".blade/extensions/" + _sampleCommandJarFile.getName());

		Assert.assertTrue(extensionJar.getAbsolutePath() + " does not exist", extensionJar.exists());
	}

	@Test
	public void testInstallCustomExtensionSubdirectory() throws Exception {
		Assume.assumeFalse(_isWindows());

		String[] args = {"extension", "install", _LINK_TO_DEPLOY_COMMAND};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, false, args);

		String output = bladeTestResults.getOutput();

		String errors = bladeTestResults.getErrors();

		Assert.assertTrue(
			"Expected output to contain \"successful\"\n" + output + System.lineSeparator() + errors,
			output.contains(" successful"));

		Path rootPath = _rootDir.toPath();

		Path extensionDirPath = rootPath.resolve(Paths.get(".blade", "extensions"));

		try (Stream<Path> extensionStream = Files.list(extensionDirPath)) {
			boolean pathExists = extensionStream.map(
				Path::getFileName
			).map(
				Object::toString
			).anyMatch(
				fileNameString -> fileNameString.startsWith("maven-profile")
			);

			Assert.assertTrue("maven-profile extension jar does not exist", pathExists);
		}
	}

	@Test
	public void testInstallCustomExtensionTwiceDontOverwrite() throws Exception {
		String jarName = _sampleCommandJarFile.getName();

		File extensionJar = new File(_extensionsDir, jarName);

		String[] args = {"extension", "install", _sampleCommandJarFile.getAbsolutePath()};

		Path extensionPath = extensionJar.toPath();

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		_testJarsDiff(_sampleCommandJarFile, extensionJar);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));
		Assert.assertTrue(output.contains(jarName));

		File tempDir = temporaryFolder.newFolder("overwrite");

		Path tempPath = tempDir.toPath();

		Path sampleCommandPath = tempPath.resolve(_sampleCommandJarFile.getName());

		Files.copy(
			_sampleCommandJarFile.toPath(), sampleCommandPath, StandardCopyOption.COPY_ATTRIBUTES,
			StandardCopyOption.REPLACE_EXISTING);

		File sampleCommandFile = sampleCommandPath.toFile();

		sampleCommandFile.setLastModified(0);

		args = new String[] {"extension", "install", sampleCommandFile.getAbsolutePath()};

		output = _testBladeWithInteractive(_rootDir, _extensionsDir, args, "n");

		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
		Assert.assertFalse(
			"Expected output to not contain \"installed successfully\"\n" + output,
			output.contains(" installed successfully"));

		Assert.assertTrue(sampleCommandFile.lastModified() == 0);

		File extensionFile = extensionPath.toFile();

		Assert.assertFalse(extensionFile.lastModified() == 0);

		output = _testBladeWithInteractive(_rootDir, _extensionsDir, args, "defaultShouldBeNo");

		Assert.assertFalse(extensionFile.lastModified() == 0);
		Assert.assertTrue(
			"Expected output to contain \"already exists\"\n" + output, output.contains(" already exists"));
		Assert.assertFalse("Expected output to not contain \"Overwriting\"\n" + output, output.contains("Overwriting"));
		Assert.assertFalse(
			"Expected output to not contain \"installed successfully\"\n" + output,
			output.contains(" installed successfully"));
	}

	@Test
	public void testInstallCustomExtensionTwiceOverwrite() throws Exception {
		String jarName = _sampleCommandJarFile.getName();

		File extensionJar = new File(_extensionsDir, jarName);

		String[] args = {"extension", "install", _sampleCommandJarFile.getAbsolutePath()};

		Path extensionPath = extensionJar.toPath();

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		_testJarsDiff(_sampleCommandJarFile, extensionJar);

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));
		Assert.assertTrue(output.contains(jarName));

		File tempDir = temporaryFolder.newFolder("overwrite");

		Path tempPath = tempDir.toPath();

		Path sampleCommandPath = tempPath.resolve(_sampleCommandJarFile.getName());

		Files.copy(
			_sampleCommandJarFile.toPath(), sampleCommandPath, StandardCopyOption.COPY_ATTRIBUTES,
			StandardCopyOption.REPLACE_EXISTING);

		File sampleCommandFile = sampleCommandPath.toFile();

		sampleCommandFile.setLastModified(0);

		args = new String[] {"extension", "install", sampleCommandFile.getAbsolutePath()};

		output = _testBladeWithInteractive(_rootDir, _extensionsDir, args, "y");

		_testJarsDiff(sampleCommandFile, extensionJar);

		Assert.assertTrue("Expected output to contain \"Overwrite\"\n" + output, output.contains("Overwrite"));
		boolean assertCorrect = output.contains(" installed successfully");

		if (!assertCorrect) {
			Assert.assertTrue("Expected output to contain \"installed successfully\"\n" + output, assertCorrect);
		}

		File extensionFile = extensionPath.toFile();

		Assert.assertEquals(sampleCommandFile.lastModified(), extensionFile.lastModified());
	}

	@Test
	public void testInstallCustomGithubExtension() throws Exception {
		Assume.assumeFalse(_isWindows());

		String[] args = {"extension", "install", "https://github.com/gamerson/blade-sample-command"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Path rootPath = _rootDir.toPath();

		Path extensionJarPath = rootPath.resolve(
			Paths.get(".blade", "extensions", "blade-sample-command-master-1.0.0.jar"));

		boolean pathExists = Files.exists(extensionJarPath);

		Assert.assertTrue(extensionJarPath.toAbsolutePath() + " does not exist", pathExists);
	}

	@Test
	public void testInstallExtensionPathRequired() throws Exception {
		String[] args = {"extension install"};

		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		BladeTest bladeTest = bladeTestBuilder.build();

		String error = null;

		try {
			bladeTest.run(args);
		}
		catch (Exception e) {
			error = e.getMessage();
		}

		Assert.assertTrue(error, error.contains("Unable to acquire an answer"));
	}

	@Test
	public void testInstallUninstallCustomExtension() throws Exception {
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		args = new String[] {"extension uninstall", _sampleCommandJarFile.getName()};

		bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		output = bladeTestResults.getOutput();

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static void _testJarsDiff(File warFile1, File warFile2) throws IOException {
		DifferenceCalculator differenceCalculator = new DifferenceCalculator(warFile1, warFile2);

		differenceCalculator.setFilenameRegexToIgnore(Collections.singleton(".*META-INF.*"));
		differenceCalculator.setIgnoreTimestamps(true);

		Differences differences = differenceCalculator.getDifferences();

		if (!differences.hasDifferences()) {
			return;
		}

		StringBuilder message = new StringBuilder();

		message.append("WAR ");
		message.append(warFile1);
		message.append(" and ");
		message.append(warFile2);
		message.append(" do not match:");
		message.append(System.lineSeparator());

		boolean realChange;

		Map<String, ZipArchiveEntry> added = differences.getAdded();
		Map<String, ZipArchiveEntry[]> changed = differences.getChanged();
		Map<String, ZipArchiveEntry> removed = differences.getRemoved();

		if (added.isEmpty() && !changed.isEmpty() && removed.isEmpty()) {
			realChange = false;

			ZipFile zipFile1 = null;
			ZipFile zipFile2 = null;

			try {
				zipFile1 = new ZipFile(warFile1);
				zipFile2 = new ZipFile(warFile2);

				for (Map.Entry<String, ZipArchiveEntry[]> entry : changed.entrySet()) {
					ZipArchiveEntry[] zipArchiveEntries = entry.getValue();

					ZipArchiveEntry zipArchiveEntry1 = zipArchiveEntries[0];
					ZipArchiveEntry zipArchiveEntry2 = zipArchiveEntries[0];

					if (zipArchiveEntry1.isDirectory() && zipArchiveEntry2.isDirectory() &&
						(zipArchiveEntry1.getSize() == zipArchiveEntry2.getSize()) &&
						(zipArchiveEntry1.getCompressedSize() <= 2) && (zipArchiveEntry2.getCompressedSize() <= 2)) {

						// Skip zipdiff bug

						continue;
					}

					try (InputStream inputStream1 = zipFile1.getInputStream(
							zipFile1.getEntry(zipArchiveEntry1.getName()));
						InputStream inputStream2 = zipFile2.getInputStream(
							zipFile2.getEntry(zipArchiveEntry2.getName()))) {

						List<String> lines1 = StringTestUtil.readLines(inputStream1);
						List<String> lines2 = StringTestUtil.readLines(inputStream2);

						message.append(System.lineSeparator());

						message.append("--- ");
						message.append(zipArchiveEntry1.getName());
						message.append(System.lineSeparator());

						message.append("+++ ");
						message.append(zipArchiveEntry2.getName());
						message.append(System.lineSeparator());

						Patch<String> diff = DiffUtils.diff(lines1, lines2);

						for (Delta<String> delta : diff.getDeltas()) {
							message.append('\t');
							message.append(delta.getOriginal());
							message.append(System.lineSeparator());

							message.append('\t');
							message.append(delta.getRevised());
							message.append(System.lineSeparator());
						}
					}

					realChange = true;

					break;
				}
			}
			finally {
				ZipFile.closeQuietly(zipFile1);
				ZipFile.closeQuietly(zipFile2);
			}
		}
		else {
			realChange = true;
		}

		Assert.assertFalse(message.toString(), realChange);
	}

	private boolean _isWindows() {
		String osName = System.getProperty("os.name");

		osName = osName.toLowerCase();

		return osName.contains("win");
	}

	private String _testBladeWithInteractive(File userHomeDir, File extensionsDir, String[] args, String data)
		throws Exception {

		InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

		try {
			BladeTestResults bladeTestResults = TestUtil.runBlade(userHomeDir, extensionsDir, in, args);

			return bladeTestResults.getOutput();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static final String _LINK_TO_DEPLOY_COMMAND =
		"https://github.com/liferay/liferay-blade-cli/tree/master/extensions/maven-profile";

	private static final File _sampleCommandJarFile = new File(System.getProperty("sampleCommandJarFile"));

	private File _extensionsDir = null;
	private File _rootDir = null;

}