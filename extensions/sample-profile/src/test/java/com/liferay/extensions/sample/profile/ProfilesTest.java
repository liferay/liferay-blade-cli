/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.sample.profile;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ProfilesTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testProfileExtension() throws Exception {
		File workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");

		String[] args = {
			"--base", workspaceDir.getPath(), "init", "-b", "foo", "-v", BladeTest.PRODUCT_VERSION_PORTAL_74
		};

		TestUtil.runBlade(_rootDir, _extensionsDir, args);

		args = new String[] {"--base", workspaceDir.getPath(), "foo", "bar"};

		BladeTestResults results = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = results.getOutput();

		Assert.assertTrue(output, output.contains("NewCommand"));

		args = new String[] {"--base", workspaceDir.getPath(), "deploy", "--watch"};

		results = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		output = results.getOutput();

		Assert.assertTrue(output, output.contains("OverriddenCommand says true"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _rootDir = null;

}