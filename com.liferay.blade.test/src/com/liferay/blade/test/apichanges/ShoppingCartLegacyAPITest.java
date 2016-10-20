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

package com.liferay.blade.test.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Problem;
import com.liferay.blade.test.Util;
import com.liferay.blade.upgrade.liferay70.apichanges.ShoppingCartLegacyAPI;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ShoppingCartLegacyAPITest {
	final File testFile = new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CartAction.java");

	final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	ServiceTracker<FileMigrator, FileMigrator> fileMigratorTracker;

	FileMigrator fileMigrator;

	ServiceReference<FileMigrator>[] fileMigrators;

	@Before
	public void beforeTest() {
		fileMigratorTracker = new ServiceTracker<FileMigrator, FileMigrator>(context, FileMigrator.class, null);

		fileMigratorTracker.open();

		fileMigrators = fileMigratorTracker.getServiceReferences();

		assertNotNull(fileMigrators);

		assertTrue(fileMigrators.length > 0);
	}

	@Test
	public void shoppingCartLegacyAPITest() throws Exception {
		List<Problem> problems = null;

		for (ServiceReference<FileMigrator> fm : fileMigrators) {
			final FileMigrator fmigrator = context.getService(fm);

			if (fmigrator instanceof ShoppingCartLegacyAPI) {
				problems = fmigrator.analyze(testFile);
			}

			context.ungetService(fm);
		}

		assertNotNull(problems);
		assertEquals(4, problems.size());

		Problem problem = problems.get(0);

		assertEquals(32, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1475, problem.startOffset);
			assertEquals(1540, problem.endOffset);
		} else {
			assertEquals(1444, problem.startOffset);
			assertEquals(1509, problem.endOffset);
		}

		problem = problems.get(1);

		assertEquals(143, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(4691, problem.startOffset);
			assertEquals(4858, problem.endOffset);
		} else {
			assertEquals(4549, problem.startOffset);
			assertEquals(4714, problem.endOffset);
		}

		problem = problems.get(2);

		assertEquals(33, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1550, problem.startOffset);
			assertEquals(1615, problem.endOffset);
		} else {
			assertEquals(1518, problem.startOffset);
			assertEquals(1583, problem.endOffset);
		}

		problem = problems.get(3);

		assertEquals(118, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(3987, problem.startOffset);
			assertEquals(4031, problem.endOffset);
		} else {
			assertEquals(3870, problem.startOffset);
			assertEquals(3914, problem.endOffset);
		}
	}

}
