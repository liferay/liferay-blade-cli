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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CreateCommandTest {

	@Before
	public void setUp() throws Exception {
		File testdir = IO.getFile("generated/test");

		if (testdir.exists()) {
			IO.delete(testdir);
			assertFalse(testdir.exists());
		}
	}

	@Test
	public void testCreateActivator() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "activator", "bar-activator"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/bar-activator";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/bar/activator/BarActivator.java"),
			".*^public class BarActivator implements BundleActivator.*$");

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "bar.activator-1.0.0.jar");
	}

	@Test
	public void testCreateGradleFragment() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "fragment", "-h",
			"com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/loginHook";

		checkFileExists(projectPath);

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "loginhook-1.0.0.jar");
	}

	@Test
	public void testCreateGradleMVCPortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "mvcportlet", "foo"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/foo";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		checkFileExists(projectPath + "/gradlew");

		checkFileExists(projectPath + "/gradlew.bat");

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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "foo-1.0.0.jar");
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPackage()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test", "-t", "mvcportlet", "-p",
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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "foo-1.0.0.jar");
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPortletSuffix() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "mvcportlet", "portlet-portlet"
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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "portlet.portlet-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "gradle.test-1.0.0.jar");
	}

	@Test
	public void testCreateGradleServiceBuilderDashes() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
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

		BuildTask buildServiceTask = executeGradleRunner(projectPath, "buildService");

		verifyGradleRunnerOutput(buildServiceTask);

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/backend-integration-api", "backend.integration-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/backend-integration-service", "backend.integration-service-1.0.0.jar");
	}

	@Test
	public void testCreateGradleServiceBuilderDefault() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
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
				".*compile project\\(\":guestbook-api\"\\).*");

		BuildTask buildService = executeGradleRunner(projectPath, "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/guestbook-api", "guestbook-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/guestbook-service", "guestbook-service-1.0.0.jar");
	}

	@Test
	public void testCreateGradleServiceBuilderDots() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
			"com.liferay.docs.guestbook", "com.liferay.docs.guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/com.liferay.docs.guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include \"com.liferay.docs.guestbook.api\", " +
			"\"com.liferay.docs.guestbook.svc\"");

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.api/bnd.bnd"),
			new String[] {
				".*Export-Package:\\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.svc/bnd.bnd"),
				".*Liferay-Service: true.*");

		BuildTask buildService = executeGradleRunner(projectPath, "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.api", "com.liferay.docs.guestbook-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.svc", "com.liferay.docs.guestbook-service-1.0.0.jar");
	}

	@Test
	public void testCreateGradleServicePreAction() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "service", "-s",
			"com.liferay.portal.kernel.events.LifecycleAction", "-c",
			"FooAction", "servicepreaction"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/servicepreaction";

		checkFileExists(projectPath + "/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/src/main/java/servicepreaction/FooAction.java"),
			new String[] {
				"^package servicepreaction;.*",
				".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*",
				".*service = LifecycleAction.class.*",
				".*^public class FooAction implements LifecycleAction \\{.*"
			});

		BuildTask buildService = executeGradleRunner(projectPath, "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/servicepreaction-api", "servicepreaction-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/servicepreaction-service", "servicepreaction-service-1.0.0.jar");
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicewrapper", "-s",
			"com.liferay.portal.service.UserLocalServiceWrapper",
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
				".*^import com.liferay.portal.service.UserLocalServiceWrapper;$.*",
				".*service = ServiceWrapper.class.*",
				".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*",
				".*public Serviceoverride\\(\\) \\{.*"
			});

		BuildTask buildService = executeGradleRunner(projectPath, "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/serviceoverride-api", "serviceoverride-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/serviceoverride-service", "serviceoverride-service-1.0.0.jar");
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
			".*Bundle-SymbolicName: barfoo.*");

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "barfoo-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "hello.world.portlet-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(projectPath, "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "hello.world.refresh-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/loginHook", "loginhook-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/gradle.test", "gradle.test-1.0.0.jar");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectApiPath()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules/nested/path",
			"-t", "servicebuilder", "-p", "com.liferay.sample", "sample"
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
				".*compile project\\(\":modules:nested:path:sample:sample-api\"\\).*");

		BuildTask buildService = executeGradleRunner(workspace.getPath(), "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDashes()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.sample", "workspace-sample"
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

		BuildTask buildService = executeGradleRunner(workspace.getPath(), "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-api", "workspace.sample-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-service", "workspace.sample-service-1.0.0.jar");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDefault()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.liferay.sample", "sample"
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
				".*compile project\\(\":modules:sample:sample-api\"\\).*");

		BuildTask buildService = executeGradleRunner(workspace.getPath(), "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDots()
		throws Exception {

		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.sample", "workspace.sample"
		};

		File workspace = new File("generated/test/workspace");

		makeWorkspace(workspace);

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace.sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace.sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.api/build.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.svc/build.gradle");

		BuildTask buildService = executeGradleRunner(workspace.getPath(), "buildService");

		verifyGradleRunnerOutput(buildService);

		BuildTask buildtask = executeGradleRunner(workspace.getPath() , "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/workspace.sample/com.sample.api", "workspace.sample-api-1.0.0.jar");

		verifyBuildOutput(projectPath + "/workspace.sample/com.sample.svc", "workspace.sample-service-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(workspace.getPath(), "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
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

		BuildTask buildtask = executeGradleRunner(workspace.getPath(), "build");

		verifyGradleRunnerOutput(buildtask);

		verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");

	}

	@Test
	public void testGetGradleTemplatesZip() throws Exception {
		File gradleTemplatesZip = new CreateCommand(new blade(), null).getGradleTemplatesZip();

		assertTrue(gradleTemplatesZip.exists());

		String gradleTemplatesName = gradleTemplatesZip.getName();

		assertTrue(gradleTemplatesName, gradleTemplatesName.startsWith("com.liferay.gradle.templates"));
		assertTrue(gradleTemplatesName, gradleTemplatesName.endsWith("1.0.9.jar"));
	}

	@Test
	public void testListTemplates() throws Exception {
		String[] args = {"create", "-l"};

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);

		blade blade = new bladenofail(ps);

		blade.run(args);

		String templateList = new String(output.toByteArray());

		assertNotNull(templateList);
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

	private BuildTask executeGradleRunner (String projectPath, String gradleArgument) {
		BuildResult buildResult = GradleRunner.create().withProjectDir(new File(projectPath)).withArguments(gradleArgument).build();

		BuildTask buildtask = null;

		for (BuildTask task : buildResult.getTasks()) {
			if (task.getPath().endsWith(gradleArgument)) {
				buildtask = task;
				break;
			}
		}

		return buildtask;
	}

	private void lacks(File file, String pattern) throws Exception {
		String content = new String(IO.read(file));

		assertFalse(
			Pattern.compile(
				pattern,
				Pattern.MULTILINE | Pattern.DOTALL).matcher(content).matches());
	}

	private void makeWorkspace(File workspace) throws Exception {
		workspace.mkdirs();

		String[] args = {"init", workspace.getPath()};

		new bladenofail().run(args);

		assertTrue(Util.isWorkspace(workspace));
	}

	private void verifyGradleRunnerOutput (BuildTask buildtask) {
		assertNotNull(buildtask);

		assertEquals(buildtask.getOutcome(), TaskOutcome.SUCCESS);
	}

	private void verifyBuildOutput (String projectPath, String fileName) {
		File file = IO.getFile(projectPath + "/build/libs/" + fileName);

		assertTrue(file.exists());
	}
}