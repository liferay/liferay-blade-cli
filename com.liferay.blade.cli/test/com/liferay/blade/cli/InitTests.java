package com.liferay.blade.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.lib.io.IO;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class InitTests {

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

		new blade().run(args);

		assertTrue(workspaceDir.exists());

		assertTrue(new File(workspaceDir, "build.gradle").exists());

		assertTrue(new File(workspaceDir, "gradlew").canExecute());

		assertTrue(new File(workspaceDir, "modules/apps").exists());
	}

	@Test
	public void defaultInitWorkspaceDirectoryHasFiles() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"-f",
				".*",
				"init",
		};

		assertTrue(new File(workspaceDir, "foo").createNewFile());

		new blade().run(args);

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

		new blade().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/gradlew").canExecute());

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

		new blade().run(args);

		assertTrue(new File(workspaceDir, "newproject/build.gradle").exists());

		assertTrue(new File(workspaceDir, "newproject/gradlew").canExecute());

		assertTrue(new File(workspaceDir, "newproject/modules/apps").exists());
	}

	@Test
	public void initWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String [] args = {
				"-b",
				"generated/test/workspace",
				"-f",
				".*",
				"init",
				"newproject"
		};

		assertTrue(new File(workspaceDir, "newproject").mkdirs());

		assertTrue(new File(workspaceDir, "newproject/foo").createNewFile());

		new blade().run(args);

		assertTrue(!(new File(workspaceDir, "newproject/build.gradle").exists()));
	}
}
