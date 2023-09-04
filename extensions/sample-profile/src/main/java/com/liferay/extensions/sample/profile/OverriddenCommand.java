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
public class OverriddenCommand extends BaseCommand<OverriddenArgs> {

	@Override
	public void execute() throws Exception {
		OverriddenArgs args = getArgs();

		if (!args.isQuiet()) {
			getBladeCLI().out("OverriddenCommand says " + args.isWatch());
		}
	}

	@Override
	public Class<OverriddenArgs> getArgsClass() {
		return OverriddenArgs.class;
	}

}