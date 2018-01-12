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

import aQute.lib.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.nio.file.Files;

import org.gradle.testkit.runner.BuildTask;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author David Truong
 */
public class SamplesCommandTest {

	@AfterClass
	public static void cleanUpClass() throws Exception {
		IO.delete(new File("build/wrapper.zip"));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("wrapper.zip"), new File("build/classes/java/test/wrapper.zip"));
	}

	@After
	public void cleanUp() throws Exception {
		if (testDir.exists()) {
			IO.delete(testDir);
			Assert.assertFalse(testDir.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		testDir = Files.createTempDirectory("samplestest").toFile();
	}

	@Test
	public void testGetSample() throws Exception {
		String[] args = {"samples", "-d", testDir.getPath() + "/test", "blade.friendlyurl"};

		new bladenofail().run(args);

		File projectDir = new File(testDir, "test/blade.friendlyurl");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "blade.friendlyurl-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithDependencies() throws Exception {
		String[] args = {"samples", "-d", testDir.getPath() + "/test", "blade.rest"};

		new bladenofail().run(args);

		File projectDir = new File(testDir, "test/blade.rest");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "blade.rest-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapper() throws Exception {
		String[] args = {"samples", "-d", testDir.getPath() + "/test", "blade.authenticator.shiro"};

		new bladenofail().run(args);

		File projectDir = new File(testDir, "test/blade.authenticator.shiro");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertTrue(gradleWrapperJar.exists());
		Assert.assertTrue(gradleWrapperProperties.exists());
		Assert.assertTrue(gradleWrapperShell.exists());

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "blade.authenticator.shiro-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapperExisting() throws Exception {
		String[] initArgs = {"-b", testDir.getPath() + "/test/workspace", "init"};

		new bladenofail().run(initArgs);

		String[] samplesArgs = {"samples", "-d", testDir.getPath() + "/test/workspace/modules", "blade.authfailure"};

		new bladenofail().run(samplesArgs);

		File projectDir = new File(testDir, "test/workspace/modules/blade.authfailure");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertFalse(gradleWrapperJar.exists());
		Assert.assertFalse(gradleWrapperProperties.exists());
		Assert.assertFalse(gradleWrapperShell.exists());

		File workspaceDir = new File(testDir, "test/workspace");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "blade.authfailure-1.0.0.jar");
	}

	@Test
	public void testListSamples() throws Exception {
		String[] args = {"samples"};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);

		new bladenofail(ps).run(args);

		String content = baos.toString();

		Assert.assertTrue(content.contains("blade.portlet.ds"));
	}

	private File testDir;

}