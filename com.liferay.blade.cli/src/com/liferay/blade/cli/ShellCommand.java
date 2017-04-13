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

package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gregory Amerson
 */
public class ShellCommand {

	public static final String DESCRIPTION =
		"Connects to Liferay and executes gogo command and returns output.";

	public ShellCommand(blade blade, ShellOptions options) throws Exception {
		_blade = blade;
		_options = options;
		_host = options.host() != null ? options.host() : "localhost";
		_port = options.port() != 0 ? options.port() : 11311;
	}

	public void execute() throws Exception {
		if (!Util.canConnect(_host , _port)) {
			addError(
				"sh",
				"Unable to connect to gogo shell on " + _host + ":" + _port);

			return;
		}

		String gogoCommand = StringUtils.join(_options._arguments(), " ");

		executeCommand(gogoCommand);
	}

	@Arguments(arg = {"gogo-command", "args..."})
	@Description(DESCRIPTION)
	public interface ShellOptions extends Options {

		@Description("")
		public String host();

		@Description("The port to use to connect to gogo shell")
		public int port();

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void executeCommand(String cmd) throws Exception {
		final GogoTelnetClient telnetClient =
			new GogoTelnetClient(_host, _port);

		String response = telnetClient.send(cmd);

		_blade.out().println(response);

		telnetClient.close();
	}

	private final blade _blade;
	private final String _host;
	private final ShellOptions _options;
	private final int _port;

}