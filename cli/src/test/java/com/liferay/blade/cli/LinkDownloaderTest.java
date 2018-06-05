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

import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 */
public class LinkDownloaderTest {

	@Test
	public void testMavenInitWorkspaceDirectoryHasFiles() throws Exception {
		Path targetFile = new File(tempFolder.getRoot(), "bnd.bnd").toPath();

		String link = "https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/bnd.bnd";

		BladeUtil.downloadLink(link, targetFile);

		Assert.assertTrue(Files.exists(targetFile));
	}

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

}