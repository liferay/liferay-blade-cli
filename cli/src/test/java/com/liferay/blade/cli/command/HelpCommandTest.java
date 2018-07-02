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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.powermock.reflect.Whitebox;

/**
 * @author Christopher Bryan Boyd
 */
public class HelpCommandTest {

	@Before
	public void setUp() throws Exception {
		Whitebox.setInternalState(BladeCLI.class, "USER_HOME_DIR", temporaryFolder.getRoot());

		BladeTest bladeTest = new BladeTest();

		File cacheDir = bladeTest.getCacheDir();

		if (cacheDir.exists()) {
			FileUtil.deleteDir(cacheDir.toPath());
		}
	}

	@Test
	public void testHelpCommand() throws Exception {
		String content = _runBlade("help");

		Assert.assertTrue(content, content.contains("Usage:"));

		Assert.assertFalse(content, content.contains("--"));
	}

	@Test
	public void testHelpCommandSpecific() throws Exception {
		String content = _runBlade("help", "create");

		Assert.assertTrue(content, content.contains("Usage:"));

		Assert.assertTrue(content, content.contains("--"));
	}

	@Test
	public void testHelpFlag() throws Exception {
		String content = _runBlade("--help");

		Assert.assertTrue(content, content.contains("Usage:"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static String _runBlade(String... args) throws Exception {
		String content = TestUtil.runBlade(args);

		Assert.assertFalse(content, content.contains("No such command"));

		return content;
	}

}