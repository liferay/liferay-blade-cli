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
public class LockLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/LockProtectedAction.java");
	LockLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new LockLegacyAPI();
	}

	@Test
	public void lockLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(4, results.size());

		SearchResult problem = results.get(0);

		assertEquals(22, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(893, problem.startOffset);
			assertEquals(940, problem.endOffset);
		} else {
			assertEquals(872, problem.startOffset);
			assertEquals(919, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(46, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1420, problem.startOffset);
			assertEquals(1484, problem.endOffset);
		}
		else {
			assertEquals(1375, problem.startOffset);
			assertEquals(1438, problem.endOffset);
		}

		problem = results.get(2);

		assertEquals(62, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1747, problem.startOffset);
			assertEquals(1806, problem.endOffset);
		}
		else {
			assertEquals(1686, problem.startOffset);
			assertEquals(1745, problem.endOffset);
		}

		problem = results.get(3);

		assertEquals(73, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1971, problem.startOffset);
			assertEquals(2044, problem.endOffset);
		}
		else {
			assertEquals(1899, problem.startOffset);
			assertEquals(1971, problem.endOffset);
		}
	}

}
