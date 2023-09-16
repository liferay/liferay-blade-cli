/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

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
 */
public class HelpCommandTest {

	@Before
	public void setUpTestExtensions() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testHelpCommand() throws Exception {
		String content = _runBlade("help");

		Assert.assertTrue(content, content.contains("Usage:"));
	}

	@Test
	public void testHelpCommandSpecific() throws Exception {
		String content = _runBlade("help", "create");

		Assert.assertTrue(content, content.contains("Usage:"));
	}

	@Test
	public void testHelpFlag() throws Exception {
		String content = _runBlade("--help");

		Assert.assertTrue(content, content.contains("Usage:"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _runBlade(String... args) throws Exception {
		BladeTestResults bladeTestResults = TestUtil.runBlade(_rootDir, _extensionsDir, args);

		String output = bladeTestResults.getOutput();

		Assert.assertFalse(output, output.contains("No such command"));

		return output;
	}

	private File _extensionsDir = null;
	private File _rootDir = null;

}