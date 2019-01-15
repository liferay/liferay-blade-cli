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
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testGradleWrapper() throws Exception {
		File temporaryDir = temporaryFolder.getRoot();

		String[] args = {"--base", temporaryDir.getAbsolutePath(), "create", "-t", "api", "foo"};

		_getBladeTest().run(args);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream ps = new PrintStream(baos);

		BladeTestBuilder builder = BladeTest.builder();

		builder.setExtensionsDir(_extensionsDir.toPath());
		builder.setSettingsDir(_rootDir.toPath());
		builder.setStdOut(ps);

		BladeCLI blade = builder.build();

		GradleExec gradleExec = new GradleExec(blade);

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
		BladeTestBuilder builder = BladeTest.builder();

		builder.setExtensionsDir(_extensionsDir.toPath());
		builder.setSettingsDir(_rootDir.toPath());

		return builder.build();
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}