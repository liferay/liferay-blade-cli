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
import com.liferay.blade.cli.command.CreateArgs;
import com.liferay.blade.cli.command.CreateCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.util.Properties;

/**
 * @author Gregory Amerson
 * @author David Truong
 * @author Christopher Bryan Boyd
 * @author Charles Wu
 */
@BladeProfile("maven")
public class CreateCommandMaven extends CreateCommand {

	public CreateCommandMaven() {
	}

	public CreateCommandMaven(BladeCLI bladeCLI) {
		super(bladeCLI);
	}

	@Override
	public void execute() throws Exception {
		CreateArgs createArgs = getArgs();

		createArgs.setBuild("maven");

		super.execute();
	}

	@Override
	protected Properties getWorkspaceProperties() {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = new File(baseArgs.getBase());

		return MavenUtil.getMavenProperties(baseDir);
	}

}