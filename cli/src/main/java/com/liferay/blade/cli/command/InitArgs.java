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

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Initializes a new Liferay workspace", commandNames = "init")
public class InitArgs extends BaseArgs {

	public String getBuild() {
		return _build;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public String getName() {
		return _name;
	}

	public boolean isForce() {
		return _force;
	}

	public boolean isRefresh() {
		return _refresh;
	}

	public boolean isUpgrade() {
		return _upgrade;
	}

	public void setBuild(String build) {
		_build = build;
	}

	public void setForce(boolean force) {
		_force = force;
	}

	public void setLiferayVersion(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setRefresh(boolean refresh) {
		_refresh = refresh;
	}

	public void setUpgrade(boolean upgrade) {
		_upgrade = upgrade;
	}

	@Parameter(
		description = "Specify the build type of liferay workspace. Available options are gradle, maven. (default gradle)",
		names = {"-b", "--build"}
	)
	private String _build = "gradle";

	@Parameter(description = "create anyway if there are files located at target folder", names = {"-f", "--force"})
	private boolean _force;

	@Parameter(
		description = "The version of Liferay to target when init the workspace. Available options are 7.0, 7.1. (default 7.1).",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion = "7.1";

	@Parameter(description = "[name]")
	private String _name;

	@Parameter(description = "force to refresh workspace template", names = {"-r", "--refresh"})
	private boolean _refresh;

	@Parameter(description = "upgrade plugins-sdk from 6.2 to 7.0", names = {"-u", "--upgrade"})
	private boolean _upgrade;

}