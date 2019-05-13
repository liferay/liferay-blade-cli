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

package com.liferay.blade.extensions.remote.deploy.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Builds and deploys bundles to the Liferay module framework with gogo shell.",
	commandNames = {"rdeploy", "remote-deploy"}
)
public class RemoteDeployArgs extends BaseArgs {

	public boolean isWatch() {
		return _watch;
	}

	@Parameter(
		description = "Watches the deployed file for changes and will automatically redeploy", names = {"-w", "--watch"}
	)
	private boolean _watch;

}