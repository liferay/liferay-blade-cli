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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class HelpCommand {

	public HelpCommand(BladeCLI blade, HelpCommandArgs args) throws Exception {
		_blade = blade;
		_args = args;
	}

	public void execute() throws Exception {
		String commandName = _args.getName();

		List<Object> argsList = Arrays.asList(
			new CreateCommandArgs(), new ConvertCommandArgs(), new DeployCommandArgs(), new GradleCommandArgs(),
			new HelpCommandArgs(), new InitCommandArgs(), new InstallCommandArgs(), new OpenCommandArgs(),
			new OutputsCommandArgs(), new SamplesCommandArgs(), new ServerStartCommandArgs(),
			new ServerStopCommandArgs(), new ShellCommandArgs(), new UpdateCommandArgs(), new UpgradePropsArgs(),
			new VersionCommandArgs());

		Builder builder = JCommander.newBuilder();

		for (Object o : argsList) {
			builder.addCommand(o);
		}

		JCommander commander = builder.build();

		if (Objects.nonNull(commandName) && (commandName.length() > 0)) {
			commander.parse(commandName, "--help");

			Map<String, JCommander> commands = commander.getCommands();

			JCommander jcommander = commands.get(commandName);

			if (Objects.nonNull(jcommander)) {
				jcommander.usage();
			}
			else {
				commander.usage();
			}
		}

		else
		{
			commander.usage();
		}
	}

	private BladeCLI _blade;
	private HelpCommandArgs _args;

}