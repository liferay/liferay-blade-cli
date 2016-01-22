package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;

import aQute.lib.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

		createWorkspace();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		new bladenofail(ps).run(args);

		String content = baos.toString();

		Assert.assertTrue(content.contains("compass-theme"));
	}

	@Test
	public void testMigrateCompassTheme() throws Exception {
		String[] args = {
			"-b", "generated/test/workspace", "migrateTheme", "-a"
		};

		File workspace = createWorkspace();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		new bladenofail(ps).run(args);

		File compassTheme = new File(workspace, "themes/compass-theme");

		Assert.assertTrue(compassTheme.exists());

		File packageJson = new File(compassTheme, "package.json");

		String json = new String(Files.readAllBytes(packageJson.toPath()));

		Assert.assertTrue(json.contains("\"supportCompass\": true"));

		File nonCompassTheme = new File(workspace, "themes/non-compass-theme");

		Assert.assertTrue(compassTheme.exists());

		packageJson = new File(nonCompassTheme, "package.json");

		json = new String(Files.readAllBytes(packageJson.toPath()));

		Assert.assertTrue(json.contains("\"supportCompass\": false"));
	}

	private void createTheme(File workspace, String themeName, boolean compass)
		throws Exception {

		File theme = new File(workspace, "plugins-sdk/themes/" + themeName);

		File diffs = new File(theme, "/docroot/_diffs");

		diffs.mkdirs();

		String css = "";

		if (compass) {
			css = "@import \"compass\";";
		}

		File customCss = new File(diffs, "custom.css");

		Files.write(customCss.toPath(), css.getBytes());

		File webInf = new File(theme, "/docroot/WEB-INF/");

		webInf.mkdirs();

		String xml = "";

		File lookAndFeelXml = new File(webInf, "liferay-look-and-feel.xml");

		Files.write(lookAndFeelXml.toPath(), xml.getBytes());

		String properties = "liferay-versions=7.0.0+";

		File liferayPluginPackage = new File(
			webInf, "liferay-plugin-package.properties");

		Files.write(liferayPluginPackage.toPath(), properties.getBytes());
	}

	private File createWorkspace() throws Exception {
		File workspace = new File("generated/test/workspace");
		File themesDir = new File(workspace, "themes");

		themesDir.mkdirs();

		String settings = "apply plugin: \"com.liferay.workspace\"";

		File settingsFile = new File(workspace, "settings.gradle");

		Files.write(settingsFile.toPath(), settings.getBytes());

		createTheme(workspace, "compass-theme", true);

		createTheme(workspace, "non-compass-theme", false);

		return workspace;
	}

}