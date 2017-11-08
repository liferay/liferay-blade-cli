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

import com.liferay.properties.locator.PropertiesLocator;
import com.liferay.properties.locator.PropertiesLocatorArgs;

import java.io.File;
import java.util.Collections;

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.justif.Justif;

/**
 * @author Gregory Amerson
 */
public class UpgradePropsCommand {

	public static final String DESCRIPTION =
		"Helps to upgrade portal properties from Liferay server 6.x to 7.x versions";

	public UpgradePropsCommand(blade blade, UpgradePropsOptions options)
		throws Exception {

		File bundleDir = options.bundleDir();
		File propertiesFile = options.propertiesFile();

		if (bundleDir == null || propertiesFile == null) {
			blade.addErrors("upgradeProps", Collections.singleton("bundleDir and propertiesFile options both required."));
			options._command().help(new Justif().formatter(), blade);

			return;
		}

		PropertiesLocatorArgs args = new PropertiesLocatorArgs();

		args.setBundleDir(options.bundleDir());
		args.setOutputFile(options.outputFile());
		args.setPropertiesFile(options.propertiesFile());

		new PropertiesLocator(args);
	}

	@Description(DESCRIPTION)
	public interface UpgradePropsOptions extends Options {

		@Description("Liferay server bundle directory.")
		public File bundleDir();

		@Description("If specified, write out report to this file, otherwise uses stdout.")
		public File outputFile();

		@Description("Specify existing Liferay 6.x portal-ext.properties file.")
		public File propertiesFile();
	}

}