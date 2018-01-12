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

@Parameters(commandNames = {"samples"}, commandDescription = SamplesCommand.DESCRIPTION)
public class SamplesCommandArgs {

	public File getDir() {
		return dir;
	}

	public String getSampleName() {
		return sampleName;
	}

	@Parameter(names = {"-d", "--dir"}, description ="The directory where to create the new project.")
	private File dir;

	@Parameter(description ="name")
	private String sampleName;

}