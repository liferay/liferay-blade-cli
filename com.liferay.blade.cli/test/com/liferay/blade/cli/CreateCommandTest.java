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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;
import aQute.lib.io.IO;

import com.liferay.project.templates.ProjectTemplates;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.tooling.internal.consumer.ConnectorServices;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CreateCommandTest {
	private File testdir = IO.getFile("generated/test");

	@Before
	public void setUp() throws Exception {
		testdir.mkdirs();

		assertTrue(new File(testdir, "afile").createNewFile());
	}

	@After
	public void cleanUp() throws Exception {
		ConnectorServices.reset();

		if (testdir.exists()) {
			IO.delete(testdir);
			assertFalse(testdir.exists());
		}
	}

	@Test
	public void testCreateActivator() throws Exception {
		String[] gradleArgs = {
			"create", "-d", "generated/test", "-t", "activator",
			"bar-activator"
		};

		String[] mavenArgs = {
			"create", "-d", "generated/test", "-b", "maven", "-t", "activator",
			"bar-activator"
		};

		String projectPath = "generated/test/bar-activator";

		new bladenofail().run(gradleArgs);

		checkGradleBuildFiles(projectPath);

		contains(
			checkFileExists(
				projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "bar.activator-1.0.0.jar");
		verifyImportPackage(new File(projectPath + "/build/libs/bar.activator-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new bladenofail().run(mavenArgs);

		checkMavenBuildFiles(projectPath);

		contains(
			checkFileExists(
				projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[]{"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "bar-activator-1.0.0.jar");
		verifyImportPackage(new File(projectPath + "/target/bar-activator-1.0.0.jar"));
	}

	@Test
	public void testCreateApi() throws Exception {
		String[] gradleArgs = {
			"create", "-d", "generated/test", "-t", "api", "foo"
		};

		String[] mavenArgs = {
			"create", "-d", "generated/test", "-b", "maven", "-t", "api", "foo"
		};

		String projectPath = "generated/test/foo";

		new bladenofail().run(gradleArgs);

		checkGradleBuildFiles(projectPath);

		contains(
			checkFileExists(
				projectPath + "/src/main/java/foo/api/Foo.java"),
				".*^public interface Foo.*");

		contains(
			checkFileExists(
				projectPath + "/src/main/resources/foo/api/packageinfo"),
				"version 1.0.0");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		try (Jar jar = new Jar(new File(projectPath + "/build/libs/foo-1.0.0.jar"))) {
			assertEquals(
				"foo.api;version=\"1.0.0\"",
				jar.getManifest().getMainAttributes().getValue("Export-Package"));
		}

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new bladenofail().run(mavenArgs);

		checkMavenBuildFiles(projectPath);

		contains(
			checkFileExists(
				projectPath + "/src/main/java/foo/api/Foo.java"),
				".*^public interface Foo.*");

		contains(
			checkFileExists(
				projectPath + "/src/main/resources/foo/api/packageinfo"),
				"version 1.0.0");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[]{"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		verifyImportPackage(new File(projectPath + "/target/foo-1.0.0.jar"));

		try (Jar jar = new Jar(new File(projectPath + "/target/foo-1.0.0.jar"))) {
			assertEquals(
				"foo.api;version=\"1.0.0\"",
				jar.getManifest().getMainAttributes().getValue("Export-Package"));
		}
	}

	@Test
	public void testCreateFragment() throws Exception {
		String[] gradleArgs = {
			"create", "-d", "generated/test", "-t", "fragment", "-h",
			"com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		String[] mavenArgs = {
			"create", "-d", "generated/test", "-b", "maven", "-t", "fragment", "-h",
			"com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		String projectPath = "generated/test/loginHook";

		new bladenofail().run(gradleArgs);

		checkGradleBuildFiles(projectPath);

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "loginhook-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/loginhook-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new bladenofail().run(mavenArgs);

		checkMavenBuildFiles(projectPath);

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[]{"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "loginHook-1.0.0.jar");
		verifyImportPackage(new File(projectPath + "/target/loginHook-1.0.0.jar"));
	}

	@Test
	public void testCreateMVCPortlet() throws Exception {
		String[] gradleArgs = {
			"create", "-d", "generated/test", "-t", "mvc-portlet", "foo"
		};

		String[] mavenArgs = {
			"create", "-d", "generated/test", "-b", "maven", "-t",
			"mvc-portlet", "foo"
		};

		String projectPath = "generated/test/foo";

		new bladenofail().run(gradleArgs);

		checkGradleBuildFiles(projectPath);

		contains(
			checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/foo-1.0.0.jar"));

		FileUtils.deleteDirectory(IO.getFile(projectPath));

		new bladenofail().run(mavenArgs);

		checkMavenBuildFiles(projectPath);

		contains(
			checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		MavenRunnerUtil.executeMavenPackage(projectPath, new String[]{"clean", "package"});
		MavenRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		verifyImportPackage(new File(projectPath + "/target/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPackage()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test", "-t", "mvc-portlet", "-p",
			"com.liferay.test", "foo"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/foo";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/com/liferay/test/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists("generated/test/foo/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "com.liferay.test-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/com.liferay.test-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPortletSuffix() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "mvc-portlet", "portlet-portlet"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/portlet-portlet";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		contains(
			checkFileExists(projectPath + "/src/main/java/portlet/portlet/portlet/PortletPortlet.java"),
			".*^public class PortletPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "portlet.portlet-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/portlet.portlet-1.0.0.jar"));
	}

	@Test
	public void testCreateGradlePortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portlet", "-c", "Foo",
			"gradle.test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/gradle.test";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "gradle.test-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/gradle.test-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceBuilderDashes() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service-builder", "-p",
			"com.liferay.backend.integration", "backend-integration"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/backend-integration";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"backend-integration-api\", " +
			"\"backend-integration-service\"");

		contains(
			checkFileExists(projectPath + "/backend-integration-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.backend.integration.exception,\\\\.*",
				".*com.liferay.backend.integration.model,\\\\.*",
				".*com.liferay.backend.integration.service,\\\\.*",
				".*com.liferay.backend.integration.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/backend-integration-service/bnd.bnd"),
				".*Liferay-Service: true.*");

		BuildTask buildServiceTask = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildServiceTask);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-api",
				"com.liferay.backend.integration.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-service",
				"com.liferay.backend.integration.service-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/backend-integration-service/build/libs/com.liferay.backend.integration.service-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceBuilderDefault() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service-builder", "-p",
			"com.liferay.docs.guestbook", "guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"guestbook-api\", \"guestbook-service\"");

		contains(
			checkFileExists(projectPath + "/guestbook-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(projectPath + "/guestbook-service/bnd.bnd"),
				".*Liferay-Service: true.*");

		contains(
			checkFileExists(projectPath + "/guestbook-service/build.gradle"),
				".*compileOnly project\\(\":guestbook-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-api", "com.liferay.docs.guestbook.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-service", "com.liferay.docs.guestbook.service-1.0.0.jar");

		File serviceJar = new File(projectPath + "/guestbook-service/build/libs/com.liferay.docs.guestbook.service-1.0.0.jar");

		verifyImportPackage(serviceJar);

		try(JarFile serviceJarFile = new JarFile(serviceJar)) {
			String springContext = serviceJarFile.getManifest().getMainAttributes().getValue("Liferay-Spring-Context");

			assertTrue(springContext.equals("META-INF/spring"));
		}
	}

	@Test
	public void testCreateGradleServiceBuilderDots() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service-builder", "-p",
			"com.liferay.docs.guestbook", "com.liferay.docs.guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/com.liferay.docs.guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"com.liferay.docs.guestbook-api\", " +
			"\"com.liferay.docs.guestbook-service\"");

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook-api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook-service/bnd.bnd"),
				".*Liferay-Service: true.*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook-api",
				"com.liferay.docs.guestbook.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook-service",
				"com.liferay.docs.guestbook.service-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/com.liferay.docs.guestbook-service/build/libs/com.liferay.docs.guestbook.service-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleService() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service", "-s",
			"com.liferay.portal.kernel.events.LifecycleAction", "-c",
			"FooAction", "servicepreaction"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/servicepreaction";

		checkFileExists(projectPath + "/build.gradle");

		File file = new File(projectPath + "/src/main/java/servicepreaction/FooAction.java");

		contains(
			checkFileExists(file.getPath()),
			new String[] {
				"^package servicepreaction;.*",
				".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*",
				".*service = LifecycleAction.class.*",
				".*^public class FooAction implements LifecycleAction \\{.*"
			});

		List<String> lines = new ArrayList<String>();
		String line = null;

		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((line = reader.readLine()) !=null) {
				lines.add(line);
				if (line.equals("import com.liferay.portal.kernel.events.LifecycleAction;")) {
					lines.add("import com.liferay.portal.kernel.events.LifecycleEvent;");
					lines.add("import com.liferay.portal.kernel.events.ActionException;");
				}

				if (line.equals("public class FooAction implements LifecycleAction {")) {
					String s = new StringBuilder()
					           .append("@Override\n")
					           .append("public void processLifecycleEvent(LifecycleEvent lifecycleEvent)\n")
					           .append("throws ActionException {\n")
					           .append("System.out.println(\"login.event.pre=\" + lifecycleEvent);\n")
					           .append("}\n")
					           .toString();
					lines.add(s);
				}
			}
		}

		try(Writer writer = new FileWriter(file)) {
			for(String string : lines){
				writer.write(string + "\n");
			}
		}

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "servicepreaction-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/servicepreaction-1.0.0.jar"));
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service-wrapper", "-s",
			"com.liferay.portal.kernel.service.UserLocalServiceWrapper",
			"serviceoverride"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/serviceoverride";

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/serviceoverride/Serviceoverride.java"),
			new String[] {
				"^package serviceoverride;.*",
				".*^import com.liferay.portal.kernel.service.UserLocalServiceWrapper;$.*",
				".*service = ServiceWrapper.class.*",
				".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*",
				".*public Serviceoverride\\(\\) \\{.*"
			});

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "serviceoverride-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/serviceoverride-1.0.0.jar"));
	}

	@Test
	public void testCreateOnExistFolder() throws Exception {
		String[] args = {
			"create", "-d", "generated", "-t", "activator", "exist"
		};

		File existFile = IO.getFile("generated/exist/file.txt");

		if(!existFile.exists()) {
			IO.getFile("generated/exist").mkdirs();
			existFile.createNewFile();
			assertTrue(existFile.exists());
		}

		new bladenofail().run(args);

		String projectPath = "generated/exist";

		checkFileDoesNotExists(projectPath+"/bnd.bnd");
	}

	@Test
	public void testCreateGradleSymbolicName() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-p", "foo.bar", "barfoo"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/barfoo";

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			".*Bundle-SymbolicName: foo.bar.*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.bar-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/foo.bar-1.0.0.jar"));
	}

	@Test
	public void testCreatePortletConfigurationIcon() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portlet-configuration-icon", "-p", "blade.test", "icontest"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/icontest";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/configuration/icon/" +
				"IcontestPortletConfigurationIcon.java");

		contains(
			componentFile,
			".*^public class IcontestPortletConfigurationIcon.*extends BasePortletConfigurationIcon.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreatePortletToolbarContributor() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portlet-toolbar-contributor", "-p", "blade.test",  "toolbartest"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/toolbartest";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/toolbar/contributor/" +
				"ToolbartestPortletToolbarContributor.java");

		contains(
			componentFile,
			".*^public class ToolbartestPortletToolbarContributor.*implements PortletToolbarContributor.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreateProjectAllDefaults() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "hello-world-portlet"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/hello-world-portlet";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/src/main/java/hello/world/portlet/portlet/" +
				"HelloWorldPortlet.java");

		contains(
			portletFile,
			".*^public class HelloWorldPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.portlet-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/hello.world.portlet-1.0.0.jar"));
	}

	@Test
	public void testCreateProjectWithRefresh() throws Exception {
		String[] args = {
				"create", "-d", "generated/test", "hello-world-refresh"
			};

		new bladenofail().run(args);

		String projectPath = "generated/test/hello-world-refresh";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/src/main/java/hello/world/refresh/portlet/" +
				"HelloWorldRefreshPortlet.java");

		contains(
			portletFile,
			".*^public class HelloWorldRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.refresh-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/hello.world.refresh-1.0.0.jar"));
	}

	@Test
	public void testCreateSimulationPanelEntry() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "simulation-panel-entry", "-p", "test.simulator", "simulator"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/simulator";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = checkFileExists(
			projectPath + "/src/main/java/test/simulator/application/list/" +
				"SimulatorSimulationPanelApp.java");

		contains(
			componentFile,
			".*^public class SimulatorSimulationPanelApp.*extends BaseJSPPanelApp.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		verifyBuild(projectPath, projectPath, "test.simulator-1.0.0.jar");
	}

	@Test
	public void testCreateSpringMvcPortlet() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "spring-mvc-portlet", "-p", "test.spring.portlet", "spring-test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/spring-test";

		checkFileExists(projectPath);

		checkFileExists(
			projectPath + "/src/main/java/test/spring/portlet/portlet/" +
				"SpringTestPortletViewController.java");

		checkFileExists(projectPath + "/build.gradle");

		verifyBuild(projectPath, projectPath, "spring-test.war");
	}

	@Test
	public void testCreateTemplateContextContributor() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "template-context-contributor", "blade-test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/blade-test";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = checkFileExists(
			projectPath + "/src/main/java/blade/test/context/contributor/" +
				"BladeTestTemplateContextContributor.java");

		contains(
			componentFile,
			".*^public class BladeTestTemplateContextContributor.*implements TemplateContextContributor.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
	}

	@Test
	public void testCreateTheme() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "theme", "theme-test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/theme-test";

		checkFileExists(projectPath);

		checkFileDoesNotExists(projectPath + "/bnd.bnd");

		checkFileExists(projectPath + "/src/main/webapp/css/_custom.scss");

		File properties = checkFileExists(
			projectPath + "/src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		contains(properties, ".*^name=theme-test.*");

		File buildFile = new File(projectPath + "/build.gradle");

		FileWriter fileWriter = new FileWriter(buildFile, true);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write("\nbuildTheme { jvmArgs \"-Djava.awt.headless=true\" }");
		bufferWriter.close();

		verifyBuild(projectPath, projectPath, "theme-test.war");
	}

	@Test
	public void testCreateThemeContributor() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "theme-contributor", "-C", "foobar",
			"theme-contributor-test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/theme-contributor-test";

		checkFileExists(projectPath);

		File bnd = checkFileExists(projectPath + "/bnd.bnd");

		contains(bnd, ".*Liferay-Theme-Contributor-Type: foobar.*");

		verifyBuild(projectPath, projectPath, "theme.contributor.test-1.0.0.jar");
	}

	@Test
	public void testCreateWorkspaceGradleFragment() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/extensions", "-t",
			"fragment", "-h", "com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/extensions";

		checkFileExists(projectPath + "/loginHook");

		contains(
			checkFileExists(projectPath + "/loginHook/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		checkFileExists(projectPath + "/loginHook/build.gradle");

		lacks(
			checkFileExists(projectPath + "/loginHook/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/loginHook", "loginhook-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/loginHook/build/libs/loginhook-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradlePortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "-t",
			"portlet", "-c", "Foo", "gradle.test"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/gradle.test/build.gradle");

		checkFileDoesNotExists(projectPath + "/gradle.test/gradlew");

		contains(
			checkFileExists(
				projectPath + "/gradle.test/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		lacks(
			checkFileExists(projectPath + "/gradle.test/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/gradle.test", "gradle.test-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/gradle.test/build/libs/gradle.test-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectApiPath()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules/nested/path",
			"-t", "service-builder", "-p", "com.liferay.sample", "sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		assertTrue(
			new File("generated/test/workspace/modules/nested/path").mkdirs());

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/nested/path";

		checkFileExists(projectPath + "/sample/build.gradle");

		checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/sample/sample-service/build.gradle"),
				".*compileOnly project\\(\":modules:nested:path:sample:sample-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "com.liferay.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "com.liferay.sample.service-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/sample/sample-service/build/libs/com.liferay.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDashes()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"service-builder", "-p", "com.sample", "workspace-sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace-sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace-sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace-sample/workspace-sample-api/build.gradle");

		checkFileExists(
			projectPath + "/workspace-sample/workspace-sample-service/build.gradle");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-api",
				"com.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-service",
				"com.sample.service-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/workspace-sample/workspace-sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDefault()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"service-builder", "-p", "com.liferay.sample", "sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/sample/build.gradle");

		checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/sample/sample-service/build.gradle"),
				".*compileOnly project\\(\":modules:sample:sample-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "com.liferay.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "com.liferay.sample.service-1.0.0.jar");

		File serviceJar = new File(projectPath + "/sample/sample-service/build/libs/com.liferay.sample.service-1.0.0.jar");

		verifyImportPackage(serviceJar);

		try (JarFile serviceJarFile = new JarFile(serviceJar)) {
			String springContext = serviceJarFile.getManifest().getMainAttributes().getValue("Liferay-Spring-Context");

			assertTrue(springContext.equals("META-INF/spring"));
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDots()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"service-builder", "-p", "com.sample", "workspace.sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace.sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace.sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/workspace.sample-api/build.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/workspace.sample-service/build.gradle");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/workspace.sample-api",
				"com.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/workspace.sample-service",
				"com.sample.service-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/workspace.sample/workspace.sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceModuleLocation() throws Exception {
		String[] args = {"-b", "generated/test/workspace", "create", "foo"};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/foo");

		checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		contains(
			portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/foo/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceProjectAllDefaults() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "foo"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/foo");

		checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		contains(
			portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/foo/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceProjectWithRefresh() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps",
			"foo-refresh"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath =
				"generated/test/workspace/modules/apps/foo-refresh";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = checkFileExists(
				projectPath +
				"/src/main/java/foo/refresh/portlet/FooRefreshPortlet.java");

		contains(
			portletFile,
			".*^public class FooRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");

		verifyImportPackage(new File(projectPath + "/build/libs/foo.refresh-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceThemeLocation() throws Exception {
		String[] args = {
				"-b", "generated/test/workspace", "create", "-t", "theme",
				"theme-test"};
		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/wars/theme-test";

		checkFileExists(projectPath);

		checkFileDoesNotExists(projectPath + "/bnd.bnd");

		checkFileExists(projectPath + "/src/main/webapp/css/_custom.scss");

		File properties = checkFileExists(
			projectPath + "/src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		contains(properties, ".*^name=theme-test.*");

		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "war");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, "theme-test.war");
	}

	@Test
	public void testListTemplates() throws Exception {
		String[] args = {"create", "-l"};

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);

		blade blade = new bladenofail(ps);

		blade.run(args);

		String templateList = new String(output.toByteArray());

		List<String> templateNames =
			new ArrayList<>(ProjectTemplates.getTemplates().keySet());

		for (String templateName : templateNames) {
			assertTrue(templateList.contains(templateName));
		}
	}

	@Test
	public void testWrongTemplateTyping() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "activatorXXX", "wrong-activator"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/wrong-activator";

		checkFileDoesNotExists(projectPath);
	}

	private void checkGradleBuildFiles(String projectPath) {
		checkFileExists(projectPath);
		checkFileExists(projectPath + "/bnd.bnd");
		checkFileExists(projectPath + "/build.gradle");
		checkFileExists(projectPath + "/gradlew");
		checkFileExists(projectPath + "/gradlew.bat");
	}

	private void checkMavenBuildFiles(String projectPath) {
		checkFileExists(projectPath);
		checkFileExists(projectPath + "/bnd.bnd");
		checkFileExists(projectPath + "/pom.xml");
		checkFileExists(projectPath + "/mvnw");
		checkFileExists(projectPath + "/mvnw.cmd");
	}

	private File checkFileDoesNotExists(String path) {
		File file = IO.getFile(path);

		assertFalse(file.exists());

		return file;
	}

	private File checkFileExists(String path) {
		File file = IO.getFile(path);

		assertTrue(file.exists());

		return file;
	}

	private void contains(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		contains(content, pattern);
	}

	private void contains(File file, String[] patterns) throws Exception {
		String content = new String(IO.read(file));

		for (String pattern : patterns) {
			contains(content, pattern);
		}
	}

	private void contains(String content, String pattern) throws Exception {
		assertTrue(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private void lacks(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		assertFalse(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private void makeWorkspace(File workspace) throws Exception {
		String[] args = {"-b", workspace.getParentFile().getPath(), "init", workspace.getName()};

		new bladenofail().run(args);

		assertTrue(Util.isWorkspace(workspace));
	}

	private void verifyBuild(String runnerPath, String projectPath, String outputFileName) {
		BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(runnerPath, "build");
		GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
		GradleRunnerUtil.verifyBuildOutput(projectPath, outputFileName);
	}

	private void verifyImportPackage(File serviceJar) throws Exception {
		try (Jar jar = new Jar(serviceJar)) {
			Manifest m = jar.getManifest();
			Domain domain = Domain.domain(m);
			Parameters imports = domain.getImportPackage();

			for (String key : imports.keySet()) {
				assertFalse(key.isEmpty());
			}
		}
	}
}