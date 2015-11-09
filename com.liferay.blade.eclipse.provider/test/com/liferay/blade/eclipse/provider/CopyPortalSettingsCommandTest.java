package com.liferay.blade.eclipse.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import com.liferay.blade.eclipse.provider.cmds.CopyPortalSettingsCommand;

import aQute.lib.io.IO;

/**
 * @author Gregory Amerson
 */
public class CopyPortalSettingsCommandTest {

	final File dest = new File("generated/copyPortalSettings");

	@Before
	public void cleanUpFiles() {
		IO.delete(dest);
	}

	@Test
	public void copyOnlyInterestingPropertiesFiles() throws Exception {
		Files.createDirectories(dest.toPath());

		File src = new File("tests/copyPortalSettings");

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

}
