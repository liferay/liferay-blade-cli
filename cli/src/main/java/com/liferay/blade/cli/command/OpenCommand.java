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
import com.liferay.blade.cli.jmx.IDEConnector;

import java.io.File;

import java.util.Collections;

/**
 * @author Gregory Amerson
 */
public class OpenCommand extends BaseCommand<OpenArgs> {

	public OpenCommand() {
	}

	@Override
	public void execute() throws Exception {
		OpenArgs openArgs = getArgs();

		File file = openArgs.getFile();

		if (file == null) {
			file = openArgs.getBase();
		}

		file = file.getAbsoluteFile();

		if (!file.exists()) {
			_addError("open", "Unable to find specified file " + file.getAbsolutePath());

			return;
		}

		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.trace("Trying to open directory : " + file);

		try {
			if (file.isDirectory()) {
				IDEConnector connector = new IDEConnector(trace -> bladeCLI.trace(trace));

				Object retval = connector.openDir(file);

				if (retval != null) {
					_addError("open", retval.toString());
				}
			}
		}
		catch (Exception e) {
			bladeCLI.error("Unable to connect to IDE to open directory.");

			e.printStackTrace(bladeCLI.error());
		}
	}

	@Override
	public Class<OpenArgs> getArgsClass() {
		return OpenArgs.class;
	}

	private void _addError(String prefix, String msg) {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.addErrors(prefix, Collections.singleton(msg));
	}

}