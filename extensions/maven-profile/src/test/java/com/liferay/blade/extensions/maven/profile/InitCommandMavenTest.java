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

package com.liferay.blade.extensions.maven.profile;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.nio.file.Files;

import java.util.Properties;
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
public class InitCommandMavenTest implements MavenExecutor {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testMavenInitDXPProject() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "mavenworkspace", "-v",
			BladeTest.LIFERAY_VERSION_73101, "--product", "dxp"
		};

		File mavenworkspace = new File(_workspaceDir, "mavenworkspace");

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		Assert.assertTrue(mavenworkspace.exists());

		_contains(_checkFileExists(mavenworkspace + "/pom.xml"), ".*<artifactId>release.dxp.bom</artifactId>.*");

		File modulesDir = new File(mavenworkspace, "modules");

		args = new String[] {
			"create", "-t", "portlet", "-d", modulesDir.getAbsolutePath(), "project1", "-v",
			BladeTest.LIFERAY_VERSION_73101
		};

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		File projectDirectory = new File(mavenworkspace, "modules/project1");

		Assert.assertTrue(projectDirectory.exists());

		File projectPomFile = new File(projectDirectory, "pom.xml");

		Assert.assertTrue(projectPomFile.exists());
	}

	@Test
	public void testMavenInitProjectCorrectLocation() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "mavenworkspace", "-v",
			BladeTest.LIFERAY_VERSION_741
		};

		File mavenworkspace = new File(_workspaceDir, "mavenworkspace");

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		Assert.assertTrue(mavenworkspace.exists());

		File modulesDir = new File(mavenworkspace, "modules");

		args = new String[] {
			"create", "--base", mavenworkspace.getAbsolutePath(), "-t", "portlet", "-d", modulesDir.getAbsolutePath(),
			"project1", "-v", BladeTest.LIFERAY_VERSION_741
		};

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		File projectDirectory = new File(mavenworkspace, "modules/project1");

		Assert.assertTrue(projectDirectory.exists());

		File projectPomFile = new File(projectDirectory, "pom.xml");

		Assert.assertTrue(projectPomFile.exists());
	}

	@Test
	public void testMavenInitProjectDirLocation() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "mavenworkspace", "-v",
			BladeTest.LIFERAY_VERSION_741
		};

		File mavenworkspace = new File(_workspaceDir, "mavenworkspace");

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		Assert.assertTrue(mavenworkspace.exists());

		File modulesDir = new File(mavenworkspace, "modules");

		args = new String[] {
			"create", "-t", "portlet", "-d", modulesDir.getAbsolutePath(), "project1", "-v",
			BladeTest.LIFERAY_VERSION_741
		};

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		File projectDirectory = new File(mavenworkspace, "modules/project1");

		Assert.assertTrue(projectDirectory.exists());

		File projectPomFile = new File(projectDirectory, "pom.xml");

		Assert.assertTrue(projectPomFile.exists());
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "newproject", "-v", BladeTest.LIFERAY_VERSION_741
		};

		File newproject = new File(_workspaceDir, "newproject");

		Assert.assertTrue(newproject.mkdirs());

		TestUtil.runBlade(newproject, _extensionsDir, args);

		File pomXMLFile = new File(newproject, "pom.xml");

		Assert.assertTrue(pomXMLFile.exists());

		_checkExists(new File(newproject, "modules"));

		String contents = new String(Files.readAllBytes(pomXMLFile.toPath()));

		Assert.assertTrue(contents, contents.contains("com.liferay.portal.tools.bundle.support"));

		File metadataFile = new File(_workspaceDir, "newproject/.blade.properties");

		Assert.assertTrue(metadataFile.exists());

		Properties settingsProperties = new Properties();

		FileInputStream settingsInputStream = new FileInputStream(metadataFile);

		settingsProperties.load(settingsInputStream);

		String profile = settingsProperties.getProperty("profile.name");

		Assert.assertEquals("maven", profile);
	}

	@Test
	public void testMavenInitWithNameWorkspaceDirectoryHasFiles() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "newproject", "-v", BladeTest.LIFERAY_VERSION_741
		};

		File projectDir = new File(_workspaceDir, "newproject");

		Assert.assertTrue(projectDir.mkdirs());

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		_checkExists(new File(_workspaceDir, "newproject/pom.xml"));
	}

	@Test
	public void testMavenInitWithNameWorkspaceNotExists() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "newproject", "-v", BladeTest.LIFERAY_VERSION_741
		};

		File projectDir = new File(_workspaceDir, "newproject");

		TestUtil.runBlade(projectDir, _extensionsDir, args);

		_checkExists(new File(_workspaceDir, "newproject/pom.xml"));
		_checkExists(new File(_workspaceDir, "newproject/modules"));
	}

	@Test
	public void testMavenInitWithPrompter() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "mavenworkspace", "--product", "dxp"
		};

		String responses = "4" + System.lineSeparator();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responses.getBytes());

		TestUtil.runBlade(_workspaceDir, _extensionsDir, byteArrayInputStream, true, args);

		File mavenworkspaceDir = new File(_workspaceDir, "mavenworkspace");

		Assert.assertTrue(mavenworkspaceDir.exists());

		File projectPomFile = new File(mavenworkspaceDir, "pom.xml");

		Assert.assertTrue(projectPomFile.exists());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryEmpty() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-P", "maven", "-v", BladeTest.LIFERAY_VERSION_741};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		_checkExists(new File(_workspaceDir, "pom.xml"));
		_checkExists(new File(_workspaceDir, "modules"));
		_checkNotExists(new File(_workspaceDir, "build.gradle"));
		_checkNotExists(new File(_workspaceDir, "gradle.properties"));
		_checkNotExists(new File(_workspaceDir, "gradle-local.properties"));
		_checkNotExists(new File(_workspaceDir, "settings.gradle"));

		_verifyMavenBuild();
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFiles() throws Exception {
		File foo = new File(_workspaceDir, "foo");

		foo.mkdirs();

		_checkExists(foo);

		File bar = new File(foo, "bar");

		Assert.assertTrue(bar.getAbsolutePath(), bar.createNewFile());

		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "foo", "-v", BladeTest.LIFERAY_VERSION_741
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, false, args);

		_checkNotExists(new File(_workspaceDir, "pom.xml"));
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFilesForce() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-f", "-P", "maven", "-v", BladeTest.LIFERAY_VERSION_741
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		Assert.assertTrue(_workspaceDir.exists());

		_checkExists(new File(_workspaceDir, "pom.xml"));
		_checkExists(new File(_workspaceDir, "modules"));
		_checkNotExists(new File(_workspaceDir, "build.gradle"));
		_checkNotExists(new File(_workspaceDir, "gradle.properties"));
		_checkNotExists(new File(_workspaceDir, "settings.gradle"));

		_verifyMavenBuild();
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _checkExists(File file) {
		Assert.assertTrue(file.exists());
	}

	private File _checkFileExists(String path) {
		File file = IO.getFile(path);

		Assert.assertTrue(file.exists());

		return file;
	}

	private void _checkNotExists(File file) {
		Assert.assertFalse(file.exists());
	}

	private void _contains(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		_contains(content, pattern);
	}

	private void _contains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(content);

		Assert.assertTrue(matcher.matches());
	}

	private void _createMavenBundle() throws Exception {
		File projectDir = new File(_workspaceDir, "modules");

		String projectPath = projectDir.getAbsolutePath();

		String[] args = {
			"create", "--base", _workspaceDir.getAbsolutePath(), "-t", "mvc-portlet", "-d", projectPath, "-P", "maven",
			"foo", "-v", BladeTest.LIFERAY_VERSION_741
		};

		TestUtil.runBlade(_workspaceDir, _extensionsDir, args);

		File file = IO.getFile(projectPath + "/foo");
		File bndFile = IO.getFile(projectPath + "/foo/bnd.bnd");

		Assert.assertTrue(file.exists());

		Assert.assertTrue(bndFile.exists());
	}

	private void _verifyMavenBuild() throws Exception {
		_createMavenBundle();

		String projectPath = _workspaceDir.getPath() + "/modules/foo";

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
	}

	private File _extensionsDir = null;
	private File _workspaceDir = null;

}