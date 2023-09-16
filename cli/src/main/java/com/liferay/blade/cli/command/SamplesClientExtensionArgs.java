/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

/**
 * @author Simon Jiang
 */
@Parameters(
	commandDescription = "Generate a sample client extension project", commandNames = "samples client-extensions"
)
public class SamplesClientExtensionArgs extends BaseArgs {

	public File getDir() {
		return _dir;
	}

	public String getSampleName() {
		return _sampleName;
	}

	public boolean isListAllCientExtensions() {
		return _list;
	}

	@Parameter(description = "The directory where to create the new client extension project.", names = {"-d", "--dir"})
	private File _dir;

	@Parameter(description = "Show all client extension types.", names = {"-l", "--list"})
	private boolean _list;

	@Parameter(description = "[name]")
	private String _sampleName;

}