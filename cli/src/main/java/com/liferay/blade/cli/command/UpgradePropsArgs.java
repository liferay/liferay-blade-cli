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
@Parameters(
	commandDescription = "Analyzes current portal properties for properties that have been moved to OSGi configurations or removed in the target version from 6.x to 7.x versions",
	commandNames = "upgradeProps"
)
public class UpgradePropsArgs extends BaseArgs {

	public File getBundleDir() {
		return _bundleDir;
	}

	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public File getOutputFile() {
		return _outputFile;
	}

	public File getPropertiesFile() {
		return _propertiesFile;
	}

	@Parameter(description = "Liferay server bundle directory.", names = {"-d", "--bundle-dir"})
	private File _bundleDir;

	@Parameter(
		description = "If specified, write out report to this file, otherwise uses stdout.",
		names = {"-o", "--output-file"}
	)
	private File _outputFile;

	@Parameter(
		description = "Specify existing Liferay 6.x portal-ext.properties file.", names = {"-p", "--properties-file"}
	)
	private File _propertiesFile;

}