/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ReleaseUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;
import com.liferay.release.util.ReleaseEntry;

import java.io.File;

import java.util.Properties;

/**
 * @author Christopher Bryan Boyd
 */
public class MavenWorkspaceProvider implements WorkspaceProvider {

	@Override
	public String getLiferayVersion(File dir) {
		Properties mavenProperties = MavenUtil.getMavenProperties(getWorkspaceDir(dir));

		return mavenProperties.getProperty("liferay.bom.version");
	}

	@Override
	public String getProduct(File workspaceDir) {
		String targetPlatformVersion = getLiferayVersion(workspaceDir);

		if (targetPlatformVersion == null) {
			return "portal";
		}

		ReleaseEntry releaseEntry = ReleaseUtil.getReleaseEntry(targetPlatformVersion);

		if (releaseEntry == null) {
			return "portal";
		}

		return releaseEntry.getProduct();
	}

	@Override
	public File getWorkspaceDir(File dir) {
		return MavenUtil.getWorkspaceDir(dir);
	}

	@Override
	public boolean isDependencyManagementEnabled(File dir) {
		Properties mavenProperties = MavenUtil.getMavenProperties(getWorkspaceDir(dir));

		return !BladeUtil.isEmpty(mavenProperties.getProperty("liferay.bom.version"));
	}

	@Override
	public boolean isWorkspace(File dir) {
		if (MavenUtil.isWorkspace(dir)) {
			return true;
		}

		return MavenUtil.isWorkspace(getWorkspaceDir(dir));
	}

}