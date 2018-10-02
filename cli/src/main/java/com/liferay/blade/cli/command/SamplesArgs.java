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
@Parameters(commandDescription = "Generate a sample project", commandNames = "samples")
public class SamplesArgs extends BaseArgs {

	public String getBuild() {
		return _build;
	}

	public File getDir() {
		return _dir;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public String getSampleName() {
		return _sampleName;
	}

	@Parameter(
		description = "Specify the build type of the project. Available options are gradle, maven. (gradle is default)",
		names = {"-b", "--build"}
	)
	private String _build = "gradle";

	@Parameter(description = "The directory where to create the new project.", names = {"-d", "--dir"})
	private File _dir;

	@Parameter(
		description = "The version of Liferay to target when downloading the sample project. Available options are 7.0, 7.1. (default 7.1).",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion = "7.1";

	@Parameter(description = "name")
	private String _sampleName;

}