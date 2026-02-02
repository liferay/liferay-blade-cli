/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.ReleaseUtil;
import com.liferay.release.util.ReleaseEntry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		_workspacePath = _workspaceDir.toPath();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testBladeInitDontLoseGitDirectory() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testBladeInitDontLoseGitDirectory");

		testdir.mkdirs();

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		File gitdir = new File(projectDir, ".git");

		Assert.assertTrue(gitdir.exists());

		File oldGitIgnore = new File(projectDir, "plugins-sdk/.gitignore");

		Assert.assertTrue(oldGitIgnore.exists());
	}

	@Test
	public void testBladeInitEmptyDirectory() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		String[] args = {"--base", emptyDir.getPath(), "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		Assert.assertNotNull(bladeTest.getWorkspaceProvider(emptyDir));
	}

	@Test
	public void testBladeInitEmptyDirectoryHandleDot() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		File dotFile = new File(emptyDir.getPath(), ".");

		String pathStringToTest = dotFile.getAbsolutePath();

		String[] args = {"--base", pathStringToTest, "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		Assert.assertNotNull(bladeTest.getWorkspaceProvider(emptyDir));
	}

	@Test
	public void testBladeInitEmptyDirectoryHandleTwoDots() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		File dotFile = new File(emptyDir.getPath(), ".");

		String pathStringToTest = dotFile.getAbsolutePath();

		String[] args = {"--base", pathStringToTest, "init", ".", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		Assert.assertNotNull(bladeTest.getWorkspaceProvider(emptyDir));
	}

	@Test
	public void testBladeInitUpgradePluginsSDKTo70() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testUpgradePluginsSDKTo70");

		testdir.mkdirs();

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_DXP_70};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(projectDir);

		bladeTest.run(args);

		File buildProperties = new File(projectDir, "plugins-sdk/build.properties");

		Properties props = new Properties();

		props.load(new FileInputStream(buildProperties));

		String version = props.getProperty("lp.version");

		Assert.assertEquals("7.0.0", version);
	}

	@Test
	public void testBladeInitWithCustomProfile() throws Exception {
		File tempDir = temporaryFolder.newFolder();

		File base = new File(tempDir.getPath());

		String basePath = base.getAbsolutePath();

		String[] args = {"--base", basePath, "init", "-P", "myprofile", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(tempDir, _extensionsDir, args);

		File settingsFile = new File(basePath, ".blade.properties");

		Properties props = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(settingsFile)) {
			props.load(fileInputStream);

			String profileName = props.getProperty("profile.name");

			Assert.assertEquals("myprofile", profileName);
		}
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(_workspaceDir.exists());

		Path buildGradlePath = _workspacePath.resolve("build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));

		Path modulesPath = _workspacePath.resolve("modules");

		Assert.assertTrue(Files.exists(modulesPath));

		Path comPath = _workspacePath.resolve("com");

		Assert.assertFalse(Files.exists(comPath));

		_verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		Path fooPath = _workspacePath.resolve("foo");

		Files.createFile(fooPath);

		Assert.assertTrue(Files.exists(fooPath));

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		Path buildGradlePath = _workspacePath.resolve("build.gradle");

		Assert.assertFalse(Files.exists(buildGradlePath));
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(_workspaceDir.exists());

		Path buildGradlePath = _workspacePath.resolve("build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));

		Path modulesPath = _workspacePath.resolve("modules");

		Assert.assertTrue(Files.exists(modulesPath));

		_verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceInWorkspaceRoot() {
		_testDefaultInitWorkspaceInWorkspace(".");
	}

	@Test
	public void testDefaultInitWorkspaceInWorkspaceSubdir() {
		_testDefaultInitWorkspaceInWorkspace("modules");
	}

	@Test
	public void testInitCommandGradleOption() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-b", "gradle", "gradleworkspace", "-v",
			BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path gradleWorkspace = _workspacePath.resolve("gradleworkspace");

		Assert.assertTrue(Files.exists(gradleWorkspace));

		Path pomXmlPath = gradleWorkspace.resolve("pom.xml");

		Assert.assertFalse(Files.exists(pomXmlPath));

		Path buildGradlePath = gradleWorkspace.resolve("build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));
	}

	@Test
	public void testInitCommandListAll() throws Exception {
		String[] args = {"init", "--list", "--all"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("dxp-2023.q4.6"));

		Assert.assertTrue(output, output.contains("dxp-7.4-u110"));

		Assert.assertTrue(output, output.contains("portal-7.1-ga2"));
	}

	@Test
	public void testInitCommandListMoreOptions() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "testworkspace"};

		String responses = "more" + System.lineSeparator() + "dxp-7.2-dxp-5" + System.lineSeparator();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responses.getBytes());

		BladeTestResults bladeTestResults = TestUtil.runBlade(
			_workspaceDir, _extensionsDir, byteArrayInputStream, true, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("dxp-7.3-u34"));
	}

	@Test
	public void testInitCommandListPromoted() throws Exception {
		String[] args = {"init", "--list"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		List<String> lines = new ArrayList<>();

		try (Scanner scanner = new Scanner(output)) {
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
		}

		String firstLine = lines.get(1);

		ReleaseEntry releaseEntry = ReleaseUtil.getReleaseEntry(firstLine);

		Assert.assertNotNull(releaseEntry);

		Assert.assertTrue(releaseEntry.isPromoted());
	}

	@Test
	public void testInitInPluginsSDKDirectory() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		_makeSDK(_workspaceDir);

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path buildGradlePath = _workspacePath.resolve("build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));

		Path modulesPath = _workspacePath.resolve("modules");

		Assert.assertTrue(Files.exists(modulesPath));

		Path themesPath = _workspacePath.resolve("themes");

		Assert.assertTrue(Files.exists(themesPath));

		Path portletsPath = _workspacePath.resolve("portlets");

		Assert.assertFalse(Files.exists(portletsPath));

		Path hooksPath = _workspacePath.resolve("hooks");

		Assert.assertFalse(Files.exists(hooksPath));

		Path buildPropertiesPath = _workspacePath.resolve("build.properties");

		Assert.assertFalse(Files.exists(buildPropertiesPath));

		Path buildXmlPath = _workspacePath.resolve("build.xml");

		Assert.assertFalse(Files.exists(buildXmlPath));

		Path pluginBuildPropertiesPath = _workspacePath.resolve("plugins-sdk/build.properties");

		Assert.assertTrue(Files.exists(pluginBuildPropertiesPath));

		Path pluginBuildXmlPath = _workspacePath.resolve("plugins-sdk/build.xml");

		Assert.assertTrue(Files.exists(pluginBuildXmlPath));
	}

	@Test(expected = AssertionError.class)
	public void testInitLegacyProductKey() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "portal-7.0-ga1"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);
	}

	@Test
	public void testInitWithLiferayVersion70() throws Exception {
		_testInitWithLiferayVersion(BladeTest.PRODUCT_VERSION_DXP_70);
	}

	@Test
	public void testInitWithLiferayVersion71() throws Exception {
		_testInitWithLiferayVersion(BladeTest.PRODUCT_VERSION_DXP_71);
	}

	@Test
	public void testInitWithLiferayVersion72() throws Exception {
		_testInitWithLiferayVersion(BladeTest.PRODUCT_VERSION_DXP_72);
	}

	@Test
	public void testInitWithLiferayVersion73() throws Exception {
		_testInitWithLiferayVersion(BladeTest.PRODUCT_VERSION_PORTAL_73);
	}

	@Test(expected = AssertionError.class)
	public void testInitWithLiferayVersionUnset() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "newproject", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		Path newproject = _workspacePath.resolve("newproject");

		Files.createDirectories(newproject);

		Assert.assertTrue(Files.exists(newproject));

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path buildGradlePath = newproject.resolve("build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));

		Path modulesPath = newproject.resolve("modules");

		Assert.assertTrue(Files.exists(modulesPath));

		Path settingsGradlePath = newproject.resolve("settings.gradle");

		String contents = new String(Files.readAllBytes(settingsGradlePath));

		Assert.assertTrue(
			String.join(
				System.lineSeparator(), "Error checking com.liferay.gradle.plugins.workspace version.",
				"Expected " + _GRADLE_PLUGINS_WORKSPACE_VERSION, contents),
			contents.contains(_GRADLE_PLUGINS_WORKSPACE_VERSION));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "newproject", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		Path newProjectPath = _workspacePath.resolve("newproject");

		Files.createDirectories(newProjectPath);

		Assert.assertTrue(Files.exists(newProjectPath));

		Path fooPath = newProjectPath.resolve("foo");

		Files.createFile(fooPath);

		Assert.assertTrue(Files.exists(fooPath));

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		Path buildGradlePath = newProjectPath.resolve("build.gradle");

		Assert.assertFalse(Files.exists(buildGradlePath));
	}

	@Test
	public void testInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "newproject", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path buildGradlePath = _workspacePath.resolve("newproject/build.gradle");

		Assert.assertTrue(Files.exists(buildGradlePath));

		Path modulesPath = _workspacePath.resolve("newproject/modules");

		Assert.assertTrue(Files.exists(modulesPath));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _createBundle() throws Exception {
		Path projectPath = _workspacePath.resolve("modules");

		String[] args = {
			"create", "--base", _workspacePath.toString(), "-t", "mvc-portlet", "-d", projectPath.toString(), "foo"
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path path = projectPath.resolve("foo");

		Path bndPath = path.resolve("bnd.bnd");

		Assert.assertTrue(Files.exists(path));

		Assert.assertTrue(Files.exists(bndPath));
	}

	private BladeTest _getBladeTestCustomWorkspace(File workspaceDir) {
		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setExtensionsDir(_extensionsDir.toPath());
		bladeTestBuilder.setSettingsDir(workspaceDir.toPath());

		return bladeTestBuilder.build();
	}

	private void _makeSDK(File dir) throws Exception {
		Path dirPath = dir.toPath();

		Path portletsPath = dirPath.resolve("portlets");

		Files.createDirectories(portletsPath);

		Assert.assertTrue(Files.exists(portletsPath));

		Path hooksPath = dirPath.resolve("hooks");

		Files.createDirectories(hooksPath);

		Assert.assertTrue(Files.exists(hooksPath));

		Path layouttplPath = dirPath.resolve("layouttpl");

		Files.createDirectories(layouttplPath);

		Assert.assertTrue(Files.exists(layouttplPath));

		Path themesPath = dirPath.resolve("themes");

		Files.createDirectories(themesPath);

		Assert.assertTrue(Files.exists(themesPath));

		Path buildPropsPath = dirPath.resolve("build.properties");

		Files.createDirectories(buildPropsPath);

		Assert.assertTrue(Files.exists(buildPropsPath));

		Path buildXmlPath = dirPath.resolve("build.xml");

		Files.createDirectories(buildXmlPath);

		Assert.assertTrue(Files.exists(buildXmlPath));

		Path buildCommonXmlPath = dirPath.resolve("build-common.xml");

		Files.createDirectories(buildCommonXmlPath);

		Assert.assertTrue(Files.exists(buildCommonXmlPath));

		Path buildCommonPluginXmlPath = dirPath.resolve("build-common-plugin.xml");

		Files.createDirectories(buildCommonPluginXmlPath);

		Assert.assertTrue(Files.exists(buildCommonPluginXmlPath));
	}

	private void _testDefaultInitWorkspaceInWorkspace(String subworkspaceBaseDirName) {
		File settingsFile = new File(_workspaceDir, "settings.gradle");

		Assert.assertFalse(settingsFile.exists());

		String[] args = {"--base", _workspacePath.toString(), "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(settingsFile.exists());

		Path subworkspacePath = _workspacePath.resolve(subworkspaceBaseDirName);

		String[] moreArgs = {
			"--base", String.valueOf(subworkspacePath), "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_workspaceDir, _extensionsDir, false, moreArgs);

		String errorMessage = bladeTestResults.getErrors();

		String expectedErrorMessage = "does not support initializing a workspace inside of another workspace";

		Assert.assertTrue(
			"should say '" + expectedErrorMessage + "', but says: " + errorMessage,
			errorMessage.contains(expectedErrorMessage));
	}

	private void _testInitWithLiferayVersion(String liferayVersion) throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", liferayVersion};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Path gradlePropertiesPath = _workspacePath.resolve("gradle.properties");

		String contents = new String(Files.readAllBytes(gradlePropertiesPath));

		Assert.assertTrue(contents, contents.contains(String.format("liferay.workspace.product=%s", liferayVersion)));
	}

	private void _verifyGradleBuild() throws Exception {
		_createBundle();

		Path projectPath = _workspacePath.resolve("modules");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(_workspacePath.toString(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		projectPath = projectPath.resolve("foo");

		GradleRunnerUtil.verifyBuildOutput(projectPath.toString(), "foo-1.0.0.jar");
	}

	private static final String _GRADLE_PLUGINS_WORKSPACE_VERSION = "15.0.0";

	private File _extensionsDir = null;
	private File _workspaceDir = null;
	private Path _workspacePath = null;

}