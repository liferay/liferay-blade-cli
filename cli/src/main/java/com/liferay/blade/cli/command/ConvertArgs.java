/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

		_liferayProduct = product;
	}

	public ConvertArgs(boolean all, boolean list, boolean themeBuilder, File source, List<String> name) {
		this(all, list, themeBuilder, false, source, name, "portal");
	}

	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public String getLiferayProduct() {
		return _liferayProduct;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public List<String> getName() {
		return _name;
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

	public void setLiferayProduct(String liferayProduct) {
		_liferayProduct = liferayProduct;
	}

	public void setLiferayVersion(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	@Parameter(description = "Migrate all plugin projects", names = {"-a", "--all"})
	private boolean _all;

	@Parameter(description = "The option for Liferay Platform product. (portal)|(dxp)", names = "--liferay-product")
	private String _liferayProduct = "portal";

	@Parameter(
		description = "The version of Liferay to target when converting the project. Available options are 7.0, 7.1, 7.2, 7.3, 7.4.",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion;

	@Parameter(description = "List the projects available to be converted", names = {"-l", "--list"})
	private boolean _list;

	@Parameter(description = "[name]")
	private List<String> _name = new ArrayList<>();

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