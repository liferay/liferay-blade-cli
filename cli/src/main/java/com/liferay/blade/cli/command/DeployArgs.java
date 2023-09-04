/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Builds and deploys bundles to the Liferay module framework.", commandNames = "deploy")
public class DeployArgs extends BaseArgs {

	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public boolean isWatch() {
		return _watch;
	}

	@Parameter(
		description = "Watches the deployed file for changes and will automatically redeploy", names = {"-w", "--watch"}
	)
	private boolean _watch;

}