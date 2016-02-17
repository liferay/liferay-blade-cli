package com.liferay.blade.cli.gradle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class GradleToolingTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("deps.zip"), new File("bin_test/deps.zip"));
	}

	@Test
	public void testGetOutputFile() throws Exception {
		Set<File> files =
			GradleTooling.getOutputFiles(new File("bin_test"), new File("."));

		assertNotNull(files);
		assertEquals(2, files.size());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		Set<File> files =
			GradleTooling.getOutputFiles(new File("bin_test"), new File(".."));

		assertNotNull(files);
		assertEquals(17, files.size());
	}

	@Test
	public void testCheckLatestArchetypeVersionOffline() throws Exception {
		File file =
			GradleTooling.findLatestAvailableArtifact(
				"group: 'com.liferay', name: 'com.liferay.gradle.plugins.workspace', version: '1+', classifier: 'sources', ext: 'jar'",
				new File("test-localrepo").toURI().toURL().toExternalForm());

		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(
			file.getName().startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(file.getName().endsWith("sources.jar"));
		assertTrue(file.getName().contains("1.0.8"));
	}

	@Test
	public void testCheckLatestArchetypeVersionOnline() throws Exception {
		File file =
			GradleTooling.findLatestAvailableArtifact(
				"group: 'com.liferay', name: 'com.liferay.gradle.plugins.workspace', version: '1+', classifier: 'sources', ext: 'jar'");

		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(
			file.getName().startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(file.getName().endsWith("sources.jar"));
		assertTrue(file.getName().contains("1.0.9"));
	}
}