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

import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class SamplesCommandTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("wrapper.zip"), new File("build/classes/java/test/wrapper.zip"));
	}

	@Test
	public void testGetSample() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "friendly-url"};

		TestUtil.runBlade(args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.friendly.url-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithDependencies() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "rest"};

		TestUtil.runBlade(args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/rest");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.rest-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapper() throws Exception {
		String[] args = {"samples", "-d", temporaryFolder.getRoot().getPath() + "/test", "authenticator-shiro"};

		TestUtil.runBlade(args);

		File projectDir = new File(temporaryFolder.getRoot(), "test/authenticator-shiro");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertTrue(gradleWrapperJar.exists());
		Assert.assertTrue(gradleWrapperProperties.exists());
		Assert.assertTrue(gradleWrapperShell.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.authenticator.shiro-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapperExisting() throws Exception {
		String[] initArgs = {"--base", temporaryFolder.getRoot().getPath() + "/test/workspace", "init"};

		String output = TestUtil.runBlade(initArgs);

		Assert.assertTrue(output, output == null || output.isEmpty());

		String[] samplesArgs =
			{"samples", "-d", temporaryFolder.getRoot().getPath() + "/test/workspace/modules", "auth-failure"};

		output = TestUtil.runBlade(samplesArgs);

		Assert.assertTrue(output, output == null || output.isEmpty());

		File projectDir = new File(temporaryFolder.getRoot(), "test/workspace/modules/auth-failure");

		Assert.assertTrue(projectDir.exists());

		File buildFile = IO.getFile(projectDir, "build.gradle");

		File gradleWrapperJar = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = IO.getFile(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = IO.getFile(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertFalse(gradleWrapperJar.exists());
		Assert.assertFalse(gradleWrapperProperties.exists());
		Assert.assertFalse(gradleWrapperShell.exists());

		File workspaceDir = new File(temporaryFolder.getRoot(), "test/workspace");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspaceDir.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectDir.toString(), "com.liferay.blade.auth.failure-1.0.0.jar");
	}

	@Test
	public void testListSamples() throws Exception {
		String content = TestUtil.runBlade("samples");

		Assert.assertTrue(content.contains("ds-portlet"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}