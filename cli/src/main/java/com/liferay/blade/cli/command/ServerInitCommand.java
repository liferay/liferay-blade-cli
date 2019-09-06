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

		File baseDir = new File(serverInitArgs.getBase());

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			bladeCLI.out("Executing gradle task initBundle...\n");

			GradleExec gradleExec = new GradleExec(bladeCLI);

			StringBuilder commandStringBuilder = new StringBuilder(":initBundle");

			String liferayWorkspaceEnvironment = serverInitArgs.getEnvironment();

			if ((liferayWorkspaceEnvironment != null) && (liferayWorkspaceEnvironment.length() > 0)) {
				commandStringBuilder.append(" -Pliferay.workspace.environment=" + serverInitArgs.getEnvironment());
			}

			String command = commandStringBuilder.toString();

			ProcessResult processResult = gradleExec.executeTask(command, true);

			if (processResult.getResultCode() == 0) {
				bladeCLI.out("\nserver init completed successfully.");
			}
			else {
				bladeCLI.error(processResult.getError() + "\nerror: server init failed.  See error output above.");
			}
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