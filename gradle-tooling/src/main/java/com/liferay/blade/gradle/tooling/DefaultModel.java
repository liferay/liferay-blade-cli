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

package com.liferay.blade.gradle.tooling;

import java.io.File;
import java.io.Serializable;

import java.util.Map;
import java.util.Set;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class DefaultModel implements CustomModel, Serializable {

	public DefaultModel(Set<String> pluginClassNames, Map<String, Set<File>> projectOutputFiles) {
		_pluginClassNames = pluginClassNames;
		_projectOutputFiles = projectOutputFiles;
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
	public boolean hasPlugin(String pluginClassName) {
		return _pluginClassNames.contains(pluginClassName);
	}

	@Override
	public boolean isLiferayModule() {
		if (hasPlugin("aQute.bnd.gradle.BndBuilderPlugin") || hasPlugin("com.liferay.gradle.plugins.LiferayPlugin") ||
			hasPlugin("com.liferay.gradle.plugins.LiferayOSGiPlugin") ||
			hasPlugin("com.liferay.gradle.plugins.gulp.GulpPlugin")) {

			return true;
		}

		return false;
	}

	private final Set<String> _pluginClassNames;
	private final Map<String, Set<File>> _projectOutputFiles;

}