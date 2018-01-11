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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gregory Amerson
 */
public class ShellCommand {

	public static final String DESCRIPTION = "Connects to Liferay and executes gogo command and returns output.";

	public ShellCommand(blade blade, ShellOptions options) throws Exception {
		_blade = blade;
		_options = options;

		_host = options.getHost() != null ? options.getHost() : "localhost";
		_port = options.getPort() != 0 ? options.getPort() : 11311;
	}

	public void execute() throws Exception {
		if (!Util.canConnect(_host, _port)) {
			addError("sh", "Unable to connect to gogo shell on " + _host + ":" + _port);

			return;
		}

		String gogoCommand = StringUtils.join(_options.getArgs(), " ");

		executeCommand(gogoCommand);
	}

	@Parameters(commandNames = {"sh"},
		commandDescription = ShellCommand.DESCRIPTION)
	public static class ShellOptions {

		public List<String> getArgs() {
			return args;
		}

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		@Parameter
		private List<String> args = new ArrayList<>();

		@Parameter(names = {"-h", "--host"}, description ="The host to use to connect to gogo shell")
		private String host;

		@Parameter(names = {"-p", "--port"}, description ="The port to use to connect to gogo shell")
		private int port;

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void executeCommand(String cmd) throws Exception {
		final GogoTelnetClient telnetClient = new GogoTelnetClient(_host, _port);

		String response = telnetClient.send(cmd);

		_blade.out().println(response);

		telnetClient.close();
	}

	private final blade _blade;
	private final String _host;
	private final ShellOptions _options;
	private final int _port;

}