/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.gogo.shell.client.GogoShellClient;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gregory Amerson
 */
public class ShellCommand extends BaseCommand<ShellArgs> {

	public ShellCommand() {
	}

	@Override
	public void execute() throws Exception {
		ShellArgs shellArgs = getArgs();

		String host = shellArgs.getHost() != null ? shellArgs.getHost() : "localhost";
		int port = shellArgs.getPort() != 0 ? shellArgs.getPort() : 11311;

		if (!BladeUtil.canConnect(host, port)) {
			_addError("sh", "Unable to connect to gogo shell on " + host + ":" + port);

			return;
		}

		String gogoCommand = StringUtils.join(shellArgs.getArgs(), " ");

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