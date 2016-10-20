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
import com.liferay.blade.upgrade.liferay70.apichanges.DDMLegacyAPI;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class DDMLegacyAPITest {

	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/JournalArticleAssetRendererFactory.java");

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
	public void dDMLegacyAPITest() throws Exception {
		List<Problem> problems = null;

		for (ServiceReference<FileMigrator> fm : fileMigrators) {
			final FileMigrator fmigrator = context.getService(fm);

			if (fmigrator instanceof DDMLegacyAPI) {
				problems = fmigrator.analyze(testFile);
			}

			context.ungetService(fm);
		}

		assertNotNull(problems);
		assertEquals(5, problems.size());

		Problem problem = problems.get(0);

		assertEquals(36, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1704, problem.startOffset);
			assertEquals(1779, problem.endOffset);
		} else {
			assertEquals(1669, problem.startOffset);
			assertEquals(1744, problem.endOffset);
		}

		problem = problems.get(1);

		assertEquals(134, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(4829, problem.startOffset);
			assertEquals(4886, problem.endOffset);
		} else {
			assertEquals(4696, problem.startOffset);
			assertEquals(4753, problem.endOffset);
		}

		problem = problems.get(2);

		assertEquals(147, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(5177, problem.startOffset);
			assertEquals(5234, problem.endOffset);
		} else {
			assertEquals(5031, problem.startOffset);
			assertEquals(5088, problem.endOffset);

		}
		problem = problems.get(3);

		assertEquals(37, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(1789, problem.startOffset);
			assertEquals(1859, problem.endOffset);
		} else {
			assertEquals(1753, problem.startOffset);
			assertEquals(1823, problem.endOffset);
		}

		problem = problems.get(4);

		assertEquals(162, problem.lineNumber);

		if (Util.isWindows()) {
			assertEquals(5573, problem.startOffset);
			assertEquals(5690, problem.endOffset);
		} else {
			assertEquals(5412, problem.startOffset);
			assertEquals(5527, problem.endOffset);
		}
	}

}
