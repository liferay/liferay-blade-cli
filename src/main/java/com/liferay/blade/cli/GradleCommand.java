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

package com.liferay.blade.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.liferay.blade.cli.gradle.GradleExec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author David Truong
 */
public class GradleCommand {

	public static final String DESCRIPTION =
		"Execute gradle command using the gradle wrapper if detected";

	public GradleCommand(blade blade, GradleOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		String gradleCommand = StringUtils.join(_options.getArgs(), " ");

		GradleExec gradleExec = new GradleExec(_blade);

		gradleExec.executeGradleCommand(gradleCommand);
	}

	@Parameters(commandNames = {"gw"},
		commandDescription = GradleCommand.DESCRIPTION)
	public static class GradleOptions {

		public List<String> getArgs() {
			return args;
		}

		@Parameter(description="[arguments]")
		private List<String> args = new ArrayList<>();
	}


	private blade _blade;
	private GradleOptions _options;

}