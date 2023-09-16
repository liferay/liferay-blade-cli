/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

	@Parameter(description = "Do not write any output.", names = {"-q", "--quiet"})
	private boolean _quiet;

}