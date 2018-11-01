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

package com.liferay.blade.cli;

import com.liferay.blade.cli.util.Prompter;
import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class BladeSettings {

	public BladeSettings(File settingsFile) throws IOException {
		_settingsFile = settingsFile;

		if (_settingsFile.exists()) {
			load();
		}
		else {
			if (WorkspaceUtil.isWorkspace(settingsFile)) {
				File workspaceDirectory = WorkspaceUtil.getWorkspaceDir(settingsFile);

				File pomFile = new File(workspaceDirectory, "pom.xml");

				if (pomFile.exists()) {
					String canonicalPath = settingsFile.getCanonicalPath();

					String questionString =
						"Blade has detected that " + canonicalPath + " does not exist. Should blade create this file?" +
							System.lineSeparator() +
								"(WARNING: Blade will not function properly in a Maven workspace without this file)" +
									System.lineSeparator();

					if (Prompter.confirm(questionString)) {
						setProfileName("maven");
						save();
					}
				}
			}
		}
	}

	public String getLiferayVersionDefault() {
		if (_properties.getProperty("liferay.version.default") != null) {
			return _properties.getProperty("liferay.version.default");
		}
		else {
			return "7.1";
		}
	}

	public String getProfileName() {
		return _properties.getProperty("profile.name");
	}

	public void load() throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(_settingsFile)) {
			_properties.load(fileInputStream);
		}
	}

	public void save() throws IOException {
		if (!_settingsFile.exists()) {
			File parentDir = _settingsFile.getParentFile();

			parentDir.mkdirs();
		}

		try (FileOutputStream out = new FileOutputStream(_settingsFile)) {
			_properties.store(out, null);
		}
	}

	public void setLiferayVersionDefault(String liferayVersion) {
		_properties.setProperty("liferay.version.default", liferayVersion);
	}

	public void setProfileName(String profileName) {
		_properties.setProperty("profile.name", profileName);
	}

	private final Properties _properties = new Properties();
	private final File _settingsFile;

}