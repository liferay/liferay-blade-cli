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
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public class UpdateCommand extends BaseCommand<UpdateArgs> {

	public UpdateCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		if (BladeUtil.isWindows()) {
			bladeCLI.out(
				"blade update cannot execute successfully because of Windows file locking. Please use following " +
					"command:");
			bladeCLI.out("\tjpm install -f https://releases.liferay.com/tools/blade-cli/latest/blade.jar");
		}
		else {
			BaseArgs baseArgs = bladeCLI.getBladeArgs();

			File baseDir = new File(baseArgs.getBase());

			Process process = BladeUtil.startProcess(
				"jpm install -f https://releases.liferay.com/tools/blade-cli/latest/blade.jar", baseDir);

			int errCode = process.waitFor();

			if (errCode == 0) {
				bladeCLI.out("Update completed successfully");
			}
			else {
				bladeCLI.error("blade exited with code: " + errCode);
			}
		}
	}

	@Override
	public Class<UpdateArgs> getArgsClass() {
		return UpdateArgs.class;
	}

}