/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.gradle.tooling;

import java.io.File;
import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("serial")
public class DefaultModel implements ProjectInfo, Serializable {

	public DefaultModel(
		Set<String> pluginClassNames, Map<String, Set<File>> projectOutputFiles, String deployDir, String liferayHome,
		String dockerImageLiferay, String dockerImageId, String dockerContainerId) {

		_pluginClassNames = pluginClassNames;
		_projectOutputFiles = projectOutputFiles;
		_deployDir = deployDir;
		_liferayHome = liferayHome;
		_dockerImageLiferay = dockerImageLiferay;
		_dockerImageId = dockerImageId;
		_dockerContainerId = dockerContainerId;
	}

	@Override
	public String getDeployDir() {
		return _deployDir;
	}

	@Override
	public String getDockerContainerId() {
		return _dockerContainerId;
	}

	@Override
	public String getDockerImageId() {
		return _dockerImageId;
	}

	@Override
	public String getDockerImageLiferay() {
		return _dockerImageLiferay;
	}

	@Override
	public String getLiferayHome() {
		return _liferayHome;
	}

	@Override
	public Set<String> getPluginClassNames() {
		return _pluginClassNames;
	}

	@Override
	public Map<String, Set<File>> getProjectOutputFiles() {
		return _projectOutputFiles;
	}

	@Override
	public boolean isLiferayProject() {
		if (_hasPlugin("aQute.bnd.gradle.BndBuilderPlugin") || _hasPlugin("com.liferay.gradle.plugins.LiferayPlugin") ||
			_hasPlugin("com.liferay.gradle.plugins.LiferayOSGiPlugin") ||
			_hasPlugin("com.liferay.gradle.plugins.gulp.GulpPlugin")) {

			return true;
		}

		return false;
	}

	private boolean _hasPlugin(String pluginClassName) {
		return _pluginClassNames.contains(pluginClassName);
	}

	private final String _deployDir;
	private final String _dockerContainerId;
	private final String _dockerImageId;
	private final String _dockerImageLiferay;
	private final String _liferayHome;
	private final Set<String> _pluginClassNames;
	private final Map<String, Set<File>> _projectOutputFiles;

}