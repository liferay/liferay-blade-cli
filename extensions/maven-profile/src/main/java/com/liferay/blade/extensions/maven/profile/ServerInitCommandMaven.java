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
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.ServerInitCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

/**
 * @author Christopher Bryan Boyd
 */
@BladeProfile("maven")
public class ServerInitCommandMaven extends ServerInitCommand implements MavenExecutor {

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		if (MavenUtil.isWorkspace(baseDir)) {
			File pomXMLFile = MavenUtil.getpomXMLFile(baseDir);

			if (pomXMLFile.exists()) {
				if (!baseArgs.isQuiet()) {
					bladeCLI.out("Executing maven task bundle-support:init...\n");
				}

				execute(baseDir.getAbsolutePath(), new String[] {"bundle-support:init"});
			}
		}
		else {
			bladeCLI.error("'server init' command is only supported inside a Liferay workspace project.");
		}
	}

}