package com.liferay.blade.cli.util;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class ExtensionConfigEntry {

	public static Collection<ExtensionConfigEntry> getEntries(File file) {
		Collection<ExtensionConfigEntry> entries = new HashSet<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setValidating(false);
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder builder; builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList nList = doc.getElementsByTagName("extension");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)nNode;

					ExtensionConfigEntry entry = get(element);

					entries.add(entry);
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {

			e.printStackTrace();
		}

		return entries;
	}

	public ExtensionConfigEntry(String name, String description, String location) {
		_name = name;
		_description = description;
		_location = location;
	}

	public String getDescription() {
		return _description;
	}

	public String getLocation() {
		return _location;
	}

	public String getName() {
		return _name;
	}

	private static ExtensionConfigEntry get(Element element) {
		String name = null;
		String description = null;
		String location = null;

		NodeList nodeList = element.getElementsByTagName("name");

		if (nodeList.getLength() > 0) {
			name = nodeList.item(0).getTextContent();
		}

		nodeList = element.getElementsByTagName("description");

		if (nodeList.getLength() > 0) {
			description = nodeList.item(0).getTextContent();
		}

		nodeList = element.getElementsByTagName("location");

		if (nodeList.getLength() > 0) {
			location = nodeList.item(0).getTextContent();
		}

		ExtensionConfigEntry entry = new ExtensionConfigEntry(name, description, location);

		return entry;
	}

	private final String _description;
	private final String _location;
	private final String _name;

}