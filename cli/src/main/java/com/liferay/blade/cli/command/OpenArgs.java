/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Opens or imports a file or project in Liferay IDE.", commandNames = "open")
public class OpenArgs extends BaseArgs {

	public File getFile() {
		return _file;
	}

	public String getWorkspace() {
		return _workspace;
	}

	public void setFile(File file) {
		_file = file.getAbsoluteFile();
	}

	@Parameter(description = "[file|directory]")
	private File _file;

	@Parameter(description = "The workspace to open or import this file or project", names = {"-w", "--workspace"})
	private String _workspace;

}