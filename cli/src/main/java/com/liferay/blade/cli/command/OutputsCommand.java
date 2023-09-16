/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.gradle.tooling.ProjectInfo;

import java.io.File;

import java.nio.file.Path;

import java.util.Map;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
public class OutputsCommand extends BaseCommand<OutputsArgs> {

	public OutputsCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File base = args.getBase();

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(base.toPath());

		Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

		for (Map.Entry<String, Set<File>> entry : projectOutputFiles.entrySet()) {
			String projectPath = entry.getKey();

			bladeCLI.out(projectPath);

			Set<File> outputFiles = entry.getValue();

			for (File output : outputFiles) {
				Path outputPath = output.toPath();

				bladeCLI.out("\t" + outputPath);
			}

			bladeCLI.out("\n");
		}
	}

	@Override
	public Class<OutputsArgs> getArgsClass() {
		return OutputsArgs.class;
	}

}