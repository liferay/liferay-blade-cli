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
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class ConvertThemeCommand {

	public ConvertThemeCommand(BladeCLI bladeCLI, ConvertArgs convertArgs) throws Exception {
		_bladeCLI = bladeCLI;
		_convertArgs = convertArgs;

		File baseDir = new File(_convertArgs.getBase());

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
		final List<String> args = _convertArgs.getName();

		final String themeName = !args.isEmpty() ? args.get(0) : null;

		File baseDir = new File(_convertArgs.getBase());

		WorkspaceProvider workspaceProvider = _bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider == null) {
			_bladeCLI.error("Please execute this in a Liferay Workspace project.");

			return;
		}

		if (themeName == null) {
			List<String> themes = new ArrayList<>();

			for (File file : _pluginsSDKThemesDir.listFiles()) {
				if (file.isDirectory()) {
					if (_convertArgs.isAll()) {
						importTheme(file.getCanonicalPath());
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
					_bladeCLI.out(WordUtils.wrap(StringUtils.join(themes, ", "), 80));
				}
				else {
					_bladeCLI.out("Good news! All your themes have already been migrated to " + _themesDir);
				}
			}
		}
		else {
			File themeDir = new File(_pluginsSDKThemesDir, themeName);

			if (themeDir.exists()) {
				importTheme(themeDir.getCanonicalPath());
			}
			else {
				_bladeCLI.error("Theme does not exist");
			}
		}
	}

	public void importTheme(String themePath) throws Exception {
		Process process = BladeUtil.startProcess("node -v", _themesDir);

		int nodeJSInstalledChecker = process.waitFor();

		if (nodeJSInstalledChecker != 0) {
			_bladeCLI.error("Please check Node.js is installed or not.");

			return;
		}

		process = BladeUtil.startProcess(
			"yo liferay-theme:import -p \"" + themePath + "\" -c " + _compassSupport(themePath) +
				" --skip-install",
			_themesDir, _bladeCLI.out(), _bladeCLI.error());

		int errCode = process.waitFor();

		if (errCode == 0) {
			_bladeCLI.out("Theme " + themePath + " migrated successfully");

			File theme = new File(themePath);

			FileUtil.deleteDir(theme.toPath());
		}
		else {
			_bladeCLI.error("blade exited with code: " + errCode);
		}
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

	private static final Pattern _compassImport = Pattern.compile("@import\\s*['\"]compass['\"];");

	private BladeCLI _bladeCLI;
	private ConvertArgs _convertArgs;
	private File _pluginsSDKThemesDir;
	private File _themesDir;

}