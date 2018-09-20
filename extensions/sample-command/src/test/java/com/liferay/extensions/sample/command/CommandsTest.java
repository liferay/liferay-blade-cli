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

package com.liferay.extensions.sample.command;

import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class CommandsTest {

	@Test
	public void testCommandExtension() throws Exception {
		File rootDir = temporaryFolder.getRoot();

		String rootPathString = rootDir.getAbsolutePath();

		String[] args = {"--base", rootPathString, "hello", "--name", "foobar"};

		BladeTestResults results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		String output = results.getOutput();

		Assert.assertTrue(output, output.contains("foobar"));

		Assert.assertFalse(output, output.contains("maven"));

		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		args = new String[] {"--base", workspaceDir.getPath(), "init", "-b", "maven"};

		results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		args = new String[] {"--base", workspaceDir.getPath(), "hello", "--name", "foobar"};

		results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		output = results.getOutput();

		Assert.assertTrue(output, output.contains("foobar"));

		Assert.assertTrue(output, output.contains("maven"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}