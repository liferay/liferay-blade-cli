/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameters;

/**
 * @author Christopher Bryan Boyd
 */
@Parameters(commandDescription = "List all available project templates.", commandNames = "create -l", hidden = true)
public class ListProjectTemplatesArgs extends BaseArgs {

	public CommandType getCommandType() {
		return CommandType.HIDDEN;
	}

}