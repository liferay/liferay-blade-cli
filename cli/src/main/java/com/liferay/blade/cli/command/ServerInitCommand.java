/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleExec;

import java.io.File;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ServerInitCommand extends BaseCommand<ServerInitArgs> {

	public ServerInitCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		ServerInitArgs serverInitArgs = getArgs();

		File baseDir = serverInitArgs.getBase();

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			GradleExec gradleExec = new GradleExec(bladeCLI);

			StringBuilder commandStringBuilder = new StringBuilder(":initBundle");

			String liferayWorkspaceEnvironment = serverInitArgs.getEnvironment();

			if ((liferayWorkspaceEnvironment != null) && (liferayWorkspaceEnvironment.length() > 0)) {
				commandStringBuilder.append(" -Pliferay.workspace.environment=" + serverInitArgs.getEnvironment());
			}

			String command = commandStringBuilder.toString();

			gradleExec.executeTask(command, false);
		}
		else {
			bladeCLI.error("'server init' command is only supported inside a Liferay workspace project.");
		}
	}

	@Override
	public Class<ServerInitArgs> getArgsClass() {
		return ServerInitArgs.class;
	}

}