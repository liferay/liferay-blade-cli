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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.lib.io.IO;

import java.io.File;
import java.io.IOException;

import org.gradle.testkit.runner.BuildTask;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class InitCommandTest {

	@Before
	public void setUp() throws Exception {
		IO.delete(workspaceDir);

		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		assertFalse(new File(workspaceDir, "com").exists());

		if (SysProps.verifyBuilds) {
			verifyGradleBuild();
		}
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init"};

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertFalse(new File(workspaceDir, "build.gradle").exists());
	}

	@Test
	public void testDefaultInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"-b", workspaceDir.getPath(), "init", "-f"};

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules").exists());

		if (SysProps.verifyBuilds) {
			verifyGradleBuild();
		}
	}

	@Test
	public void testGetWorkspaceZip() throws Exception {
		File workspaceZip = new InitCommand(new blade(), null).getWorkspaceZip();

		assertTrue(workspaceZip.exists());

		String workspaceName = workspaceZip.getName();

		assertTrue(workspaceName.startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(workspaceName.endsWith("sources.jar"));
		assertTrue(workspaceName.contains(InitCommand.WORKSPACE_VERSION));
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
			"-b", workspaceDir.getPath(), "init", "newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules").exists());
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

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules").exists());
	}

	private void createBundle(File workspaceDir) throws Exception {
		String projectPath = "generated/test/workspace/modules";

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

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "build");

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

	private final File workspaceDir = IO.getFile("generated/test/workspace");

}