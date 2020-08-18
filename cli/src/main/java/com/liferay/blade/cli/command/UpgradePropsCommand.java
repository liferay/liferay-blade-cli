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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.properties.locator.PropertiesLocator;
import com.liferay.properties.locator.PropertiesLocatorArgs;

import java.io.File;

import java.util.Collections;

/**
 * @author Gregory Amerson
 */
public class UpgradePropsCommand extends BaseCommand<UpgradePropsArgs> {

	public UpgradePropsCommand() {
	}

	@Override
	public void execute() throws Exception {
		UpgradePropsArgs upgradePropsArgs = getArgs();

		File bundleDir = upgradePropsArgs.getBundleDir();
		File propertiesFile = upgradePropsArgs.getPropertiesFile();

		if ((bundleDir == null) || (propertiesFile == null)) {
			BladeCLI bladeCLI = getBladeCLI();

			bladeCLI.addErrors(
				"upgradeProps", Collections.singleton("bundleDir and propertiesFile options both required."));

			return;
		}

		PropertiesLocatorArgs args = new PropertiesLocatorArgs();

		args.setBundleDir(upgradePropsArgs.getBundleDir());
		args.setOutputFile(upgradePropsArgs.getOutputFile());
		args.setPropertiesFile(upgradePropsArgs.getPropertiesFile());

		new PropertiesLocator(args);
	}

	@Override
	public Class<UpgradePropsArgs> getArgsClass() {
		return UpgradePropsArgs.class;
	}

}