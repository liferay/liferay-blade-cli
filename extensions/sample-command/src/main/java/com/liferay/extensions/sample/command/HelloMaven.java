/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.sample.command;

import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.command.BladeProfile;

/**
 * @author Liferay
 */
@BladeProfile("maven")
public class HelloMaven extends BaseCommand<HelloArgs> {

	@Override
	public void execute() throws Exception {
		HelloArgs helloArgs = getArgs();

		if (!helloArgs.isQuiet()) {
			getBladeCLI().out("Hello from maven " + helloArgs.getName());
		}
	}

	@Override
	public Class<HelloArgs> getArgsClass() {
		return HelloArgs.class;
	}

}