package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;

import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

/**
 * @author David Truong
 */
public class UtilTest {

	@Before
	public void setUp() throws Exception {
		File testdir = IO.getFile("generated/test");

		if (testdir.exists()) {
			IO.delete(testdir);
			assertFalse(testdir.exists());
		}
	}

	@Test
	public void testIsWorkspace1() throws Exception {
		File workspace = new File("generated/test/workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: \"com.liferay.workspace\"";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		assertTrue(Util.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace2() throws Exception {
		File workspace = new File("generated/test/workspace");

		workspace.mkdirs();

		File gradleFile = new File(workspace, "settings.gradle");

		String plugin = "apply plugin: 'com.liferay.workspace'";

		Files.write(gradleFile.toPath(), plugin.getBytes());

		assertTrue(Util.isWorkspace(workspace));
	}

	@Test
	public void testIsWorkspace3() throws Exception {
		File workspace = new File("generated/test/workspace");

		workspace.mkdirs();

		File buildFile = new File(workspace, "build.gradle");

		File settingsFile = new File(workspace, "settings.gradle");

		settingsFile.createNewFile();

		String plugin = "\napply   plugin:   \n\"com.liferay.workspace\"";

		Files.write(buildFile.toPath(), plugin.getBytes());

		assertTrue(Util.isWorkspace(workspace));
	}

}