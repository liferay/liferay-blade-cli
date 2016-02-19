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

package com.liferay.blade.gradle.model;

import java.io.File;
import java.io.Serializable;

import java.util.Set;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class DefaultModel implements Serializable {

	public DefaultModel(Set<String> pluginClassNames, Set<File> outputFiles) {
		_pluginClassNames = pluginClassNames;
		_outputFiles = outputFiles;
	}

	public Set<File> getOutputFiles() {
		return _outputFiles;
	}

	public Set<String> getPluginClassNames() {
		return _pluginClassNames;
	}

	public boolean hasPlugin(String pluginClassName) {
		return _pluginClassNames.contains(pluginClassName);
	}

	private final Set<File> _outputFiles;
	private final Set<String> _pluginClassNames;

}