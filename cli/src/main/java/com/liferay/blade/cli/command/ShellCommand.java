/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.gogo.shell.client.GogoShellClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gregory Amerson
 */
public class ShellCommand extends BaseCommand<ShellArgs> {

	public ShellCommand() {
	}

	@Override
	public void execute() throws Exception {
		ShellArgs shellArgs = getArgs();

		String host = (shellArgs.getHost() != null) ? shellArgs.getHost() : "localhost";
		int port = (shellArgs.getPort() != 0) ? shellArgs.getPort() : 11311;

		if (!BladeUtil.canConnect(host, port)) {
			_addError("sh", "Unable to connect to gogo shell on " + host + ":" + port);

			return;
		}

		List<String> args = shellArgs.getArgs();

		String gogoCommand = args.stream(
		).collect(
			Collectors.joining(" ")
		);

		_executeCommand(gogoCommand, host, port);
	}

	@Override
	public Class<ShellArgs> getArgsClass() {
		return ShellArgs.class;
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _executeCommand(String gogoCommand, String host, int port) throws Exception {
		try (final GogoShellClient client = new GogoShellClient(host, port)) {
			String response = client.send(gogoCommand);

			getBladeCLI().out(response);
		}
	}

}