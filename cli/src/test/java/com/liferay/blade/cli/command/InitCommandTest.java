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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.gradle.testkit.runner.BuildTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTest.BladeTestBuilder;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.WorkspaceUtil;

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

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

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);
		
		File gitdir = new File(projectDir, ".git");

		Assert.assertTrue(gitdir.exists());

		File oldGitIgnore = new File(projectDir, "plugins-sdk/.gitignore");

		Assert.assertTrue(oldGitIgnore.exists());
	}

	@Test
	public void testBladeInitEmptyDirectory() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		String[] args = {"--base", emptyDir.getPath(), "init"};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		boolean workspace = WorkspaceUtil.isWorkspace(emptyDir);

		Assert.assertTrue(workspace);
	}

	@Test
	public void testBladeInitEmptyDirectoryHandleDot() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		String pathStringToTest = new File(emptyDir.getPath(), ".").getAbsolutePath();

		String[] args = {"--base", pathStringToTest, "init"};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		boolean workspace = WorkspaceUtil.isWorkspace(emptyDir);

		Assert.assertTrue(workspace);
	}

	@Test
	public void testBladeInitEmptyDirectoryHandleTwoDots() throws Exception {
		File emptyDir = temporaryFolder.newFolder();

		String pathStringToTest = new File(emptyDir.getPath(), ".").getAbsolutePath();

		String[] args = {"--base", pathStringToTest, "init", "."};

		BladeTest bladeTest = _getBladeTestCustomWorkspace(emptyDir);

		bladeTest.run(args);

		boolean workspace = WorkspaceUtil.isWorkspace(emptyDir);

		Assert.assertTrue(workspace);
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

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

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

		String basePath = new File(tempDir.getPath()).getAbsolutePath();

		String[] args = {"--base", basePath, "init", "-P", "myprofile"};

		TestUtil.runBlade(tempDir, _extensionsDir, args);

		Assert.assertTrue(WorkspaceUtil.isWorkspace(tempDir));

		File settingsFile = new File(basePath, ".blade/settings.properties");

		Properties props = new Properties();

		try (FileInputStream fileInputStream = new FileInputStream(settingsFile)) {
			props.load(fileInputStream);

			String profileName = props.getProperty("profile.name");

			Assert.assertEquals("myprofile", profileName);
		}
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);
		
		Assert.assertTrue(_workspaceDir.exists());

		Assert.assertTrue(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertFalse(new File(_workspaceDir, "com").exists());

		_verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		Assert.assertTrue(new File(_workspaceDir, "foo").createNewFile());

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);
		
		Assert.assertTrue(_workspaceDir.exists());

		Assert.assertTrue(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		_verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryIsWorkspace() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "firstWorkspace"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		File firstWorkspace = new File(_workspaceDir, "firstWorkspace");

		String[] moreArgs = {"--base", firstWorkspace.getPath(), "init", "nextWorkspace"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		Assert.assertTrue(firstWorkspace.getName() + " should exist but does not.", firstWorkspace.exists());

		File nextWorkspace = new File(_workspaceDir + File.separator + "firstWorkspace", "nextWorkspace");

		Assert.assertFalse(nextWorkspace.getName() + " should not exist, but it does.", nextWorkspace.exists());

		try {
			BladeTestResults bladeTestResults = TestUtil.runBlade(firstWorkspace, _extensionsDir, moreArgs);

			Assert.assertFalse(
				"There should be no results from the command, but bladeTestResults != null)", bladeTestResults != null);
		}
		catch (AssertionError e) {
			Assert.assertTrue(
				"should say 'does not support initializing a workspace inside of another workspace', but says: " +
					e.getMessage(),
				e.getMessage().contains("does not support initializing a workspace inside of another workspace"));
		}
	}

	@Test
	public void testInitCommandGradleOption() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "gradle", "gradleworkspace"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		File gradleWorkspace = new File(_workspaceDir, "gradleworkspace");

		Assert.assertTrue(gradleWorkspace.exists());

		Assert.assertFalse(new File(gradleWorkspace, "pom.xml").exists());

		Assert.assertTrue(new File(gradleWorkspace, "build.gradle").exists());
	}

	@Test
	public void testInitInPluginsSDKDirectory() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-u"};

		_makeSDK(_workspaceDir);

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertTrue(new File(_workspaceDir, "themes").exists());

		Assert.assertFalse(new File(_workspaceDir, "portlets").exists());

		Assert.assertFalse(new File(_workspaceDir, "hooks").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "plugins-sdk/build.properties").exists());

		Assert.assertTrue(new File(_workspaceDir, "plugins-sdk/build.xml").exists());
	}

	@Test
	public void testInitWithLiferayVersion70() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "7.0"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.0.6-ga7"));

		String properties = new String(
			Files.readAllBytes(new File(_workspaceDir, ".blade/settings.properties").toPath()));

		Assert.assertTrue(properties, properties.contains("liferay.version.default=7.0"));
	}

	@Test
	public void testInitWithLiferayVersion71() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "7.1"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.1.1-ga2"));

		String properties = new String(
			Files.readAllBytes(new File(_workspaceDir, ".blade/settings.properties").toPath()));

		Assert.assertTrue(properties, properties.contains("liferay.version.default=7.1"));
	}

	@Test
	public void testInitWithLiferayVersionDefault() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.1.1-ga2"));

		String properties = new String(
			Files.readAllBytes(new File(_workspaceDir, ".blade/settings.properties").toPath()));

		Assert.assertTrue(properties, properties.contains("liferay.version.default=7.1"));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());
		
		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(new File(newproject, "build.gradle").exists());

		Assert.assertTrue(new File(newproject, "modules").exists());

		String contents = new String(Files.readAllBytes(new File(newproject, "settings.gradle").toPath()));

		Assert.assertTrue(contents, contents.contains("1.10"));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "newproject"};

		Assert.assertTrue(new File(_workspaceDir, "newproject").mkdirs());

		Assert.assertTrue(new File(_workspaceDir, "newproject/foo").createNewFile());

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		Assert.assertFalse(new File(_workspaceDir, "newproject/build.gradle").exists());
	}

	@Test
	public void testInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "newproject"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(new File(_workspaceDir, "newproject/build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "newproject/modules").exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _createBundle() throws Exception {
		String projectPath = new File(_workspaceDir, "modules").getAbsolutePath();

		String[] args = {"create", "-t", "mvc-portlet", "-d", projectPath, "foo"};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		File file = new File(projectPath, "/foo");
		File bndFile = new File(projectPath, "/foo/bnd.bnd");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(bndFile.exists());
	}
	
	private BladeTest _getBladeTestCustomWorkspace(File workspaceDir) {
		BladeTestBuilder builder = BladeTest.builder();

		builder.setExtensionsDir(_extensionsDir.toPath());
		builder.setSettingsDir(workspaceDir.toPath());

		return builder.build();
	}

	private void _makeSDK(File dir) throws IOException {
		Assert.assertTrue(new File(dir, "portlets").mkdirs());
		Assert.assertTrue(new File(dir, "hooks").mkdirs());
		Assert.assertTrue(new File(dir, "layouttpl").mkdirs());
		Assert.assertTrue(new File(dir, "themes").mkdirs());
		Assert.assertTrue(new File(dir, "build.properties").createNewFile());
		Assert.assertTrue(new File(dir, "build.xml").createNewFile());
		Assert.assertTrue(new File(dir, "build-common.xml").createNewFile());
		Assert.assertTrue(new File(dir, "build-common-plugin.xml").createNewFile());
	}

	private void _verifyGradleBuild() throws Exception {
		_createBundle();

		String projectPath = _workspaceDir.getPath() + "/modules";

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(_workspaceDir.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
	}

	private File _extensionsDir = null;
	private File _workspaceDir = null;

}