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

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class CreateCommandTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		IO.copy(new File("templates.zip"), new File("bin_test/templates.zip"));
	}

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

		contains(
			checkFileExists(projectPath + "/src/main/java/foo/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists(projectPath + "/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");
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
				projectPath + "/src/main/java/com/liferay/test/FooPortlet.java"),
			".*^public class FooPortlet extends MVCPortlet.*$");

		contains(
			checkFileExists("generated/test/foo/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		checkFileExists(
			projectPath + "/src/main/resources/META-INF/resources/init.jsp");
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
				projectPath + "/src/main/java/gradle/test/FooPortlet.java"),
			new String[] {
				"^package gradle.test;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			".*^Private-Package: \\\\.*^\tgradle.test$.*");
	}

	@Test
	public void testCreateGradleServiceBuilder() throws Exception {
		String[] args = {
			"create", "-d", "generated/test", "-t", "servicebuilder", "-p",
			"com.liferay.docs.guestbook", "guestbook"
		};

		new bladenofail().run(args);

		String projectPath = "generated/test/guestbook";

		contains(
			checkFileExists(projectPath + "/settings.gradle"),
			"include 'com.liferay.docs.guestbook.api','com.liferay.docs.guestbook.svc','com.liferay.docs.guestbook.web'");

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.api/bnd.bnd"),
			new String[] {
				".*Export-Package: \\\\.*",
				".*com.liferay.docs.guestbook.exception,\\\\.*",
				".*com.liferay.docs.guestbook.model,\\\\.*",
				".*com.liferay.docs.guestbook.service,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.svc/bnd.bnd"),
			new String[] {
				".*Private-Package: \\\\.*",
				".*com.liferay.docs.guestbook.model.impl,\\\\.*",
				".*com.liferay.docs.guestbook.service.base,\\\\.*",
				".*com.liferay.docs.guestbook.service.http,\\\\.*",
				".*com.liferay.docs.guestbook.service.impl,\\\\.*",
				".*com.liferay.docs.guestbook.service.persistence.impl,\\\\.*",
				".*com.liferay.docs.guestbook.service.util.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.web/bnd.bnd"),
			new String[] {
				".*Private-Package: \\\\.*",
				".*com.liferay.docs.guestbook.web.*"
			});

		contains(
			checkFileExists(
				projectPath + "/com.liferay.docs.guestbook.web/src/main/java/com/liferay/docs/guestbook/portlet/GuestbookPortlet.java"),
			".*package com.liferay.docs.guestbook.portlet;.*");
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

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			".*com.liferay.portal.service;version=\"7.0.0\".*");
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

		contains(
			checkFileExists(projectPath + "/bnd.bnd"),
			new String[] {
				".*^Private-Package: \\\\.*^\tserviceoverride.*",
				".*com.liferay.portal.kernel.service;version=\'7.0.0\'.*"
			});
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
			projectPath + "/src/main/java/hello/world/portlet/" +
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
	}

	@Test
	public void testCreateWorkspaceGradlePortletProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "-t",
			"portlet", "-c", "Foo", "gradle.test"
		};

		makeWorkspace(new File("generated/test/workspace"));

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/gradle.test/build.gradle");

		contains(
			checkFileExists(
				projectPath + "/gradle.test/src/main/java/gradle/test/FooPortlet.java"),
			new String[] {
				"^package gradle.test;.*",
				".*javax.portlet.display-name=gradle.test.*",
				".*^public class FooPortlet .*",
				".*printWriter.print\\(\\\"gradle.test Portlet.*"
			});

		contains(
			checkFileExists(projectPath + "/gradle.test/bnd.bnd"),
			".*^Private-Package: \\\\.*^\tgradle.test$.*");

		lacks(
			checkFileExists(projectPath + "/gradle.test/build.gradle"),
			".*^apply plugin: \"com.liferay.plugin\".*");
	}

	@Test
	public void testCreateWorkspaceGradleServiceBuilderProject() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules", "-t",
			"servicebuilder", "-p", "com.sample", "workspace.sample"
		};

		makeWorkspace(new File("generated/test/workspace"));

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules";

		checkFileExists(projectPath + "/workspace.sample/build.gradle");

		checkFileDoesNotExists(
			projectPath + "/workspace.sample/settings.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.api/build.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.svc/build.gradle");

		checkFileExists(
			projectPath + "/workspace.sample/com.sample.web/build.gradle");
	}

	@Test
	public void testCreateWorkspaceProjectAllDefaults() throws Exception {
		String[] args = {
			"create", "-d", "generated/test/workspace/modules/apps", "foo"
		};

		makeWorkspace(new File("generated/test/workspace"));

		new bladenofail().run(args);

		String projectPath = "generated/test/workspace/modules/apps";

		checkFileExists(projectPath + "/foo");

		checkFileExists(projectPath + "/foo/bnd.bnd");

		File portletFile = checkFileExists(
			projectPath + "/foo/src/main/java/foo/FooPortlet.java");

		contains(
			portletFile, ".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = checkFileExists(
			projectPath + "/foo/build.gradle");

		lacks(gradleBuildFile, ".*^apply plugin: \"com.liferay.plugin\".*");
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

	private void makeWorkspace(File workspace) throws IOException {
		workspace.mkdirs();

		String settings = "apply plugin: \"com.liferay.workspace\"";

		File settingsFile = new File(workspace, "settings.gradle");

		Files.write(settingsFile.toPath(), settings.getBytes());
	}

}