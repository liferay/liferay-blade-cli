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

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.NodeUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class ConvertThemeCommand implements FilesSupport {

	public ConvertThemeCommand(BladeCLI bladeCLI, ConvertArgs convertArgs) throws Exception {
		_bladeCLI = bladeCLI;

		_convertArgs = convertArgs;

		File baseDir = _convertArgs.getBase();

		GradleWorkspaceProvider workspaceProviderGradle = (GradleWorkspaceProvider)_bladeCLI.getWorkspaceProvider(
			baseDir);

		File projectDir = workspaceProviderGradle.getWorkspaceDir(_bladeCLI);

		Properties gradleProperties = workspaceProviderGradle.getGradleProperties(projectDir);

		File pluginsSdkDir = _convertArgs.getSource();

		if (pluginsSdkDir == null) {
			String pluginsSDKDirPath = null;

			if (gradleProperties != null) {
				pluginsSDKDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);
			}

			if (pluginsSDKDirPath == null) {
				pluginsSDKDirPath = WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR;
			}

			_pluginsSDKThemesDir = new File(projectDir, pluginsSDKDirPath + "/themes");
		}
		else {
			_pluginsSDKThemesDir = new File(pluginsSdkDir, "themes");
		}

		String themesDirPath = null;

		if (gradleProperties != null) {
			themesDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_THEMES_DIR_PROPERTY);
		}

		if (themesDirPath == null) {
			themesDirPath = WorkspaceConstants.DEFAULT_THEMES_DIR;
		}

		_themesDir = new File(projectDir, themesDirPath);
	}

	public void execute() throws Exception {
		File baseDir = _convertArgs.getBase();

		WorkspaceProvider workspaceProvider = _bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider == null) {
			_bladeCLI.error("Please execute this in a Liferay Workspace project.");

			return;
		}

		boolean removeSource = _convertArgs.isRemoveSource();

		final List<String> args = _convertArgs.getName();

		final String themeName = !args.isEmpty() ? args.get(0) : null;

		if (themeName == null) {
			List<String> themes = new ArrayList<>();

			for (File file : _pluginsSDKThemesDir.listFiles()) {
				if (file.isDirectory()) {
					if (_convertArgs.isAll()) {
						_convertedPaths.add(importTheme(file.getCanonicalPath(), removeSource));
					}
					else {
						themes.add(file.getName());
					}
				}
			}

			if (!_convertArgs.isAll()) {
				if (!themes.isEmpty()) {
					String exampleTheme = themes.get(0);

					_bladeCLI.out(
						"Please provide the theme project name to migrate, e.g. \"blade migrateTheme " + exampleTheme +
							"\"\n");

					_bladeCLI.out("Currently available themes:");
					_bladeCLI.out(
						themes.stream(
						).collect(
							Collectors.joining(System.lineSeparator())
						));
				}
				else {
					_bladeCLI.out("Good news! All your themes have already been migrated to " + _themesDir);
				}
			}
		}
		else {
			File themeDir = new File(_pluginsSDKThemesDir, themeName);

			if (themeDir.exists()) {
				_convertedPaths.add(importTheme(themeDir.getCanonicalPath(), removeSource));
			}
			else {
				_bladeCLI.error("Theme does not exist");
			}
		}
	}

	public List<Path> getConvertedPaths() {
		return _convertedPaths;
	}

	public Path importTheme(String themePath, boolean removeSource) throws Exception {
		int errCode = NodeUtil.runYo(
			_LIFERAY_VERSION_70, _themesDir,
			new String[] {
				"liferay-theme:import", "-p", themePath, "-c", String.valueOf(_compassSupport(themePath)),
				"--skip-install"
			},
			_convertArgs.isQuiet());

		if ((errCode == 0) && removeSource) {
			BaseArgs baseArgs = _bladeCLI.getArgs();

			if (!baseArgs.isQuiet()) {
				_bladeCLI.out("Theme " + themePath + " migrated successfully");
			}

			File theme = new File(themePath);

			FileUtil.deleteDir(theme.toPath());
		}
		else if (errCode != 0) {
			_bladeCLI.error("blade exited with code: " + errCode);
		}

		return _themesDir.toPath();
	}

	private static boolean _compassSupport(String themePath) throws Exception {
		File themeDir = new File(themePath);

		File customCss = new File(themeDir, "docroot/_diffs/css/custom.css");

		if (!customCss.exists()) {
			customCss = new File(themeDir, "docroot/_diffs/css/_custom.scss");
		}

		if (!customCss.exists()) {
			return false;
		}

		String css = new String(Files.readAllBytes(customCss.toPath()));

		Matcher matcher = _compassImport.matcher(css);

		return matcher.find();
	}

	private static final String _LIFERAY_VERSION_70 = "7.0";

	private static final Pattern _compassImport = Pattern.compile("@import\\s*['\"]compass['\"];");

	private BladeCLI _bladeCLI;
	private ConvertArgs _convertArgs;
	private final List<Path> _convertedPaths = new ArrayList<>();
	private File _pluginsSDKThemesDir;
	private File _themesDir;

}