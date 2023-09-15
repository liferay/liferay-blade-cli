/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Terry Jia
 */
@Ignore
public class ConvertServiceBuilderCommandTest {

	public static final String SB_PROJECT_NAME = "sample-service-builder-portlet";

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();
		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testConvertServiceBuilder() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMigrateServiceBuilder");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "-q", SB_PROJECT_NAME};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File oldSbProject = new File(pluginsSdkDir, "portlet/sample-service-builder-portlet");

		Assert.assertFalse(oldSbProject.exists());

		File sbWar = new File(projectDir, "modules/sample-service-builder/sample-service-builder-portlet");

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains(sbWar.getAbsolutePath()));

		Assert.assertTrue(sbWar.exists());

		File buildXmlFile = new File(sbWar, "build.xml");

		Assert.assertFalse(buildXmlFile.exists());

		File buildGradleFile = new File(sbWar, "build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		File docrootDir = new File(sbWar, "docroot");

		Assert.assertFalse(docrootDir.exists());

		File moduleDir = new File(projectDir, "modules");

		File newSbDir = new File(moduleDir, "sample-service-builder");

		File sbServiceDir = new File(newSbDir, "sample-service-builder-service");
		File sbApiDir = new File(newSbDir, "sample-service-builder-api");

		Assert.assertTrue(sbServiceDir.exists());
		Assert.assertTrue(sbApiDir.exists());

		Assert.assertTrue(output, output.contains(sbServiceDir.getAbsolutePath()));
		Assert.assertTrue(output, output.contains(sbApiDir.getAbsolutePath()));

		File serviceXmlFile = new File(sbServiceDir, "service.xml");

		Assert.assertTrue(serviceXmlFile.exists());

		File servicePropertiesFile = new File(sbServiceDir, "src/main/resources/service.properties");

		Assert.assertTrue(servicePropertiesFile.exists());

		File baseSpringXmlFile = new File(sbServiceDir, "src/main/resources/META-INF/base-spring.xml");

		Assert.assertFalse(baseSpringXmlFile.getAbsolutePath(), baseSpringXmlFile.exists());

		File hintsXmlFile = new File(sbServiceDir, "src/main/resources/META-INF/portlet-model-hints.xml");

		Assert.assertTrue(hintsXmlFile.exists());

		File localImplFile = new File(
			sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/service/impl/FooLocalServiceImpl.java");

		Assert.assertTrue(localImplFile.exists());

		File serviceImplFile = new File(
			sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/service/impl/FooServiceImpl.java");

		Assert.assertTrue(serviceImplFile.exists());

		File implFile = new File(
			sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/model/impl/FooImpl.java");

		Assert.assertTrue(implFile.exists());

		File bndBnd = new File(sbApiDir, "bnd.bnd");

		Assert.assertTrue(bndBnd.exists());

		String bndContent = FileUtil.read(bndBnd);

		Assert.assertTrue(bndContent, bndContent.contains("com.liferay.sampleservicebuilder.exception"));
	}

	@Test
	public void testConvertServiceBuilderTasksPortletCustomName() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/test-tasks-portlet-conversion");

		String[] args = {"--base", testdir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path testPath = testdir.toPath();

		Path pluginsSdkPath = testPath.resolve("plugins-sdk");

		File tasksPluginsSdk = new File("test-resources/projects/tasks-plugins-sdk");

		FileUtil.copyDir(tasksPluginsSdk.toPath(), pluginsSdkPath);

		Assert.assertTrue(Files.exists(testPath.resolve("plugins-sdk/portlets/tasks-portlet")));

		String[] convertArgs = {"--base", testdir.getPath(), "convert", "tasks-portlet", "foo"};

		TestUtil.runBlade(_rootDir, _extensionsDir, convertArgs);

		Assert.assertTrue(Files.exists(testPath.resolve("modules/foo/foo-api/build.gradle")));
	}

	@Test
	public void testConvertServiceBuilderTasksPortletDefaultName() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/test-tasks-portlet-conversion");

		String[] args = {"--base", testdir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		Path testPath = testdir.toPath();

		Path pluginsSdkPath = testPath.resolve("plugins-sdk");

		File taskPluginsSdk = new File("test-resources/projects/tasks-plugins-sdk");

		FileUtil.copyDir(taskPluginsSdk.toPath(), pluginsSdkPath);

		Assert.assertTrue(Files.exists(testPath.resolve("plugins-sdk/portlets/tasks-portlet")));

		String[] convertArgs = {"--base", testdir.getPath(), "convert", "tasks-portlet"};

		TestUtil.runBlade(_rootDir, _extensionsDir, convertArgs);

		File buildGradleFile = new File(testdir, "modules/tasks/tasks-api/build.gradle");

		Assert.assertTrue(buildGradleFile.exists());

		File exceptionDir = new File(testdir, "modules/tasks/tasks-api/src/main/java/com/liferay/tasks/exception");

		Assert.assertTrue(exceptionDir.exists());

		File file = new File(
			testdir, "modules/tasks/tasks-service/src/main/java/com/liferay/tasks/model/impl/TasksEntryModelImpl.java");

		Assert.assertTrue(file.exists());

		file = new File(
			testdir,
			"modules/tasks/tasks-service/src/main/java/com/liferay/tasks/service/impl/TasksEntryServiceImpl.java");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(Files.exists(testPath.resolve("modules/tasks/tasks-service/service.xml")));

		Assert.assertFalse(
			Files.exists(testPath.resolve("modules/tasks/tasks-portlet/src/main/webapp/WEB-INF/service.xml")));

		Assert.assertTrue(
			Files.exists(testPath.resolve("modules/tasks/tasks-portlet/src/main/webapp/WEB-INF/portlet.xml")));

		File portletGradleFile = new File(testdir, "modules/tasks/tasks-portlet/build.gradle");

		Assert.assertTrue(portletGradleFile.exists());

		String content = new String(Files.readAllBytes(portletGradleFile.toPath()));

		Assert.assertTrue(content, content.contains("compileOnly project(\":modules:tasks:tasks-api\")"));
	}

	@Test
	public void testConvertServiceBuilderWithoutRemove() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMigrateServiceBuilder");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", SB_PROJECT_NAME};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File oldSbProject = new File(pluginsSdkDir, "portlets/sample-service-builder-portlet");

		Assert.assertTrue(oldSbProject.exists());

		File moduleDir = new File(projectDir, "modules");

		File newSbDir = new File(moduleDir, "sample-service-builder");

		File sbServiceDir = new File(newSbDir, "sample-service-builder-service");
		File sbApiDir = new File(newSbDir, "sample-service-builder-api");
		File sbWar = new File(newSbDir, "sample-service-builder-portlet");

		Assert.assertTrue(sbServiceDir.getAbsolutePath(), sbServiceDir.exists());
		Assert.assertTrue(sbApiDir.getAbsolutePath(), sbApiDir.exists());
		Assert.assertTrue(sbWar.getAbsolutePath(), sbWar.exists());
	}

	@Test
	public void testConvertServiceBuilderWithRemove() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMigrateServiceBuilder");

		FileUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		File pluginsSdkDir = new File(projectDir, "plugins-sdk");

		FileUtil.deleteDirIfExists(pluginsSdkDir.toPath());

		String[] args = {"--base", projectDir.getPath(), "init", "-u", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", projectDir.getPath(), "convert", "-r", SB_PROJECT_NAME};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		File oldSbProject = new File(pluginsSdkDir, "portlets/sample-service-builder-portlet");

		Assert.assertFalse(oldSbProject.exists());

		File moduleDir = new File(projectDir, "modules");

		File newSbDir = new File(moduleDir, "sample-service-builder");

		File sbServiceDir = new File(newSbDir, "sample-service-builder-service");
		File sbApiDir = new File(newSbDir, "sample-service-builder-api");
		File sbWar = new File(newSbDir, "sample-service-builder-portlet");

		Assert.assertTrue(sbServiceDir.getAbsolutePath(), sbServiceDir.exists());
		Assert.assertTrue(sbApiDir.getAbsolutePath(), sbApiDir.exists());
		Assert.assertTrue(sbWar.getAbsolutePath(), sbWar.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _rootDir = null;

}