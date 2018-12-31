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

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 */
public class DeployCommandTest {

	@Test
	public void testInstallJar() throws Exception {
		File workspaceDir = temporaryFolder.newFolder();

		String[] args = {"--base", workspaceDir.getPath(), "init"};

		TestUtil.runBlade(args);

		args = new String[] {"--base", workspaceDir.getPath(), "server", "init"};

		TestUtil.runBlade(args);

		File bundlesDirectory = new File(workspaceDir.getPath(), "bundles");

		Assert.assertTrue(bundlesDirectory.exists());

		File osgiDirectory = new File(bundlesDirectory, "osgi");

		Assert.assertTrue(osgiDirectory.exists());

		File osgiModulesDirectory = new File(osgiDirectory, "modules");

		Assert.assertTrue(osgiModulesDirectory.exists());

		int filesCount = osgiModulesDirectory.list().length;

		Assert.assertEquals(0, filesCount);

		File modulesDirectory = new File(workspaceDir, "modules");

		Assert.assertTrue(modulesDirectory.exists());

		args = new String[] {"--base", modulesDirectory.getAbsolutePath(), "create", "-t", "soy-portlet", "foo"};

		TestUtil.runBlade(args);

		File projectDirectory = new File(modulesDirectory, "foo");

		Assert.assertTrue(projectDirectory.exists());

		args = new String[] {"--base", projectDirectory.getAbsolutePath(), "deploy"};

		TestUtil.runBlade(args);

		filesCount = osgiModulesDirectory.list().length;

		Assert.assertEquals(1, filesCount);
	}

	@Test
	public void testInstallJarStandalone() throws Exception {
		File workspaceDir = temporaryFolder.newFolder();

		File standaloneDir = temporaryFolder.newFolder();

		String[] args = {"--base", workspaceDir.getPath(), "init"};

		TestUtil.runBlade(args);

		args = new String[] {"--base", workspaceDir.getPath(), "server", "init"};

		TestUtil.runBlade(args);

		File bundlesDirectory = new File(workspaceDir.getPath(), "bundles");

		Assert.assertTrue(bundlesDirectory.exists());

		args = new String[] {"--base", standaloneDir.getAbsolutePath(), "create", "-t", "soy-portlet", "foo"};

		TestUtil.runBlade(args);

		File projectDirectory = new File(standaloneDir, "foo");

		Assert.assertTrue(projectDirectory.exists());

		Path projectDirectoryPath = projectDirectory.toPath();

		File deployDirectory = new File(bundlesDirectory, "deploy");

		String deployDirectoryString = deployDirectory.getAbsolutePath();

		String deployDirectoryGradleString = String.format("    deployDir = '%s'", deployDirectoryString);

		List<String> lines = Arrays.asList("", "liferay {", deployDirectoryGradleString, "}");

		Files.write(
			projectDirectoryPath.resolve("build.gradle"), lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

		args = new String[] {"--base", projectDirectoryPath.toString(), "deploy"};

		TestUtil.runBlade(args);

		int filesCount = deployDirectory.list().length;

		Assert.assertEquals(1, filesCount);
	}

	@Test
	public void testInstallWar() throws Exception {
		File workspaceDir = temporaryFolder.newFolder();

		String[] args = {"--base", workspaceDir.getPath(), "init"};

		TestUtil.runBlade(args);

		args = new String[] {"--base", workspaceDir.getPath(), "server", "init"};

		TestUtil.runBlade(args);

		File bundlesDirectory = new File(workspaceDir.getPath(), "bundles");

		Assert.assertTrue(bundlesDirectory.exists());

		File deployDirectory = new File(bundlesDirectory, "deploy");

		Assert.assertTrue(deployDirectory.exists());

		int filesCount = deployDirectory.list().length;

		Assert.assertEquals(0, filesCount);

		File warsDirectory = new File(workspaceDir, "wars");

		Assert.assertTrue(warsDirectory.exists());

		args = new String[] {"--base", warsDirectory.getAbsolutePath(), "create", "-t", "war-mvc-portlet", "foo"};

		TestUtil.runBlade(args);

		File projectDirectory = new File(warsDirectory, "foo");

		Assert.assertTrue(projectDirectory.exists());

		args = new String[] {"--base", projectDirectory.getAbsolutePath(), "deploy"};

		TestUtil.runBlade(args);

		filesCount = deployDirectory.list().length;

		Assert.assertEquals(1, filesCount);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}