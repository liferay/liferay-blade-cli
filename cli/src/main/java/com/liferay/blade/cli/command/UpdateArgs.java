/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.validator.ParameterValidator;
import com.liferay.blade.cli.command.validator.UpdateArgsValidator;

import java.net.URL;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Update blade to latest version", commandNames = "update")
@ParameterValidator(UpdateArgsValidator.class)
public class UpdateArgs extends BaseArgs {

	public URL getUrl() {
		return _url;
	}

	public boolean isCheckOnly() {
		return _checkOnly;
	}

	public boolean isRelease() {
		return _release;
	}

	public boolean isSnapshots() {
		return _snapshots;
	}

	public void setCheckOnly(boolean checkOnly) {
		_checkOnly = checkOnly;
	}

	public void setRelease(boolean release) {
		_release = release;
	}

	public void setSnapshots(boolean snapshots) {
		_snapshots = snapshots;
	}

	public void setUrl(URL url) {
		_url = url;
	}

	@Parameter(
		description = "Do not update, only check if an update is necessary.", hidden = true, names = {"-c", "--check"}
	)
	private boolean _checkOnly;

	@Parameter(description = "Switch to use the release repository.", names = {"-r", "--release"})
	private boolean _release;

	@Parameter(description = "Switch to use the snapshot repository.", names = {"-s", "--snapshots"})
	private boolean _snapshots;

	@Parameter(description = "Override the update URL", hidden = true, names = {"-u", "--url"})
	private URL _url;

}