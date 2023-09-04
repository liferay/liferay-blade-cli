/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.LocalServer;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.util.Properties;

/**
 * @author Christopher Bryan Boyd
 */
public class LocalServerMaven extends LocalServer {

	public LocalServerMaven(BladeCLI bladeCLI) {
		super(bladeCLI);
	}

	@Override
	protected File getWorkspaceDir(BladeCLI bladeCLI) {
		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		return MavenUtil.getWorkspaceDir(baseDir);
	}

	@Override
	protected Properties getWorkspaceProperties(BladeCLI bladeCLI) {
		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		return MavenUtil.getMavenProperties(baseDir);
	}

}