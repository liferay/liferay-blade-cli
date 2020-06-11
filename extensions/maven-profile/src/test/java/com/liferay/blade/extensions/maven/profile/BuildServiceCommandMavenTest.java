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
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 */
public class BuildServiceCommandMavenTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testMavenServiceBuilder() throws Exception {
		String[] args = {
			"--base", _workspaceDir.getPath(), "init", "-P", "maven", "mavenworkspace", "-v",
			BladeTest.PRODUCT_VERSION_PORTAL_73
		};

		File mavenworkspace = new File(_workspaceDir, "mavenworkspace");

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		Assert.assertTrue(mavenworkspace.exists());

		args = new String[] {"--base", mavenworkspace.getPath(), "create", "-t", "service-builder", "sb1"};

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		args = new String[] {"--base", mavenworkspace.getPath(), "create", "-t", "service-builder", "sb2"};

		TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		Path mavenworkspacePath = mavenworkspace.toPath();

		Path modulesPath = mavenworkspacePath.resolve("modules");

		Path sb1SourcePath = Paths.get("sb1", "sb1-service", "src");

		sb1SourcePath = modulesPath.resolve(sb1SourcePath);

		Path sb2SourcePath = Paths.get("sb2", "sb2-service", "src");

		sb2SourcePath = modulesPath.resolve(sb2SourcePath);

		boolean sb1SourceExists = Files.exists(sb1SourcePath);
		boolean sb2SourceExists = Files.exists(sb2SourcePath);

		Assert.assertFalse(sb1SourceExists && sb2SourceExists);

		args = new String[] {"--base", mavenworkspace.getPath(), "buildService"};

		BladeTestResults results = TestUtil.runBlade(mavenworkspace, _extensionsDir, args);

		sb1SourceExists = Files.exists(sb1SourcePath);
		sb2SourceExists = Files.exists(sb2SourcePath);

		Path fooPath = Paths.get("sb1", "sb1-api", "src", "main", "java", "sb1", "model", "Foo.java");

		fooPath = modulesPath.resolve(fooPath);

		boolean fooExists = Files.exists(fooPath);

		Assert.assertTrue(sb1SourceExists && sb2SourceExists && fooExists);

		String output = results.getOutput();

		boolean containsSuccess = output.contains("BUILD SUCCESS");

		Assert.assertTrue("Expected 'BUILD SUCCESS' message." + System.lineSeparator() + output, containsSuccess);
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _workspaceDir = null;

}