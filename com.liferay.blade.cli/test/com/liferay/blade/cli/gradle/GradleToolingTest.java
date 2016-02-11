package com.liferay.blade.cli.gradle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		assertEquals("buildfiles", files.iterator().next().getName());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		Set<File> files =
			GradleTooling.getOutputFiles(new File("bin_test"), new File(".."));

		assertNotNull(files);
		assertEquals(17, files.size());
	}

}