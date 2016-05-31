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

package com.liferay.blade.eclipse.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.blade.api.SearchResult;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class JavaFileJDTTest {

	@Test
	public void checkStaticMethodInvocation() throws Exception {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations(null, "String", "valueOf", null);

		assertNotNull(searchResults);

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);

		if (PlatformUtil.isWindows()) {
			assertEquals(14, searchResult.startLine);
			assertEquals(15, searchResult.endLine);
			assertEquals(242, searchResult.startOffset);
			assertEquals(265, searchResult.endOffset);
		}
		else {
			assertEquals(14, searchResult.startLine);
			assertEquals(15, searchResult.endLine);
			assertEquals(229, searchResult.startOffset);
			assertEquals(251, searchResult.endOffset);
		}
	}

	@Test
	public void checkMethodInvocation() throws Exception {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations("Foo", null, "bar", null);

		assertNotNull(searchResults);

		assertEquals(4, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);

		if (PlatformUtil.isWindows()) {
			assertEquals(10, searchResult.startLine);
			assertEquals(11, searchResult.endLine);
			assertEquals(190, searchResult.startOffset);
			assertEquals(210, searchResult.endOffset);
		}
		else {
			assertEquals(10, searchResult.startLine);
			assertEquals(11, searchResult.endLine);
			assertEquals(181, searchResult.startOffset);
			assertEquals(200, searchResult.endOffset);
		}
	}
	@Test
	public void checkGuessMethodInvocation() {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> results = javaFileChecker.findMethodInvocations(null, "JavaFileChecker" , "staticCall", new String[]{"String","String","String"});
		assertNotNull(results);
		assertEquals(4, results.size());
		results = javaFileChecker.findMethodInvocations("JavaFileChecker", null, "call", new String[]{"String","String","String"});
		assertNotNull(results);
		assertEquals(4, results.size());
	}
}
