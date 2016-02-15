package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AgentCommandTest {

	private final File cacheDir = IO.getFile("generated/cache");

	@Before
	public void setUp() throws Exception {
		if (cacheDir.exists()) {
			IO.delete(cacheDir);
			assertFalse(cacheDir.exists());
		}
	}

	@Test
	public void testGetAgentJarDefaultURL() throws Exception {
		File agentJar =
			new AgentCommand(null, null).getAgentJar(cacheDir, null, null);

		assertNotNull(agentJar);
		assertTrue(agentJar.exists());
	}

}