/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.bad.command;

import com.liferay.blade.cli.command.BaseCommand;

/**
 * @author Liferay
 */
public class BadCommand extends BaseCommand<BadArgs> {

	public BadCommand() {
		throw new NoClassDefFoundError("com/liferay/blade/cli/WorkspaceLocator");
	}

	@Override
	public void execute() throws Exception {
		getBladeCLI().out("bad");
	}

	@Override
	public Class<BadArgs> getArgsClass() {
		return BadArgs.class;
	}

}