package com.liferay.blade.cli;

import aQute.lib.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author David Truong
 */
public class SamplesCommandTest {

	@Test
	public void listSamples() throws Exception {
		String [] args = {
			"samples"
		};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		new bladenofail(ps).run(args);

		String content = baos.toString();

		Assert.assertTrue(content.contains("lade.portlet.ds"));
	}

	@Test
	public void getSample() throws Exception {
		String [] args = {
			"samples",
			"-d",
			"generated/test",
			"blade.friendlyurl"
		};

		new bladenofail().run(args);

		File projectDir = IO.getFile("generated/test/blade.friendlyurl");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());
	}
}
