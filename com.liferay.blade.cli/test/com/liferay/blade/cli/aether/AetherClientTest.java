package com.liferay.blade.cli.aether;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.aether.artifact.Artifact;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AetherClientTest {

	@Test
	public void testCheckLatestArchetypeVersionOffline() throws Exception {
		AetherClient client = new AetherClient(null, "test-localrepo");

		Artifact artifact = client.findLatestAvailableArtifact(
			"com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

		assertNotNull(artifact);

		File file = artifact.getFile();

		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(
			file.getName().startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(file.getName().endsWith("sources.jar"));
		assertTrue(file.getPath().contains("test-localrepo"));
		assertTrue(file.getName().contains("1.0.1"));
	}

	@Test
	public void testCheckLatestArchetypeVersionOnline() throws Exception {
		AetherClient client = new AetherClient();

		Artifact artifact = client.findLatestAvailableArtifact(
			"com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

		assertNotNull(artifact);

		File file = artifact.getFile();

		assertNotNull(file);
		assertTrue(file.exists());
		assertTrue(
			file.getName().startsWith("com.liferay.gradle.plugins.workspace"));
		assertTrue(file.getName().endsWith("sources.jar"));
		assertTrue(file.getName().contains("1.0.8"));
	}

}