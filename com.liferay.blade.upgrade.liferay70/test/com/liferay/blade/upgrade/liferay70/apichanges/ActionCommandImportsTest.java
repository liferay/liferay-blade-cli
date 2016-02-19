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

@SuppressWarnings("restriction")
public class ActionCommandImportsTest {

	final File sayHelloActionCommandFile = new File(
			"projects/actioncommand-demo-portlet/docroot/WEB-INF/src/com/liferay/demo/portlet/action/SayHelloActionCommand.java");
	final File sayHelloActionCommandFile2 = new File(
			"projects/actioncommand-demo-portlet/docroot/WEB-INF/src/com/liferay/demo/portlet/action/SayHelloActionCommand2.java");
	MVCPortletActionCommandImports component;

	@Before
	public void beforeTest() {
		assertTrue(sayHelloActionCommandFile.exists());
		component = new MVCPortletActionCommandImports();
	}

	@Test
	public void sayHelloActionCommandFile() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void sayHelloActionCommandFile2() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile2,
				new JavaFileJDT(sayHelloActionCommandFile2));

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void sayHelloActionCommandFile2x() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}
}
