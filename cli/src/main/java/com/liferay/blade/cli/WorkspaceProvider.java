/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import com.liferay.blade.cli.command.BaseArgs;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public interface WorkspaceProvider {

	public default String getLiferayVersion(File dir) {
		return null;
	}

	public default String getProduct(File dir) {
		return null;
	}

	public default File getWorkspaceDir(BladeCLI blade) {
		BaseArgs args = blade.getArgs();

		return getWorkspaceDir(args.getBase());
	}

	public File getWorkspaceDir(File dir);

	public default boolean isDependencyManagementEnabled(File dir) {
		return false;
	}

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