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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.lib.io.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.gradle.testkit.runner.BuildTask;
import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@After
	public void cleanUp() throws Exception {
		IO.delete(workspaceDir);
	}

	@Test
	public void testBladeInitUpgradePluginsSDKTo70() throws Exception {
		File testdir = IO.getFile("build/testUpgradePluginsSDKTo70");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		testdir.mkdirs();

		Util.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		File buildProperties = new File(projectDir, "plugins-sdk/build.properties");

		Properties props = new Properties();

		props.load(new FileInputStream(buildProperties));

		String version = props.getProperty("lp.version");

		assertEquals("7.0.0", version);
	}

	@Test
	public void testBladeInitDontLoseGitDirectory() throws Exception {
		File testdir = IO.getFile("build/testBladeInitDontLoseGitDirectory");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		testdir.mkdirs();

		Util.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		File gitdir = IO.getFile(projectDir, ".git");

		assertTrue(gitdir.exists());

		File oldGitIgnore = IO.getFile(projectDir, "plugins-sdk/.gitignore");

		assertTrue(oldGitIgnore.exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		assertFalse(new File(workspaceDir, "com").exists());

		verifyGradleBuild();
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertFalse(new File(workspaceDir, "build.gradle").exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init", "-f"};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		verifyGradleBuild();
	}

	@Test
	public void testInitInPluginsSDKDirectory() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init", "-u"};

		makeSDK(workspaceDir);

		new bladenofail().run(args);

		assertTrue((new File(workspaceDir, "build.gradle").exists()));

		assertTrue((new File(workspaceDir, "modules").exists()));

		assertTrue((new File(workspaceDir, "themes").exists()));

		assertFalse((new File(workspaceDir, "portlets").exists()));

		assertFalse((new File(workspaceDir, "hooks").exists()));

		assertFalse((new File(workspaceDir, "build.properties").exists()));

		assertFalse((new File(workspaceDir, "build.xml").exists()));

		assertTrue(
			(new File(workspaceDir, "plugins-sdk/build.properties").exists()));

		assertTrue((new File(workspaceDir, "plugins-sdk/build.xml").exists()));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "-f", "newproject"
		};

		File newproject = new File(workspaceDir, "newproject");

		assertTrue(newproject.mkdirs());

		new bladenofail().run(args);

		assertTrue(new File(newproject, "build.gradle").exists());

		assertTrue(new File(newproject, "modules").exists());

		String contents = new String(Files.readAllBytes(new File(newproject, "settings.gradle").toPath()));

		assertTrue(contents, contents.contains("1.7.1"));
	}

	@Test
	public void testInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		assertTrue(new File(workspaceDir, "newproject/foo").createNewFile());

		new bladenofail().run(args);

		assertFalse(
			new File(workspaceDir, "newproject/build.gradle").exists());
	}

	@Test
	public void testInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {
			"-b", workspaceDir.getPath(), "init", "newproject"
		};

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules").exists());
	}

	private void createBundle(File workspaceDir) throws Exception {
		String projectPath = "build/test/workspace/modules";

		String[] args = {"create", "-d", projectPath, "foo"};

		new bladenofail().run(args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		assertTrue(file.exists());

		assertTrue(bndFile.exists());
	}

	private void verifyGradleBuild() throws Exception{
		createBundle(workspaceDir);

		String projectPath = workspaceDir.getPath() + "/modules";

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
	}

	private void makeSDK(File dir) throws IOException {
		assertTrue(new File(dir, "portlets").mkdirs());
		assertTrue(new File(dir, "hooks").mkdirs());
		assertTrue(new File(dir, "layouttpl").mkdirs());
		assertTrue(new File(dir, "themes").mkdirs());
		assertTrue(new File(dir, "build.properties").createNewFile());
		assertTrue(new File(dir, "build.xml").createNewFile());
		assertTrue(new File(dir, "build-common.xml").createNewFile());
		assertTrue(new File(dir, "build-common-plugin.xml").createNewFile());
	}

	private final File workspaceDir = IO.getFile("build/test/workspace");

}