package com.liferay.blade.cli.aether;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class AetherClientTest {

    @Test
    public void testCheckLatestArchetypeVersionOnline() throws Exception {
        File artifact = new AetherClient().findLatestAvailableArtifact("com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

        assertNotNull(artifact);
        assertTrue(artifact.exists());
        assertTrue(artifact.getName().startsWith("com.liferay.gradle.plugins.workspace"));
        assertTrue(artifact.getName().endsWith("sources.jar"));
    }

    @Test
    public void testCheckLatestArchetypeVersionOffline() throws Exception {
        AetherClient client = new AetherClient(null, "test-localrepo");

        File artifact = client.findLatestAvailableArtifact("com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

        assertNotNull(artifact);
        assertTrue(artifact.exists());
        assertTrue(artifact.getName().startsWith("com.liferay.gradle.plugins.workspace"));
        assertTrue(artifact.getName().endsWith("sources.jar"));
        assertTrue(artifact.getPath().contains( "test-localrepo" ));
    }

}
