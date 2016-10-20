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
import com.liferay.blade.upgrade.liferay70.apichanges.CategoryEntryforUsers;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andy Wu
 */
public class CategoryEntryforUsersTest {

	private final File testFile = new File("projects/legacy-apis-ant-portlet/docroot/WEB-INF/liferay-portlet.xml");

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
	public void tesCategoryEntryforUsersTest() throws Exception {
		List<Problem> problems = null;

		for (ServiceReference<FileMigrator> fm : fileMigrators) {
			final FileMigrator fmigrator = context.getService(fm);

			if (fmigrator instanceof CategoryEntryforUsers) {
				problems = fmigrator.analyze(testFile);
			}

			context.ungetService(fm);
		}

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

}
