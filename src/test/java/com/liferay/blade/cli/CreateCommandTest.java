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

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;

import aQute.lib.io.IO;

import com.liferay.project.templates.ProjectTemplates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import org.gradle.testkit.runner.BuildTask;
import org.gradle.tooling.internal.consumer.ConnectorServices;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CreateCommandTest {

	@After
	public void cleanUp() throws Exception {
		ConnectorServices.reset();

		if (_testdir.exists()) {
			IO.delete(_testdir);
			Assert.assertFalse(_testdir.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		_testdir.mkdirs();

		Assert.assertTrue(new File(_testdir, "afile").createNewFile());
	}

	@Test
	public void testCreateActivator() throws Exception {
		String[] gradleArgs = {"create", "-d", "build/test", "-t", "activator", "bar-activator"};

		String[] mavenArgs = {"create", "-d", "build/test", "-b", "maven", "-t", "activator", "bar-activator"};

		String projectPath = "build/test/bar-activator";

		new BladeNoFail().run(gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "bar.activator-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/bar.activator-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new BladeNoFail().run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[] {"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "bar-activator-1.0.0.jar");
		_verifyImportPackage(new File(projectPath + "/target/bar-activator-1.0.0.jar"));
	}

	@Test
	public void testCreateApi() throws Exception {
		String[] gradleArgs = {"create", "-d", "build/test", "-t", "api", "foo"};

		String[] mavenArgs = {"create", "-d", "build/test", "-b", "maven", "-t", "api", "foo"};

		String projectPath = "build/test/foo";

		new BladeNoFail().run(gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/src/main/java/foo/api/Foo.java"), ".*^public interface Foo.*");

		_contains(_checkFileExists(projectPath + "/src/main/resources/foo/api/packageinfo"), "version 1.0.0");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		try (Jar jar = new Jar(new File(projectPath + "/build/libs/foo-1.0.0.jar"))) {
			Manifest manifest = jar.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			Assert.assertEquals("foo.api;version=\"1.0.0\"", mainAttributes.getValue("Export-Package"));
		}

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new BladeNoFail().run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/src/main/java/foo/api/Foo.java"), ".*^public interface Foo.*");

		_contains(_checkFileExists(projectPath + "/src/main/resources/foo/api/packageinfo"), "version 1.0.0");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[] {"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		_verifyImportPackage(new File(projectPath + "/target/foo-1.0.0.jar"));

		try (Jar jar = new Jar(new File(projectPath + "/target/foo-1.0.0.jar"))) {
			Manifest manifest = jar.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			Assert.assertEquals("foo.api;version=\"1.0.0\"", mainAttributes.getValue("Export-Package"));
		}
	}

	@Test
	public void testCreateFragment() throws Exception {
		String[] gradleArgs =
			{"create", "-d", "build/test", "-t", "fragment", "-h", "com.liferay.login.web", "-H", "1.0.0", "loginHook"};

		String[] mavenArgs = {
			"create", "-d", "build/test", "-b", "maven", "-t", "fragment", "-h", "com.liferay.login.web", "-H", "1.0.0",
			"loginHook"
		};

		String projectPath = "build/test/loginHook";

		new BladeNoFail().run(gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		_contains(_checkFileExists(projectPath + "/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "loginhook-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/loginhook-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new BladeNoFail().run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[] {"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "loginHook-1.0.0.jar");
		_verifyImportPackage(new File(projectPath + "/target/loginHook-1.0.0.jar"));
	}

	@Test
	public void testCreateFragmentWithoutHostOptions() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "fragment", "loginHook"};

		String content = TestUtil.runBlade(args);

		Assert.assertTrue(content, content.contains("\"-t fragment\" options missing"));

		args = new String[]
			{"create", "-d", "build/test", "-t", "fragment", "-h", "com.liferay.login.web", "loginHook"};

		content = TestUtil.runBlade(args);

		Assert.assertTrue(content, content.contains("\"-t fragment\" options missing"));

		args = new String[] {"create", "-d", "build/test", "-t", "fragment", "-H", "1.0.0", "loginHook"};

		content = TestUtil.runBlade(args);

		Assert.assertTrue(content, content.contains("\"-t fragment\" options missing"));
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPackage() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "mvc-portlet", "-p", "com.liferay.test", "foo"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/foo";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/com/liferay/test/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_contains(_checkFileExists("build/test/foo/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "com.liferay.test-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/com.liferay.test-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPortletSuffix() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "mvc-portlet", "portlet-portlet"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/portlet-portlet";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/portlet/portlet/portlet/PortletPortlet.java"),
			".*^public class PortletPortlet extends MVCPortlet.*$");

		_contains(_checkFileExists(projectPath + "/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "portlet.portlet-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/portlet.portlet-1.0.0.jar"));
	}

	@Test
	public void testCreateGradlePortletProject() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "portlet", "-c", "Foo", "gradle.test"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/gradle.test";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/build.gradle");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*", ".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*", ".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "gradle.test-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/gradle.test-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleService() throws Exception {
		String[] args = {
			"create", "-d", "build/test", "-t", "service", "-s", "com.liferay.portal.kernel.events.LifecycleAction",
			"-c", "FooAction", "servicepreaction"
		};

		new BladeNoFail().run(args);

		String projectPath = "build/test/servicepreaction";

		_checkFileExists(projectPath + "/build.gradle");

		File file = new File(projectPath + "/src/main/java/servicepreaction/FooAction.java");

		_contains(
			_checkFileExists(file.getPath()),
			new String[] {
				"^package servicepreaction;.*", ".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*",
				".*service = LifecycleAction.class.*", ".*^public class FooAction implements LifecycleAction \\{.*"
			});

		List<String> lines = new ArrayList<>();
		String line = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line = reader.readLine()) != null) {
				lines.add(line);

				if (line.equals("import com.liferay.portal.kernel.events.LifecycleAction;")) {
					lines.add("import com.liferay.portal.kernel.events.LifecycleEvent;");
					lines.add("import com.liferay.portal.kernel.events.ActionException;");
				}

				if (line.equals("public class FooAction implements LifecycleAction {")) {
					StringBuilder sb = new StringBuilder();

					sb.append("@Override\n");
					sb.append("public void processLifecycleEvent(LifecycleEvent lifecycleEvent)\n");
					sb.append("throws ActionException {\n");
					sb.append("System.out.println(\"login.event.pre=\" + lifecycleEvent);\n");
					sb.append("}\n");

					lines.add(sb.toString());
				}
			}
		}

		try (Writer writer = new FileWriter(file)) {
			for (String string : lines) {
				writer.write(string + "\n");
			}
		}

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "servicepreaction-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/servicepreaction-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceBuilderDashes() throws Exception {
		String[] args = {
			"create", "-d", "build/test", "-t", "service-builder", "-p", "com.liferay.backend.integration",
			"backend-integration"
		};

		new BladeNoFail().run(args);

		String projectPath = "build/test/backend-integration";

		_contains(
			_checkFileExists(projectPath + "/settings.gradle"),
			"include \"backend-integration-api\", \"backend-integration-service\"");

		_contains(
			_checkFileExists(projectPath + "/backend-integration-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*", ".*com.liferay.backend.integration.exception,\\\\.*",
				".*com.liferay.backend.integration.model,\\\\.*", ".*com.liferay.backend.integration.service,\\\\.*",
				".*com.liferay.backend.integration.service.persistence.*"
			});

		_contains(_checkFileExists(projectPath + "/backend-integration-service/bnd.bnd"), ".*Liferay-Service: true.*");

		BuildTask buildServiceTask = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildServiceTask);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/backend-integration-api", "com.liferay.backend.integration.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/backend-integration-service", "com.liferay.backend.integration.service-1.0.0.jar");

		_verifyImportPackage(
			new File(
				projectPath +
					"/backend-integration-service/build/libs/com.liferay.backend.integration.service-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceBuilderDefault() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "service-builder", "-p", "com.liferay.docs.guestbook", "guestbook"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/guestbook";

		_contains(
			_checkFileExists(projectPath + "/settings.gradle"), "include \"guestbook-api\", \"guestbook-service\"");

		_contains(
			_checkFileExists(projectPath + "/guestbook-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*", ".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*", ".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		_contains(_checkFileExists(projectPath + "/guestbook-service/bnd.bnd"), ".*Liferay-Service: true.*");

		File file = _checkFileExists(projectPath + "/guestbook-service/build.gradle");

		_contains(file, ".*compileOnly project\\(\":guestbook-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-api", "com.liferay.docs.guestbook.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/guestbook-service", "com.liferay.docs.guestbook.service-1.0.0.jar");

		File serviceJar = new File(
			projectPath + "/guestbook-service/build/libs/com.liferay.docs.guestbook.service-1.0.0.jar");

		_verifyImportPackage(serviceJar);

		try (JarFile serviceJarFile = new JarFile(serviceJar)) {
			Manifest manifest = serviceJarFile.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			String springContext = mainAttributes.getValue("Liferay-Spring-Context");

			Assert.assertTrue(springContext.equals("META-INF/spring"));
		}
	}

	@Test
	public void testCreateGradleServiceBuilderDots() throws Exception {
		String[] args = {
			"create", "-d", "build/test", "-t", "service-builder", "-p", "com.liferay.docs.guestbook",
			"com.liferay.docs.guestbook"
		};

		new BladeNoFail().run(args);

		String projectPath = "build/test/com.liferay.docs.guestbook";

		_contains(
			_checkFileExists(projectPath + "/settings.gradle"),
			"include \"com.liferay.docs.guestbook-api\", \"com.liferay.docs.guestbook-service\"");

		_contains(
			_checkFileExists(projectPath + "/com.liferay.docs.guestbook-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*", ".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*", ".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		_contains(
			_checkFileExists(projectPath + "/com.liferay.docs.guestbook-service/bnd.bnd"), ".*Liferay-Service: true.*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/com.liferay.docs.guestbook-api", "com.liferay.docs.guestbook.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/com.liferay.docs.guestbook-service", "com.liferay.docs.guestbook.service-1.0.0.jar");

		_verifyImportPackage(
			new File(
				projectPath +
					"/com.liferay.docs.guestbook-service/build/libs/com.liferay.docs.guestbook.service-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		String[] args = {
			"create", "-d", "build/test", "-t", "service-wrapper", "-s",
			"com.liferay.portal.kernel.service.UserLocalServiceWrapper", "serviceoverride"
		};

		new BladeNoFail().run(args);

		String projectPath = "build/test/serviceoverride";

		_checkFileExists(projectPath + "/build.gradle");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/serviceoverride/Serviceoverride.java"),
			new String[] {
				"^package serviceoverride;.*",
				".*^import com.liferay.portal.kernel.service.UserLocalServiceWrapper;$.*",
				".*service = ServiceWrapper.class.*",
				".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*",
				".*public Serviceoverride\\(\\) \\{.*"
			});

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "serviceoverride-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/serviceoverride-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleSymbolicName() throws Exception {
		String[] args = {"create", "-t", "mvc-portlet", "-d", "build/test", "-p", "foo.bar", "barfoo"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/barfoo";

		_checkFileExists(projectPath + "/build.gradle");

		_contains(_checkFileExists(projectPath + "/bnd.bnd"), ".*Bundle-SymbolicName: foo.bar.*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.bar-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/foo.bar-1.0.0.jar"));
	}

	@Test
	public void testCreateMissingArgument() throws Exception {
		String[] args = {"create", "foobar"};

		String content = TestUtil.runBlade(args);

		boolean containsError = content.contains("The following option is required");

		Assert.assertTrue(containsError);
	}

	@Test
	public void testCreateMVCPortlet() throws Exception {
		String[] gradleArgs = {"create", "-d", "build/test", "-t", "mvc-portlet", "foo"};

		String[] mavenArgs = {"create", "-d", "build/test", "-b", "maven", "-t", "mvc-portlet", "foo"};

		String projectPath = "build/test/foo";

		new BladeNoFail().run(gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_contains(_checkFileExists(projectPath + "/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/foo-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new BladeNoFail().run(mavenArgs);

		_checkMavenBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[] {"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		_verifyImportPackage(new File(projectPath + "/target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateNpmAngular() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "npm-angular-portlet", "npmangular"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/npmangular";

		_checkFileExists(projectPath + "/build.gradle");

		File jsp = _checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_contains(jsp, ".*<aui:script require=\"npmangular@1.0.0\">.*");

		_contains(jsp, ".*npmangular100.default.*");
	}

	@Test
	public void testCreateOnExistFolder() throws Exception {
		String[] args = {"create", "-d", "build", "-t", "activator", "exist"};

		File existFile = IO.getFile("build/exist/file.txt");

		if (!existFile.exists()) {
			IO.getFile("build/exist").mkdirs();
			existFile.createNewFile();
			Assert.assertTrue(existFile.exists());
		}

		new BladeNoFail().run(args);

		String projectPath = "build/exist";

		_checkFileDoesNotExists(projectPath + "/bnd.bnd");
	}

	@Test
	public void testCreatePortletConfigurationIcon() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "portlet-configuration-icon", "-p", "blade.test", "icontest"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/icontest";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/configuration/icon/IcontestPortletConfigurationIcon.java");

		_contains(
			componentFile, ".*^public class IcontestPortletConfigurationIcon.*extends BasePortletConfigurationIcon.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreatePortletToolbarContributor() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "portlet-toolbar-contributor", "-p", "blade.test", "toolbartest"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/toolbartest";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/toolbar/contributor" +
				"/ToolbartestPortletToolbarContributor.java");

		_contains(
			componentFile,
			".*^public class ToolbartestPortletToolbarContributor.*implements PortletToolbarContributor.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreateProjectAllDefaults() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "mvc-portlet", "hello-world-portlet"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/hello-world-portlet";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(
			projectPath + "/src/main/java/hello/world/portlet/portlet/HelloWorldPortlet.java");

		_contains(portletFile, ".*^public class HelloWorldPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.portlet-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/hello.world.portlet-1.0.0.jar"));
	}

	@Test
	public void testCreateProjectWithRefresh() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "mvc-portlet", "hello-world-refresh"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/hello-world-refresh";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(
			projectPath + "/src/main/java/hello/world/refresh/portlet/HelloWorldRefreshPortlet.java");

		_contains(portletFile, ".*^public class HelloWorldRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.refresh-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/hello.world.refresh-1.0.0.jar"));
	}

	@Test
	public void testCreateSimulationPanelEntry() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "simulation-panel-entry", "-p", "test.simulator", "simulator"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/simulator";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/test/simulator/application/list/SimulatorSimulationPanelApp.java");

		_contains(componentFile, ".*^public class SimulatorSimulationPanelApp.*extends BaseJSPPanelApp.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_verifyBuild(projectPath, projectPath, "test.simulator-1.0.0.jar");
	}

	@Test
	public void testCreateSpringMvcPortlet() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "spring-mvc-portlet", "-p", "test.spring.portlet", "spring-test"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/spring-test";

		_checkFileExists(projectPath);

		_checkFileExists(
			projectPath + "/src/main/java/test/spring/portlet/portlet/SpringTestPortletViewController.java");

		_checkFileExists(projectPath + "/build.gradle");

		_verifyBuild(projectPath, projectPath, "spring-test.war");
	}

	@Test
	public void testCreateTemplateContextContributor() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "template-context-contributor", "blade-test"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/blade-test";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/context/contributor/BladeTestTemplateContextContributor.java");

		_contains(
			componentFile,
			".*^public class BladeTestTemplateContextContributor.*implements TemplateContextContributor.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		_verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreateTheme() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "theme", "theme-test"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/theme-test";

		_checkFileExists(projectPath);

		_checkFileDoesNotExists(projectPath + "/bnd.bnd");

		_checkFileExists(projectPath + "/src/main/webapp/css/_custom.scss");

		File properties = _checkFileExists(projectPath + "/src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		_contains(properties, ".*^name=theme-test.*");

		File buildFile = new File(projectPath + "/build.gradle");

		FileWriter fileWriter = new FileWriter(buildFile, true);

		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		bufferWriter.write("\nbuildTheme { jvmArgs \"-Djava.awt.headless=true\" }");
		bufferWriter.close();

		_verifyBuild(projectPath, projectPath, "theme-test.war");
	}

	@Test
	public void testCreateThemeContributor() throws Exception {
		String[] args =
			{"create", "-d", "build/test", "-t", "theme-contributor", "-C", "foobar", "theme-contributor-test"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/theme-contributor-test";

		_checkFileExists(projectPath);

		File bnd = _checkFileExists(projectPath + "/bnd.bnd");

		_contains(bnd, ".*Liferay-Theme-Contributor-Type: foobar.*");

		_verifyBuild(projectPath, projectPath, "theme.contributor.test-1.0.0.jar");
	}

	@Test
	public void testCreateWarHookLocation() throws Exception {
		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		_testCreateWar(workspace, "war-hook", "war-hook-test");
	}

	@Test
	public void testCreateWarMVCPortletLocation() throws Exception {
		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		_testCreateWar(workspace, "war-mvc-portlet", "war-portlet-test");
	}

	@Test
	public void testCreateWorkspaceGradleFragment() throws Exception {
		String[] args = {
			"create", "-d", "build/test/workspace/modules/extensions", "-t", "fragment", "-h", "com.liferay.login.web",
			"-H", "1.0.0", "loginHook"
		};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules/extensions";

		_checkFileExists(projectPath + "/loginHook");

		_contains(
			_checkFileExists(projectPath + "/loginHook/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		_checkFileExists(projectPath + "/loginHook/build.gradle");

		_lacks(_checkFileExists(projectPath + "/loginHook/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/loginHook", "loginhook-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/loginHook/build/libs/loginhook-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradlePortletProject() throws Exception {
		String[] args =
			{"create", "-d", "build/test/workspace/modules/apps", "-t", "portlet", "-c", "Foo", "gradle.test"};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules/apps";

		_checkFileExists(projectPath + "/gradle.test/build.gradle");

		_checkFileDoesNotExists(projectPath + "/gradle.test/gradlew");

		_contains(
			_checkFileExists(projectPath + "/gradle.test/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*", ".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*", ".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		_lacks(
			_checkFileExists(projectPath + "/gradle.test/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/gradle.test", "gradle.test-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/gradle.test/build/libs/gradle.test-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectApiPath() throws Exception {
		String[] args = {
			"create", "-d", "build/test/workspace/modules/nested/path", "-t", "service-builder", "-p",
			"com.liferay.sample", "sample"
		};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		Assert.assertTrue(new File("build/test/workspace/modules/nested/path").mkdirs());

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules/nested/path";

		_checkFileExists(projectPath + "/sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		_checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		_checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		File file = _checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		_contains(file, ".*compileOnly project\\(\":modules:nested:path:sample:sample-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "com.liferay.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/sample/sample-service", "com.liferay.sample.service-1.0.0.jar");

		_verifyImportPackage(
			new File(projectPath + "/sample/sample-service/build/libs/com.liferay.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDashes() throws Exception {
		String[] args = {
			"create", "-d", "build/test/workspace/modules", "-t", "service-builder", "-p", "com.sample",
			"workspace-sample"
		};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules";

		_checkFileExists(projectPath + "/workspace-sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/workspace-sample/settings.gradle");

		_checkFileExists(projectPath + "/workspace-sample/workspace-sample-api/build.gradle");

		_checkFileExists(projectPath + "/workspace-sample/workspace-sample-service/build.gradle");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/workspace-sample/workspace-sample-api", "com.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/workspace-sample/workspace-sample-service", "com.sample.service-1.0.0.jar");

		_verifyImportPackage(
			new File(
				projectPath + "/workspace-sample/workspace-sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDefault() throws Exception {
		String[] args = {
			"create", "-d", "build/test/workspace/modules", "-t", "service-builder", "-p", "com.liferay.sample",
			"sample"
		};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules";

		_checkFileExists(projectPath + "/sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		_checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		_checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		File file = _checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		_contains(file, ".*compileOnly project\\(\":modules:sample:sample-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "com.liferay.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/sample/sample-service", "com.liferay.sample.service-1.0.0.jar");

		File serviceJar = new File(
			projectPath + "/sample/sample-service/build/libs/com.liferay.sample.service-1.0.0.jar");

		_verifyImportPackage(serviceJar);

		try (JarFile serviceJarFile = new JarFile(serviceJar)) {
			Manifest manifest = serviceJarFile.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			String springContext = mainAttributes.getValue("Liferay-Spring-Context");

			Assert.assertTrue(springContext.equals("META-INF/spring"));
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDots() throws Exception {
		String[] args = {
			"create", "-d", "build/test/workspace/modules", "-t", "service-builder", "-p", "com.sample",
			"workspace.sample"
		};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules";

		_checkFileExists(projectPath + "/workspace.sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/workspace.sample/settings.gradle");

		_checkFileExists(projectPath + "/workspace.sample/workspace.sample-api/build.gradle");

		_checkFileExists(projectPath + "/workspace.sample/workspace.sample-service/build.gradle");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/workspace.sample/workspace.sample-api", "com.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/workspace.sample/workspace.sample-service", "com.sample.service-1.0.0.jar");

		_verifyImportPackage(
			new File(
				projectPath + "/workspace.sample/workspace.sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceModuleLocation() throws Exception {
		String[] args = {"--base", "build/test/workspace", "create", "-t", "mvc-portlet", "foo"};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules";

		_checkFileExists(projectPath + "/foo");

		_checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		_contains(portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/foo/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceProjectAllDefaults() throws Exception {
		String[] args = {"create", "-d", "build/test/workspace/modules/apps", "-t", "mvc-portlet", "foo"};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules/apps";

		_checkFileExists(projectPath + "/foo");

		_checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		_contains(portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/foo/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceProjectWithRefresh() throws Exception {
		String[] args = {"create", "-d", "build/test/workspace/modules/apps", "-t", "mvc-portlet", "foo-refresh"};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/modules/apps/foo-refresh";

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/src/main/java/foo/refresh/portlet/FooRefreshPortlet.java");

		_contains(portletFile, ".*^public class FooRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");

		_verifyImportPackage(new File(projectPath + "/build/libs/foo.refresh-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceThemeLocation() throws Exception {
		String[] args = {"--base", "build/test/workspace", "create", "-t", "theme", "theme-test"};
		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		String projectPath = "build/test/workspace/wars/theme-test";

		_checkFileExists(projectPath);

		_checkFileDoesNotExists(projectPath + "/bnd.bnd");

		_checkFileExists(projectPath + "/src/main/webapp/css/_custom.scss");

		File properties = _checkFileExists(projectPath + "/src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		_contains(properties, ".*^name=theme-test.*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "war");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "theme-test.war");
	}

	@Test
	public void testCreateWorkspaceTypeValid() throws Exception {
		String[] args = {"--base", "build/test/workspace/modules", "create", "-t", "soy-portlet", "foo"};

		File workspace = new File("build/test/workspace");

		_makeWorkspace(workspace);

		new BladeNoFail().run(args);

		File buildGradle = new File(workspace, "modules/foo/build.gradle");

		_checkFileExists(buildGradle.getAbsolutePath());

		String content = new String(IO.read(buildGradle));

		Assert.assertEquals(1, StringUtils.countMatches(content, '{'));

		Assert.assertEquals(1, StringUtils.countMatches(content, '}'));
	}

	@Test
	public void testLiferayVersion() throws Exception {
		String[] sevenZeroArgs = {"--base", "build/test", "create", "-t", "npm-angular-portlet", "seven-zero"};

		new BladeNoFail().run(sevenZeroArgs);

		File npmbundlerrc = new File("build/test/seven-zero/build.gradle");

		String content = new String(IO.read(npmbundlerrc));

		Assert.assertFalse(content.contains("js.loader.modules.extender.api"));

		String[] sevenOneArgs =
			{"--base", "build/test", "create", "-t", "npm-angular-portlet", "-v", "7.1", "seven-one"};

		new BladeNoFail().run(sevenOneArgs);

		npmbundlerrc = new File("build/test/seven-one/build.gradle");

		content = new String(IO.read(npmbundlerrc));

		Assert.assertTrue(content.contains("js.loader.modules.extender.api"));
	}

	@Test
	public void testListTemplates() throws Exception {
		String[] args = {"create", "-l"};

		String templateList = TestUtil.runBlade(args);

		List<String> templateNames = new ArrayList<>(ProjectTemplates.getTemplates().keySet());

		for (String templateName : templateNames) {
			Assert.assertTrue(templateList.contains(templateName));
		}
	}

	@Test
	public void testWrongTemplateTyping() throws Exception {
		String[] args = {"create", "-d", "build/test", "-t", "activatorXXX", "wrong-activator"};

		new BladeNoFail().run(args);

		String projectPath = "build/test/wrong-activator";

		_checkFileDoesNotExists(projectPath);
	}

	private File _checkFileDoesNotExists(String path) {
		File file = IO.getFile(path);

		Assert.assertFalse(file.exists());

		return file;
	}

	private File _checkFileExists(String path) {
		File file = IO.getFile(path);

		Assert.assertTrue(file.exists());

		return file;
	}

	private void _checkGradleBuildFiles(String projectPath) {
		_checkFileExists(projectPath);
		_checkFileExists(projectPath + "/bnd.bnd");
		_checkFileExists(projectPath + "/build.gradle");
		_checkFileExists(projectPath + "/gradlew");
		_checkFileExists(projectPath + "/gradlew.bat");
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

	private void _lacks(File file, String regex) throws Exception {
		String content = new String(IO.read(file));

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Assert.assertFalse(pattern.matcher(content).matches());
	}

	private void _makeWorkspace(File workspace) throws Exception {
		String[] args = {"--base", workspace.getParentFile().getPath(), "init", workspace.getName()};

		new BladeNoFail().run(args);

		Assert.assertTrue(Util.isWorkspace(workspace));
	}

	private void _testCreateWar(File workspace, String projectType, String projectName) throws Exception {
		String[] args = {"--base", workspace.toString(), "create", "-t", projectType, projectName};

		new BladeNoFail().run(args);

		String projectPath = new File(workspace, "wars/" + projectName).getAbsolutePath();

		_checkFileExists(projectPath);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getAbsolutePath(), "war");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, projectName + ".war");
	}

	private void _verifyBuild(String runnerPath, String projectPath, String outputFileName) {
		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(runnerPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, outputFileName);
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

	private static File _testdir = IO.getFile("build/test");

}