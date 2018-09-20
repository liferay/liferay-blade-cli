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

package com.liferay.extensions.sample.profile;

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
public class ProfilesTest {

	@Test
	public void testProfileExtension() throws Exception {
		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		String[] args = {"--base", workspaceDir.getPath(), "init", "-b", "foo"};

		BladeTestResults results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		args = new String[] {"--base", workspaceDir.getPath(), "foo", "bar"};

		results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		String output = results.getOutput();

		Assert.assertTrue(output, output.contains("NewCommand"));

		args = new String[] {"--base", workspaceDir.getPath(), "deploy", "--watch"};

		results = TestUtil.runBlade(temporaryFolder.getRoot(), args);

		output = results.getOutput();

		Assert.assertTrue(output, output.contains("OverriddenCommand says true"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}