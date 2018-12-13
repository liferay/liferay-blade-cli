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
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.WorkspaceUtil;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.File;
import java.io.PrintStream;

import java.net.ConnectException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

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

		File baseDir = new File(deployArgs.getBase());

		if (WorkspaceUtil.isWorkspace(baseDir)) {
			_deploy(gradleExec, "deploy");
		}
		else {
			ProjectInfo projectInfo = GradleTooling.loadProjectInfo(baseDir.toPath());

			_deployStandalone(gradleExec, projectInfo);
		}
	}

	@Override
	public Class<DeployArgs> getArgsClass() {
		return DeployArgs.class;
	}

	private void _addError(String msg) {
		getBladeCLI().addErrors("deploy", Collections.singleton(msg));
	}

	private void _deploy(GradleExec gradle, String command) throws Exception {
		DeployArgs deployArgs = getArgs();

		File baseDir = new File(deployArgs.getBase());

		ProcessResult processResult = gradle.executeTask(command, baseDir, false);

		int resultCode = processResult.getResultCode();

		BladeCLI bladeCLI = getBladeCLI();

		if (resultCode > 0) {
			String errorMessage = "Gradle " + command + " task failed.";

			_addError(errorMessage);

			PrintStream err = bladeCLI.error();

			new ConnectException(errorMessage).printStackTrace(err);

			return;
		}
		else {
			String output = "Gradle " + command + " task succeeded.";

			bladeCLI.out(output);
		}
	}

	private void _deployStandalone(GradleExec gradle, ProjectInfo projectInfo) throws Exception {
		DeployArgs deployArgs = getArgs();

		File baseDir = new File(deployArgs.getBase());

		ProcessResult processResult = gradle.executeTask("assemble -x check", baseDir, false);

		int resultCode = processResult.getResultCode();

		BladeCLI bladeCLI = getBladeCLI();

		if (resultCode > 0) {
			String errorMessage = "Gradle deploy task failed.";

			_addError(errorMessage);

			PrintStream err = bladeCLI.error();

			new ConnectException(errorMessage).printStackTrace(err);

			return;
		}
		else {
			String liferayHomeString = projectInfo.getLiferayHome();

			if (Objects.nonNull(liferayHomeString)) {
				File liferayHome = new File(liferayHomeString);

				File deployFolder = new File(liferayHome, "deploy");

				if (deployFolder.exists()) {
					String output = "Gradle deploy task succeeded.";

					bladeCLI.out(output);

					Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

					Collection<Set<File>> values = projectOutputFiles.values();

					Stream<Set<File>> stream = values.stream();

					Path deployFolderPath = deployFolder.toPath();

					stream.flatMap(
						files -> files.stream()
					).filter(
						File::exists
					).forEach(
						outputFile -> {
							try {
								Path outputPath = outputFile.toPath();

								Path outputFileName = outputPath.getFileName();

								Path destinationOutputPath = deployFolderPath.resolve(outputFileName);

								Files.copy(outputPath, destinationOutputPath);
							}
							catch (Exception e) {
								String message = e.getMessage();

								Class<?> exceptionClass = e.getClass();

								if (message == null) {
									message = "DeployCommand._deployStandalone threw " + exceptionClass.getSimpleName();
								}

								_addError(message);

								PrintStream error = bladeCLI.error();

								e.printStackTrace(error);
							}
						}
					);
				}
			}
		}
	}

}