/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ConvertCommandTest {

	@After
	public void cleanUp() throws Exception {
		IO.delete(workspaceDir.getParentFile());
	}

	@Test
	public void testAll() throws Exception {
		File testdir = IO.getFile("generated/testUpgradePluginsSDKTo70");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		testdir.mkdirs();

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		args = new String[] {"-b", projectDir.getPath(), "convert", "-a"};

		new bladenofail().run(args);

		assertTrue(new File(testdir, "plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-api").exists());

		assertTrue(new File(testdir, "plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-service").exists());

		assertTrue(new File(testdir, "plugins-sdk-with-git/wars/sample-service-builder-portlet").exists());
	}

	@Test
	public void testMoveLayouttplToWars() throws Exception {
		File testdir = IO.getFile("generated/testMoveLayouttplToWars");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		args = new String[] {"-b", projectDir.getPath(), "convert", "1-2-1-columns-layouttpl"};

		new bladenofail().run(args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		assertTrue(layoutWar.exists());

		assertFalse(new File(layoutWar, "build.xml").exists());

		assertFalse(new File(layoutWar, "build.gradle").exists());

		assertFalse(new File(layoutWar, "docroot").exists());
	}

	@Test
	public void testMoveThemesToWars() throws Exception {
		File testdir = IO.getFile("generated/testMoveThemesToWar");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		args = new String[] {"-b", projectDir.getPath(), "convert", "-t", "sample-styled-minimal-theme"};

		new bladenofail().run(args);

		assertTrue(theme.exists());

		assertFalse(new File(theme, "build.xml").exists());

		assertTrue(new File(theme, "build.gradle").exists());

		assertFalse(new File(theme, "docroot").exists());

		assertTrue(new File(theme, "src/main/webapp").exists());

		assertFalse(new File(theme, "src/main/webapp/_diffs").exists());

		assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme").exists());

		args = new String[] {"-b", projectDir.getPath(), "convert", "-t", "sample-styled-advanced-theme"};

		new bladenofail().run(args);

		File advancedTheme = new File(projectDir, "wars/sample-styled-advanced-theme");

		assertTrue(advancedTheme.exists());

		assertFalse(new File(advancedTheme, "build.xml").exists());

		assertTrue(new File(advancedTheme, "build.gradle").exists());

		assertFalse(new File(advancedTheme, "docroot").exists());

		assertTrue(new File(advancedTheme, "src/main/webapp").exists());

		assertFalse(new File(advancedTheme, "src/main/webapp/_diffs").exists());

		assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-advanced-theme").exists());
	}

	@Test
	public void testMovePluginsToWars() throws Exception {
		File testdir = IO.getFile("generated/testMovePluginsToWars");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		args = new String[] {"-b", projectDir.getPath(), "convert", "sample-application-adapter-hook"};

		new bladenofail().run(args);

		File sampleExpandoHook = new File(projectDir, "wars/sample-application-adapter-hook");

		assertTrue(sampleExpandoHook.exists());

		assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-application-adapter-hook").exists());

		args = new String[] {"-b", projectDir.getPath(), "convert", "sample-servlet-filter-hook"};

		new bladenofail().run(args);

		File sampleServletFilterHook = new File(projectDir, "wars/sample-servlet-filter-hook");

		assertTrue(sampleServletFilterHook.exists());

		assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-servlet-filter-hook").exists());
	}

	private File setupWorkspace(String name) throws Exception {
		File testdir = IO.getFile("generated/" + name);

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		assertTrue(new File(projectDir, "plugins-sdk").exists());

		return projectDir;
	}

	@Test
	public void testThemeDocrootBackup() throws Exception {
		File projectDir = setupWorkspace("testThemeDocrootBackup");

		String[] args = {"-b", projectDir.getPath(), "convert", "-t", "sample-html4-theme"};

		new bladenofail().run(args);

		assertTrue(new File(projectDir, "wars/sample-html4-theme/docroot_backup/other/afile").exists());
	}

	@Test
	public void testReadIvyXml() throws Exception {
		File projectDir = setupWorkspace("readIvyXml");

		String[] args = {"-b", projectDir.getPath(), "convert", "sample-dao-portlet"};

		new bladenofail().run(args);

		contains(
			new File(projectDir, "wars/sample-dao-portlet/build.gradle"),
			".*compile group: 'c3p0', name: 'c3p0', version: '0.9.0.4'.*",
			".*compile group: 'mysql', name: 'mysql-connector-java', version: '5.0.7'.*");

		args = new String[] {"-b", projectDir.getPath(), "convert", "sample-tapestry-portlet"};

		new bladenofail().run(args);

		contains(
			new File(projectDir, "wars/sample-tapestry-portlet/build.gradle"),
			".*compile group: 'hivemind', name: 'hivemind', version: '1.1'.*",
			".*compile group: 'hivemind', name: 'hivemind-lib', version: '1.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-annotations', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-framework', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-portlet', version: '4.1'.*");

		assertFalse(new File(projectDir, "wars/sample-tapestry-portlet/ivy.xml").exists());
	}

	private void contains(File file, String... patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			contains(content, pattern);
		}
	}

	private void contains(String content, String pattern) throws Exception {
		assertTrue(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}
	private final File workspaceDir = IO.getFile("generated/test/workspace");

}