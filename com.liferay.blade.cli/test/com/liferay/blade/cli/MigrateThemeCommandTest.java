package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;

import aQute.lib.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Truong
 */
public class MigrateThemeCommandTest {

	@Before
	public void setUp() throws Exception {
		File testdir = IO.getFile("generated/test");

		if (testdir.exists()) {
			IO.delete(testdir);
			assertFalse(testdir.exists());
		}
	}

	@Test
	public void testListThemes() throws Exception {
		String[] args = {"-b", "generated/test/workspace", "migrateTheme"};

		makeWorkspace();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		new bladenofail(ps).run(args);

		String content = baos.toString();

		Assert.assertTrue(content.contains("compass-theme"));
	}

	@Test
	public void testMigrateCompassTheme() throws Exception {
		String[] args = {
			"-b", "generated/test/workspace", "migrateTheme", "compass-theme"
		};

		makeWorkspace();

		new bladenofail().run(args);

		File theme = new File("generated/test/workspace/themes/compass-theme");

		Assert.assertTrue(theme.exists());
	}

	private void makeWorkspace() throws IOException {
		File workspace = new File("generated/test/workspace");
		File themesDir = new File(workspace, "themes");

		themesDir.mkdirs();

		String settings = "apply plugin: \"com.liferay.workspace\"";

		File settingsFile = new File(workspace, "settings.gradle");

		Files.write(settingsFile.toPath(), settings.getBytes());

		File diffs = new File(
			workspace, "plugins-sdk/themes/compass-theme/docroot/_diffs");

		diffs.mkdirs();

		File webInf = new File(
			workspace, "plugins-sdk/themes/compass-theme/docroot/WEB-INF/");

		webInf.mkdirs();

		String xml = "";

		File lookAndFeelXml = new File(webInf, "liferay-look-and-feel.xml");

		Files.write(lookAndFeelXml.toPath(), xml.getBytes());

		String properties = "liferay-versions=7.0.0+";

		File liferayPluginPackage = new File(
			webInf, "liferay-plugin-package.properties");

		Files.write(liferayPluginPackage.toPath(), properties.getBytes());
	}

}