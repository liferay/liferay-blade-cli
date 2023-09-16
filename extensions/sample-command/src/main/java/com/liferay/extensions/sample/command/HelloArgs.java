/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.sample.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;

/**
 * @author Liferay
 */
@Parameters(commandDescription = "Executes a hello command", commandNames = "hello")
public class HelloArgs extends BaseArgs {

	public String getName() {
		return _name;
	}

	@Parameter(description = "The name to say hello to", names = "--name", required = true)
	private String _name;

}