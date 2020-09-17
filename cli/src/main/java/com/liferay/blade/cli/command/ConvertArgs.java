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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Parameters(
	commandDescription = "Converts a plugins-sdk plugin project to a gradle WAR project in Liferay workspace",
	commandNames = "convert"
)
public class ConvertArgs extends BaseArgs {

	public ConvertArgs() {
	}

	public ConvertArgs(
		boolean all, boolean list, boolean themeBuilder, boolean removeSource, File source, List<String> name,
		String product) {

		_all = all;
		_list = list;
		_themeBuilder = themeBuilder;
		_removeSource = removeSource;
		_source = source;
		_name = name;
		_product = product;
	}

	public ConvertArgs(boolean all, boolean list, boolean themeBuilder, File source, List<String> name) {
		this(all, list, themeBuilder, false, source, name, "portal");
	}

	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public List<String> getName() {
		return _name;
	}

	public String getProduct() {
		return _product;
	}

	public File getSource() {
		return _source;
	}

	public boolean isAll() {
		return _all;
	}

	public boolean isList() {
		return _list;
	}

	public boolean isRemoveSource() {
		return _removeSource;
	}

	public boolean isThemeBuilder() {
		return _themeBuilder;
	}

	public void setLiferayVersion(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	public void setProduct(String product) {
		_product = product;
	}

	@Parameter(description = "Migrate all plugin projects", names = {"-a", "--all"})
	private boolean _all;

	@Parameter(
		description = "The version of Liferay to target when converting the project. Available options are 7.0, 7.1, 7.2, 7.3.",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion;

	@Parameter(description = "List the projects available to be converted", names = {"-l", "--list"})
	private boolean _list;

	@Parameter(description = "[name]")
	private List<String> _name = new ArrayList<>();

	@Parameter(description = "The option for Liferay Platform product. (portal)|(dxp)", names = "--product")
	private String _product = "portal";

	@Parameter(description = "Remove source plugin projects, default value is true", names = {"-r", "--remove"})
	private boolean _removeSource = false;

	@Parameter(
		description = "The Plugins SDK directory, otherwise default value is <workspace_dir>/plugins-sdk",
		names = {"-s", "--source"}
	)
	private File _source;

	@Parameter(
		description = "Use ThemeBuilder gradle plugin instead of NodeJS to convert theme project",
		names = {"-t", "--theme-builder"}
	)
	private boolean _themeBuilder;

}