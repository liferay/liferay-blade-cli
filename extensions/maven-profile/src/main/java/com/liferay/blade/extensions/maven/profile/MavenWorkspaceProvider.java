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
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import aQute.bnd.version.Version;

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
		try {
			Properties gradleProperties = getGradleProperties(workspaceDir);

			String productKey = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WORKSPACE_PRODUCT_PROPERTY);

			if (productKey == null) {
				String product = null;

				String targetPlatformVersion = gradleProperties.getProperty(
					WorkspaceConstants.DEFAULT_TARGET_PLATFORM_VERSION_PROPERTY);

				Version version = Version.parseVersion(targetPlatformVersion.replaceAll("-", "."));

				int microVersion = version.getMicro();

				if (microVersion >= 10) {
					product = "dxp";
				}

				if (product == null) {
					String dockerImageProperty = gradleProperties.getProperty(
						WorkspaceConstants.DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY);

					if (dockerImageProperty.contains("dxp")) {
						return "dxp";
					}
				}
			}
			else if (productKey.contains("dxp")) {
				return "dxp";
			}
			else {
				return "portal";
			}
		}
		catch (Exception exception) {
			BladeCLI.instance.error(exception);
		}

		return "portal";
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