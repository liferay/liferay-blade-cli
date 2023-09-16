/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.gradle.tooling;

import java.io.File;

import java.util.Map;
import java.util.Set;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public interface ProjectInfo {

	public String getDeployDir();

	public String getDockerContainerId();

	public String getDockerImageId();

	public String getDockerImageLiferay();

	public String getLiferayHome();

	public Set<String> getPluginClassNames();

	public Map<String, Set<File>> getProjectOutputFiles();

	public boolean isLiferayProject();

}