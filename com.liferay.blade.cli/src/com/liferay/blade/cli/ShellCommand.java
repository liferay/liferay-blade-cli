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

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

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
		_port = options.port() != 0 ? options.port() : Agent.DEFAULT_PORT;
	}

	public void execute() throws Exception {
		if (!Util.canConnect("localhost", _port)) {
			addError(
				"sh",
				"Unable to connect to remote agent on port " + _port + ". " +
					"To install the agent bundle run the command \"blade " +
						"agent install\".");
			return;
		}

		String gogoCommand = StringUtils.join(_options._arguments(), " ");

		executeCommand(gogoCommand);
	}

	@Arguments(arg = {"gogo-command", "args..."})
	@Description(DESCRIPTION)
	public interface ShellOptions extends Options {

		@Description("The port to use to connect to remote agent")
		public int port();

	}

	public class ShellSupervisor
		extends AgentSupervisor<Supervisor, Agent>implements Supervisor {

		public ShellSupervisor(blade blade) {
			_blade = blade;
		}

		public void connect(String host, int port) throws Exception {
			super.connect(Agent.class, this, host, port);
		}

		@Override
		public void event(Event e) throws Exception {
		}

		@Override
		public boolean stderr(String out) throws Exception {
			_blade.err().print(out);
			return true;
		}

		@Override
		public boolean stdout(String out) throws Exception {
			_blade.out().print(out.replaceAll(".*>.*$", ""));
			return true;
		}

		private final blade _blade;

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void executeCommand(String cmd) throws Exception {
		ShellSupervisor supervisor = new ShellSupervisor(_blade);

		supervisor.connect("localhost", _port);

		if (!supervisor.getAgent().redirect(-1)) {
			addError("sh", "Unable to redirect input to agent.");
			return;
		}

		supervisor.getAgent().stdin(cmd);
		supervisor.close();
	}

	private final blade _blade;
	private final ShellOptions _options;
	private final int _port;

}