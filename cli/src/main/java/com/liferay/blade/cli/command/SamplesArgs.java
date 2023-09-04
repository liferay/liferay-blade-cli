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
@Parameters(commandDescription = "Generate a sample project", commandNames = "samples")
public class SamplesArgs extends BaseArgs {

	public File getDir() {
		return _dir;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public String getSampleName() {
		return _sampleName;
	}

	@Parameter(description = "The directory where to create the new project.", names = {"-d", "--dir"})
	private File _dir;

	@Parameter(
		description = "The version of Liferay to target when downloading the sample project. Available options are 7.0, 7.1. (default 7.1).",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion;

	@Parameter(description = "[name]")
	private String _sampleName;

}