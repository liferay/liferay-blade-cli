/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameters;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Stop server defined by your Liferay project", commandNames = "server stop")
public class ServerStopArgs extends BaseArgs {

	@Override
	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

}