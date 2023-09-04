/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;

import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class HelpCommand extends BaseCommand<HelpArgs> {

	public HelpCommand() {
	}

	@Override
	public void execute() throws Exception {
		String commandName = getArgs().getName();

		BladeCLI bladeCLI = getBladeCLI();

		if (Objects.nonNull(commandName) && (commandName.length() > 0)) {
			bladeCLI.printUsage(commandName);
		}
		else {
			bladeCLI.printUsage();
		}
	}

	@Override
	public Class<HelpArgs> getArgsClass() {
		return HelpArgs.class;
	}

}