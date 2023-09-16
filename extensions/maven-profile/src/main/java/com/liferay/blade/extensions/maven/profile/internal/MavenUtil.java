/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile.internal;

import java.io.File;

import java.util.Objects;
import java.util.Properties;

/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liferay.blade.cli.util.BladeUtil;

/**
 * @author Christopher Bryan Boyd
 */
public class MavenUtil {

	public static Properties getMavenProperties(File baseDir) {
		try {
			Properties properties = new Properties();

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(getpomXMLFile(baseDir));

			Element documentElement = document.getDocumentElement();

			documentElement.normalize();

			NodeList propertiesNodeList = document.getElementsByTagName("properties");

			Node propertiesNode = propertiesNodeList.item(0);

			if (propertiesNode.getNodeType() == Node.ELEMENT_NODE) {
				NodeList nodeList = propertiesNode.getChildNodes();

				for (int nodeInt = 0; nodeInt < nodeList.getLength(); nodeInt++) {
					Node sNode = nodeList.item(nodeInt);

					if (sNode.getNodeType() == Node.ELEMENT_NODE) {
						properties.put(sNode.getNodeName(), sNode.getTextContent());
					}
				}
			}

			return properties;
		}
		catch (Throwable th) {
			throw new RuntimeException("Unable to get maven properties", th);
		}
	}

	public static File getpomXMLFile(File dir) {
		return new File(getWorkspaceDir(dir), _POM_XML_FILE_NAME);
	}

	public static File getWorkspaceDir(File dir) {
		File mavenParent = _findWorkspacePomFile(dir);

		if (_isWorkspacePomFile(new File(mavenParent, "pom.xml"))) {
			return mavenParent;
		}

		File mavenPom = new File(dir, "pom.xml");

		if (mavenPom.exists() && _isWorkspacePomFile(mavenPom)) {
			return dir;
		}

		return null;
	}

	public static boolean isWorkspace(File dir) {
		File workspaceDir = getWorkspaceDir(dir);

		if (Objects.isNull(dir) || Objects.isNull(workspaceDir)) {
			return false;
		}

		File pomFile = new File(workspaceDir, "pom.xml");

		if (_isWorkspacePomFile(pomFile)) {
			return true;
		}

		return false;
	}

	private static File _findWorkspacePomFile(File dir) {
		if (dir == null) {
			return null;
		}
		else if (Objects.equals(dir.toString(), ".") || !dir.isAbsolute()) {
			try {
				dir = dir.getCanonicalFile();
			}
			catch (Exception exception) {
				dir = dir.getAbsoluteFile();
			}
		}

		File file = new File(dir, "pom.xml");

		if (file.exists() && _isWorkspacePomFile(file)) {
			return dir;
		}

		return _findWorkspacePomFile(dir.getParentFile());
	}

	private static boolean _isWorkspacePomFile(File pomFile) {
		boolean pom = false;

		if (Objects.equals(pomFile.getName(), "pom.xml") && pomFile.exists()) {
			pom = true;
		}

		if (pom) {
			try {
				String content = BladeUtil.read(pomFile);

				if (content.contains("portal.tools.bundle.support")) {
					return true;
				}
			}
			catch (Exception exception) {
			}
		}

		return false;
	}

	private static final String _POM_XML_FILE_NAME = "pom.xml";

}