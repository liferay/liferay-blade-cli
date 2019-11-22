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