/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.gradle;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class WorkspaceProvideGradleTest {

	@Test
	public void testIsWorkspace1() throws Exception {
		File root = temporaryFolder.getRoot();

		Path workspace = root.toPath();

		workspace = workspace.resolve("workspace");

		Files.createDirectories(workspace);

		Path gradlePath = workspace.resolve("settings.gradle");

		String plugin = "apply plugin: \"com.liferay.workspace\"";

		Files.write(gradlePath, plugin.getBytes());

		GradleWorkspaceProvider workspaceProviderGradle = new GradleWorkspaceProvider();

		Assert.assertTrue(workspaceProviderGradle.isWorkspace(workspace.toFile()));
	}

	@Test
	public void testIsWorkspace2() throws Exception {
		File root = temporaryFolder.getRoot();

		Path workspace = root.toPath();

		workspace = workspace.resolve("workspace");

		Files.createDirectories(workspace);

		Path gradleFile = workspace.resolve("settings.gradle");

		String plugin = "apply plugin: 'com.liferay.workspace'";

		Files.write(gradleFile, plugin.getBytes());

		GradleWorkspaceProvider workspaceProviderGradle = new GradleWorkspaceProvider();

		Assert.assertTrue(workspaceProviderGradle.isWorkspace(workspace.toFile()));
	}

	@Test
	public void testIsWorkspace3() throws Exception {
		File root = temporaryFolder.getRoot();

		Path workspace = root.toPath();

		workspace = workspace.resolve("workspace");

		Files.createDirectories(workspace);

		Path buildFile = workspace.resolve("build.gradle");

		Path settingsFile = workspace.resolve("settings.gradle");

		Files.createFile(settingsFile);

		String plugin = "\napply   plugin:   \n\"com.liferay.workspace\"";

		Files.write(buildFile, plugin.getBytes());

		GradleWorkspaceProvider workspaceProviderGradle = new GradleWorkspaceProvider();

		Assert.assertTrue(workspaceProviderGradle.isWorkspace(workspace.toFile()));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}