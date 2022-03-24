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
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.CreateArgs;
import com.liferay.blade.cli.command.CreateCommand;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

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
		BladeCLI bladeCLI = getBladeCLI();

		CreateArgs createArgs = getArgs();

		File dir = createArgs.getDir();

		if (dir == null) {
			dir = createArgs.getBase();
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(dir);

		if ((workspaceProvider == null) || (workspaceProvider instanceof MavenWorkspaceProvider)) {
			createArgs.setProfileName("maven");

			String template = createArgs.getTemplate();

			if (template.contains("-ext")) {
				bladeCLI.error("Cannot create " + template + " project in Liferay Maven Workpspace.");

				return;
			}

			super.execute();
		}
		else {
			bladeCLI.error("Cannot create maven project here, incompatible workspace profile type.");
		}
	}

	@Override
	protected ProjectTemplatesArgs getProjectTemplateArgs(
			CreateArgs createArgs, BladeCLI bladeCLI, String template, String name, File dir)
		throws IOException {

		ProjectTemplatesArgs projectTemplatesArgs = super.getProjectTemplateArgs(
			createArgs, bladeCLI, template, name, dir);

		projectTemplatesArgs.setGradle(false);
		projectTemplatesArgs.setMaven(true);

		return projectTemplatesArgs;
	}

	@Override
	protected Properties getWorkspaceProperties() {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		return MavenUtil.getMavenProperties(baseDir);
	}

}