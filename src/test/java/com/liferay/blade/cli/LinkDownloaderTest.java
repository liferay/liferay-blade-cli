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

import aQute.lib.io.IO;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.tooling.internal.consumer.ConnectorServices;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class LinkDownloaderTest {

	@After
	public void cleanUp() throws Exception {
		ConnectorServices.reset();

		if (_TEST_DIR.exists()) {
			IO.delete(_TEST_DIR);
			Assert.assertFalse(_TEST_DIR.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		_TEST_DIR.mkdirs();

		Assert.assertTrue(new File(_TEST_DIR, "afile").createNewFile());
	}

	@Test
	public void testMavenInitWorkspaceDirectoryHasFiles() throws Exception {
		Path targetFile = new File(_TEST_DIR, "bnd.bnd").toPath();

		String link = "https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/bnd.bnd";

		Util.downloadLink(link, targetFile);

		Assert.assertTrue(Files.exists(targetFile));
	}

	private static File _TEST_DIR = IO.getFile("build/test");

}