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

package com.liferay.extensions.maven.profile;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class InitCommandMavenTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven", "newproject"};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		Assert.assertTrue(new File(newproject, "pom.xml").exists());

		Assert.assertTrue(new File(newproject, "modules").exists());

		String contents = new String(Files.readAllBytes(new File(newproject, "pom.xml").toPath()));

		Assert.assertTrue(contents, contents.contains("com.liferay.portal.tools.bundle.support"));

		File metadataFile = new File(_workspaceDir, "newproject/.blade/settings.properties");

		Assert.assertTrue(metadataFile.exists());

		BladeSettings bladeSettings = bladeTest.getBladeSettings();

		Assert.assertEquals("maven", bladeSettings.getProfileName());
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven", "newproject"};

		Assert.assertTrue(new File(_workspaceDir, "newproject").mkdirs());

		Assert.assertTrue(new File(_workspaceDir, "newproject/foo").createNewFile());

		BladeTest bladeTest = new BladeTest(false);

		bladeTest.run(args);

		Assert.assertFalse(new File(_workspaceDir, "newproject/pom.xml").exists());
	}

	@Test
	public void testMavenInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven", "newproject"};

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		Assert.assertTrue(new File(_workspaceDir, "newproject/pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "newproject/modules").exists());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven"};

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		Assert.assertTrue(new File(_workspaceDir, "pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle-local.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "settings.gradle").exists());

		_verifyMavenBuild();
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-b", "maven"};

		Assert.assertTrue(new File(_workspaceDir, "foo").createNewFile());

		BladeTest bladeTest = new BladeTest(false);

		bladeTest.run(args);

		Assert.assertFalse(new File(_workspaceDir, "pom.xml").exists());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-f", "-b", "maven"};

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		Assert.assertTrue(_workspaceDir.exists());

		Assert.assertTrue(new File(_workspaceDir, "pom.xml").exists());

		Assert.assertTrue(new File(_workspaceDir, "modules").exists());

		Assert.assertFalse(new File(_workspaceDir, "build.gradle").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "gradle-local.properties").exists());

		Assert.assertFalse(new File(_workspaceDir, "settings.gradle").exists());

		_verifyMavenBuild();
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _createMavenBundle() throws Exception {
		String projectPath = new File(_workspaceDir, "modules").getAbsolutePath();

		String[] args = {"create", "-t", "mvc-portlet", "-d", projectPath, "-b", "maven", "foo"};

		BladeTest bladeTest = new BladeTest();

		bladeTest.run(args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(bndFile.exists());
	}

	private void _verifyMavenBuild() throws Exception {
		_createMavenBundle();

		String projectPath = _workspaceDir.getPath() + "/modules/foo";

		TestUtil.updateMavenRepositories(projectPath);

		MavenUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
	}

	private File _workspaceDir = null;

}