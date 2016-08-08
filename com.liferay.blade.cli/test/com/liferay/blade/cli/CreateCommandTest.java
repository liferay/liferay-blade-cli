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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gradle.testkit.runner.BuildTask;
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "bar.activator-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "loginhook-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "portlet.portlet-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "gradle.test-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildServiceTask = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildServiceTask);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-api",
					"backend.integration-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/backend-integration-service",
					"backend.integration-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-api", "guestbook-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/guestbook-service", "guestbook-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(projectPath, "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.api",
					"com.liferay.docs.guestbook-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/com.liferay.docs.guestbook.svc",
					"com.liferay.docs.guestbook-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "servicepreaction-1.0.0.jar");
		}
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicewrapper", "-s",
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "serviceoverride-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "barfoo-1.0.0.jar");
		}
	}

	@Test
	public void testCreatePortletConfigurationIcon() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portletconfigurationicon", "-p", "blade.test", "icontest"
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

		verifyBuild(projectPath, projectPath, "icontest-1.0.0.jar");
	}

	@Test
	public void testCreatePortletToolbarContributor() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "portlettoolbarcontributor", "-p", "blade.test",  "toolbartest"
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

		verifyBuild(projectPath, projectPath, "toolbartest-1.0.0.jar");
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.portlet-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "hello.world.refresh-1.0.0.jar");
		}
	}

	@Test
	public void testCreateSimulationPanelEntry() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "simulationpanelentry", "-p", "test.simulator", "simulator"
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

		verifyBuild(projectPath, projectPath, "simulator-1.0.0.jar");
	}

	@Test
	public void testCreateTemplateContextContributor() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "templatecontextcontributor", "blade-test"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/blade-test";

		checkFileExists(projectPath);

		checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = checkFileExists(
			projectPath + "/src/main/java/blade/test/theme/contributor/" +
				"BladeTestTemplateContextContributor.java");

		contains(
			componentFile,
			".*^public class BladeTestTemplateContextContributor.*implements TemplateContextContributor.*$");

		File gradleBuildFile = checkFileExists(projectPath + "/build.gradle");

		contains(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		verifyBuild(projectPath, projectPath, "blade.test-1.0.0.jar");
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/loginHook", "loginhook-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/gradle.test", "gradle.test-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-api",
					"workspace.sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace-sample/workspace-sample-service",
					"workspace.sample-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-service", "sample-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildService);
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/com.sample.api",
					"workspace.sample-api-1.0.0.jar");
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/workspace.sample/com.sample.svc",
					"workspace.sample-service-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");
		}
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

		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");
		}
	}

	@Test
	public void testGetGradleTemplatesZip() throws Exception {
		File gradleTemplatesZip = new CreateCommand(new blade(), null).getGradleTemplatesZip();

		assertTrue(gradleTemplatesZip.exists());

		String gradleTemplatesName = gradleTemplatesZip.getName();

		assertTrue(gradleTemplatesName, gradleTemplatesName.startsWith("com.liferay.gradle.templates"));
		assertTrue(gradleTemplatesName, gradleTemplatesName.contains(CreateCommand.TEMPLATES_VERSION));
	}

	@Test
	public void testListTemplates() throws Exception {
		String[] args = {"create", "-l"};

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);

		blade blade = new bladenofail(ps);

		blade.run(args);

		String templateList = new String(output.toByteArray());

		for (String templateName : CreateCommand.TEMPLATE_NAMES) {
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
		workspace.mkdirs();

		String[] args = {"init", workspace.getPath()};

		new bladenofail().run(args);

		assertTrue(Util.isWorkspace(workspace));
	}

	private void verifyBuild(String runnerPath, String projectPath, String outputFileName) {
		if (SysProps.verifyBuilds) {
			BuildTask buildtask = GradleRunnerUtil.executeGradleRunner(runnerPath, "build");
			GradleRunnerUtil.verifyGradleRunnerOutput(buildtask);
			GradleRunnerUtil.verifyBuildOutput(projectPath, outputFileName);
		}
	}
}