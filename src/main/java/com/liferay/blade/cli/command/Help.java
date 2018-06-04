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

import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class Help extends BaseCommand<HelpArgs> {

	public Help() {
	}

	public void execute() throws Exception {
		String commandName = _args.getName();

		if (Objects.nonNull(commandName) && (commandName.length() > 0)) {
			_blade.printUsage(commandName);
		}
		else {
			_blade.printUsage();
		}
	}

	@Override
	public Class<HelpArgs> getArgsClass() {
		return HelpArgs.class;
	}

}