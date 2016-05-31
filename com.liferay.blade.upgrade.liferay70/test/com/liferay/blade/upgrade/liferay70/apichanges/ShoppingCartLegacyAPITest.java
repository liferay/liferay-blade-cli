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
public class ShoppingCartLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CartAction.java");
	ShoppingCartLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new ShoppingCartLegacyAPI();
	}

	@Test
	public void shoppingCartLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(
			testFile, new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(4, results.size());

		SearchResult problem = results.get(0);

		assertEquals(32, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1475, problem.startOffset);
			assertEquals(1540, problem.endOffset);
		}
		else {
			assertEquals(1444, problem.startOffset);
			assertEquals(1509, problem.endOffset);
		}

		problem = results.get(1);

		assertEquals(143, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(4691, problem.startOffset);
			assertEquals(4858, problem.endOffset);
		} else {
			assertEquals(4549, problem.startOffset);
			assertEquals(4714, problem.endOffset);
		}

		problem = results.get(2);

		assertEquals(33, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(1550, problem.startOffset);
			assertEquals(1615, problem.endOffset);
		}
		else {
			assertEquals(1518, problem.startOffset);
			assertEquals(1583, problem.endOffset);
		}

		problem = results.get(3);

		assertEquals(118, problem.startLine);

		if (PlatformUtil.isWindows()) {
			assertEquals(3987, problem.startOffset);
			assertEquals(4031, problem.endOffset);
		}
		else {
			assertEquals(3870, problem.startOffset);
			assertEquals(3914, problem.endOffset);
		}
	}

}
