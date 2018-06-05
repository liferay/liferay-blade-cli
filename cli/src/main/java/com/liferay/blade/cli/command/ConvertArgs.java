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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Converts a plugins-sdk plugin project to a gradle WAR project in Liferay workspace",
	commandNames = "convert"
)
public class ConvertArgs extends BaseArgs {

	public ConvertArgs() {
	}

	public ConvertArgs(boolean all, boolean list, boolean themeBuilder, List<String> name) {
		_all = all;
		_list = list;
		_themeBuilder = themeBuilder;
		_name = name;
	}

	public List<String> getName() {
		return _name;
	}

	public boolean isAll() {
		return _all;
	}

	public boolean isList() {
		return _list;
	}

	public boolean isThemeBuilder() {
		return _themeBuilder;
	}

	@Parameter(description = "Migrate all plugin projects", names = {"-a", "--all"})
	private boolean _all;

	@Parameter(description = "List the projects available to be converted", names = {"-l", "--list"})
	private boolean _list;

	@Parameter(description = "name")
	private List<String> _name = new ArrayList<>();

	@Parameter(
		description = "Use ThemeBuilder gradle plugin instead of NodeJS to convert theme project",
		names = {"-t", "--theme-builder"}
	)
	private boolean _themeBuilder;

}