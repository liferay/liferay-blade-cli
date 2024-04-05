/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gradle.testkit.runner.BuildTask;

import org.junit.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 * @author Foo Bar
 */
public class TestUtil {

	public static void appendGradleProperty(File workspaceDir, String key, String value) throws Exception {
		File gradleProperties = new File(workspaceDir, "gradle.properties");

		Assert.assertTrue(gradleProperties.exists());

		String propertyString = String.format("%n%s=%s", key, value);

		Files.write(gradleProperties.toPath(), propertyString.getBytes(), StandardOpenOption.APPEND);
	}

	public static void increaseGradleMemory(File workspaceDir) throws Exception {

		// Increase maximum memory to fix issue with some 7zip bundles

		appendGradleProperty(workspaceDir, "org.gradle.jvmargs", "-Xmx8g");
	}

	public static void removeComments(String projectPath) throws Exception {
		File pomXMLFile = new File(projectPath, "/pom.xml");

		Path pomXmlPath = pomXMLFile.toPath();

		byte[] pomXmlBytes = Files.readAllBytes(pomXmlPath);

		String content = new String(pomXmlBytes);

		content = content.replaceAll("(<!--<)", "<");

		content = content.replaceAll("(>-->)", ">");

		Files.delete(pomXmlPath);

		Files.write(pomXmlPath, content.getBytes(), StandardOpenOption.CREATE_NEW);
	}

	public static BladeTestResults runBlade(
		BladeTest bladeTest, PrintStream outputStream, PrintStream errorStream, boolean assertErrors, String... args) {

		PrintStream originalOutputStream = System.out;
		PrintStream originalErrorStream = System.err;

		try {
			System.setOut(outputStream);
			System.setErr(errorStream);
			bladeTest.run(args);
		}
		catch (Exception exception) {
			if (assertErrors) {
				exception.printStackTrace(errorStream);
				Assert.fail("Encountered error: " + errorStream.toString());
			}
		}
		finally {
			System.setOut(originalOutputStream);
			System.setErr(originalErrorStream);
		}

		String content = outputStream.toString();

		return new BladeTestResults(bladeTest, content, errorStream.toString());
	}

	public static BladeTestResults runBlade(
		BladeTest bladeTest, PrintStream outputStream, PrintStream errorStream, String... args) {

		return runBlade(bladeTest, outputStream, errorStream, true, args);
	}

	public static BladeTestResults runBlade(boolean assertErrors, String... args) {
		return runBlade(_getHomeDir(), _getHomeDir(), System.in, assertErrors, args);
	}

	public static BladeTestResults runBlade(
		File settingsDir, File extensionsDir, boolean assertErrors, String... args) {

		return runBlade(settingsDir, extensionsDir, System.in, assertErrors, args);
	}

	public static BladeTestResults runBlade(
		File settingsDir, File extensionsDir, InputStream in, boolean assertErrors, String... args) {

		Predicate<String> localeFilter = line -> line.contains("LC_ALL: cannot change locale");

		Predicate<String> slf4JFilter = line -> line.startsWith("SLF4J:");

		Predicate<String> warningFilter = line -> line.startsWith("WARNING:");

		StringPrintStream outputPrintStream = StringPrintStream.newInstance();

		Collection<Predicate<String>> filters = Arrays.asList(localeFilter, slf4JFilter, warningFilter);

		StringPrintStream errorPrintStream = StringPrintStream.newFilteredInstance(filters);

		return runBlade(settingsDir, extensionsDir, outputPrintStream, errorPrintStream, in, assertErrors, args);
	}

	public static BladeTestResults runBlade(File settingsDir, File extensionsDir, InputStream in, String... args)
		throws Exception {

		return runBlade(settingsDir, extensionsDir, in, true, args);
	}

	public static BladeTestResults runBlade(
		File settingsDir, File extensionsDir, PrintStream out, PrintStream err, InputStream in, boolean assertErrors,
		String... args) {

		BladeTest.BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setAssertErrors(assertErrors);
		bladeTestBuilder.setExtensionsDir(extensionsDir.toPath());
		bladeTestBuilder.setSettingsDir(settingsDir.toPath());
		bladeTestBuilder.setStdError(err);
		bladeTestBuilder.setStdIn(in);
		bladeTestBuilder.setStdOut(out);

		return runBlade(bladeTestBuilder.build(), out, err, assertErrors, args);
	}

	public static BladeTestResults runBlade(File settingsDir, File extensionsDir, String... args) {
		return runBlade(settingsDir, extensionsDir, System.in, true, args);
	}

	public static BladeTestResults runBlade(Path settingsDir, Path extensionsDir, String... args) {
		return runBlade(settingsDir.toFile(), extensionsDir.toFile(), System.in, true, args);
	}

	public static BladeTestResults runBlade(String... args) {
		return runBlade(_getHomeDir(), _getHomeDir(), System.in, true, args);
	}

	public static void updateMavenRepositories(String projectPath) throws Exception {
		File pomXMLFile = new File(projectPath + "/pom.xml");

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(pomXMLFile);

		_addNexusRepositoriesElement(document, "repositories", "repository");
		_addNexusRepositoriesElement(document, "pluginRepositories", "pluginRepository");

		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		DOMSource domSource = new DOMSource(document);

		StreamResult streamResult = new StreamResult(pomXMLFile);

		transformer.transform(domSource, streamResult);
	}

	public static void verifyBuild(String projectPath, String outputFileName) throws Exception {
		BuildTask buildTask = GradleRunnerUtil.executeGradleRunner(projectPath, "build");

		GradleRunnerUtil.verifyGradleRunnerOutput(buildTask);

		GradleRunnerUtil.verifyBuildOutput(projectPath, outputFileName);
	}

	private static void _addNexusRepositoriesElement(Document document, String parentElementName, String elementName) {
		Element projectElement = document.getDocumentElement();

		Element repositoriesElement = XMLTestUtil.getChildElement(projectElement, parentElementName);

		if (repositoriesElement == null) {
			repositoriesElement = document.createElement(parentElementName);

			projectElement.appendChild(repositoriesElement);
		}

		Element repositoryElement = document.createElement(elementName);

		Element idElement = document.createElement("id");

		idElement.appendChild(document.createTextNode(System.currentTimeMillis() + ""));

		Element urlElement = document.createElement("url");

		Text urlText = document.createTextNode(_REPOSITORY_CDN_URL);

		urlElement.appendChild(urlText);

		repositoryElement.appendChild(idElement);
		repositoryElement.appendChild(urlElement);

		repositoriesElement.appendChild(repositoryElement);
	}

	private static File _getHomeDir() {
		return new File(System.getProperty("user.home"));
	}

	private static final String _REPOSITORY_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/groups/public";

}