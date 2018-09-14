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

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class GradleWrapperCommand extends BaseCommand<GradleWrapperArgs> {

	public GradleWrapperCommand() {
	}

	@Override
	public void execute() throws Exception {
		String gradleCommand = StringUtils.join(getArgs().getArgs(), " ");

		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		BaseArgs args = bladeCLI.getBladeArgs();

		File baseDir = new File(args.getBase());

		gradleExec.executeTask(gradleCommand, baseDir, false);
	}

	@Override
	public Class<GradleWrapperArgs> getArgsClass() {
		return GradleWrapperArgs.class;
	}

}