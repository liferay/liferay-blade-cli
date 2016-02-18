package com.liferay.blade.gradle.model;

import java.io.File;

import java.util.Set;

/**
 * @author Gregory Amerson
 */
public interface CustomModel {

	public Set<File> getOutputFiles();

	public Set<String> getPluginClassNames();

	public boolean hasPlugin(String className);

}