/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Options;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author David Truong
 */
public class MigrateThemeCommand {

	public MigrateThemeCommand(blade blade, MigrateThemeOptions options)
		throws Exception {

		_blade = blade;
		_options = options;

		File projectDir = Util.getWorkspaceDir(_blade);

		Properties gradleProperties = Util.getGradleProperties(projectDir);

		String pluginsSDKDirPath = null;

		if (gradleProperties != null) {
			pluginsSDKDirPath = gradleProperties.getProperty(
				"liferay.workspace.plugins.sdk.dir");
		}

		if (pluginsSDKDirPath == null) {
			pluginsSDKDirPath = Workspace.DEFAULT_PLUGINS_SDK_DIR;
		}

		_pluginsSDKThemesDir = new File(
			projectDir, pluginsSDKDirPath + "/themes");

		String themesDirPath = null;

		if (gradleProperties != null) {
			themesDirPath = gradleProperties.getProperty(
				"liferay.workspace.themes.dir");
		}

		if (themesDirPath == null) {
			themesDirPath = Workspace.DEFAULT_THEMES_DIR;
		}

		_themesDir = new File(projectDir, themesDirPath);
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String themeName = args.size() > 0 ? args.get(0) : null;

		if (!Util.isWorkspace(_blade)) {
			_blade.error("Please execute this in a Liferay Workspace Project");

			return;
		}

		if (themeName == null) {
			List<String> themes = new ArrayList<>();

			for (File file : _pluginsSDKThemesDir.listFiles()) {
				if (file.isDirectory()) {
					themes.add(file.getName());
				}
			}

			_blade.out().println(
				"Please provide the theme project name to migrate, " +
					"e.g. \"blade migrateTheme my-theme\"\n");
			_blade.out().println("Currently available themes:");
			_blade.out().println(
				WordUtils.wrap(StringUtils.join(themes, ", "), 80));
		}
		else {
			File themeDir = new File(_pluginsSDKThemesDir, themeName);

			if (themeDir.exists()) {
				importTheme(themeDir.getCanonicalPath());
			}
			else {
				_blade.error("Theme does not exist");
			}
		}
	}

	public void importTheme(String themePath) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.directory(_themesDir);

		List<String> commands = new ArrayList<>();

		commands.add("yo");
		commands.add("liferay-theme:import");
		commands.add("-p");
		commands.add(themePath);
		commands.add("-c");
		commands.add("false");
		commands.add("--skip-install");

		processBuilder.command(commands);

		processBuilder.inheritIO();

		Process process = processBuilder.start();

		Thread.sleep(5000);

		process.destroy();

		int errCode = process.exitValue();

		if (errCode == 143 || errCode == 0) {
			_blade.out().println(
				"Theme " + themePath + " migrated successfully");
		}
		else {
			_blade.error("update: jpm exited with code: " + errCode);
		}
	}

	@Arguments(arg = "[name]")
	public interface MigrateThemeOptions extends Options {
	}

	private blade _blade;
	private MigrateThemeOptions _options;
	private File _pluginsSDKThemesDir;
	private File _themesDir;

}