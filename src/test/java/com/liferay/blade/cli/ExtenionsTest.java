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

import com.liferay.blade.cli.extensions.Extensions;

import java.util.Objects;

import org.gradle.internal.impldep.org.testng.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ExtenionsTest {

	@Test
	public void testArgsSort() {
		String[] args = {"--base", "/foo/bar/dir/", "--flag1", "extension", "install", "/path/to/jar.jar", "--flag2" };

		String[] sortedArgs = Extensions.sort(args);

		boolean correctSort = false;

		for (String arg : sortedArgs) {
			if (Objects.equals(arg, "extension install")) {
				correctSort = true;
			}
		}

		Assert.assertTrue(correctSort);
	}

}