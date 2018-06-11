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

import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Terry Jia
 */
public class ConvertServiceBuilderCommandTest {

	public static final String SB_PROJECT_NAME = "sample-service-builder-portlet";

	@Test
	public void testConvertServiceBuilder() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/testMigrateServiceBuilder");

		BladeUtil.unzip(new File("test-resources/projects/plugins-sdk-with-git.zip"), testdir);

		Assert.assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"--base", projectDir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		args = new String[] {"--base", projectDir.getPath(), "convert", SB_PROJECT_NAME};

		new BladeTest().run(args);

		File sbWar = new File(projectDir, "wars/sample-service-builder-portlet");

		Assert.assertTrue(sbWar.exists());

		Assert.assertFalse(new File(sbWar, "build.xml").exists());

		Assert.assertTrue(new File(sbWar, "build.gradle").exists());

		Assert.assertFalse(new File(sbWar, "docroot").exists());

		args = new String[] {"--base", projectDir.getPath(), "convert", SB_PROJECT_NAME};

		new BladeTest().run(args);

		File moduleDir = new File(projectDir, "modules");

		File newSbDir = new File(moduleDir, "sample-service-builder");

		File sbServiceDir = new File(newSbDir, "sample-service-builder-service");
		File sbApiDir = new File(newSbDir, "sample-service-builder-api");

		Assert.assertTrue(sbServiceDir.exists());
		Assert.assertTrue(sbApiDir.exists());

		Assert.assertTrue(new File(sbServiceDir, "service.xml").exists());
		Assert.assertTrue(new File(sbServiceDir, "src/main/resources/service.properties").exists());
		Assert.assertTrue(new File(sbServiceDir, "src/main/resources/META-INF/portlet-model-hints.xml").exists());
		Assert.assertTrue(
			new File(
				sbServiceDir,
				"src/main/java/com/liferay/sampleservicebuilder/service/impl/FooLocalServiceImpl.java").exists());
		Assert.assertTrue(
			new File(
				sbServiceDir,
				"src/main/java/com/liferay/sampleservicebuilder/service/impl/FooServiceImpl.java").exists());
		Assert.assertTrue(
			new File(sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/model/impl/FooImpl.java").exists());

		File bndBnd = new File(sbApiDir, "bnd.bnd");

		Assert.assertTrue(bndBnd.exists());

		String bndContent = new String(IO.read(bndBnd));

		Assert.assertTrue(bndContent, bndContent.contains("com.liferay.sampleservicebuilder.exception"));
	}

	@Test
	public void testConvertServiceBuilderTasksPortletCustomName() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/test-tasks-portlet-conversion");

		String[] args = {"--base", testdir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		File pluginsSdkDir = new File(testdir, "plugins-sdk");

		IO.copy(new File("test-resources/projects/tasks-plugins-sdk"), pluginsSdkDir);

		Assert.assertTrue(new File(testdir, "plugins-sdk/portlets/tasks-portlet").exists());

		String[] convertArgs = {"--base", testdir.getPath(), "convert", "tasks-portlet", "foo"};

		new BladeTest().run(convertArgs);

		Assert.assertTrue(new File(testdir, "modules/foo/foo-api/build.gradle").exists());
	}

	@Test
	public void testConvertServiceBuilderTasksPortletDefaultName() throws Exception {
		File testdir = new File(temporaryFolder.getRoot(), "build/test-tasks-portlet-conversion");

		String[] args = {"--base", testdir.getPath(), "init", "-u"};

		new BladeTest().run(args);

		File pluginsSdkDir = new File(testdir, "plugins-sdk");

		IO.copy(new File("test-resources/projects/tasks-plugins-sdk"), pluginsSdkDir);

		Assert.assertTrue(new File(testdir, "plugins-sdk/portlets/tasks-portlet").exists());

		String[] convertArgs = {"--base", testdir.getPath(), "convert", "tasks-portlet"};

		new BladeTest().run(convertArgs);

		Assert.assertTrue(new File(testdir, "modules/tasks/tasks-api/build.gradle").exists());

		Assert.assertTrue(
			new File(testdir, "modules/tasks/tasks-api/src/main/java/com/liferay/tasks/exception").exists());

		File file = new File(
			testdir, "modules/tasks/tasks-service/src/main/java/com/liferay/tasks/model/impl/TasksEntryModelImpl.java");

		Assert.assertTrue(file.exists());

		file = new File(
			testdir,
			"modules/tasks/tasks-service/src/main/java/com/liferay/tasks/service/impl/TasksEntryServiceImpl.java");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(new File(testdir, "modules/tasks/tasks-service/service.xml").exists());

		Assert.assertFalse(new File(testdir, "wars/tasks-portlet/src/main/webapp/WEB-INF/service.xml").exists());

		Assert.assertTrue(new File(testdir, "wars/tasks-portlet/src/main/webapp/WEB-INF/portlet.xml").exists());

		File portletGradleFile = new File(testdir, "wars/tasks-portlet/build.gradle");

		Assert.assertTrue(portletGradleFile.exists());

		String content = new String(Files.readAllBytes(portletGradleFile.toPath()));

		Assert.assertTrue(content.contains("compileOnly project(\":modules:tasks:tasks-api\")"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}