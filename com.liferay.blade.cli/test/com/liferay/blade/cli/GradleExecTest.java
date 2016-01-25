package com.liferay.blade.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author David Truong
 */
public class GradleExecTest {

	@Test
	public void testGradleWrapper() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		blade blade = new bladenofail(ps);

		GradleExec gradleExec = new GradleExec(blade);

		int errorCode = gradleExec.executeGradleCommand("tasks");

		Assert.assertTrue(errorCode == 0);
	}
}