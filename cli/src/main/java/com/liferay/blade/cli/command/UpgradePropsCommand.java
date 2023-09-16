/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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