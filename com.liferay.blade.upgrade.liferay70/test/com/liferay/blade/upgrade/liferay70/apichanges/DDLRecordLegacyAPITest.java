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
public class DDLRecordLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditRecordAction.java");
	DDLRecordLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new DDLRecordLegacyAPI();
	}

	@Test
	public void dDLRecordLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(3, results.size());

		SearchResult problem = results.get(0);

		assertEquals(30, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1361, problem.startOffset);
			assertEquals(1426, problem.endOffset);
		}
		else {
			assertEquals(1332, problem.startOffset);
			assertEquals(1397, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(132, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(4220, problem.startOffset);
			assertEquals(4263, problem.endOffset);
		}
		else {
			assertEquals(4089, problem.startOffset);
			assertEquals(4132, problem.endOffset);
		}

		problem = results.get(2);

		assertEquals(145, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(4619, problem.startOffset);
			assertEquals(4699, problem.endOffset);
		}
		else {
			assertEquals(4475, problem.startOffset);
			assertEquals(4554, problem.endOffset);
		}
	}

}
