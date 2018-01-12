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
		Assert.assertTrue(file.getPath().contains("test-resources/localrepo"));

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