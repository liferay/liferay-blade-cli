/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Vernon Singleton
 */
public class BladeCliTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testBladePrintUpdateIfAvailable() throws Exception {
		BladeTestResults results = TestUtil.runBlade(_rootDir, _extensionsDir, "update", "--check");

		String output = results.getOutput();

		output = output.trim();

		boolean updateAvailable = output.contains("A new update is available for this version of blade");

		Assert.assertFalse("Current jar should be the latest version", updateAvailable);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _rootDir = null;

}