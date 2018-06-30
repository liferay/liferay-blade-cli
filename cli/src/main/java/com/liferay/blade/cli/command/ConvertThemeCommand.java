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
 */
public class ConvertThemeCommand {

	public ConvertThemeCommand(BladeCLI blade, ConvertArgs args) throws Exception {
		_blade = blade;
		_args = args;

		File projectDir = BladeUtil.getWorkspaceDir(_blade);

		Properties gradleProperties = BladeUtil.getGradleProperties(projectDir);

		String pluginsSDKDirPath = null;

		if (gradleProperties != null) {
			pluginsSDKDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);
		}

		if (pluginsSDKDirPath == null) {
			pluginsSDKDirPath = WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR;
		}

		_pluginsSDKThemesDir = new File(projectDir, pluginsSDKDirPath + "/themes");

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
		final List<String> args = _args.getName();

		final String themeName = !args.isEmpty() ? args.get(0) : null;

		if (!BladeUtil.isWorkspace(_blade)) {
			_blade.error("Please execute this in a Liferay Workspace Project");

			return;
		}

		if (themeName == null) {
			List<String> themes = new ArrayList<>();

			for (File file : _pluginsSDKThemesDir.listFiles()) {
				if (file.isDirectory()) {
					if (_args.isAll()) {
						importTheme(file.getCanonicalPath());
					}
					else {
						themes.add(file.getName());
					}
				}
			}

			if (!_args.isAll()) {
				if (!themes.isEmpty()) {
					String exampleTheme = themes.get(0);

					_blade.out(
						"Please provide the theme project name to migrate, e.g. \"blade migrateTheme " + exampleTheme +
							"\"\n");

					_blade.out("Currently available themes:");
					_blade.out(WordUtils.wrap(StringUtils.join(themes, ", "), 80));
				}
				else {
					_blade.out("Good news! All your themes have already been migrated to " + _themesDir);
				}
			}
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
		Process process = BladeUtil.startProcess(
			"yo liferay-theme:import -p \"" + themePath + "\" -c " + _compassSupport(themePath) +
				" --skip-install",
			_themesDir, _blade.out(), _blade.err());

		int errCode = process.waitFor();

		if (errCode == 0) {
			_blade.out("Theme " + themePath + " migrated successfully");

			File theme = new File(themePath);

			FileUtil.deleteDir(theme.toPath());
		}
		else {
			_blade.error("blade exited with code: " + errCode);
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

	private ConvertArgs _args;
	private BladeCLI _blade;
	private File _pluginsSDKThemesDir;
	private File _themesDir;

}