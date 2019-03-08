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

import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class ConvertCommandTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testAll() throws Exception {
		File testdir = new File(_rootDir, "build/testUpgradePluginsSDKTo70");

		testdir.mkdirs();

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "-a"};

		TestUtil.runBlade(_rootDir, _extensionsDir, false, args);

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
		File testdir = new File("build/testMoveLayouttplToWars");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "1-2-1-columns-layouttpl"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		Assert.assertTrue(layoutWar.exists());

		Assert.assertFalse(new File(layoutWar, "build.xml").exists());

		Assert.assertFalse(new File(layoutWar, "build.gradle").exists());

		Assert.assertFalse(new File(layoutWar, "docroot").exists());
	}

	@Test
	public void testMovePluginsToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMovePluginsToWars");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-application-adapter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File sampleExpandoHook = new File(projectDir, "wars/sample-application-adapter-hook");

		Assert.assertTrue(sampleExpandoHook.exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-application-adapter-hook").exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-servlet-filter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File sampleServletFilterHook = new File(projectDir, "wars/sample-servlet-filter-hook");

		Assert.assertTrue(sampleServletFilterHook.exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/hooks/sample-servlet-filter-hook").exists());
	}

	@Test
	public void testMoveThemesToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveThemesToWar");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-minimal-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(theme.exists());

		Assert.assertFalse(new File(theme, "build.xml").exists());

		Assert.assertTrue(new File(theme, "build.gradle").exists());

		Assert.assertFalse(new File(theme, "docroot").exists());

		Assert.assertTrue(new File(theme, "src/main/webapp").exists());

		Assert.assertFalse(new File(theme, "src/main/webapp/_diffs").exists());

		Assert.assertFalse(new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme").exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-advanced-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

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

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		_contains(
			new File(projectDir, "wars/sample-dao-portlet/build.gradle"),
			".*compile group: 'c3p0', name: 'c3p0', version: '0.9.0.4'.*",
			".*compile group: 'mysql', name: 'mysql-connector-java', version: '5.0.7'.*");

		args = new String[] {"--base", projectDir.getPath(), "convert", "sample-tapestry-portlet"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

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
	public void testReadLiferayPlguinPackageProperties() throws Exception {
		File projectDir = _setupWorkspace("readLiferayPlguinPackageProperties");

		String[] args = {"--base", projectDir.getPath(), "convert", "sample-hibernate-portlet"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		_contains(
			new File(projectDir, "wars/sample-hibernate-portlet/build.gradle"),
			".*compile group: 'commons-collections', name: 'commons-collections', version: '3.2.2'.*",
			".*compile group: 'commons-httpclient', name: 'commons-httpclient', version: '3.1'.*",
			".*compile group: 'dom4j', name: 'dom4j', version: '1.6.1'.*",
			".*compile group: 'javax.xml.soap', name: 'saaj-api', version: '1.3'.*",
			".*compile group: 'org.slf4j', name: 'slf4j-api', version: '1.7.2'.*");

		_notContains(
			new File(projectDir, "wars/sample-hibernate-portlet/build.gradle"),
			".*antlr2.*", ".*hibernate3.*", ".*util-slf4j.*");
	}

	@Test
	public void testSourceParameter() throws Exception {
		File testdir = new File(_rootDir, "plugins-sdk-alternative-location");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		File workspaceParent = new File(_rootDir, "workspace-parent");

		String[] args = {"--base", workspaceParent.getPath(), "init", "ws"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File workspaceDir = new File(workspaceParent, "ws");

		Assert.assertTrue(workspaceDir.exists());

		args = new String[] {
			"--base", workspaceDir.getPath(), "convert", "--source", projectDir.getPath(),
			"sample-application-adapter-hook"
		};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File warDir = new File(workspaceDir, "wars/sample-application-adapter-hook");

		Assert.assertTrue(warDir.exists());
	}

	@Test
	public void testThemeDocrootBackup() throws Exception {
		File projectDir = _setupWorkspace("testThemeDocrootBackup");

		String[] args = {"--base", projectDir.getPath(), "convert", "-t", "sample-html4-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(new File(projectDir, "wars/sample-html4-theme/docroot_backup/other/afile").exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _contains(File file, String... patterns) throws Exception {
		String content = FileUtil.read(file);

		for (String pattern : patterns) {
			_contains(content, pattern);
		}
	}

	private void _contains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Assert.assertTrue(pattern.matcher(content).matches());
	}

	private void _notContains(File file, String... patterns) throws Exception {
		String content = FileUtil.read(file);

		for (String pattern : patterns) {
			_notContains(content, pattern);
		}
	}

	private void _notContains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Assert.assertFalse(pattern.matcher(content).matches());
	}

	private File _setupWorkspace(String name) throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/" + name);

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(pluginsSdkDir.exists());

		return projectDir;
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}