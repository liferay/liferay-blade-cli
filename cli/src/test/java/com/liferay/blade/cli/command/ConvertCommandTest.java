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

import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.regex.Matcher;
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
		Path rootPath = _rootDir.toPath();

		Path testPath = rootPath.resolve("build/testUpgradePluginsSDKTo70");

		Files.createDirectories(testPath);

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testPath.toFile());

		Assert.assertTrue(Files.exists(testPath));

		Path projectPath = testPath.resolve("plugins-sdk-with-git");

		Path pluginsSdkPath = projectPath.resolve("plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkPath);

		String[] args = {"--base", projectPath.toString(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectPath.toString(), "convert", "-a", "-r"};

		TestUtil.runBlade(_rootDir, _extensionsDir, false, args);

		Path apiPath = testPath.resolve(
			"plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-api");

		Assert.assertTrue(Files.exists(apiPath));

		Path servicePath = projectPath.resolve("modules/sample-service-builder/sample-service-builder-service");

		Assert.assertTrue(Files.exists(servicePath));

		Path portletPath = projectPath.resolve("wars/sample-service-builder-portlet");

		Assert.assertTrue(Files.exists(portletPath));
	}

	@Test
	public void testAllNotRemoveSource() throws Exception {
		Path rootPath = _rootDir.toPath();

		Path testPath = rootPath.resolve("build/testUpgradePluginsSDKTo70");

		Files.createDirectories(testPath);

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testPath.toFile());

		Assert.assertTrue(Files.exists(testPath));

		Path projectPath = testPath.resolve("plugins-sdk-with-git");

		Path pluginsSdkPath = projectPath.resolve("plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkPath);

		String[] args = {"--base", projectPath.toString(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectPath.toString(), "convert", "-a"};

		TestUtil.runBlade(_rootDir, _extensionsDir, false, args);

		Path apiPath = testPath.resolve(
			"plugins-sdk-with-git/modules/sample-service-builder/sample-service-builder-api");

		Assert.assertTrue(Files.exists(apiPath));

		Path servicePath = projectPath.resolve("modules/sample-service-builder/sample-service-builder-service");

		Assert.assertTrue(Files.exists(servicePath));

		Path portletPath = projectPath.resolve("wars/sample-service-builder-portlet");

		Assert.assertTrue(Files.exists(portletPath));

		Path pluginServiceBuilderPath = pluginsSdkPath.resolve("portlets/sample-service-builder-portlet");

		Assert.assertTrue(Files.exists(pluginServiceBuilderPath));
	}

	@Test
	public void testFindPluginsSdkPlugin() throws Exception {
		Path rootPath = _rootDir.toPath();

		Path testPath = rootPath.resolve("build/testPluginsSdkWithMetadata");

		Files.createDirectories(testPath);

		FileUtil.unzip(new File("test-resources/projects/invalid-plugins-sdk-path.zip"), testPath.toFile());

		Assert.assertTrue(Files.exists(testPath));

		Path pluginsSdkPath = testPath.resolve("invalid-plugins-sdk-path");

		Assert.assertTrue(Files.exists(pluginsSdkPath));

		Path workspacePath = testPath.resolve("workspace");

		Files.createDirectories(workspacePath);

		String[] args = {"--base", workspacePath.toString(), "init", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {
			"--base", workspacePath.toString(), "convert", "-s", pluginsSdkPath.toString(), "tasks-portlet"
		};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, false, args);

		String errors = bladeTestResults.getErrors();

		Assert.assertTrue(errors, errors.contains("pluginsSdkDir is not a valid Plugins SDK"));
	}

	@Test
	public void testMoveLayouttplToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveLayouttplToWars1");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "1-2-1-columns-layouttpl"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		Assert.assertTrue(layoutWar.exists());

		File buildXmlFile = new File(layoutWar, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		File buildGradleFile = new File(layoutWar, "build.gradle");

		Assert.assertFalse(buildGradleFile.exists());

		File docrootDir = new File(layoutWar, "docroot");

		Assert.assertFalse(docrootDir.exists());
	}

	@Test
	public void testMoveLayouttplToWarsNotRemoveSource() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveLayouttplToWars2");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "1-2-1-columns-layouttpl"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File layoutWar = new File(projectDir, "wars/1-2-1-columns-layouttpl");

		Assert.assertTrue(layoutWar.exists());

		File buildXmlFile = new File(layoutWar, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		File buildGradleFile = new File(layoutWar, "build.gradle");

		Assert.assertFalse(buildGradleFile.exists());

		File docrootDir = new File(layoutWar, "docroot");

		Assert.assertFalse(docrootDir.exists());

		File srclayoutDir = new File(pluginsSdkDir, "layouttpl/1-2-1-columns-layouttpl");

		Assert.assertTrue(srclayoutDir.exists());

		File srclayoutBuildXml = new File(srclayoutDir, "build.xml");

		Assert.assertTrue(srclayoutBuildXml.exists());
	}

	@Test
	public void testMovePluginsToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMovePluginsToWars");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Path testPath = testdir.toPath();

		Assert.assertTrue(Files.exists(testPath));

		Path projectDir = testPath.resolve("plugins-sdk-with-git");

		Path pluginsSdkDir = projectDir.resolve("plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir);

		String[] args = {"--base", projectDir.toString(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.toString(), "convert", "-r", "sample-application-adapter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path sampleExpandoHook = projectDir.resolve("wars/sample-application-adapter-hook");

		Assert.assertTrue(Files.exists(sampleExpandoHook));

		Path sampleHookWrongPath = projectDir.resolve("plugins-sdk/hooks/sample-application-adapter-hook");

		Assert.assertFalse(Files.exists(sampleHookWrongPath));

		args = new String[] {"--base", projectDir.toString(), "convert", "-r", "sample-servlet-filter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path sampleServletFilterHook = projectDir.resolve("wars/sample-servlet-filter-hook");

		Assert.assertTrue(Files.exists(sampleServletFilterHook));

		Path hookDir = projectDir.resolve("plugins-sdk/hooks/sample-servlet-filter-hook");

		Assert.assertFalse(Files.exists(hookDir));
	}

	@Test
	public void testMovePluginsToWarsNotRemoveSource() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMovePluginsToWars");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Path testPath = testdir.toPath();

		Assert.assertTrue(Files.exists(testPath));

		Path projectDir = testPath.resolve("plugins-sdk-with-git");

		Path pluginsSdkDir = projectDir.resolve("plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir);

		String[] args = {"--base", projectDir.toString(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.toString(), "convert", "sample-application-adapter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path sampleExpandoHook = projectDir.resolve("wars/sample-application-adapter-hook");

		Assert.assertTrue(Files.exists(sampleExpandoHook));

		Path sampleHookWrongPath = projectDir.resolve("plugins-sdk/hooks/sample-application-adapter-hook");

		Assert.assertTrue(Files.exists(sampleHookWrongPath));

		args = new String[] {"--base", projectDir.toString(), "convert", "sample-servlet-filter-hook"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path sampleServletFilterHook = projectDir.resolve("wars/sample-servlet-filter-hook");

		Assert.assertTrue(Files.exists(sampleServletFilterHook));

		Path hookDir = projectDir.resolve("plugins-sdk/hooks/sample-servlet-filter-hook");

		Assert.assertTrue(Files.exists(hookDir));
	}

	@Test
	public void testMoveThemesToWars() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveThemesToWar");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "-r", "sample-styled-minimal-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(theme.exists());

		File buildXmlFile = new File(theme, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		File buildGradleFile = new File(theme, "build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		File docrootDir = new File(theme, "docroot");

		Assert.assertFalse(docrootDir.exists());

		File webappDir = new File(theme, "src/main/webapp");

		Assert.assertTrue(webappDir.exists());

		File diffsDir = new File(theme, "src/main/webapp/_diffs");

		Assert.assertFalse(diffsDir.exists());

		File themeDir = new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme");

		Assert.assertFalse(themeDir.exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "-r", "sample-styled-advanced-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File advancedTheme = new File(projectDir, "wars/sample-styled-advanced-theme");

		Assert.assertTrue(advancedTheme.exists());

		buildXmlFile = new File(advancedTheme, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		buildGradleFile = new File(advancedTheme, "build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		docrootDir = new File(advancedTheme, "docroot");

		Assert.assertFalse(docrootDir.exists());

		webappDir = new File(advancedTheme, "src/main/webapp");

		Assert.assertTrue(webappDir.exists());

		diffsDir = new File(advancedTheme, "src/main/webapp/_diffs");

		Assert.assertFalse(diffsDir.exists());

		themeDir = new File(projectDir, "plugins-sdk/themes/sample-styled-advanced-theme");

		Assert.assertFalse(themeDir.exists());
	}

	@Test
	public void testMoveThemesToWarsNotRemoveSource() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMoveThemesToWarNotRemoveSource");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File theme = new File(projectDir, "wars/sample-styled-minimal-theme");

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-minimal-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(theme.exists());

		File buildXmlFile = new File(theme, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		File buildGradleFile = new File(theme, "build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		File docrootDir = new File(theme, "docroot");

		Assert.assertFalse(docrootDir.exists());

		File webappDir = new File(theme, "src/main/webapp");

		Assert.assertTrue(webappDir.exists());

		File diffsDir = new File(theme, "src/main/webapp/_diffs");

		Assert.assertFalse(diffsDir.exists());

		File themeDir = new File(projectDir, "plugins-sdk/themes/sample-styled-minimal-theme");

		Assert.assertTrue(themeDir.exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", "-t", "sample-styled-advanced-theme"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File advancedTheme = new File(projectDir, "wars/sample-styled-advanced-theme");

		Assert.assertTrue(advancedTheme.exists());

		buildXmlFile = new File(advancedTheme, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		buildGradleFile = new File(advancedTheme, "build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		docrootDir = new File(advancedTheme, "docroot");

		Assert.assertFalse(docrootDir.exists());

		webappDir = new File(advancedTheme, "src/main/webapp");

		Assert.assertTrue(webappDir.exists());

		diffsDir = new File(advancedTheme, "src/main/webapp/_diffs");

		Assert.assertFalse(diffsDir.exists());

		themeDir = new File(projectDir, "plugins-sdk/themes/sample-styled-advanced-theme");

		Assert.assertTrue(themeDir.exists());
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

		File ivmXmlFile = new File(projectDir, "wars/sample-tapestry-portlet/ivy.xml");

		Assert.assertFalse(ivmXmlFile.exists());
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
			new File(projectDir, "wars/sample-hibernate-portlet/build.gradle"), ".*antlr2.*", ".*hibernate3.*",
			".*util-slf4j.*");
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

		String[] args = {"--base", workspaceParent.getPath(), "init", "ws", "-v", "7.2"};

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

		File afile = new File(projectDir, "wars/sample-html4-theme/docroot_backup/other/afile");

		Assert.assertTrue(afile.exists());
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

		Matcher matcher = pattern.matcher(content);

		Assert.assertTrue(matcher.matches());
	}

	private void _notContains(File file, String... patterns) throws Exception {
		String content = FileUtil.read(file);

		for (String pattern : patterns) {
			_notContains(content, pattern);
		}
	}

	private void _notContains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(content);

		Assert.assertFalse(matcher.matches());
	}

	private File _setupWorkspace(String name) throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/" + name);

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", "7.2"};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Assert.assertTrue(pluginsSdkDir.exists());

		return projectDir;
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}