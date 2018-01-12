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

import java.io.File;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandNames = {"upgradeProps"},
	commandDescription = "Helps to upgrade portal properties from Liferay server 6.x to 7.x versions"
)
public class UpgradePropsOptions {

	public File getBundleDir() {
		return bundleDir;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public File getPropertiesFile() {
		return propertiesFile;
	}

	@Parameter(names = {"-d", "--bundleDir"}, description ="Liferay server bundle directory.")
	private File bundleDir;

	@Parameter(
		names = {"-o", "--outputFile"},
		description ="If specified, write out report to this file, otherwise uses stdout."
	)
	private File outputFile;

	@Parameter(
		names = {"-p", "--propertiesFile"}, description ="Specify existing Liferay 6.x portal-ext.properties file."
	)
	private File propertiesFile;

}