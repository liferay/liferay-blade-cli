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
	commandDescription = "Connects to Liferay and executes gogo command and returns output.", commandNames = "sh"
)
public class ShellArgs extends BaseArgs {

	public List<String> getArgs() {
		return _args;
	}

	public String getHost() {
		return _host;
	}

	public int getPort() {
		return _port;
	}

	@Parameter
	private List<String> _args = new ArrayList<>();

	@Parameter(description = "The host to use to connect to gogo shell", names = {"-h", "--host"})
	private String _host;

	@Parameter(description = "The port to use to connect to gogo shell", names = {"-p", "--port"})
	private int _port;

}