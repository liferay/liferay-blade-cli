/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.gradle.GradleExec;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class GradleWrapperCommand extends BaseCommand<GradleWrapperArgs> {

	public GradleWrapperCommand() {
	}

	@Override
	public void execute() throws Exception {
		GradleWrapperArgs gradleWrapperArgs = getArgs();

		List<String> args = gradleWrapperArgs.getArgs();

		String gradleCommand = args.stream(
		).collect(
			Collectors.joining(" ")
		);

		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		BaseArgs baseArgs = bladeCLI.getArgs();

		gradleExec.executeTask(gradleCommand, baseArgs.getBase(), false);
	}

	@Override
	public Class<GradleWrapperArgs> getArgsClass() {
		return GradleWrapperArgs.class;
	}

}