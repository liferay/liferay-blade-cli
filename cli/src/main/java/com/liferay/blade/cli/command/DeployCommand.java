/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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