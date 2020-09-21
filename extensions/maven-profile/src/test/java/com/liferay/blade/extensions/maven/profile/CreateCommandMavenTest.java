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

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;

import java.io.File;

import java.util.jar.Attributes;
import java.util.jar.Manifest;
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
public class CreateCommandMavenTest implements MavenExecutor {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		_workspaceDir = temporaryFolder.newFolder("mavenWorkspace");
	}

	@Test
	public void testCreateApi() throws Exception {
		File workspaceDir = _workspaceDir;

		MavenTestUtil.makeMavenWorkspace(_extensionsDir, workspaceDir, BladeTest.PRODUCT_VERSION_PORTAL_73);

		File modulesDir = new File(workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-P", "maven", "-t",
			"api", "foo"
		};

		File projectDir = new File(modulesDir, "foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/src/main/java/foo/api/Foo.java"), ".*^public interface Foo.*");

		_contains(_checkFileExists(projectPath + "/src/main/resources/foo/api/packageinfo"), "version 1.0.0");

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

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
		File workspaceDir = _workspaceDir;

		MavenTestUtil.makeMavenWorkspace(_extensionsDir, workspaceDir, BladeTest.PRODUCT_VERSION_PORTAL_73);

		File modulesDir = new File(workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-P", "maven", "-t",
			"fragment", "-h", "com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		File projectDir = new File(modulesDir, "loginHook");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "loginHook-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/loginHook-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortlet() throws Exception {
		File workspaceDir = _workspaceDir;

		MavenTestUtil.makeMavenWorkspace(_extensionsDir, workspaceDir, BladeTest.PRODUCT_VERSION_PORTAL_73);

		File modulesDir = new File(workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-P", "maven", "-t",
			"mvc-portlet", "foo"
		};

		File projectDir = new File(modulesDir, "foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortletDXP72() throws Exception {
		MavenTestUtil.makeMavenWorkspace(_extensionsDir, _workspaceDir, "dxp-7.2-sp2");

		File modulesDir = new File(_workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", _workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-P", "maven",
			"-t", "mvc-portlet", "foo", "--product", "dxp"
		};

		File projectDir = new File(modulesDir, "foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(_workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/pom.xml"), ".*<artifactId>release.portal.bom</artifactId>.*");

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortletDXP73() throws Exception {
		MavenTestUtil.makeMavenWorkspace(_extensionsDir, _workspaceDir, "dxp-7.3-ep5");

		File modulesDir = new File(_workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", _workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-P", "maven",
			"-t", "mvc-portlet", "foo", "--product", "dxp"
		};

		File projectDir = new File(modulesDir, "foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(_workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/pom.xml"), ".*<artifactId>release.portal.api</artifactId>.*");

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortletLegacyFlag() throws Exception {
		File workspaceDir = _workspaceDir;

		MavenTestUtil.makeMavenWorkspace(_extensionsDir, workspaceDir, BladeTest.PRODUCT_VERSION_PORTAL_73);

		File modulesDir = new File(workspaceDir, "modules");

		String[] mavenArgs = {
			"create", "--base", workspaceDir.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-b", "maven", "-t",
			"mvc-portlet", "foo"
		};

		File projectDir = new File(modulesDir, "foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspaceDir, _extensionsDir, mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		TestUtil.updateMavenRepositories(projectPath);

		execute(projectPath, new String[] {"clean", "package"});

		MavenTestUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortletStandalone() throws Exception {
		File tempRoot = temporaryFolder.getRoot();

		String[] mavenArgs = {
			"create", "-d", tempRoot.getAbsolutePath(), "-P", "maven", "-t", "mvc-portlet", "foo", "-v", "7.3"
		};

		try {
			TestUtil.runBlade(_rootDir, _extensionsDir, mavenArgs);
		}
		catch (Throwable t) {
			Assert.assertTrue(t.toString(), t instanceof AssertionError);

			String message = t.getMessage();

			Assert.assertTrue(message, message.contains("The indicated directory is not a Liferay workspace"));
		}
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

		Matcher matcher = pattern.matcher(content);

		Assert.assertTrue(matcher.matches());
	}

	/*
		private void _enableStandaloneProfile(File pomXmlFile) throws Exception {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(pomXmlFile);

			Element projectElement = document.getDocumentElement();

			Element profilesElement = XMLTestUtil.getChildElement(projectElement, "profiles");

			Element profileElement = XMLTestUtil.getChildElement(profilesElement, "profile");

			Element activationElement = XMLTestUtil.getChildElement(profileElement, "activation");

			Element activeByDefaultElement = XMLTestUtil.getChildElement(activationElement, "activeByDefault");

			activeByDefaultElement.setTextContent("true");
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(pomXmlFile);

			transformer.transform(domSource, streamResult);
		}

	*/

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

	private File _extensionsDir = null;
	private File _rootDir = null;
	private File _workspaceDir = null;

}