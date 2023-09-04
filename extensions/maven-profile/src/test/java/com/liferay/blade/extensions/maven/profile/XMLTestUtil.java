/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import org.junit.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Gregory Amerson
 */
public class XMLTestUtil {

	public static final String BUNDLE_URL_PROPERTY = "liferay.workspace.bundle.url";

	public static final String LIFERAY_PORTAL_URL = "https://releases-cdn.liferay.com/portal/";

	public static final Map<String, String> liferayBundleUrlVersions = new HashMap<String, String>() {
		{
			put("7.0.6-2", LIFERAY_PORTAL_URL + "7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip");
			put("7.1.2", LIFERAY_PORTAL_URL + "7.1.2-ga3/liferay-ce-portal-tomcat-7.1.2-ga3-20190107144105508.tar.gz");
			put("7.2.0", LIFERAY_PORTAL_URL + "7.2.0-ga1/liferay-ce-portal-tomcat-7.2.0-ga1-20190531153709761.tar.gz");
			put("7.3.7", LIFERAY_PORTAL_URL + "7.3.7-ga8/liferay-ce-portal-tomcat-7.3.7-ga8-20210610183559721.tar.gz");
			put(
				"7.4.3.56",
				LIFERAY_PORTAL_URL + "7.4.3.56-ga56/liferay-ce-portal-tomcat-7.4.3.56-ga56-20221222175515613.tar.gz");
		}
	};

	public static void editXml(File xmlFile, Consumer<Document> consumer) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(xmlFile);

		consumer.accept(document);

		DOMSource domSource = new DOMSource(document);

		transformer.transform(domSource, new StreamResult(xmlFile));
	}

	public static Element getChildElement(Element parentElement, String name) {
		Node node = parentElement.getFirstChild();

		do {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;

				if (name.equals(element.getTagName())) {
					return element;
				}
			}
		}
		while ((node = node.getNextSibling()) != null);

		return null;
	}

	public static List<Element> getChildElements(Element element) {
		NodeList nodeList = element.getChildNodes();

		List<Element> elements = new ArrayList<>(nodeList.getLength());

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element)node;

				boolean ignoreNode = false;

				NodeList childNodeList = childElement.getChildNodes();

				for (int j = 0; j < childNodeList.getLength(); j++) {
					Node childNode = childNodeList.item(j);

					if (childNode.getNodeType() == Node.TEXT_NODE) {
						Text text = (Text)childNode;

						String textContent = text.getTextContent();

						if (textContent.contains("Ignore Dependency Comparison")) {
							ignoreNode = true;

							break;
						}
					}
				}

				if (!ignoreNode) {
					elements.add(childElement);
				}
			}
		}

		return elements;
	}

	public static Model getMavenModel(File pomFile) throws IOException, XmlPullParserException {
		MavenXpp3Reader mavenReader = new MavenXpp3Reader();

		mavenReader.setAddDefaultEntities(true);

		return mavenReader.read(new FileReader(pomFile));
	}

	public static void testXmlElement(
			Path path, String parentElementString, List<Element> elements, int index, String expectedTagName,
			String expectedTextContent)
		throws TransformerException {

		if (elements.size() <= index) {
			StringBuilder sb = new StringBuilder();

			sb.append("Missing child element <");
			sb.append(expectedTagName);
			sb.append('>');
			sb.append(expectedTextContent);
			sb.append("</");
			sb.append(expectedTagName);
			sb.append("> of ");
			sb.append(parentElementString);
			sb.append(" in ");
			sb.append(path);

			Assert.fail(sb.toString());
		}

		Element element = elements.get(index);

		String elementString = toString(element);

		Assert.assertEquals(
			"Incorrect tag name of " + elementString + " in " + path, expectedTagName, element.getTagName());
		Assert.assertEquals(
			"Incorrect text content of " + elementString + " in " + path, expectedTextContent,
			element.getTextContent());
	}

	public static String toString(Element element) throws TransformerException {
		StringWriter stringWriter = new StringWriter();

		_transformer.transform(new DOMSource(element), new StreamResult(stringWriter));

		return stringWriter.toString();
	}

	public static void updateMavenPom(Model model, File file) throws IOException {
		MavenXpp3Writer mavenWriter = new MavenXpp3Writer();

		FileWriter fileWriter = new FileWriter(file);

		mavenWriter.write(fileWriter, model);
	}

	public static void updateWorkspaceBundleUrl(File workspaceDir, String liferayVersion) throws Exception {
		try {
			File workspacePomFile = new File(workspaceDir, "pom.xml");

			if (!workspacePomFile.exists()) {
				throw new Exception("Can not find workspace pom.xml file");
			}

			Model pomModel = getMavenModel(workspacePomFile);

			Properties properties = pomModel.getProperties();

			properties.setProperty(BUNDLE_URL_PROPERTY, liferayBundleUrlVersions.get(liferayVersion));

			updateMavenPom(pomModel, workspacePomFile);
		}
		catch (Exception exception) {
			throw new Exception(exception);
		}
	}

	private static final Transformer _transformer;

	static {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		try {
			_transformer = transformerFactory.newTransformer();
		}
		catch (TransformerConfigurationException transformerConfigurationException) {
			throw new ExceptionInInitializerError(transformerConfigurationException);
		}

		_transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	}

}