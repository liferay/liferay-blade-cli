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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@Parameters(commandNames = {"convert"}, commandDescription = ConvertCommand.DESCRIPTION)
public class ConvertCommandArgs {

	public ConvertCommandArgs() {
	}

	public ConvertCommandArgs(boolean all, boolean list, boolean themeBuilder, List<String> name) {
	this.all = all; this.list = list; this.themeBuilder = themeBuilder; this.name = name;
	}

	public List<String> getName() {
		return name;
	}

	public boolean isAll() {
		return all;
	}

	public boolean isList() {
		return list;
	}

	public boolean isThemeBuilder() {
		return themeBuilder;
	}

	@Parameter(names = {"-a", "--all"}, description ="Migrate all plugin projects")
	private boolean all;

	@Parameter(names = {"-l", "--list"}, description ="List the projects available to be converted")
	private boolean list;

	@Parameter(description ="[name]")
	private List<String> name = new ArrayList<>();

	@Parameter(
		names = {"-t", "--themeBuilder"},
		description ="Use ThemeBuilder gradle plugin instead of NodeJS to convert theme project"
	)
	private boolean themeBuilder;

}