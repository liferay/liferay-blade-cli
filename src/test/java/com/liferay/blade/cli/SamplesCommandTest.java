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

import java.io.File;

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
		if (_testDir.exists()) {
			IO.delete(_testDir);
			Assert.assertFalse(_testDir.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		_testDir = Files.createTempDirectory("samplestest").toFile();
	}

	@Test
	public void testGetSample() throws Exception {
		String[] args = {"samples", "-d", _testDir.getPath() + "/test", "friendly-url"};

		new BladeNoFail().run(args);

		File projectDir = new File(_testDir, "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.friendly.url-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithDependencies() throws Exception {
		String[] args = {"samples", "-d", _testDir.getPath() + "/test", "rest"};

		new BladeNoFail().run(args);

		File projectDir = new File(_testDir, "test/rest");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.rest-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapper() throws Exception {
		String[] args = {"samples", "-d", _testDir.getPath() + "/test", "authenticator-shiro"};

		new BladeNoFail().run(args);

		File projectDir = new File(_testDir, "test/authenticator-shiro");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertTrue(gradleWrapperJar.exists());
		Assert.assertTrue(gradleWrapperProperties.exists());
		Assert.assertTrue(gradleWrapperShell.exists());

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectDir.getPath(), "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.authenticator.shiro-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapperExisting() throws Exception {
		String[] initArgs = {"--base", _testDir.getPath() + "/test/workspace", "init"};

		new BladeNoFail().run(initArgs);

		String[] samplesArgs = {"samples", "-d", _testDir.getPath() + "/test/workspace/modules", "auth-failure"};

		new BladeNoFail().run(samplesArgs);

		File projectDir = new File(_testDir, "test/workspace/modules/auth-failure");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertFalse(gradleWrapperJar.exists());
		Assert.assertFalse(gradleWrapperProperties.exists());
		Assert.assertFalse(gradleWrapperShell.exists());

		File workspaceDir = new File(_testDir, "test/workspace");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.auth.failure-1.0.0.jar");
	}

	@Test
	public void testListSamples() throws Exception {
		String content = TestUtil.runBlade("samples");

		Assert.assertTrue(content.contains("ds-portlet"));
	}

	private File _testDir;

}