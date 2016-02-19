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

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class MVCPortletInitParamsChangeClassTest {

	@Before
	public void setUp() {
		assertTrue(testFile.exists());
		component = new MVCPortletInitParamsChangeClass();
	}

	@Test
	public void testMVCPortletChangeExtendsTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(1, results.size());
	}

	private MVCPortletInitParamsChangeClass component;
	private final File testFile = new File(
		"projects/knowledge-base-portlet-6.2.x/docroot/"+
		"WEB-INF/src/com/liferay/knowledgebase/portlet/BaseKBPortlet.java");

}
