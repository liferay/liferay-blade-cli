/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Christopher Bryan Boyd
 */
@Parameters(commandDescription = "Installs an extension into blade.", commandNames = "extension install")
public class InstallExtensionArgs extends BaseArgs {

	public String getPath() {
		return _path;
	}

	@Parameter(description = "[path]", required = true)
	private String _path;

}