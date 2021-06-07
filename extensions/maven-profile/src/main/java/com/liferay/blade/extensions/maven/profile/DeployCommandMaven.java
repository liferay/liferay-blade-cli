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