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

/**
 * @author Gregory Amerson
 */
public class UpdateCommand {

	public UpdateCommand(BladeCLI blade, UpdateCommandArgs args) throws Exception {
		_blade = blade;
	}

	public void execute() throws Exception {
		if (Util.isWindows()) {
			_blade.out(
				"blade update cannot execute successfully because of Windows file locking. Please use following " +
					"command:");
			_blade.out("\tjpm install -f https://releases.liferay.com/tools/blade-cli/latest/blade.jar");
		}
		else {
			Process process = Util.startProcess(
				_blade, "jpm install -f https://releases.liferay.com/tools/blade-cli/latest/blade.jar");

			int errCode = process.waitFor();

			if (errCode == 0) {
				_blade.out("Update completed successfully");
			}
			else {
				_blade.error("blade exited with code: " + errCode);
			}
		}
	}

	private BladeCLI _blade;

}