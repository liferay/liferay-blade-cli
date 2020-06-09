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

import com.liferay.blade.cli.command.BaseArgs;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public interface WorkspaceProvider {

	public String getLiferayVersion(File dir);

	public default File getWorkspaceDir(BladeCLI blade) {
		BaseArgs args = blade.getArgs();

		return getWorkspaceDir(args.getBase());
	}

	public File getWorkspaceDir(File dir);

	public boolean isDependencyManagementEnabled(File dir);

	public default boolean isWorkspace(BladeCLI blade) {
		File dirToCheck;

		if (blade == null) {
			dirToCheck = new File(".");

			dirToCheck = dirToCheck.getAbsoluteFile();
		}
		else {
			BaseArgs args = blade.getArgs();

			dirToCheck = args.getBase();
		}

		return isWorkspace(dirToCheck);
	}

	public boolean isWorkspace(File dir);

}