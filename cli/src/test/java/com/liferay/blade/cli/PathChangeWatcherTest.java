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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 */
public class PathChangeWatcherTest {

	@Test
	public void testPathChangeWatcher() throws Exception {
		File fileToWatch = new File(temporaryFolder.getRoot(), "foo.bar");

		fileToWatch.createNewFile();

		try (PathChangeWatcher watcher = new PathChangeWatcher(fileToWatch.toPath())) {
			Assert.assertFalse("File should not have been modified", watcher.get());

			fileToWatch.setLastModified(System.currentTimeMillis());

			Assert.assertTrue("File should have been modified", watcher.get());
		}
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

}