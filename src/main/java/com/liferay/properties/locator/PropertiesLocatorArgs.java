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

package com.liferay.properties.locator;

import com.beust.jcommander.Parameter;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public class PropertiesLocatorArgs {

	public PropertiesLocatorArgs() {
	}

	public File getBundleDir() {
		return _bundleDir;
	}

	public File getOutputFile() {
		return _outputFile;
	}

	public File getPropertiesFile() {
		return _propertiesFile;
	}

	public boolean isHelp() {
		return _help;
	}

	public boolean isQuiet() {
		return _quiet;
	}

	public void setBundleDir(File bundleDir) {
		_bundleDir = bundleDir;
	}

	public void setOutputFile(File outputFile) {
		_outputFile = outputFile;
	}

	public void setPropertiesFile(File propertiesFile) {
		_propertiesFile = propertiesFile;
	}

	public void setQuiet(boolean quiet) {
		_quiet = quiet;
	}

	@Parameter(description = "Path to Liferay server bundle directory.", names = {"-d", "--bundleDir"}, required = true)
	private File _bundleDir;

	@Parameter(description = "Print this message.", help = true, names = {"-h", "--help"})
	private boolean _help;

	@Parameter(
		description = "If specified, write out report to this file, otherwise uses stdout.",
		names = {"-o", "--outputFile"}
	)
	private File _outputFile;

	@Parameter(
		description = "Specify existing Liferay 6.x portal-ext.properties file.", names = {"-p", "--propertiesFile"},
		required = true
	)
	private File _propertiesFile;

	@Parameter(description = "Don't write any output.", names = {"-q", "--quiet"})
	private boolean _quiet;

}