/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;

/**
 * @author Christopher Bryan Boyd
 */
@Parameters(
	commandDescription = "Run Service Builder on all relevant projects in the workspace.", commandNames = "buildService"
)
public class BuildServiceArgsMaven extends BaseArgs {
}