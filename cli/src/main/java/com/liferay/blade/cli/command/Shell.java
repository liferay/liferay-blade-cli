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
public class Shell extends BaseCommand<ShellArgs> {

	public Shell() {
	}

	@Override
	public void execute() throws Exception {
		ShellArgs _args = getArgs();

		_host = _args.getHost() != null ? _args.getHost() : "localhost";
		_port = _args.getPort() != 0 ? _args.getPort() : 11311;

		if (!BladeUtil.canConnect(_host, _port)) {
			_addError("sh", "Unable to connect to gogo shell on " + _host + ":" + _port);

			return;
		}

		String gogoCommand = StringUtils.join(_args.getArgs(), " ");

		_executeCommand(gogoCommand);
	}

	@Override
	public Class<ShellArgs> getArgsClass() {
		return ShellArgs.class;
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _executeCommand(String cmd) throws Exception {
		try (final GogoShellClient client = new GogoShellClient(_host, _port)) {
			String response = client.send(cmd);

			getBladeCLI().out(response);
		}
	}

	private String _host;
	private int _port;

}