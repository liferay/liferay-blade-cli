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

package com.liferay.blade.cli.command;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class ConvertCommandTest {

	@Test
	public void testAll() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testUpgradePluginsSDKTo70");

		testdir.mkdirs();

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "-a"};

		new BladeTest().run(args);

		Assert.assertTrue(
			new File(
				testdir, "plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-api").exists());

		Assert.assertTrue(
			new File(
				testdir,
				"plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-service").exists());

		Assert.assertTrue(new File(testdir, "plugins-sdk-with-git/wars/sample-service-builder-portlet").exists());
	}

	@Test
	public void testMoveLayouttplToWars() throws Exception {
		File testdir = IO.getFile("build/testMoveLayouttplToWars");

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "1-2-1-columns-layouttpl"};

		new BladeTest().run(args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		Assert.assertTrue(layoutWar.exists());

		Assert.assertFalse(new File(layoutWar, "build.xml").exists());

		Assert.assertFalse(new File(layoutWar, "build.gradle").exists());

		Assert.assertFalse(new File(layoutWar, "docroot").exists());
	}

	@Test
	public void testMovePluginsToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMovePluginsToWars");

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-application-adapter-hook"};

		new BladeTest().run(args);

		File sampleExpandoHook = new File(projectDir, "wars/sample-application-adapter-hook");

		Assert.assertTrue(sampleExpandoHook.exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-application-adapter-hook").exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-servlet-filter-hook"};

		new BladeTest().run(args);

		File sampleServletFilterHook = new File(projectDir, "wars/sample-servlet-filter-hook");

		Assert.assertTrue(sampleServletFilterHook.exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-servlet-filter-hook").exists());
	}

	@Test
	public void testMoveThemesToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveThemesToWar");

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-minimal-theme"};

		new BladeTest().run(args);

		Assert.assertTrue(theme.exists());

		Assert.assertFalse(new File(theme, "build.xml").exists());

		Assert.assertTrue(new File(theme, "build.gradle").exists());

		Assert.assertFalse(new File(theme, "docroot").exists());

		Assert.assertTrue(new File(theme, "src/main/webapp").exists());

		Assert.assertFalse(new File(theme, "src/main/webapp/_diffs").exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme").exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-advanced-theme"};

		new BladeTest().run(args);

		File advancedTheme = new File(projectDir, "wars/sample-styled-advanced-theme");

		Assert.assertTrue(advancedTheme.exists());

		Assert.assertFalse(new File(advancedTheme, "build.xml").exists());

		Assert.assertTrue(new File(advancedTheme, "build.gradle").exists());

		Assert.assertFalse(new File(advancedTheme, "docroot").exists());

		Assert.assertTrue(new File(advancedTheme, "src/main/webapp").exists());

		Assert.assertFalse(new File(advancedTheme, "src/main/webapp/_diffs").exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-advanced-theme").exists());
	}

	@Test
	public void testReadIvyXml() throws Exception {
		File projectDir = _setupWorkspace("readIvyXml");

		String[] args = {"--base", projectDir.getPath(), "convert", "sample-dao-portlet"};

		new BladeTest().run(args);

		_contains(
			new File(projectDir, "wars/sample-dao-portlet/build.gradle"),
			".*compile group: 'c3p0', name: 'c3p0', version: '0.9.0.4'.*",
			".*compile group: 'mysql', name: 'mysql-connector-java', version: '5.0.7'.*");

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-tapestry-portlet"};

		new BladeTest().run(args);

		_contains(
			new File(projectDir, "wars/sample-tapestry-portlet/build.gradle"),
			".*compile group: 'hivemind', name: 'hivemind', version: '1.1'.*",
			".*compile group: 'hivemind', name: 'hivemind-lib', version: '1.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-annotations', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-framework', version: '4.1'.*",
			".*compile group: 'org.apache.tapestry', name: 'tapestry-portlet', version: '4.1'.*");

		Assert.assertFalse(new File(projectDir, "wars/sample-tapestry-portlet/ivy.xml").exists());
	}

	@Test
	public void testThemeDocrootBackup() throws Exception {
		File projectDir = _setupWorkspace("testThemeDocrootBackup");

		String[] args = {"--base", projectDir.getPath(), "convert", "-t", "sample-html4-theme"};

		new BladeTest().run(args);

		Assert.assertTrue(new File(projectDir, "wars/sample-html4-theme/docroot_backup/other/afile").exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _contains(File file, String... patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			_contains(content, pattern);
		}
	}

	private void _contains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Assert.assertTrue(pattern.matcher(content).matches());
	}

	private File _setupWorkspace(String name) throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/" + name);

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		Assert.assertTrue(new File(projectDir, "plugins-sdk").exists());

		return projectDir;
	}

}