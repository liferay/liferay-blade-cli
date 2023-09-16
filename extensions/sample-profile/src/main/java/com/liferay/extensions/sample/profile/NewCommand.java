/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.sample.profile;

import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.command.BladeProfile;

/**
 * @author Liferay
 */
@BladeProfile("foo")
public class NewCommand extends BaseCommand<NewArgs> {

	@Override
	public void execute() throws Exception {
		NewArgs args = getArgs();

		if (!args.isQuiet()) {
			getBladeCLI().out("NewCommand says " + args.getData());
		}
	}

	@Override
	public Class<NewArgs> getArgsClass() {
		return NewArgs.class;
	}

}