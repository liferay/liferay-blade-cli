/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.LocalServer;
import com.liferay.blade.cli.command.ServerRunCommand;

/**
 * @author Gregory Amerson
 */
@BladeProfile("maven")
public class ServerRunCommandMaven extends ServerRunCommand {

	@Override
	protected LocalServer newLocalServer(BladeCLI bladeCLI) {
		return new LocalServerMaven(bladeCLI);
	}

}