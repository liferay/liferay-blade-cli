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

import java.net.URL;

/**
 * @author Gregory Amerson
 */
@Parameters(commandDescription = "Update blade to latest version", commandNames = "update")
public class UpdateArgs extends BaseArgs {

	public URL getUrl() {
		return _url;
	}

	public boolean isCheckOnly() {
		return _checkOnly;
	}

	public boolean isSnapshots() {
		return _snapshots;
	}

	public void setSnapshots(boolean snapshots) {
		_snapshots = snapshots;
	}

	public void setUrl(URL url) {
		_url = url;
	}

	@Parameter(description = "Switch to use the snapshot repository.", names = {"-s", "--snapshots"})
	private boolean _snapshots;
	
	@Parameter(description = "Do not update, only check if an update is necessary.", names = {"-c", "--check"})
	private boolean _checkOnly;

	@Parameter(description = "Override the update URL", hidden = true, names = {"-u", "--url"})
	private URL _url;

}