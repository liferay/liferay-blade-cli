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

import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.zip.ZipException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Gregory Amerson
 */
public class ZipSlipTest {

	@Test
	public void testNoZipSlipZip() throws Exception {
		_testZip("no-zip-slip.zip");

		File tempDir = temporaryFolder.getRoot();

		File expectedAFile = new File(tempDir, "afile");

		Assert.assertTrue("Expected file " + expectedAFile + " not found.", expectedAFile.exists());

		File expectedBFile = new File(tempDir, "b/bfile");

		Assert.assertTrue("Expected file " + expectedBFile + " not found.", expectedBFile.exists());

		File expectedEFile = new File(tempDir, "c/d/efile");

		Assert.assertTrue("Expected file " + expectedEFile + " not found.", expectedEFile.exists());
	}

	@Test(expected = ZipException.class)
	public void testZipSlipZip() throws Exception {
		_testZip("zip-slip.zip");
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testZip(String fileName) throws Exception {
		File tempDir = temporaryFolder.getRoot();

		Path tempPath = tempDir.toPath();

		Path zipPath = tempPath.resolve(fileName);

		Files.copy(getClass().getResourceAsStream(fileName), zipPath);

		FileUtil.unzip(zipPath.toFile(), tempDir);
	}

}