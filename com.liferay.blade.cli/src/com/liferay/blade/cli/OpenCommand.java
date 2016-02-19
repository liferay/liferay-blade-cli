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

import com.liferay.blade.cli.jmx.IDEConnector;

import java.io.File;

import java.util.Collections;

/**
 * @author Gregory Amerson
 */
public class OpenCommand {

	public static final String DESCRIPTION =
		"Opens or imports a file or project in Liferay IDE.";

	public OpenCommand(blade blade, OpenOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		File fileName = new File(_options._arguments().get(0));

		if (!fileName.exists()) {
			addError(
				"open",
				"Unable to find specified file " + fileName.getAbsolutePath());
			return;
		}

		IDEConnector connector = null;

		try {
			connector = new IDEConnector();
		}
		catch (Exception e) {

			// ignore

		}

		if (connector == null) {
			addError(
				"open", "Unable to connect to Eclipse/Liferay IDE instance.");
			return;
		}

		if (fileName.isDirectory()) {
			Object retval = connector.openDir(fileName);

			if (retval != null) {
				addError("open", retval.toString());
				return;
			}
		}
	}

	@Arguments(arg = "file or directory to open/import")
	@Description(DESCRIPTION)
	public interface OpenOptions extends Options {

		@Description("The workspace to open or import this file or project")
		public String workspace();

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private final blade _blade;
	private final OpenOptions _options;

}