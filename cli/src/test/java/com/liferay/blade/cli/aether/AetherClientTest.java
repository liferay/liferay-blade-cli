/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.aether;

import java.io.File;

import org.eclipse.aether.artifact.Artifact;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AetherClientTest {

	@Ignore
	@Test
	public void testCheckLatestArchetypeVersionOffline() throws Exception {
		AetherClient client = new AetherClient(null, "test-resources/localrepo");

		Artifact artifact = client.findLatestAvailableArtifact(
			"com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

		Assert.assertNotNull(artifact);

		File file = artifact.getFile();

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());

		String filePathString = file.getPath();

		Assert.assertTrue(filePathString.contains("test-resources/localrepo"));

		String name = file.getName();

		Assert.assertTrue(name.startsWith("com.liferay.gradle.plugins.workspace"));
		Assert.assertTrue(name.endsWith("sources.jar"));
		Assert.assertTrue(name.contains("1.0.8"));
	}

	@Ignore
	@Test
	public void testCheckLatestArchetypeVersionOnline() throws Exception {
		AetherClient client = new AetherClient();

		Artifact artifact = client.findLatestAvailableArtifact(
			"com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

		Assert.assertNotNull(artifact);

		File file = artifact.getFile();

		Assert.assertNotNull(file);
		Assert.assertTrue(file.exists());

		String name = file.getName();

		Assert.assertTrue(name.startsWith("com.liferay.gradle.plugins.workspace"));
		Assert.assertTrue(name.endsWith("sources.jar"));
		Assert.assertEquals(name, true, name.matches(".*-1\\.[0-9]+\\.[0-9]+-.*"));
	}

}