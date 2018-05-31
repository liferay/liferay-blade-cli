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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class ListExtensionsCommandTest {

	@Test
	public void testListExtensions() throws Exception {
		String[] args = {"extension", "list", "src/test/resources/com/liferay/blade/cli/extensions/extensions.xml"};

		String content = TestUtil.runBlade(args);

		Assert.assertTrue(content.contains("ext1 description"));
		Assert.assertTrue(content.contains("ext1 location"));
		Assert.assertTrue(content.contains("ext2 description"));
		Assert.assertTrue(content.contains("ext2 location"));
	}
}