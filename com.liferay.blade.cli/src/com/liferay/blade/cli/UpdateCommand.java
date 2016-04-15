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

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public class UpdateCommand {

	public static final String DESCRIPTION = "Update blade to latest version";

	public UpdateCommand(blade blade, UpdateOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String jarPath = args.size() > 0 ? args.get(0) : _DEFAULT_URL;

		Process process = Util.startProcess(
			_blade, "jpm install -f " + jarPath);

		int errCode = process.waitFor();

		if (errCode == 0) {
			_blade.out().println("Update completed successfully");
		}
		else {
			_blade.error("blade exited with code: " + errCode);
		}
	}

	@Arguments(arg = "[updateUrl]")
	@Description(DESCRIPTION)
	public interface UpdateOptions extends Options {
	}

	private static final String _DEFAULT_URL =
		"https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/" +
			"lastSuccessfulBuild/artifact/com.liferay.blade.cli/generated/" +
				"com.liferay.blade.cli.jar";

	private blade _blade;
	private UpdateOptions _options;

}