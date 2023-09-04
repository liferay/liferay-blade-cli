/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Execute gradle command using the gradle wrapper if detected. Example: blade gw -- --help",
	commandNames = "gw"
)
public class GradleWrapperArgs extends BaseArgs {

	public List<String> getArgs() {
		return _args;
	}

	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	@Parameter(description = "[arguments]")
	private List<String> _args = new ArrayList<>();

}