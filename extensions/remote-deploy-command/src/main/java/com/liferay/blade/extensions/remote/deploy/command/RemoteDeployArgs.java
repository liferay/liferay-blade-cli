/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.remote.deploy.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Builds and deploys bundles to the Liferay module framework with gogo shell.",
	commandNames = {"rdeploy", "remote-deploy"}
)
public class RemoteDeployArgs extends BaseArgs {

	public boolean isWatch() {
		return _watch;
	}

	@Parameter(
		description = "Watches the deployed file for changes and will automatically redeploy", names = {"-w", "--watch"}
	)
	private boolean _watch;

}