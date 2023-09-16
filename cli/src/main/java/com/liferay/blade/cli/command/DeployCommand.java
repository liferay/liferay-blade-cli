/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.ProcessResult;

import java.io.File;

import java.util.Collections;

/**
 * @author Gregory Amerson
 */
public class DeployCommand extends BaseCommand<DeployArgs> {

	public DeployCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		DeployArgs deployArgs = getArgs();

		File baseDir = deployArgs.getBase();

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			_deploy(gradleExec, "deploy");
		}
		else {
			_deploy(gradleExec, "clean deploy");
		}
	}

	@Override
	public Class<DeployArgs> getArgsClass() {
		return DeployArgs.class;
	}

	private void _addError(String msg) {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.addErrors("deploy", Collections.singleton(msg));
	}

	private void _deploy(GradleExec gradle, String command) throws Exception {
		DeployArgs deployArgs = getArgs();

		File baseDir = deployArgs.getBase();

		ProcessResult processResult = gradle.executeTask(command, baseDir, false);

		int resultCode = processResult.getResultCode();

		if (resultCode > 0) {
			_addError("Gradle \"" + command + "\" task failed.");

			return;
		}

		if (!deployArgs.isQuiet()) {
			String output = "Gradle \"" + command + "\" task succeeded.";

			BladeCLI bladeCLI = getBladeCLI();

			bladeCLI.out(output);
		}
	}

}