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

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.MavenRunnerUtil;
import com.liferay.blade.cli.util.BladeUtil;

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

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");
	}

	@Test
	public void testBladeInitDontLoseGitDirectory() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testBladeInitDontLoseGitDirectory");

		testdir.mkdirs();

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		File gitdir = IO.getFile(projectDir, ".git");

		Assert.assertTrue(gitdir.exists());

		File oldGitIgnore = IO.getFile(projectDir, "plugins-sdk/.gitignore");

		Assert.assertTrue(oldGitIgnore.exists());
	}

	@Test
	public void testBladeInitUpgradePluginsSDKTo70() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testUpgradePluginsSDKTo70");

		testdir.mkdirs();

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		File buildProperties = new File(projectDir, "plugins-sdk/build.properties");

		Properties props = new Properties();

		props.load(new FileInputStream(buildProperties));

		String version = props.getProperty("lp.version");

		Assert.assertEquals("7.0.0", version);
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		new BladeTest().run(args);

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

		new BladeTest().run(args);

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f"};

		new BladeTest().run(args);

		Assert.assertTrue(_workspaceDir.exists());

		Assert.assertTrue(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		_verifyGradleBuild();
	}

	@Test
	public void testInitCommandGradleOption() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "gradle", "gradleworkspace"};

		new BladeTest().run(args);

		File gradleWorkspace = new File(_workspaceDir, "gradleworkspace");

		Assert.assertTrue(gradleWorkspace.exists());

		Assert.assertFalse(new File(gradleWorkspace, "pom.xml").exists());

		Assert.assertTrue(new File(gradleWorkspace, "build.gradle").exists());
	}

	@Test
	public void testInitInPluginsSDKDirectory() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-u"};

		_makeSDK(_workspaceDir);

		new BladeTest().run(args);

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

		new BladeTest().run(args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.0.6-ga7"));
	}

	@Test
	public void testInitWithLiferayVersion71() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "7.1"};

		new BladeTest().run(args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.1.0-ga1"));
	}

	@Test
	public void testInitWithLiferayVersionDefault() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init"};

		new BladeTest().run(args);

		String contents = new String(Files.readAllBytes(new File(_workspaceDir, "gradle.properties").toPath()));

		Assert.assertTrue(contents, contents.contains("7.1.0-ga1"));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		new BladeTest().run(args);

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

		new BladeTest().run(args);

		Assert.assertFalse(new File(_workspaceDir, "newproject/build.gradle").exists());
	}

	@Test
	public void testInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "newproject"};

		new BladeTest().run(args);

		Assert.assertTrue(new File(_workspaceDir, "newproject/build.gradle").exists());

		Assert.assertTrue(new File(_workspaceDir, "newproject/modules").exists());
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		Assert.assertTrue(new File(newproject, "pom.xml").exists());

		Assert.assertTrue(new File(newproject, "modules").exists());

		String contents = new String(Files.readAllBytes(new File(newproject, "pom.xml").toPath()));

		Assert.assertTrue(contents, contents.contains("3.2.1"));

		File metadataFile = new File(_workspaceDir, "newproject/.blade/settings.properties");

		Assert.assertTrue(metadataFile.exists());

		BladeSettings bladeSettings = bladeTest.getSettings();

		Assert.assertEquals("maven", bladeSettings.getProfileName());
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven", "newproject"};

		Assert.assertTrue(new File(_workspaceDir, "newproject").mkdirs());

		Assert.assertTrue(new File(_workspaceDir, "newproject/foo").createNewFile());

		new BladeTest().run(args);

		Assert.assertFalse(new File(_workspaceDir, "newproject/pom.xml").exists());
	}

	@Test
	public void testMavenInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven", "newproject"};

		new BladeTest().run(args);

		Assert.assertTrue(new File(_workspaceDir, "newproject/pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "newproject/modules").exists());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven"};

		new BladeTest().run(args);

		Assert.assertTrue(new File(_workspaceDir, "pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle-local.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "settings.gradle").exists());

		_verifyMavenBuild();
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven"};

		Assert.assertTrue(new File(_workspaceDir, "foo").createNewFile());

		new BladeTest().run(args);

		Assert.assertFalse(new File(_workspaceDir, "pom.xml").exists());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven"};

		new BladeTest().run(args);

		Assert.assertTrue(_workspaceDir.exists());

		Assert.assertTrue(new File(_workspaceDir, "pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle-local.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "settings.gradle").exists());

		_verifyMavenBuild();
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _createBundle() throws Exception {
		String projectPath = new File(_workspaceDir, "modules").getAbsolutePath();

		String[] args = {"create", "-t", "mvc-portlet", "-d", projectPath, "foo"};

		new BladeTest().run(args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(bndFile.exists());
	}

	private void _createMavenBundle() throws Exception {
		String projectPath = new File(_workspaceDir, "modules").getAbsolutePath();

		String[] args = {"create", "-t", "mvc-portlet", "-d", projectPath, "-b", "maven", "foo"};

		new BladeTest().run(args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(bndFile.exists());
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

	private void _verifyMavenBuild() throws Exception {
		_createMavenBundle();

		String projectPath = _workspaceDir.getPath() + "/modules/foo";

		MavenRunnerUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
	}

	private File _workspaceDir = null;

}