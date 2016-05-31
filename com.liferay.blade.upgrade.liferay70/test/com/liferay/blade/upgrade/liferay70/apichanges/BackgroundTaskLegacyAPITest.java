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
public class BackgroundTaskLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/DLFileNameBackgroundTaskServiceImpl.java");
	BackgroundTaskLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new BackgroundTaskLegacyAPI();
	}

	@Test
	public void backgroundTaskLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());

		SearchResult problem = results.get(0);

		assertEquals(18, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(688, problem.startOffset);
			assertEquals(736, problem.endOffset);
		}
		else {
			assertEquals(671, problem.startOffset);
			assertEquals(719, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(19, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(746, problem.startOffset);
			assertEquals(801, problem.endOffset);
		}
		else {
			assertEquals(728, problem.startOffset);
			assertEquals(783, problem.endOffset);
		}
	}

}
