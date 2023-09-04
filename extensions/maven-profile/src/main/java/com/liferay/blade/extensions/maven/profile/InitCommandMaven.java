/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.InitArgs;
import com.liferay.blade.cli.command.InitCommand;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@BladeProfile("maven")
public class InitCommandMaven extends InitCommand {

	public InitCommandMaven() {
	}

	@Override
	public void execute() throws Exception {
		InitArgs initArgs = getArgs();

		initArgs.setProfileName("maven");

		super.execute();
	}

}