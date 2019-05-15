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

package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTest.BladeTestBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 */
public class GradleExecTest {

	@Before
	public void setUp() throws Exception {
		File rootDir = temporaryFolder.getRoot();

		_rootPath = rootDir.toPath();

		File extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		_extensionsPath = extensionsDir.toPath();
	}

	@Test
	public void testGradleWrapper() throws Exception {
		Path temporaryPath = _rootPath.normalize();

		String[] args = {"--base", temporaryPath.toString(), "create", "-t", "api", "foo"};

		_getBladeTest().run(args);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream ps = new PrintStream(baos);

		BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setExtensionsDir(_extensionsPath);
		bladeTestBuilder.setSettingsDir(_rootPath);
		bladeTestBuilder.setStdOut(ps);

		BladeCLI bladeCLI = bladeTestBuilder.build();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		ProcessResult result = gradleExec.executeTask("tasks");

		int resultCode = result.getResultCode();

		String output = result.get();

		if (resultCode > 0) {
			Assert.assertEquals(
				"Gradle command returned error code " + resultCode + System.lineSeparator() + output, 0, resultCode);
		}
		else {
			Assert.assertFalse(
				"Gradle build failed " + System.lineSeparator() + output, output.contains("BUILD FAILED"));
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private BladeTest _getBladeTest() {
		BladeTestBuilder bladeTestBuilder = BladeTest.builder();

		bladeTestBuilder.setExtensionsDir(_extensionsPath);
		bladeTestBuilder.setSettingsDir(_rootPath);

		return bladeTestBuilder.build();
	}

	private Path _extensionsPath = null;
	private Path _rootPath = null;

}