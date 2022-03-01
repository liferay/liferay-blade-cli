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

import com.liferay.blade.cli.command.validator.LiferayDefaultVersionValidator;
import com.liferay.blade.cli.command.validator.LiferayMoreVersionValidator;
import com.liferay.blade.cli.command.validator.ParameterPossibleValues;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Parameters(commandDescription = "Initializes a new Liferay workspace", commandNames = "init")
public class InitArgs extends BaseArgs {

	@Override
	public CommandType getCommandType() {
		return CommandType.NON_WORKSPACE;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public String getName() {
		return _name;
	}

	public String getProduct() {
		return _product;
	}

	public boolean isAll() {
		return _all;
	}

	public boolean isForce() {
		return _force;
	}

	public boolean isList() {
		return _list;
	}

	public boolean isRefresh() {
		return _refresh;
	}

	public boolean isUpgrade() {
		return _upgrade;
	}

	public void setAll(boolean all) {
		_all = all;
	}

	public void setForce(boolean force) {
		_force = force;
	}

	public void setLiferayVersion(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	public void setList(boolean list) {
		_list = list;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setProduct(String product) {
		_product = product;
	}

	public void setRefresh(boolean refresh) {
		_refresh = refresh;
	}

	public void setUpgrade(boolean upgrade) {
		_upgrade = upgrade;
	}

	@Parameter(description = "Only used with (-l|--list) option, list all Liferay products keys", names = "--all")
	private boolean _all;

	@Parameter(
		description = "Initialize a workspace even if there are files located in target location",
		names = {"-f", "--force"}
	)
	private boolean _force;

	@Parameter(
		description = "The Liferay product to target. Typing \"more\" will show all product versions.",
		names = {"-v", "--liferay-version"}, required = true, validateValueWith = LiferayMoreVersionValidator.class
	)
	@ParameterPossibleValues(more = LiferayMoreVersionValidator.class, value = LiferayDefaultVersionValidator.class)
	private String _liferayVersion;

	@Parameter(
		description = "List Liferay products that can be configured during initial workspace creation",
		names = {"-l", "--list"}
	)
	private boolean _list;

	@Parameter(description = "[name]")
	private String _name;

	@Parameter(description = "The option for Liferay Platform product. (portal)|(dxp)", names = "--product")
	private String _product = "portal";

	@Parameter(description = "force to refresh workspace template", names = {"-r", "--refresh"})
	private boolean _refresh;

	@Parameter(description = "upgrade plugins-sdk from 6.2 to 7.0", names = {"-u", "--upgrade"})
	private boolean _upgrade;

}