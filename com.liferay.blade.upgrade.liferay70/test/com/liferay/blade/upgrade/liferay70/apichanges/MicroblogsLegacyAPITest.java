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
public class MicroblogsLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MicroblogsPortlet.java");
	MicroblogsLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new MicroblogsLegacyAPI();
	}

	@Test
	public void microblogsLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(
			testFile,new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(4, results.size());

		SearchResult problem = results.get(0);

		assertEquals(22, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(972, problem.startOffset);
			assertEquals(1029, problem.endOffset);
		}
		else {
			assertEquals(951, problem.startOffset);
			assertEquals(1008, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(47, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1809, problem.startOffset);
			assertEquals(1876, problem.endOffset);
		}
		else {
			assertEquals(1763, problem.startOffset);
			assertEquals(1830, problem.endOffset);
		}

		problem = results.get(2);

		assertEquals(77, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(2879, problem.startOffset);
			assertEquals(2997, problem.endOffset);
		}
		else {
			assertEquals(2803, problem.startOffset);
			assertEquals(2920, problem.endOffset);
		}

		problem = results.get(3);

		assertEquals(81, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(3018, problem.startOffset);
			assertEquals(3194, problem.endOffset);
		}
		else {
			assertEquals(2938, problem.startOffset);
			assertEquals(3112, problem.endOffset);
		}
	}

}
