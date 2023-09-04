/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class GradleToolingTest {

	@Before
	public void setUp() throws Exception {
		File wsDir = temporaryFolder.newFolder("build", "testws1");

		_wsPath = wsDir.toPath();

		Path toolingZipPath = Paths.get("build/tooling.zip");

		Assert.assertTrue("Expected to find tooling.zip", Files.exists(toolingZipPath));

		Files.copy(toolingZipPath, _TOOLING_ZIP, StandardCopyOption.REPLACE_EXISTING);

		FileUtil.copyDir(Paths.get("test-resources/projects/testws1"), _wsPath);
	}

	@After
	public void tearDownClass() throws Exception {
		Files.delete(_TOOLING_ZIP);
	}

	@Test
	public void testGetDockerContainerId() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(_wsPath);

		Assert.assertEquals("custom-workspace-liferay1", projectInfo.getDockerContainerId());
	}

	@Test
	public void testGetDockerImageId() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(_wsPath);

		Assert.assertEquals("custom-workspace-image:1.0.0", projectInfo.getDockerImageId());
	}

	@Test
	public void testGetDockerImageLiferay() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(_wsPath);

		Assert.assertNotNull("liferay/portal:7.2.0-ga1", projectInfo.getDockerImageLiferay());
	}

	@Test
	public void testGetOutputFiles() throws Exception {
		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(_wsPath);

		Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

		Assert.assertNotNull(projectOutputFiles);

		Assert.assertEquals(
			projectOutputFiles.toString(), true, projectOutputFiles.containsKey(":modules:testportlet"));

		Set<File> files = projectOutputFiles.get(":modules:testportlet");

		Assert.assertEquals(files.toString(), 1, files.size());
	}

	@Test
	public void testGetPluginClassNames() throws Exception {
		Path projectPath = _wsPath.resolve("modules/testportlet");

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(projectPath);

		Set<String> pluginClassNames = projectInfo.getPluginClassNames();

		Assert.assertNotNull(pluginClassNames);
		Assert.assertTrue(pluginClassNames.contains("com.liferay.gradle.plugins.LiferayOSGiPlugin"));
	}

	@Test
	public void testIsLiferayModule() throws Exception {
		Path projectPath = _wsPath.resolve("modules/testportlet");

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(projectPath);

		Assert.assertTrue(projectInfo.isLiferayProject());
	}

	@Test
	public void testIsNotLiferayModule() throws Exception {
		Path projectPath = _wsPath.resolve("modules");

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(projectPath);

		Assert.assertFalse(projectInfo.isLiferayProject());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static final String _TEST_OUTPUT_PATH = System.getProperty("testOutputPath");

	private static final Path _TOOLING_ZIP = Paths.get(_TEST_OUTPUT_PATH, "tooling.zip");

	private Path _wsPath = null;

}