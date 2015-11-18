package com.liferay.blade.eclipse.provider.cmds;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CreateProjectCommandTests {

	private final CreateProjectCommand cmd = new CreateProjectCommand();

	@BeforeClass
	public static void copyTemplates() throws IOException {
		IO.copy(new File("templates.zip"), new File("bin_test/templates.zip"));
	}

	@Test
	public void createGradleJSPPortletProject() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"jspportlet",
				null,
				"foo",
				null,
				null,
				null);

		assertTrue(IO.getFile("generated/test/foo").exists());

		assertTrue(IO.getFile("generated/test/foo/bnd.bnd").exists());

		File portletFile =
			IO.getFile("generated/test/foo/src/main/java/foo/FooPortlet.java");

		assertTrue(portletFile.exists());

		String portletFileContent = new String(IO.read(portletFile));

		contains(
			portletFileContent,
			".*^public class FooPortlet extends MVCPortlet.*$");

		File gradleBuildFile = IO.getFile("generated/test/foo/build.gradle");

		assertTrue(gradleBuildFile.exists());

		String gradleBuildFileContent = new String(IO.read(gradleBuildFile));

		contains(
			gradleBuildFileContent,
			".*classpath 'com.liferay:com.liferay.ant.bnd:1.0.9'.*");

		contains(gradleBuildFileContent,
			".*apply plugin: 'biz.aQute.bnd.builder'.*");

		File viewJSPFile = IO.getFile(
			"generated/test/foo/src/main/resources/META-INF/resources/view.jsp");

		assertTrue(viewJSPFile.exists());

		File initJSPFile = IO.getFile(
			"generated/test/foo/src/main/resources/META-INF/resources/init.jsp");

		assertTrue(initJSPFile.exists());
	}

	@Test
	public void createGradlePortletProject() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"portlet",
				"gradle",
				"gradle.test",
				"Foo",
				null,
				null);

		assertTrue(
			IO.getFile("generated/test/gradle.test/build.gradle").exists());

		File portletFile = IO.getFile(
			"generated/test/gradle.test/src/main/java/gradle/test/FooPortlet.java");

		assertTrue(portletFile.exists());

		String portletFileContent = new String(IO.read(portletFile));

		contains(portletFileContent, "^package gradle.test;.*");

		contains(portletFileContent,
			".*javax.portlet.display-name=Gradle.test.*");

		contains(portletFileContent, ".*^public class FooPortlet .*");

		contains(portletFileContent,
			".*printWriter.print\\(\\\"Gradle.test Portlet.*");

		File bndFile = IO.getFile("generated/test/gradle.test/bnd.bnd");

		assertTrue(bndFile.exists());

		String bndFileContent = new String(IO.read(bndFile));

		contains(bndFileContent, ".*^Private-Package: gradle.test$.*");
	}

	@Test
	public void createGradleServicePreAction() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"service",
				"gradle",
				"servicepreaction",
				null,
				"com.liferay.portal.kernel.events.LifecycleAction",
				null);

		File buildFile =
			IO.getFile("generated/test/servicepreaction/build.gradle");

		assertTrue(buildFile.exists());

		String buildFileContent = new String(IO.read(buildFile));

		contains(
			buildFileContent,
			".*compile 'com.liferay.portal:portal-service:7.0.0-SNAPSHOT'.*");

		File serviceFile = IO.getFile(
			"generated/test/servicepreaction/src/main/java/servicepreaction/Servicepreaction.java");

		assertTrue(serviceFile.exists());

		String serviceFileContent = new String(IO.read(serviceFile));

		contains(serviceFileContent, "^package servicepreaction;.*");

		contains(serviceFileContent,
			".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*");

		contains(serviceFileContent, ".*service = LifecycleAction.class.*");

		contains(serviceFileContent,
			".*^public class Servicepreaction implements LifecycleAction \\{.*");

		File bndFile = IO.getFile("generated/test/servicepreaction/bnd.bnd");

		assertTrue(bndFile.exists());

		String bndFileContent = new String(IO.read(bndFile));

		contains(
			bndFileContent, ".*com.liferay.portal.service;version=\"7.0.0\".*");
	}

	@Test
	public void createGradleServiceWrapper() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"servicewrapper",
				"gradle",
				"serviceoverride",
				null,
				"com.liferay.portal.service.UserLocalServiceWrapper",
				null);

		File buildFile =
			IO.getFile("generated/test/serviceoverride/build.gradle");

		assertTrue(buildFile.exists());


		String buildFileContent = new String(IO.read(buildFile));

		contains(
			buildFileContent,
			".*compile 'com.liferay.portal:portal-service:7.0.0-SNAPSHOT'.*");

        contains(
                buildFileContent,
                ".*classpath 'biz.aQute.bnd:biz.aQute.bnd.gradle:3.0.0'.*");

		File serviceWrapperFile = IO.getFile(
			"generated/test/serviceoverride/src/main/java/serviceoverride/Serviceoverride.java");

		assertTrue(serviceWrapperFile.exists());

		String serviceWrapperFileContent = new String(IO.read(serviceWrapperFile));

		contains(serviceWrapperFileContent, "^package serviceoverride;.*");

		contains(serviceWrapperFileContent,
			".*^import com.liferay.portal.service.UserLocalServiceWrapper;$.*");

		contains(serviceWrapperFileContent, ".*service = ServiceWrapper.class.*");

		contains(serviceWrapperFileContent,
			".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*");

		contains(serviceWrapperFileContent,
			".*public Serviceoverride\\(\\) \\{.*");

		File bndFile = IO.getFile("generated/test/serviceoverride/bnd.bnd");

		assertTrue(bndFile.exists());

		String bndFileContent = new String(IO.read(bndFile));

		contains(
			bndFileContent, ".*Private-Package\\: serviceoverride.*");

		contains(
			bndFileContent, ".*com.liferay.portal.service;version=\'\\[7.0\\,7.1\\)\'.*");

	}
	
	@Test
	public void createGradleServiceBuilder() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"servicebuilder",
				"gradle",
				"guestbook",
				null,
				null,
				"com.liferay.docs.guestbook");

		File settingsFile =
			IO.getFile("generated/test/guestbook/settings.gradle");

		assertTrue(settingsFile.exists());

		String settingsFileContent = new String(IO.read(settingsFile));

		contains(
			settingsFileContent,
			"include 'com.liferay.docs.guestbook.api','com.liferay.docs.guestbook.svc','com.liferay.docs.guestbook.web'");

		File apiBndFile = IO.getFile(
			"generated/test/guestbook/com.liferay.docs.guestbook.api/bnd.bnd");

		assertTrue(apiBndFile.exists());

		String apiBndFileContent = new String(IO.read(apiBndFile));

		contains(apiBndFileContent, ".*Export-Package\\: \\\\.*");
		contains(apiBndFileContent, ".*com.liferay.docs.guestbook.exception\\,\\\\.*");
		contains(apiBndFileContent, ".*com.liferay.docs.guestbook.model\\,\\\\.*");
		contains(apiBndFileContent, ".*com.liferay.docs.guestbook.service\\,\\\\.*");
		contains(apiBndFileContent, ".*com.liferay.docs.guestbook.service.persistence.*");

		File svcBndFile = IO.getFile(
			"generated/test/guestbook/com.liferay.docs.guestbook.svc/bnd.bnd");

		assertTrue(svcBndFile.exists());

		String svcBndFileContent = new String(IO.read(svcBndFile));
		
		contains(svcBndFileContent, ".*Private-Package\\: \\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.model.impl\\,\\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.service.base\\,\\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.service.http\\,\\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.service.impl\\,\\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.service.persistence.impl\\,\\\\.*");
		contains(svcBndFileContent, ".*com.liferay.docs.guestbook.service.util.*");
		
		File webBndFile = IO.getFile(
			"generated/test/guestbook/com.liferay.docs.guestbook.web/bnd.bnd");

		assertTrue(webBndFile.exists());

		String webBndFileContent = new String(IO.read(webBndFile));
		
		contains(webBndFileContent, ".*Private-Package\\: \\\\.*");
		contains(webBndFileContent, ".*com.liferay.docs.guestbook.web.*");

		File portletFile = IO.getFile(
			"generated/test/guestbook/com.liferay.docs.guestbook.web/src/main/java" +
			"/com/liferay/docs/guestbook/portlet/GuestbookPortlet.java");

		assertTrue(portletFile.exists());

		String portletFileContent = new String(IO.read(portletFile));
		
		contains(portletFileContent, ".*package com.liferay.docs.guestbook.portlet;.*");
	}
	
	@Test
	public void createBndtoolsServicePreAction() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"service",
				"bndtools",
				"service.pre.action",
				"ServicePreAction",
				"com.liferay.portal.kernel.events.LifecycleAction",
				null);

		File bndFile =
			IO.getFile("generated/test/service.pre.action/bnd.bnd");

		assertTrue(bndFile.exists());

		String bndFileContent = new String(IO.read(bndFile));

		contains(
			bndFileContent,
			".*com.liferay.portal:portal-service;version='\\[7\\.0,8\\)'.*");

		File serviceFile = IO.getFile(
			"generated/test/service.pre.action/src/service/pre/action/ServicePreAction.java");

		assertTrue(serviceFile.exists());

		String serviceFileContent = new String(IO.read(serviceFile));

		contains(serviceFileContent, "^package service.pre.action;.*");

		contains(serviceFileContent,
			".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*");

		contains(serviceFileContent, ".*service = LifecycleAction.class.*");

		contains(serviceFileContent,
			".*^public class ServicePreAction implements LifecycleAction \\{.*");
	}

	@Test
	public void createMavenJSPPortletProject() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"jspportlet",
				"maven",
				"foo",
				null,
				null,
				null);

		assertTrue(IO.getFile("generated/test/foo").exists());

		assertTrue(IO.getFile("generated/test/foo/bnd.bnd").exists());

		assertTrue(IO.getFile("generated/test/foo/pom.xml").exists());

		File portletFile = IO.getFile(
			"generated/test/foo/src/main/java/foo/FooPortlet.java");

		assertTrue(portletFile.exists());

		String portletFileContent = new String(IO.read(portletFile));

		contains(
			portletFileContent,
			".*^public class FooPortlet extends MVCPortlet.*$");

		File pomFile = IO.getFile("generated/test/foo/pom.xml");

		assertTrue(pomFile.exists());

		String pomFileContent = new String(IO.read(pomFile));

		contains(pomFileContent, ".*<groupId>foo</groupId>.*");

		contains(pomFileContent, ".*<artifactId>foo</artifactId>.*");

		contains(pomFileContent, ".*<name>Foo</name>.*");

		File viewJSPFile = IO.getFile(
			"generated/test/foo/src/main/resources/META-INF/resources/view.jsp");

		assertTrue(viewJSPFile.exists());

		File initJSPFile = IO.getFile(
			"generated/test/foo/src/main/resources/META-INF/resources/init.jsp");

		assertTrue(initJSPFile.exists());
	}

	@Test
	public void createMavenPackagePath() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"service",
				"maven",
				"lfr.package.path.test",
				"PackagePathTest",
				"com.liferay.portal.kernel.events.LifecycleAction",
				null);

		File serviceFile = IO.getFile(
			"generated/test/lfr.package.path.test/src/main/java/lfr/package/path/test/PackagePathTest.java");

		assertTrue(serviceFile.exists());

		String serviceFileContent = new String(IO.read(serviceFile));

		contains(serviceFileContent, "^package lfr.package.path.test;.*");

		contains(serviceFileContent,
			".*^public class PackagePathTest implements LifecycleAction \\{.*");

		File pomFile =
			IO.getFile("generated/test/lfr.package.path.test/pom.xml");

		String pomFileContent = new String(IO.read(pomFile));

		contains(
			pomFileContent,
			".*<Private-Package>lfr.package.path.test</Private-Package>.*");
	}

	@Test
	public void createMavenPortletProject() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"portlet",
				"maven",
				"foo",
				null,
				null,
				null);

		assertTrue(IO.getFile("generated/test/foo").exists());

		assertTrue(IO.getFile("generated/test/foo/pom.xml").exists());

		File portletFile = IO.getFile(
			"generated/test/foo/src/main/java/foo/FooPortlet.java");

		assertTrue(portletFile.exists());

		String portletFileContent = new String(IO.read(portletFile));

		contains(portletFileContent, "^package foo;.*");

		contains(portletFileContent, ".*javax.portlet.display-name=foo.*");

		contains(portletFileContent, ".*^public class Foo.*");

		contains(portletFileContent, ".*printWriter.print\\(\\\"Foo Portlet.*");

		File pomFile = IO.getFile("generated/test/foo/pom.xml");

		assertTrue(pomFile.exists());

		String pomFileContent = new String(IO.read(pomFile));

		contains(pomFileContent, ".*<groupId>foo</groupId>.*");

		contains(pomFileContent, ".*<artifactId>foo</artifactId>.*");

		contains(pomFileContent, ".*<name>Foo</name>.*");

		contains(pomFileContent, ".*<Private-Package>foo</Private-Package>.*");
	}

	@Test
	public void createMavenServicePreAction() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"service",
				"maven",
				"servicepreaction",
				null,
				"com.liferay.portal.kernel.events.LifecycleAction",
				null);

		assertTrue(IO.getFile("generated/test/servicepreaction").exists());

		assertTrue(
			IO.getFile("generated/test/servicepreaction/pom.xml").exists());

		File serviceFile = IO.getFile(
			"generated/test/servicepreaction/src/main/java/servicepreaction/Servicepreaction.java");

		assertTrue(serviceFile.exists());

		String serviceFileContent = new String(IO.read(serviceFile));

		contains(serviceFileContent, "^package servicepreaction;.*");

		contains(serviceFileContent,
			".*^import com.liferay.portal.kernel.events.LifecycleAction;$.*");

		contains(serviceFileContent, ".*service = LifecycleAction.class.*");

		contains(serviceFileContent,
			".*^public class Servicepreaction implements LifecycleAction \\{.*");
	}

	@Test
	public void createMavenServicePreActionClassname() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"service",
				null,
				"loginpre",
				"LoginPreAction",
				"com.liferay.portal.kernel.events.LifecycleAction",
				null);

		File serviceFile = IO.getFile(
			"generated/test/loginpre/src/main/java/loginpre/LoginPreAction.java");

		assertTrue(serviceFile.exists());

		String serviceFileContent = new String(IO.read(serviceFile));

		contains(
			serviceFileContent,
			".*^public class LoginPreAction implements LifecycleAction \\{.*");
	}

	@Test
	public void createMavenServiceWrapper() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"servicewrapper",
				"maven",
				"serviceoverride",
				null,
				"com.liferay.portal.service.UserLocalServiceWrapper",
				null);

		assertTrue(IO.getFile("generated/test/serviceoverride").exists());

		assertTrue(
			IO.getFile("generated/test/serviceoverride/pom.xml").exists());

		File serviceWrapperFile = IO.getFile(
			"generated/test/serviceoverride/src/main/java/serviceoverride/Serviceoverride.java");

		assertTrue(serviceWrapperFile.exists());

		String serviceWrapperFileContent = new String(IO.read(serviceWrapperFile));

		contains(serviceWrapperFileContent, "^package serviceoverride;.*");

		contains(serviceWrapperFileContent,
			".*^import com.liferay.portal.service.UserLocalServiceWrapper;$.*");

		contains(serviceWrapperFileContent,
			".*^public class Serviceoverride extends UserLocalServiceWrapper \\{.*");

		contains(serviceWrapperFileContent,
			".*public Serviceoverride\\(\\) \\{.*");

	}

	@Test
	public void createMavenServiceWrapperClassname() throws Exception {
		cmd.createProject(
				new File(""),
				new File("generated/test"),
				"servicewrapper",
				null,
				"serviceoverride",
				"UserLocalServiceOverride",
				"com.liferay.portal.service.UserLocalServiceWrapper",
				null);

		File serviceWrapperFile = IO.getFile(
			"generated/test/serviceoverride/src/main/java/serviceoverride/UserLocalServiceOverride.java");

		assertTrue(serviceWrapperFile.exists());

		String serviceWrapperFileContent = new String(IO.read(serviceWrapperFile));

		contains(
			serviceWrapperFileContent,
			".*^public class UserLocalServiceOverride extends UserLocalServiceWrapper \\{.*");

		contains(serviceWrapperFileContent,
			".*public UserLocalServiceOverride\\(\\) \\{.*");

	}

	@Before
	public void setup() {
		File testdir = IO.getFile("generated/test");

		if (testdir.exists()) {
			IO.delete(testdir);
			assertFalse(testdir.exists());
		}
	}

	private void contains(String content, String pattern) {
		assertTrue(
			Pattern.compile(
				pattern, Pattern.MULTILINE | Pattern.DOTALL).matcher(
					content).matches());
	}

}
