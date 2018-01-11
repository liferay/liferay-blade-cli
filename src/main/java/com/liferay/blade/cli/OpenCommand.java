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
		File fileName = _options.getFile();

		if (!fileName.exists()) {
			addError(
				"open",
				"Unable to find specified file " + fileName.getAbsolutePath());
			return;
		}

		IDEConnector connector = new IDEConnector();

		if (fileName.isDirectory()) {
			Object retval = connector.openDir(fileName);

			if (retval != null) {
				addError("open", retval.toString());
				return;
			}
		}
	}

	@Parameters(commandNames = {"open"},
		commandDescription = OpenCommand.DESCRIPTION)
	public static class OpenOptions {

		public String getWorkspace() {
			return workspace;
		}

		public File getFile() {
			return file;
		}

		@Parameter(
			names = {"-w", "--workspace"},
			description ="The workspace to open or import this file or project")
		private String workspace;
		
		@Parameter(description ="<file or directory to open/import>")
		private File file;
	}


	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private final blade _blade;
	private final OpenOptions _options;

}