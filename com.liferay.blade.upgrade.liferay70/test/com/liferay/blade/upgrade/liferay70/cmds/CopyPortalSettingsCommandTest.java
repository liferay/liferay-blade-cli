package com.liferay.blade.upgrade.liferay70.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import aQute.lib.io.IO;

/**
 * @author Gregory Amerson
 */
public class CopyPortalSettingsCommandTest {

	final File dest = new File("generated/copyPortalSettings");
	final File src = new File("projects/copyPortalSettings");

	@Before
	public void cleanUpFiles() throws IOException {
		IO.delete(dest);
		Files.createDirectories(dest.toPath());
	}

	@Test
	public void copyOnlyInterestingPropertiesFiles() throws Exception {
		Object retval =
			new CopyPortalSettingsCommand().copyPortalSettings(src, dest);

		assertNull(retval);

		File[] expected = {
			new File(dest, "portal-ext.properties"),
			new File(dest, "portal-liferay.com.properties"),
			new File(dest, "portal-setup-wizard.properties"),
			new File(dest, "system-ext.properties"),
		};

		File[] actual = dest.listFiles();

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

	@Test
	public void pullParmetersFromMap() throws Exception {
		Map<String, File> parameters = new HashMap<>();
		parameters.put(CopyPortalSettingsCommand.PARAM_SOURCE, src);
		parameters.put(CopyPortalSettingsCommand.PARAM_DEST, dest);

		Object retval =
			new CopyPortalSettingsCommand().execute(parameters);

		assertNull(retval);

		File[] expected = {
			new File(dest, "portal-ext.properties"),
			new File(dest, "portal-liferay.com.properties"),
			new File(dest, "portal-setup-wizard.properties"),
			new File(dest, "system-ext.properties"),
		};

		File[] actual = dest.listFiles();

		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

}
