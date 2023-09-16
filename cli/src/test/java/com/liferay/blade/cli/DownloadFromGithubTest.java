/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import com.liferay.blade.cli.util.BladeUtil;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Christopher Bryan Boyd
 */
public class DownloadFromGithubTest {

	@Test
	public void testDownloadFromGithub() throws Exception {
		Path masterZipPath = BladeUtil.downloadGithubProject(
			"https://github.com/liferay/liferay-blade-cli", "master.zip");

		Assert.assertTrue(Files.exists(masterZipPath));
	}

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

}