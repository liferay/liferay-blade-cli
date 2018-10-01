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
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerInitCommand extends BaseCommand<ServerInitArgs> {

	public ServerInitCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getBladeArgs();

		File baseDir = new File(baseArgs.getBase());

		if (WorkspaceUtil.isWorkspace(baseDir)) {
			if (GradleExec.isGradleProject(baseDir)) {
				GradleExec gradleExec = new GradleExec(bladeCLI);

				ProcessResult processResult = gradleExec.executeTask("initBundle");

				int resultCode = processResult.getResultCode();

				if (resultCode > 0) {
					String errorMessage = "Gradle assemble task failed.";

					bladeCLI.error(errorMessage);
				}

				bladeCLI.out(processResult.getOutput());
				bladeCLI.error(processResult.getError());
			}
		}
		else {
			bladeCLI.error("`server init` is only supported inside a liferay workspace.");
		}
	}

	@Override
	public Class<ServerInitArgs> getArgsClass() {
		return ServerInitArgs.class;
	}

}