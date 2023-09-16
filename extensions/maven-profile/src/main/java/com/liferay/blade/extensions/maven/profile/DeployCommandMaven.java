/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.DeployArgs;
import com.liferay.blade.cli.command.DeployCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.util.Collections;

/**
 * @author Christopher Bryan Boyd
 */
@BladeProfile("maven")
public class DeployCommandMaven extends DeployCommand implements MavenExecutor {

	public DeployCommandMaven() {
	}

	@Override
	public void execute() throws Exception {
		DeployArgs deployArgs = getArgs();

		File baseDir = deployArgs.getBase();

		File pomXMLFile = MavenUtil.getpomXMLFile(baseDir);

		if (pomXMLFile.exists()) {
			execute(baseDir.getAbsolutePath(), new String[] {"clean", "package", "bundle-support:deploy"}, true);
		}
		else {
			_addError("Unable to locate pom.xml file.");
		}
	}

	private void _addError(String msg) {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.addErrors("deploy", Collections.singleton(msg));
	}

}