/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.ServerInitCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

/**
 * @author Christopher Bryan Boyd
 */
@BladeProfile("maven")
public class ServerInitCommandMaven extends ServerInitCommand implements MavenExecutor {

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		if (MavenUtil.isWorkspace(baseDir)) {
			File pomXMLFile = MavenUtil.getpomXMLFile(baseDir);

			if (pomXMLFile.exists()) {
				if (!baseArgs.isQuiet()) {
					bladeCLI.out("Executing maven task bundle-support:init...\n");
				}

				execute(baseDir.getAbsolutePath(), new String[] {"bundle-support:init"});
			}
		}
		else {
			bladeCLI.error("'server init' command is only supported inside a Liferay workspace project.");
		}
	}

}