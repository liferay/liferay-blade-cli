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

import com.liferay.blade.api.Problem;
import com.liferay.blade.eclipse.provider.PlatformUtil;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class PortalPropertiesTest {
	final File file = new File(
			"projects/knowledge-base-portlet-6.2.x/docroot/WEB-INF/src/portal.properties");
	PortalProperties component;

	@Before
	public void beforeTest() {
		assertTrue(file.exists());
		component = new PortalProperties();
		component.addPropertiesToSearch(component._properties);
	}

	@Test
	public void portalPropertiesAnalyzeTest() throws Exception {
		List<Problem> problems = component.analyze(file);

		assertNotNull(problems);
		assertEquals(1, problems.size());

		Problem problem = problems.get(0);
		if (PlatformUtil.isWindows()) {
			assertEquals(19, problem.lineNumber);
		}
		else {
			assertEquals(10, problem.lineNumber);
		}
	}

	@Test
	public void portalPropertiesAnalyzeTest2() throws Exception {
		List<Problem> problems = component.analyze(file);
		problems = component.analyze(file);

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

}
