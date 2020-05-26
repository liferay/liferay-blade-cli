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

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;
import org.junit.Before;
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
		File wrapperZipFile = new File("build/wrapper.zip");

		Files.copy(wrapperZipFile.toPath(), new FileOutputStream(new File("build/classes/java/test/wrapper.zip")));

		_deleteSamplesCache();
	}

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testGetSample() throws Exception {
		File root = temporaryFolder.newFolder("samplesroot");

		String[] args = {"samples", "-d", root.getPath() + "/test", "friendly-url"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.friendly.url-1.0.0.jar");
	}

	@Test
	public void testGetSampleMaven70() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "-b", "maven", "-v", "7.0", "friendly-url"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File gradleBuildFile = new File(projectDir, "build.gradle");
		File mavenBuildFile = new File(projectDir, "pom.xml");

		Assert.assertFalse(gradleBuildFile.exists());
		Assert.assertTrue(mavenBuildFile.exists());

		String content = FileUtil.read(mavenBuildFile);

		Assert.assertTrue(
			content,
			content.contains("<artifactId>com.liferay.portal.kernel</artifactId>\n\t\t\t<version>2.0.0</version>"));
	}

	@Test
	public void testGetSampleMaven71() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "-b", "maven", "-v", "7.1", "friendly-url"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File gradleBuildFile = new File(projectDir, "build.gradle");
		File mavenBuildFile = new File(projectDir, "pom.xml");

		Assert.assertFalse(gradleBuildFile.exists());
		Assert.assertTrue(mavenBuildFile.exists());

		String content = FileUtil.read(mavenBuildFile);

		Assert.assertTrue(
			content,
			content.contains("<artifactId>com.liferay.portal.kernel</artifactId>\n\t\t\t<version>3.0.0</version>"));
	}

	@Test
	public void testGetSampleMaven72() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "-b", "maven", "-v", "7.2", "friendly-url"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/friendly-url");

		Assert.assertTrue(projectDir.exists());

		File gradleBuildFile = new File(projectDir, "build.gradle");
		File mavenBuildFile = new File(projectDir, "pom.xml");

		Assert.assertFalse(gradleBuildFile.exists());
		Assert.assertTrue(mavenBuildFile.exists());

		String content = FileUtil.read(mavenBuildFile);

		Assert.assertTrue(content, content.contains("<artifactId>com.liferay.portal.kernel</artifactId>"));
	}

	@Test
	public void testGetSampleWithDependencies() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "rest"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/rest");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		Assert.assertTrue(buildFile.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.rest-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapper() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "authenticator-shiro"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/authenticator-shiro");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		File gradleWrapperJar = new File(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = new File(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = new File(projectDir, "gradlew");

		Assert.assertTrue(buildFile.exists());
		Assert.assertTrue(gradleWrapperJar.exists());
		Assert.assertTrue(gradleWrapperProperties.exists());
		Assert.assertTrue(gradleWrapperShell.exists());

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.authenticator.shiro-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithGradleWrapperExisting() throws Exception {
		String[] initArgs = {
			"--base", _rootDir.getPath() + "/test/workspace", "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_72
		};

		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, initArgs);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, (output == null) || output.isEmpty());

		String[] samplesArgs = {"samples", "-d", _rootDir.getPath() + "/test/workspace/modules", "auth-failure"};

		bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, samplesArgs);

		output = bladeTestResults.getOutput();

		Assert.assertTrue(output, (output == null) || output.isEmpty());

		File projectDir = new File(temporaryFolder.getRoot(), "test/workspace/modules/auth-failure");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		File gradleWrapperJar = new File(projectDir, "gradle/wrapper/gradle-wrapper.jar");

		File gradleWrapperProperties = new File(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		File gradleWrapperShell = new File(projectDir, "gradlew");

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
	public void testGetSampleWithVersion70() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test", "-v", "7.0", "jsp-portlet"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test/jsp-portlet");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		String content = FileUtil.read(buildFile);

		Assert.assertTrue(buildFile.exists());

		Assert.assertTrue(content, content.contains("\"com.liferay.portal.kernel\", version: \"2.0.0\""));

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.jsp.portlet-1.0.0.jar");
	}

	@Test
	public void testGetSampleWithVersion71() throws Exception {
		File root = temporaryFolder.getRoot();

		String[] args = {"samples", "-d", root.getPath() + "/test71", "-v", "7.1", "jsp-portlet"};

		BladeTest bladeTest = _getBladeTest();

		bladeTest.run(args);

		File projectDir = new File(root, "test71/jsp-portlet");

		Assert.assertTrue(projectDir.exists());

		File buildFile = new File(projectDir, "build.gradle");

		String content = FileUtil.read(buildFile);

		Assert.assertTrue(buildFile.exists());

		Assert.assertTrue(content, content.contains("\"com.liferay.portal.kernel\", version: \"3.0.0\""));

		String projectPath = projectDir.getPath();

		TestUtil.verifyBuild(projectPath, "com.liferay.blade.jsp.portlet-1.0.0.jar");
	}

	@Test
	public void testListSamples() throws Exception {
		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, "samples");

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output.contains("ds-portlet"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static void _deleteSamplesCache() throws IOException {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path samplesCachePath = userHomePath.resolve(".blade/cache/samples");

		FileUtil.deleteDirIfExists(samplesCachePath);
	}

	private BladeTest _getBladeTest() {
		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setExtensionsDir(_extensionsDir.toPath());

		bladeTestBuilder.setSettingsDir(_rootDir.toPath());

		return bladeTestBuilder.build();
	}

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private File _extensionsDir = null;
	private File _rootDir = null;

}