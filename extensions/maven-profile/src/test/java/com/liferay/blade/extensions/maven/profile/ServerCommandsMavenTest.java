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
			"--base", workspaceDir.getPath(), "init", "-f", "-P", "maven", "-v", BladeTest.PRODUCT_VERSION_PORTAL_73
		};

		TestUtil.runBlade(workspaceDir, extensionsDir, args);

		File pomXmlFile = new File(workspaceDir, "pom.xml");

		Assert.assertTrue(pomXmlFile.getAbsolutePath() + " does not exist.", pomXmlFile.exists());

		XMLTestUtil.editXml(
			pomXmlFile,
			document -> {
				_addNexusRepositoriesElement(document, "repositories", "repository");
				_addNexusRepositoriesElement(document, "pluginRepositories", "pluginRepository");
			});

		args = new String[] {"--base", workspaceDir.getPath(), "server", "init"};

		File bundlesDir = new File(workspaceDir.getPath(), "bundles");

		Assert.assertFalse(bundlesDir.exists());

		TestUtil.runBlade(workspaceDir, extensionsDir, args);

		Assert.assertTrue(bundlesDir.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

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

	private static final String _REPOSITORY_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/groups/public";

}