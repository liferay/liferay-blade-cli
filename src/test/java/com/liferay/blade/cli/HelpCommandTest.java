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

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class HelpCommandTest {

	@Test
	public void testHelpCommand() throws UnsupportedEncodingException {
		String content = _runBlade("help");

		Assert.assertTrue(content, content.contains("Usage:"));

		Assert.assertFalse(content, content.contains("--"));
	}

	@Test
	public void testHelpCommandSpecific() throws UnsupportedEncodingException {
		String content = _runBlade("help", "create");

		Assert.assertTrue(content, content.contains("Usage:"));

		Assert.assertTrue(content, content.contains("--"));
	}

	@Test
	public void testHelpFlag() throws UnsupportedEncodingException {
		String content = _runBlade("--help");

		Assert.assertTrue(content, content.contains("Usage:"));
	}

	private static String _runBlade(String... args) throws UnsupportedEncodingException {
		String content = TestUtil.runBlade(args);

		Assert.assertFalse(content, content.contains("No such command"));

		return content;
	}

}