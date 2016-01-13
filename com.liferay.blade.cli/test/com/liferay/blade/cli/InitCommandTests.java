package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.lib.io.IO;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class InitCommandTests {

	final File workspaceDir = IO.getFile("generated/test/workspace");

	@Before
	public void deleteTmp() throws Exception {
		IO.delete(workspaceDir);
		if (!workspaceDir.mkdirs()) {
			fail("Unable to create workspace dir");
		}
	}

	@Test
	public void getWorkspaceZip() throws Exception {
		assertTrue(new InitCommand(new blade(), null).getWorkspaceZip().exists());
	}

	@Test
	public void defaultInitWorkspaceDirectoryEmpty() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
		};

		new bladenofail().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "modules/apps").exists());

		assertFalse(new File(workspaceDir, "com").exists());
	}

	@Test
	public void defaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
		};

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new bladenofail().run(args);

		assertTrue(!(new File(workspaceDir, "build.gradle").exists()));
	}

	@Test
	public void initWithNameWorkspaceNotExists() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
				"newproject"
		};

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules/apps").exists());
	}

	@Test
	public void initWithNameWorkspaceDirectoryEmpty() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
				"newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		new bladenofail().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/modules/apps").exists());
	}

	@Test
	public void initWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
				"newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		assertTrue(new File(workspaceDir, "newproject/foo").createNewFile());

		new bladenofail().run(args);

		assertTrue(!(new File(workspaceDir, "newproject/build.gradle").exists()));
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

	@Test
	public void initInPluginsSDKDirectory() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"init",
		};

		makeSDK(workspaceDir);

		new bladenofail().run(args);

		assertTrue((new File(workspaceDir, "build.gradle").exists()));

		assertTrue((new File(workspaceDir, "modules").exists()));

		assertTrue((new File(workspaceDir, "themes").exists()));

		assertFalse((new File(workspaceDir, "portlets").exists()));

		assertFalse((new File(workspaceDir, "hooks").exists()));

		assertFalse((new File(workspaceDir, "build.properties").exists()));

		assertFalse((new File(workspaceDir, "build.xml").exists()));

		assertTrue((new File(workspaceDir, "plugins-sdk/build.properties").exists()));

		assertTrue((new File(workspaceDir, "plugins-sdk/build.xml").exists()));
	}
}
