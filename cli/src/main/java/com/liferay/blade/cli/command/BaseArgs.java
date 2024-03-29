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
@Parameters(commandDescription = "Options valid for all commands. Must be given before sub command")
public class BaseArgs {

	public File getBase() {
		return _base;
	}

	public CommandType getCommandType() {
		return CommandType.GLOBAL;
	}

	public String getProfileName() {
		return _profileName;
	}

	public boolean isHelp() {
		return _help;
	}

	public boolean isQuiet() {
		return _quiet;
	}

	public boolean isRefreshReleases() {
		return _refreshReleases;
	}

	public boolean isTrace() {
		return _trace;
	}

	public void setBase(File baseDir) {
		_base = baseDir.getAbsoluteFile();
	}

	public void setProfileName(String profileName) {
		_profileName = profileName;
	}

	public void setQuiet(boolean quiet) {
		_quiet = quiet;
	}

	public void setRefreshReleases(boolean refreshReleases) {
		_refreshReleases = refreshReleases;
	}

	@Parameter(
		description = "Specify a new base directory (default working directory).", hidden = true, names = "--base"
	)
	private File _base = new File(System.getProperty("user.dir"));

	@Parameter(description = "Get help on a specific command.", help = true, names = "--help")
	private boolean _help;

	@Parameter(
		description = "Specify the profile to use when invoking the command.",
		names = {"-b", "--build", "-P", "--profile-name"}
	)
	private String _profileName;

	@Parameter(description = "Do not print any optional messages to console.", hidden = true, names = {"-q", "--quiet"})
	private boolean _quiet;

	@Parameter(description = "Force Blade to check for new releases", names = "--refresh-releases")
	private boolean _refreshReleases;

	@Parameter(description = "Print exception stack traces when they occur.", hidden = true, names = "--trace")
	private boolean _trace;

}