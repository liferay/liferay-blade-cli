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