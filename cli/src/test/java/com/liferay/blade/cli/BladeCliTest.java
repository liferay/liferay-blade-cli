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

package com.liferay.blade.cli;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Vernon Singleton
 */
public class BladeCliTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testBladePrintUpdateIfAvailable() throws Exception {
		BladeTestResults results = TestUtil.runBlade(_rootDir, _extensionsDir, "update", "--check");

		String output = results.getOutput();

		output = output.trim();

		boolean containsFalse = output.equals("false");

		Assert.assertTrue("Current jar should be the latest version", containsFalse);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _rootDir = null;

}