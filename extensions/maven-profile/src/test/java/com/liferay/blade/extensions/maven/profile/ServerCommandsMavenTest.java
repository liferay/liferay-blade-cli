/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerCommandsMavenTest {

	@Test
	public void testServerInit() throws Exception {
		File extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		String[] args = {
			"--base", workspaceDir.getPath(), "init", "-f", "-P", "maven", "-v", BladeTest.LIFERAY_VERSION_PORTAL_7456
		};

		TestUtil.runBlade(workspaceDir, extensionsDir, args);

		File pomXMLFile = new File(workspaceDir, "pom.xml");

		Assert.assertTrue(pomXMLFile.getAbsolutePath() + " does not exist.", pomXMLFile.exists());

		XMLTestUtil.editXml(
			pomXMLFile,
			document -> {
				_addNexusRepositoriesElement(document, "repositories", "repository");
				_addNexusRepositoriesElement(document, "pluginRepositories", "pluginRepository");
			});

		XMLTestUtil.updateWorkspaceBundleUrl(workspaceDir, BladeTest.LIFERAY_VERSION_PORTAL_7456);

		args = new String[] {"--base", workspaceDir.getPath(), "server", "init"};

		File bundlesDir = new File(workspaceDir.getPath(), "bundles");

		Assert.assertFalse(bundlesDir.exists());

		TestUtil.runBlade(workspaceDir, extensionsDir, args);

		Assert.assertTrue(bundlesDir.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _addNexusRepositoriesElement(Document document, String parentElementName, String elementName) {
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

	private static final String _REPOSITORY_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/groups/public";

}