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

package com.liferay.blade.upgrade.liferay70.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.eclipse.provider.JavaFileJDT;
import com.liferay.blade.eclipse.provider.PlatformUtil;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class WebContentLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/LegacyAPIsAntPortlet.java");
	WebContentLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new WebContentLegacyAPI();
	}

	@Test
	public void webContentLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(5, results.size());

		SearchResult problem = results.get(0);

		assertEquals(20, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(961, problem.startOffset);
			assertEquals(1023, problem.endOffset);
		}
		else {
			assertEquals(942, problem.startOffset);
			assertEquals(1004, problem.endOffset);
		}
		problem = results.get(1);

		assertEquals(47, problem.startLine);
		if (PlatformUtil.isWindows()) {
			assertEquals(1917, problem.startOffset);
			assertEquals(1950, problem.endOffset);
		}
		else {
			assertEquals(1871, problem.startOffset);
			assertEquals(1904, problem.endOffset);
		}
		problem = results.get(2);

		assertEquals(21, problem.startLine);
		if (PlatformUtil.isWindows()) {
			assertEquals(1033, problem.startOffset);
			assertEquals(1099, problem.endOffset);
		}
		else {
			assertEquals(1013, problem.startOffset);
			assertEquals(1079, problem.endOffset);
		}
		problem = results.get(3);

		assertEquals(41, problem.startLine);
		if (PlatformUtil.isWindows()) {
			assertEquals(1637, problem.startOffset);
			assertEquals(1695, problem.endOffset);
		}
		else {
			assertEquals(1597, problem.startOffset);
			assertEquals(1655, problem.endOffset);
		}
		problem = results.get(4);

		assertEquals(45, problem.startLine);
		if (PlatformUtil.isWindows()) {
			assertEquals(1830, problem.startOffset);
			assertEquals(1873, problem.endOffset);
		}
		else {
			assertEquals(1786, problem.startOffset);
			assertEquals(1829, problem.endOffset);
		}
	}

}
