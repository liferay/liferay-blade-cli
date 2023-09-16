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
@Parameters(
	commandDescription = "Initializes the Liferay server configured in this workspace project.",
	commandNames = "server init"
)
public class ServerInitArgs extends BaseArgs {

	@Override
	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public String getEnvironment() {
		return _environment;
	}

	@Parameter(
		description = "Set the environment with the settings appropriate for current development.",
		names = {"-e", "--environment"}
	)
	private String _environment;

}