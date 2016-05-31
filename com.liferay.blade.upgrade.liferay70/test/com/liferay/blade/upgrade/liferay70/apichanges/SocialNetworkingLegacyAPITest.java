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
public class SocialNetworkingLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/GroupModelListener.java");
	final File testFile2 = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MeetupsPortlet.java");
	SocialNetworkingLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new SocialNetworkingLegacyAPI();
	}

	@Test
	public void socialNetworkingLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());

		SearchResult problem = results.get(0);

		assertEquals(20, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(781, problem.startOffset);
			assertEquals(843, problem.endOffset);
		}
		else {
			assertEquals(762, problem.startOffset);
			assertEquals(824, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(31, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1086, problem.startOffset);
			assertEquals(1149, problem.endOffset);
		}
		else {
			assertEquals(1056, problem.startOffset);
			assertEquals(1119, problem.endOffset);
		}

		results = component.searchFile(
			testFile2, new JavaFileJDT(testFile2));

		assertNotNull(results);
		assertEquals(6, results.size());

		problem = results.get(0);

		assertEquals(24, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1005, problem.startOffset);
			assertEquals(1070, problem.endOffset);
		}
		else {
			assertEquals(982, problem.startOffset);
			assertEquals(1047, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(57, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1892, problem.startOffset);
			assertEquals(1955, problem.endOffset);
		}
		else {
			assertEquals(1836, problem.startOffset);
			assertEquals(1899, problem.endOffset);
		}

		problem = results.get(2);

		assertEquals(128, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(4271, problem.startOffset);
			assertEquals(4570, problem.endOffset);
		}
		else {
			assertEquals(4144, problem.startOffset);
			assertEquals(4439, problem.endOffset);
		}

		problem = results.get(3);

		assertEquals(135, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(4591, problem.startOffset);
			assertEquals(4914, problem.endOffset);
		}
		else {
			assertEquals(4457, problem.startOffset);
			assertEquals(4775, problem.endOffset);
		}

		problem = results.get(4);

		assertEquals(25, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1080, problem.startOffset);
			assertEquals(1152, problem.endOffset);
		}
		else {
			assertEquals(1056, problem.startOffset);
			assertEquals(1128, problem.endOffset);
		}

		problem = results.get(5);

		assertEquals(156, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(5378, problem.startOffset);
			assertEquals(5504, problem.endOffset);
		}
		else {
			assertEquals(5223, problem.startOffset);
			assertEquals(5348, problem.endOffset);
		}
	}

}
