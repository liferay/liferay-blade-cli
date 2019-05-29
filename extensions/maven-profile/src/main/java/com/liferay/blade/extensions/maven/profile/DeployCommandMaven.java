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
import com.liferay.blade.cli.command.DeployArgs;
import com.liferay.blade.cli.command.DeployCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.util.Collections;

/**
 * @author Christopher Bryan Boyd
 */
public class DeployCommandMaven extends DeployCommand implements MavenExecutor {

	public DeployCommandMaven() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		DeployArgs deployArgs = getArgs();

		File baseDir = new File(deployArgs.getBase());

		File pomXmlFile = MavenUtil.getPomXMLFile(baseDir);

		if (pomXmlFile.exists()) {
			bladeCLI.out("Executing Maven task clean...\n");

			execute(baseDir.getAbsolutePath(), new String[] {"clean"});

			bladeCLI.out("Executing Maven task package...\n");

			execute(baseDir.getAbsolutePath(), new String[] {"package"});

			bladeCLI.out("Executing Maven task bundle-support:deploy...\n");

			execute(baseDir.getAbsolutePath(), new String[] {"bundle-support:deploy"});
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