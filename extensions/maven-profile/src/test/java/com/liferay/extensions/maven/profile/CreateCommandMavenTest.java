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

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class CreateCommandMavenTest {

	@Before
	public void setUp() throws Exception {
		_bladeTest = new BladeTest(temporaryFolder.getRoot());
	}

	@Test
	public void testCreateActivator() throws Exception {
		File tempRoot = temporaryFolder.getRoot();

		String[] mavenArgs =
			{"create", "-d", tempRoot.getAbsolutePath(), "-b", "maven", "-t", "activator", "bar-activator"};

		String projectPath = new File(tempRoot, "bar-activator").getAbsolutePath();

		_bladeTest.run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		MavenUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "bar-activator-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/bar-activator-1.0.0.jar"));
	}

	@Test
	public void testCreateApi() throws Exception {
		File tempRoot = temporaryFolder.getRoot();

		String[] mavenArgs = {"create", "-d", tempRoot.getAbsolutePath(), "-b", "maven", "-t", "api", "foo"};

		String projectPath = new File(tempRoot, "foo").getAbsolutePath();

		_bladeTest.run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/src/main/java/foo/api/Foo.java"), ".*^public interface Foo.*");

		_contains(_checkFileExists(projectPath + "/src/main/resources/foo/api/packageinfo"), "version 1.0.0");

		MavenUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));

		try (Jar jar = new Jar(new File(projectPath, "target/foo-1.0.0.jar"))) {
			Manifest manifest = jar.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			Assert.assertEquals("foo.api;version=\"1.0.0\"", mainAttributes.getValue("Export-Package"));
		}
	}

	@Test
	public void testCreateFragment() throws Exception {
		File tempRoot = temporaryFolder.getRoot();

		String[] mavenArgs = {
			"create", "-d", tempRoot.getAbsolutePath(), "-b", "maven", "-t", "fragment", "-h", "com.liferay.login.web",
			"-H", "1.0.0", "loginHook"
		};

		String projectPath = new File(tempRoot, "loginHook").getAbsolutePath();

		_bladeTest.run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		TestUtil.updateMavenRepositories(projectPath);

		MavenUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "loginHook-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/loginHook-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortlet() throws Exception {
		File tempRoot = temporaryFolder.getRoot();

		String[] mavenArgs = {"create", "-d", tempRoot.getAbsolutePath(), "-b", "maven", "-t", "mvc-portlet", "foo"};

		String projectPath = new File(tempRoot, "foo").getAbsolutePath();

		_bladeTest.run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		TestUtil.updateMavenRepositories(projectPath);

		MavenUtil.executeGoals(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _checkFileExists(String path) {
		File file = IO.getFile(path);

		Assert.assertTrue(file.exists());

		return file;
	}

	private void _checkMavenBuildFiles(String projectPath) {
		_checkFileExists(projectPath);
		_checkFileExists(projectPath + "/bnd.bnd");
		_checkFileExists(projectPath + "/pom.xml");
		_checkFileExists(projectPath + "/mvnw");
		_checkFileExists(projectPath + "/mvnw.cmd");
	}

	private void _contains(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		_contains(content, pattern);
	}

	private void _contains(File file, String[] patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			_contains(content, pattern);
		}
	}

	private void _contains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Assert.assertTrue(pattern.matcher(content).matches());
	}

	private void _verifyImportPackage(File serviceJar) throws Exception {
		try (Jar jar = new Jar(serviceJar)) {
			Manifest m = jar.getManifest();

			Domain domain = Domain.domain(m);

			Parameters imports = domain.getImportPackage();

			for (String key : imports.keySet()) {
				Assert.assertFalse(key.isEmpty());
			}
		}
	}

	private BladeTest _bladeTest;

}