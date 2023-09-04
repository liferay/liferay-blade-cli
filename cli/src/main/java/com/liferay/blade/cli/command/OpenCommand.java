/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
		catch (Exception exception) {
			bladeCLI.error("Unable to connect to IDE to open directory.");

			exception.printStackTrace(bladeCLI.error());
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