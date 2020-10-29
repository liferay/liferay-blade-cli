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

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.GradleRunnerUtil;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.project.templates.ProjectTemplates;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class CreateCommandTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testCreateApi() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {"create", "--base", workspace.getAbsolutePath(), "-t", "api", "foo"};

		TestUtil.runBlade(workspace, _extensionsDir, gradleArgs);

		File projectDir = new File(workspace, "modules/foo");

		String projectPath = projectDir.getAbsolutePath();

		_checkGradleBuildFiles(projectPath);

		_contains(_checkFileExists(projectPath + "/src/main/java/foo/api/Foo.java"), ".*^public interface Foo.*");

		_contains(_checkFileExists(projectPath + "/src/main/resources/foo/api/packageinfo"), "version 1.0.0");
	}

	@Test
	public void testCreateExtModule() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "modules-ext", "-m", "com.liferay.login.web",
			"loginExt"
		};

		File projectDir = new File(workspace, "ext/loginExt");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspace, _extensionsDir, gradleArgs);

		_contains(
			_checkFileExists(projectPath + "/build.gradle"),
			"^.*originalModule group: \"com.liferay\", name: \"com.liferay.login.web\".*$");
	}

	@Test
	public void testCreateExtModuleWithoutOriginalModuleOptions() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "modules-ext", "loginExt"};

		String data = "foobar" + System.lineSeparator() + "1.0" + System.lineSeparator();

		InputStream testData = new ByteArrayInputStream(data.getBytes("UTF-8"));

		BladeTestResults bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, testData, false, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("Successfully created project"));
	}

	@Test
	public void testCreateFragment() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "fragment", "-h", "com.liferay.login.web", "-H",
			"1.0.0", "loginHook"
		};

		File projectDir = new File(workspace, "modules/loginHook");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspace, _extensionsDir, gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Bundle-SymbolicName: loginhook.*$",
				".*^Fragment-Host: com.liferay.login.web;bundle-version=\"1.0.0\".*$"
			});
	}

	@Test
	public void testCreateFragmentWithoutHostOptions() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "fragment", "loginHook"};

		String data = "foobar" + System.lineSeparator() + "1.0" + System.lineSeparator();

		InputStream testData = new ByteArrayInputStream(data.getBytes("UTF-8"));

		BladeTestResults bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, testData, args);

		String output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("Successfully created project"));

		args = new String[] {
			"create", "--base", workspace.getAbsolutePath(), "-t", "fragment", "-H", "1.0.0", "loginHook2"
		};

		data = "foobar" + System.lineSeparator();

		testData = new ByteArrayInputStream(data.getBytes("UTF-8"));

		bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, testData, args);

		output = bladeTestResults.getOutput();

		Assert.assertTrue(output, output.contains("Successfully created project"));
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPackage() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "-p", "com.liferay.test", "foo"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/foo");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/com/liferay/test/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/build.gradle");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");
	}

	@Test
	public void testCreateGradleMVCPortletProjectWithPortletSuffix() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "portlet-portlet"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/portlet-portlet");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/portlet/portlet/portlet/PortletPortlet.java"),
			".*^public class PortletPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/build.gradle");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");
	}

	@Test
	public void testCreateGradlePortletProject() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "portlet", "-c", "Foo", "gradle.test"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/gradle.test");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/build.gradle");

		_contains(
			_checkFileExists(projectPath + "/src/main/java/gradle/test/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");
	}

	@Test
	public void testCreateGradleService() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "service", "-s",
			"com.liferay.portal.kernel.events.LifecycleAction", "-c", "FooAction", "servicepreaction"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/servicepreaction");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath + "/build.gradle");

		File file = new File(projectPath, "src/main/java/servicepreaction/FooAction.java");

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
	}

	@Test
	public void testCreateGradleServiceWrapper() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "service-wrapper", "-s",
			"com.liferay.portal.kernel.service.UserLocalServiceWrapper", "serviceoverride"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/serviceoverride");

		String projectPath = projectDir.getAbsolutePath();

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
	}

	@Test
	public void testCreateGradleSymbolicName() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "-p", "foo.bar", "barfoo"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/barfoo");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath + "/build.gradle");

		_contains(_checkFileExists(projectPath + "/bnd.bnd"), ".*Bundle-SymbolicName: foo.bar.*");
	}

	@Test
	public void testCreateMissingArgument() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "foobar", "--base", workspace.getAbsolutePath()};

		InputStream nullInputStream = new InputStream() {

			@Override
			public int read() throws IOException {
				return -1;
			}

		};

		String message = null;

		try {
			TestUtil.runBlade(workspace, _extensionsDir, nullInputStream, true, args);
		}
		catch (Throwable t) {
			message = t.getMessage();
		}

		Assert.assertNotNull(message);

		Assert.assertTrue(message, message.contains("Unable to acquire an answer"));
	}

	@Test
	public void testCreateMVCPortlet() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "foo"};

		File projectDir = new File(workspace, "modules/foo");

		String projectPath = projectDir.getAbsolutePath();

		TestUtil.runBlade(workspace, _extensionsDir, gradleArgs);

		_checkGradleBuildFiles(projectPath);

		_contains(
			_checkFileExists(projectPath + "/src/main/java/foo/portlet/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/build.gradle");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");
	}

	@Test
	public void testCreateMVCPortletInteractive() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet"};

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("foo\n".getBytes());

		TestUtil.runBlade(workspace, _extensionsDir, byteArrayInputStream, true, gradleArgs);

		File projectDir = new File(workspace, "modules/foo");

		_checkGradleBuildFiles(projectDir.getAbsolutePath());
	}

	@Test
	public void testCreateNpmAngular71() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspaceVersion(workspace, BladeTest.PRODUCT_VERSION_PORTAL_71);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "npm-angular-portlet", "-v", "7.1", "npmangular"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/npmangular");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath + "/build.gradle");

		File packageJsonFile = _checkFileExists(projectPath + "/package.json");

		_contains(packageJsonFile, ".*\"build\": \"tsc && liferay-npm-bundler\".*");

		File tsConfigJsonFile = _checkFileExists(projectPath + "/tsconfig.json");

		_contains(tsConfigJsonFile, ".*META-INF/resources/lib.*");
	}

	@Test
	public void testCreateOnExistFolder() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "activator", "exist"};

		File existFile = new File(workspace, "modules/exist/file.txt");

		if (!existFile.exists()) {
			File parentFile = existFile.getParentFile();

			parentFile.mkdirs();

			Assert.assertTrue(existFile.createNewFile());
		}

		try {
			TestUtil.runBlade(workspace, _extensionsDir, false, args);
		}
		catch (Exception e) {
		}

		File projectDir = new File(workspace, "modules/exist");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileDoesNotExists(projectPath + "/bnd.bnd");
	}

	@Test
	public void testCreatePortletConfigurationIcon() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "portlet-configuration-icon", "-p", "blade.test",
			"icontest"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/icontest");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/configuration/icon/IcontestPortletConfigurationIcon.java");

		_contains(
			componentFile, ".*^public class IcontestPortletConfigurationIcon.*extends BasePortletConfigurationIcon.*$");

		_checkFileExists(projectPath + "/build.gradle");
	}

	@Test
	public void testCreatePortletToolbarContributor() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "portlet-toolbar-contributor", "-p", "blade.test",
			"toolbartest"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/toolbartest");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/portlet/toolbar/contributor" +
				"/ToolbartestPortletToolbarContributor.java");

		_contains(
			componentFile,
			".*^public class ToolbartestPortletToolbarContributor.*implements PortletToolbarContributor.*$");

		_checkFileExists(projectPath + "/build.gradle");
	}

	@Test
	public void testCreateProjectAllDefaults() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "hello-world-portlet"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/hello-world-portlet");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(
			projectPath + "/src/main/java/hello/world/portlet/portlet/HelloWorldPortlet.java");

		_contains(portletFile, ".*^public class HelloWorldPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/build.gradle");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");
	}

	@Test
	public void testCreateProjectWithRefresh() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "hello-world-refresh"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/hello-world-refresh");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(
			projectPath + "/src/main/java/hello/world/refresh/portlet/HelloWorldRefreshPortlet.java");

		_contains(portletFile, ".*^public class HelloWorldRefreshPortlet extends MVCPortlet.*$");

		_checkFileExists(projectPath + "/build.gradle");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		_checkFileExists(projectPath + "/src/main/resources/META-INF/resources/init.jsp");
	}

	@Test
	public void testCreateServiceTemplateServiceParameterRequired() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "service", "foo"};

		BladeTestResults bladeTestResults = null;

		String errors = null;

		String output = null;

		try {
			String data = "com.test.Foo" + System.lineSeparator();

			InputStream testData = new ByteArrayInputStream(data.getBytes("UTF-8"));

			bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, testData, false, args);

			output = bladeTestResults.getOutput();

			errors = bladeTestResults.getErrors();
		}
		catch (Throwable t) {
			errors = t.getMessage();
		}

		Assert.assertTrue(errors.isEmpty());

		Assert.assertTrue(output, output.contains("Successfully created project"));

		args = new String[] {"create", "-t", "service", "-s com.test.Foo", "foo"};

		bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, false, args);

		try {
			errors = bladeTestResults.getErrors();
		}
		catch (Throwable t) {
			errors = t.getMessage();
		}

		Assert.assertFalse(errors, errors.contains("Usage:"));
	}

	@Test
	public void testCreateSimulationPanelEntry() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "simulation-panel-entry", "-p", "test.simulator",
			"simulator"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/simulator");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/test/simulator/application/list/SimulatorSimulationPanelApp.java");

		_contains(componentFile, ".*^public class SimulatorSimulationPanelApp.*extends BaseJSPPanelApp.*$");

		_checkFileExists(projectPath + "/build.gradle");
	}

	@Test
	public void testCreateSpringMvcPortlet() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "spring-mvc-portlet", "springtest", "--package-name",
			"com.test", "--classname", "Sample", "--framework", "springportletmvc", "--view-type", "jsp", "-v", "7.3"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/springtest");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/src/main/java/com/test/controller/UserController.java");

		_checkFileExists(projectPath + "/build.gradle");
	}

	@Test
	public void testCreateSpringMVCPortletInteractive() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] gradleArgs = {"create", "--base", workspace.getAbsolutePath(), "-t", "spring-mvc-portlet", "foo"};

		String interactiveModeInput = "springportletmvc" + System.lineSeparator() + "jsp" + System.lineSeparator();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(interactiveModeInput.getBytes());

		TestUtil.runBlade(workspace, _extensionsDir, byteArrayInputStream, gradleArgs);

		File project = new File(workspace, "modules/foo");

		_checkGradleBuildFiles(project.getAbsolutePath());
	}

	@Test
	public void testCreateTemplateContextContributor() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "template-context-contributor", "blade-test"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/blade-test");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File componentFile = _checkFileExists(
			projectPath + "/src/main/java/blade/test/context/contributor/BladeTestTemplateContextContributor.java");

		_contains(
			componentFile,
			".*^public class BladeTestTemplateContextContributor.*implements TemplateContextContributor.*$");

		_checkFileExists(projectPath + "/build.gradle");
	}

	@Test
	public void testCreateTheme() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "theme", "theme-test"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/theme-test");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileDoesNotExists(projectPath + "/bnd.bnd");

		_checkFileExists(projectPath + "/src/main/webapp/css/_custom.scss");

		File properties = _checkFileExists(projectPath + "/src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		_contains(properties, ".*^name=theme-test.*");
	}

	@Test
	public void testCreateThemeContributor() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-t", "theme-contributor", "-C", "foobar",
			"theme-contributor-test"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/theme-contributor-test");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		File bnd = _checkFileExists(projectPath + "/bnd.bnd");

		_contains(bnd, ".*Liferay-Theme-Contributor-Type: foobar.*");
	}

	@Test
	public void testCreateWarCoreExt() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "war-core-ext", "warCoreExt"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, false, args);

		String output = bladeTestResults.getOutput();

		output = output.trim();

		Assert.assertTrue(output, output.endsWith(File.separator + "workspace" + File.separator + "ext"));

		Assert.assertTrue(output, output.startsWith("Success"));

		File projectDir = new File(workspace, "ext/warCoreExt");

		args = new String[] {"server", "init", "--base", workspace.getAbsolutePath()};

		TestUtil.runBlade(workspace, _extensionsDir, false, args);

		args = new String[] {"gw", "build", "--base", projectDir.getAbsolutePath()};

		TestUtil.runBlade(workspace, _extensionsDir, false, args);
	}

	@Test
	public void testCreateWarHookLocation() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		_testCreateWar(workspace, "war-hook", "war-hook-test");
	}

	@Test
	public void testCreateWarMVCPortletLegacyProperty() throws Exception {
		String projectName = "war-portlet-legacy-test";

		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File gradleProperties = new File(workspace, "gradle.properties");

		Assert.assertTrue(gradleProperties.exists());

		String configLine = System.lineSeparator() + "liferay.workspace.wars.dir=wars";

		Files.write(gradleProperties.toPath(), configLine.getBytes(), StandardOpenOption.APPEND);

		String[] args = {"--base", workspace.getAbsolutePath(), "create", "-t", "war-mvc-portlet", projectName};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "wars/" + projectName);

		_checkFileExists(projectDir.getAbsolutePath());
	}

	@Test
	public void testCreateWarMVCPortletLocation() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		_testCreateWar(workspace, "war-mvc-portlet", "war-portlet-test");
	}

	@Test
	public void testCreateWorkspaceCommaDelimitedModulesDirGradleProject() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File gradleProperties = new File(workspace, "gradle.properties");

		Assert.assertTrue(gradleProperties.exists());

		String configLine = System.lineSeparator() + "liferay.workspace.modules.dir=modules,foo,bar";

		Files.write(gradleProperties.toPath(), configLine.getBytes(), StandardOpenOption.APPEND);

		String[] args = {"create", "-t", "rest", "--base", workspace.getAbsolutePath(), "resttest"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		String fooBar = workspace.getAbsolutePath() + "/modules,foo,bar";

		File fooBarDir = new File(fooBar);

		Assert.assertFalse(
			"directory named '" + fooBarDir.getName() + "' should not exist, but it does.", fooBarDir.exists());
	}

	@Test
	public void testCreateWorkspaceDXPLiferayVersionFromDocker() throws Exception {
		File workspace73 = new File(_rootDir, "workspace73");

		_makeWorkspace(workspace73);

		File gradleProperties = new File(workspace73, "gradle.properties");

		Files.deleteIfExists(gradleProperties.toPath());

		String dockerImageProperty =
			WorkspaceConstants.DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY + "=liferay/dxp:7.3.10-ep5-202008181831";

		Files.createFile(gradleProperties.toPath());

		Files.write(gradleProperties.toPath(), dockerImageProperty.getBytes(), StandardOpenOption.APPEND);

		String[] sevenThreeArgs = {"--base", workspace73.getAbsolutePath(), "create", "-t", "portlet", "seven-three"};

		TestUtil.runBlade(workspace73, _extensionsDir, sevenThreeArgs);

		File buildGradle = new File(workspace73, "seven-three/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("release.dxp.api"));
	}

	@Test
	public void testCreateWorkspaceDXPVersionDefault() throws Exception {
		File workspaceDXP73 = new File(_rootDir, "workspace73");

		File modulesDir = new File(workspaceDXP73, "modules");

		_makeWorkspaceVersion(workspaceDXP73, "dxp-7.3-ep5");

		String[] sevenThreeArgs = {
			"--base", workspaceDXP73.getAbsolutePath(), "create", "-t", "portlet", "seven-three"
		};

		TestUtil.runBlade(workspaceDXP73, _extensionsDir, sevenThreeArgs);

		File buildGradle = new File(modulesDir, "seven-three/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("release.dxp.api"));
	}

	@Test
	public void testCreateWorkspaceFormField72() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspaceVersion(workspace, BladeTest.PRODUCT_VERSION_PORTAL_72);

		File modulesDir = new File(workspace, "modules");

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-t", "form-field",
			"sampleFormField", "-v", BladeTest.LIFERAY_VERSION_72
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		String projectPath = modulesDir.getAbsolutePath();

		_checkFileExists(projectPath + "/sampleFormField");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sampleFormField", "sampleformfield-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "sampleFormField/build/libs/sampleformfield-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceFormField73React() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspaceVersion(workspace, BladeTest.PRODUCT_VERSION_PORTAL_73);

		File modulesDir = new File(workspace, "modules");

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", modulesDir.getAbsolutePath(), "-t", "form-field",
			"sampleFormField", "--js-framework", "react", "-v", BladeTest.LIFERAY_VERSION_73
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		String projectPath = modulesDir.getAbsolutePath();

		_checkFileExists(projectPath + "/sampleFormField");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sampleFormField", "sampleformfield-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "sampleFormField/build/libs/sampleformfield-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleExtModuleTargetPlatform() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File gradleProperties = new File(workspace, "gradle.properties");

		Path gradlePropertiesPath = gradleProperties.toPath();

		byte[] gradlePropertiesBytes = Files.readAllBytes(gradlePropertiesPath);

		String content = new String(gradlePropertiesBytes);

		StringBuilder sb = new StringBuilder();

		try (Scanner scanner = new Scanner(content)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.contains("liferay.workspace.target.platform.version")) {
					line = line.substring(line.indexOf("#") + 1);
				}

				sb.append(line + System.lineSeparator());
			}
		}

		content = sb.toString();

		Files.delete(gradlePropertiesPath);

		Files.write(gradlePropertiesPath, content.getBytes(), StandardOpenOption.CREATE_NEW);

		File extDir = new File(workspace, "ext");

		String data = "1.0" + System.lineSeparator();

		InputStream testData = new ByteArrayInputStream(data.getBytes("UTF-8"));

		String[] gradleArgs = {
			"create", "--base", workspace.getAbsolutePath(), "-d", extDir.getAbsolutePath(), "-t", "modules-ext", "-m",
			"com.liferay.login.web", "loginExt"
		};

		TestUtil.runBlade(workspace, _extensionsDir, testData, gradleArgs);

		String projectPath = extDir.getAbsolutePath();

		_checkFileExists(projectPath + "/loginExt");

		_contains(
			_checkFileExists(projectPath + "/loginExt/build.gradle"),
			new String[] {"^.*originalModule group: \"com.liferay\", name: \"com.liferay.login.web\".*$"});

		_lacks(
			_checkFileExists(projectPath + "/loginExt/build.gradle"),
			".*^apply plugin: \"com.liferay.osgi.ext.plugin\".*$");

		GradleRunnerUtil.verifyGradleRunnerOutput(GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar"));

		Path extJarPath = GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/loginExt", "com\\.liferay\\.login\\.web-([0-9]+\\.[0-9]+\\.[0-9]+)\\.ext\\.jar", true);

		Path extJarPathName = extJarPath.getFileName();

		_verifyImportPackage(new File(projectPath, "loginExt/build/libs/" + extJarPathName.toString()));
	}

	@Test
	public void testCreateWorkspaceGradleFragment() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File extensionsDir = new File(workspace, "modules/extensions");

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", extensionsDir.getAbsolutePath(), "-t", "fragment",
			"-h", "com.liferay.login.web", "-H", "1.0.0", "loginHook"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		String projectPath = extensionsDir.getAbsolutePath();

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

		_verifyImportPackage(new File(projectPath, "loginHook/build/libs/loginhook-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradlePortletProject() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File appsDir = new File(workspace, "modules/apps");

		String projectPath = appsDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "portlet", "-c", "Foo",
			"gradle.test"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/gradle.test/build.gradle");

		_checkFileDoesNotExists(projectPath + "/gradle.test/gradlew");

		_contains(
			_checkFileExists(projectPath + "/gradle.test/src/main/java/gradle/test/portlet/FooPortlet.java"),
			new String[] {
				"^package gradle.test.portlet;.*", ".*^import gradle.test.constants.FooPortletKeys;$.*",
				".*^import javax.portlet.Portlet;$.*", ".*^public class FooPortlet extends MVCPortlet.*$",
				".*service = Portlet.class.*"
			});

		_lacks(
			_checkFileExists(projectPath + "/gradle.test/build.gradle"), ".*^apply plugin: \"com.liferay.plugin\".*");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectApiPath() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File nestedDir = new File(workspace, "modules/nested/path");

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", nestedDir.getAbsolutePath(), "-t", "service-builder",
			"-p", "com.liferay.sample", "sample"
		};

		Assert.assertTrue(nestedDir.mkdirs());

		TestUtil.runBlade(workspace, _extensionsDir, args);

		String projectPath = nestedDir.getAbsolutePath();

		_checkFileExists(projectPath + "/sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		_checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		_checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		File file = _checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		_contains(file, ".*compile project\\(\":modules:nested:path:sample:sample-api\"\\).*");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDashes() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File modulesDir = new File(workspace, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "service-builder", "-p",
			"com.sample", "workspace-sample"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

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
			new File(projectPath, "workspace-sample/workspace-sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDefault() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File modulesDir = new File(workspace, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "service-builder", "-p",
			"com.liferay.sample", "sample"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		_checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		_checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		File file = _checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		_contains(file, ".*compile project\\(\":modules:sample:sample-api\"\\).*");

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/sample/sample-api", "com.liferay.sample.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/sample/sample-service", "com.liferay.sample.service-1.0.0.jar");

		File serviceJar = new File(
			projectPath, "sample/sample-service/build/libs/com.liferay.sample.service-1.0.0.jar");

		_verifyImportPackage(serviceJar);

		try (JarFile serviceJarFile = new JarFile(serviceJar)) {
			Manifest manifest = serviceJarFile.getManifest();

			Attributes mainAttributes = manifest.getMainAttributes();

			String liferayService = mainAttributes.getValue("Liferay-Service");

			Assert.assertNotNull(liferayService);

			Assert.assertTrue(liferayService.equals("true"));
		}
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectWithAddOns() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File modulesDir = new File(workspace, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "service-builder", "-p",
			"com.liferay.sample", "sample", "--add-ons", "true"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/sample/build.gradle");

		_checkFileDoesNotExists(projectPath + "/sample/settings.gradle");

		_checkFileExists(projectPath + "/sample/sample-api/build.gradle");

		_checkFileExists(projectPath + "/sample/sample-service/build.gradle");

		File file = _checkFileExists(projectPath + "/sample/sample-uad/build.gradle");

		_contains(file, ".*compile project\\(\":modules:sample:sample-api\"\\).*");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProjectDots() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File modulesDir = new File(workspace, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "service-builder", "-p",
			"com.sample", "workspace.sample"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

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
			new File(projectPath, "workspace.sample/workspace.sample-service/build/libs/com.sample.service-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceLiferayVersion70() throws Exception {
		File workspace70 = new File(_rootDir, "workspace70");

		File modulesDir = new File(workspace70, "modules");

		_makeWorkspaceVersion(workspace70, BladeTest.PRODUCT_VERSION_PORTAL_70);

		String[] sevenZeroArgs = {
			"--base", workspace70.getAbsolutePath(), "create", "-t", "npm-angular-portlet", "seven-zero"
		};

		TestUtil.runBlade(workspace70, _extensionsDir, sevenZeroArgs);

		File buildGradle = new File(modulesDir, "seven-zero/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("com.liferay.frontend.js.loader.modules.extender.api"));
	}

	@Test
	public void testCreateWorkspaceLiferayVersion71() throws Exception {
		File workspace71 = new File(_rootDir, "workspace71");

		File modulesDir = new File(workspace71, "modules");

		_makeWorkspaceVersion(workspace71, BladeTest.PRODUCT_VERSION_PORTAL_71);

		String[] sevenOneArgs = {
			"--base", workspace71.getAbsolutePath(), "create", "-v", "7.1", "-t", "npm-angular-portlet", "seven-one"
		};

		TestUtil.runBlade(workspace71, _extensionsDir, sevenOneArgs);

		File buildGradle = new File(modulesDir, "seven-one/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("com.liferay.frontend.js.loader.modules.extender.api"));
	}

	@Test
	public void testCreateWorkspaceLiferayVersionDefault() throws Exception {
		File workspace73 = new File(_rootDir, "workspace73");

		File modulesDir = new File(workspace73, "modules");

		_makeWorkspace(workspace73);

		String[] sevenThreeArgs = {"--base", workspace73.getAbsolutePath(), "create", "-t", "portlet", "seven-three"};

		TestUtil.runBlade(workspace73, _extensionsDir, sevenThreeArgs);

		File buildGradle = new File(modulesDir, "seven-three/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("release.portal.api"));
	}

	@Test
	public void testCreateWorkspaceLiferayVersionFromCommandLine() throws Exception {
		File workspace73 = new File(_rootDir, "workspace73");

		File modulesDir = new File(workspace73, "modules");

		_makeWorkspace(workspace73);

		String[] sevenTwoArgs = {
			"--base", workspace73.getAbsolutePath(), "create", "-t", "portlet", "seven-two", "-v", "7.2"
		};

		TestUtil.runBlade(workspace73, _extensionsDir, sevenTwoArgs);

		File buildGradle = new File(modulesDir, "seven-two/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertFalse(content, content.contains("release.portal.api"));
	}

	@Test
	public void testCreateWorkspaceModuleLocation() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		File modulesDir = new File(workspace, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		String[] args = {"--base", workspace.getAbsolutePath(), "create", "-t", "mvc-portlet", "foo"};

		_makeWorkspace(workspace);

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/foo");

		_checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		_contains(portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/foo/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceModulesDirNullProject() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File gradlePropertiesFile = new File(workspace, "gradle.properties");

		String modulesDirNull = "\n" + WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY + "=";

		Files.write(gradlePropertiesFile.toPath(), modulesDirNull.getBytes(), StandardOpenOption.APPEND);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "mvc-portlet", "foo"};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "foo");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceNpmAngularPortletProject() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File appsDir = new File(workspace, "modules/apps");

		String projectPath = appsDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "npm-angular-portlet", "foo"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/foo/build.gradle");

		_checkFileDoesNotExists(projectPath + "/foo/gradlew");

		_contains(
			_checkFileExists(projectPath + "/foo/package.json"),
			new String[] {
				".*@angular/animations.*", ".*liferay-npm-bundler\": \"2.18.2.*",
				".*build\": \"tsc && liferay-npm-bundler.*"
			});

		_checkFileExists(projectPath + "/foo/src/main/resources/META-INF/resources/lib/angular-loader.ts");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "foo/build/libs/foo-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspacePortalLiferayVersionFromDocker() throws Exception {
		File workspace73 = new File(_rootDir, "workspace73");

		_makeWorkspace(workspace73);

		File gradleProperties = new File(workspace73, "gradle.properties");

		Files.deleteIfExists(gradleProperties.toPath());

		String dockerImageProperty =
			WorkspaceConstants.DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY + "=liferay/portal:7.3.4-ga5-202008131318";

		Files.createFile(gradleProperties.toPath());

		Files.write(gradleProperties.toPath(), dockerImageProperty.getBytes(), StandardOpenOption.APPEND);

		String[] sevenThreeArgs = {"--base", workspace73.getAbsolutePath(), "create", "-t", "portlet", "seven-three"};

		TestUtil.runBlade(workspace73, _extensionsDir, sevenThreeArgs);

		File buildGradle = new File(workspace73, "seven-three/build.gradle");

		String content = FileUtil.read(buildGradle);

		Assert.assertTrue(content, content.contains("release.portal.api"));
	}

	@Test
	public void testCreateWorkspaceProjectAllDefaults() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File appsDir = new File(workspace, "modules/apps");

		String projectPath = appsDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", projectPath, "-t", "mvc-portlet", "foo"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkFileExists(projectPath + "/foo");

		_checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/foo/src/main/java/foo/portlet/FooPortlet.java");

		_contains(portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/foo/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/foo", "foo-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "foo/build/libs/foo-1.0.0.jar"));

		_resolveProject(buildTask, projectPath);
	}

	@Test
	public void testCreateWorkspaceProjectWithRefresh() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		File appsDir = new File(workspace, "modules/apps");

		String appsPath = appsDir.getAbsolutePath();

		String[] args = {
			"create", "--base", workspace.getAbsolutePath(), "-d", appsPath, "-t", "mvc-portlet", "foo-refresh"
		};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(appsDir, "foo-refresh");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileExists(projectPath);

		_checkFileExists(projectPath + "/bnd.bnd");

		File portletFile = _checkFileExists(projectPath + "/src/main/java/foo/refresh/portlet/FooRefreshPortlet.java");

		_contains(portletFile, ".*^public class FooRefreshPortlet extends MVCPortlet.*$");

		File gradleBuildFile = _checkFileExists(projectPath + "/build.gradle");

		_lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, "foo.refresh-1.0.0.jar");

		_verifyImportPackage(new File(projectPath, "build/libs/foo.refresh-1.0.0.jar"));
	}

	@Test
	public void testCreateWorkspaceTargetPlatformLiferayVersion70() throws Exception {
		File workspace70 = new File(_rootDir, "workspace70");

		File modulesDir = new File(workspace70, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		_makeWorkspaceVersion(workspace70, BladeTest.PRODUCT_VERSION_PORTAL_70);

		_enableTargetPlatformInWorkspace(workspace70, "7.0.6-2");

		String[] sevenZeroArgs = {
			"--base", workspace70.getAbsolutePath(), "create", "-t", "service-builder", "seven-zero"
		};

		TestUtil.runBlade(workspace70, _extensionsDir, sevenZeroArgs);

		_checkFileExists(projectPath + "/seven-zero/build.gradle");

		_checkFileDoesNotExists(projectPath + "/seven-zero/settings.gradle");

		_checkFileExists(projectPath + "/seven-zero/seven-zero-api/build.gradle");

		File buildGradle = new File(projectPath + "/seven-zero/seven-zero-service/build.gradle");

		Assert.assertTrue(buildGradle.exists());

		String content = FileUtil.read(buildGradle);

		content = content.replaceAll("compileOnly project", "compile project");

		Files.write(buildGradle.toPath(), content.getBytes());

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace70.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace70.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/seven-zero/seven-zero-api", "seven.zero.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(
			projectPath + "/seven-zero/seven-zero-service", "seven.zero.service-1.0.0.jar");

		_verifyImportPackage(
			new File(projectPath, "seven-zero/seven-zero-service/build/libs/seven.zero.service-1.0.0.jar"));

		_resolveProject(buildTask, projectPath);
	}

	@Test
	public void testCreateWorkspaceTargetPlatformLiferayVersion71() throws Exception {
		File workspace71 = new File(_rootDir, "workspace71");

		File modulesDir = new File(workspace71, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		_makeWorkspaceVersion(workspace71, BladeTest.PRODUCT_VERSION_PORTAL_71);

		_enableTargetPlatformInWorkspace(workspace71, "7.1.3-1");

		String[] sevenOneArgs = {
			"--base", workspace71.getAbsolutePath(), "create", "-t", "service-builder", "seven-one"
		};

		TestUtil.runBlade(workspace71, _extensionsDir, sevenOneArgs);

		_checkFileExists(projectPath + "/seven-one/build.gradle");

		_checkFileDoesNotExists(projectPath + "/seven-one/settings.gradle");

		_checkFileExists(projectPath + "/seven-one/seven-one-api/build.gradle");

		File buildGradle = new File(projectPath + "/seven-one/seven-one-service/build.gradle");

		Assert.assertTrue(buildGradle.exists());

		String content = FileUtil.read(buildGradle);

		content = content.replaceAll("compileOnly project", "compile project");

		Files.write(buildGradle.toPath(), content.getBytes());

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace71.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace71.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/seven-one/seven-one-api", "seven.one.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/seven-one/seven-one-service", "seven.one.service-1.0.0.jar");

		_verifyImportPackage(
			new File(projectPath, "seven-one/seven-one-service/build/libs/seven.one.service-1.0.0.jar"));

		_resolveProject(buildTask, projectPath);
	}

	@Test
	public void testCreateWorkspaceTargetPlatformLiferayVersion72() throws Exception {
		File workspace72 = new File(_rootDir, "workspace72");

		File modulesDir = new File(workspace72, "modules");

		String projectPath = modulesDir.getAbsolutePath();

		_makeWorkspaceVersion(workspace72, BladeTest.PRODUCT_VERSION_PORTAL_72);

		_enableTargetPlatformInWorkspace(workspace72, "7.2.1-1");

		String[] sevenTwoArgs = {
			"--base", workspace72.getAbsolutePath(), "create", "-t", "service-builder", "seven-two"
		};

		TestUtil.runBlade(workspace72, _extensionsDir, sevenTwoArgs);

		_checkFileExists(projectPath + "/seven-two/build.gradle");

		_checkFileDoesNotExists(projectPath + "/seven-two/settings.gradle");

		_checkFileExists(projectPath + "/seven-two/seven-two-api/build.gradle");

		File buildGradle = new File(projectPath + "/seven-two/seven-two-service/build.gradle");

		Assert.assertTrue(buildGradle.exists());

		String content = FileUtil.read(buildGradle);

		content = content.replaceAll("compileOnly project", "compile project");

		Files.write(buildGradle.toPath(), content.getBytes());

		BuildTask buildService = GradleRunnerUtil.executeGradleRunner(workspace72.getPath(), "buildService");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildService);

		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(workspace72.getPath(), "jar");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath + "/seven-two/seven-two-api", "seven.two.api-1.0.0.jar");
		GradleRunnerUtil.verifyBuildOutput(projectPath + "/seven-two/seven-two-service", "seven.two.service-1.0.0.jar");

		_verifyImportPackage(
			new File(projectPath, "seven-two/seven-two-service/build/libs/seven.two.service-1.0.0.jar"));

		_resolveProject(buildTask, projectPath);
	}

	@Test
	public void testCreateWorkspaceThemeLocation() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		String[] args = {"--base", workspace.getAbsolutePath(), "create", "-t", "theme", "theme-test"};

		_makeWorkspace(workspace);

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/theme-test");

		String projectPath = projectDir.getAbsolutePath();

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
		File workspace = new File(_rootDir, "workspace");

		File modulesDir = new File(workspace, "modules");

		String[] args = {"--base", modulesDir.getAbsolutePath(), "create", "-t", "mvc-portlet", "foo"};

		_makeWorkspace(workspace);

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File buildGradle = new File(modulesDir, "foo/build.gradle");

		_checkFileExists(buildGradle.getAbsolutePath());

		String content = FileUtil.read(buildGradle);

		Assert.assertEquals(1, StringUtils.countMatches(content, '{'));

		Assert.assertEquals(1, StringUtils.countMatches(content, '}'));
	}

	@Test
	public void testListTemplates() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "-l"};

		BladeTestResults bladeTestResults = TestUtil.runBlade(workspace, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Map<String, String> templates = ProjectTemplates.getTemplates();

		List<String> templateNames = new ArrayList<>(templates.keySet());

		for (String templateName : templateNames) {
			Assert.assertTrue(output.contains(templateName));
		}
	}

	@Test
	public void testWrongTemplateTyping() throws Exception {
		File workspace = new File(_rootDir, "workspace");

		_makeWorkspace(workspace);

		String[] args = {"create", "--base", workspace.getAbsolutePath(), "-t", "activatorXXX", "wrong-activator"};

		try {
			TestUtil.runBlade(workspace, _extensionsDir, false, args);
		}
		catch (Exception e) {
		}

		File projectDir = new File(workspace, "modules/wrong-activator");

		String projectPath = projectDir.getAbsolutePath();

		_checkFileDoesNotExists(projectPath);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static File _enableTargetPlatformInWorkspace(File workspaceDir, String liferayVersion) throws IOException {
		File gradlePropertiesFile = new File(workspaceDir, "gradle.properties");

		String targetPlatformVersionProperty =
			System.lineSeparator() + WorkspaceConstants.DEFAULT_TARGET_PLATFORM_VERSION_PROPERTY + "=" + liferayVersion;

		Files.write(gradlePropertiesFile.toPath(), targetPlatformVersionProperty.getBytes(), StandardOpenOption.APPEND);

		return gradlePropertiesFile;
	}

	private void _addDefaultModulesDir(File workspace) throws Exception {
		File gradleProperties = new File(workspace, "gradle.properties");

		Assert.assertTrue(gradleProperties.exists());

		String configLine =
			System.lineSeparator() + WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY + "=" +
				WorkspaceConstants.DEFAULT_MODULES_DIR;

		Files.write(gradleProperties.toPath(), configLine.getBytes(), StandardOpenOption.APPEND);
	}

	private File _checkFileDoesNotExists(String path) {
		File file = new File(path);

		Assert.assertFalse(file.exists());

		return file;
	}

	private File _checkFileExists(String path) {
		File file = new File(path);

		Assert.assertTrue(file.exists());

		return file;
	}

	private void _checkGradleBuildFiles(String projectPath) throws IOException {
		_checkFileExists(projectPath);

		Path gradlePath = Paths.get(projectPath, "build.gradle");

		Assert.assertTrue(gradlePath.toString(), Files.exists(gradlePath));

		List<String> lines = Files.readAllLines(gradlePath);

		if (!lines.isEmpty() && !lines.contains("war {")) {
			_checkFileExists(projectPath + "/bnd.bnd");
		}
	}

	private void _contains(File file, String pattern) throws Exception {
		String content = FileUtil.read(file);

		_contains(content, pattern);
	}

	private void _contains(File file, String[] patterns) throws Exception {
		String content = FileUtil.read(file);

		for (String pattern : patterns) {
			_contains(content, pattern);
		}
	}

	private void _contains(String content, String regex) throws Exception {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(content);

		Assert.assertTrue(matcher.matches());
	}

	private void _lacks(File file, String regex) throws Exception {
		String content = FileUtil.read(file);

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(content);

		Assert.assertFalse(matcher.matches());
	}

	private void _makeWorkspace(File workspace) throws Exception {
		File parentFile = workspace.getParentFile();

		parentFile.mkdirs();

		Assert.assertTrue(workspace.mkdir());

		String[] args = {"--base", workspace.getAbsolutePath(), "init", "-v", BladeTest.PRODUCT_VERSION_PORTAL_73};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_checkGradleBuildFiles(workspace.getAbsolutePath());

		File gradlewFile = new File(workspace, "gradlew");

		_checkFileExists(gradlewFile.getAbsolutePath());

		_addDefaultModulesDir(workspace);
	}

	private void _makeWorkspaceVersion(File workspace, String version) throws Exception {
		File parentFile = workspace.getParentFile();

		String[] args = {"--base", parentFile.getPath(), "init", workspace.getName(), "-v", version};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		_addDefaultModulesDir(workspace);
	}

	private void _resolveProject(BuildTask buildTask, String projectPath) throws Exception {
		String version = System.getProperty("java.specification.version");

		if (version.indexOf('.') > -1) {
			version = version.substring(version.indexOf('.') + 1);
		}

		int versionNumber = Integer.parseInt(version);

		Assume.assumeTrue(versionNumber < 9);

		buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "resolve");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);
	}

	private void _testCreateWar(File workspace, String projectType, String projectName) throws Exception {
		String[] args = {"--base", workspace.getAbsolutePath(), "create", "-t", projectType, projectName};

		TestUtil.runBlade(workspace, _extensionsDir, args);

		File projectDir = new File(workspace, "modules/" + projectName);

		_checkFileExists(projectDir.getAbsolutePath());
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

	private File _extensionsDir = null;
	private File _rootDir = null;

}