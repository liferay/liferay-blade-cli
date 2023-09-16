/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.Prompter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Objects;
import java.util.Optional;
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
	}

	public String getLiferayVersionDefault() {
		if (_properties.getProperty("liferay.version.default") != null) {
			return _properties.getProperty("liferay.version.default");
		}

		return "7.4";
	}

	public String getProfileName() {
		return _properties.getProperty("profile.name");
	}

	public void load() throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(_settingsFile)) {
			_properties.load(fileInputStream);
		}
	}

	public void migrateWorkspaceIfNecessary(BladeCLI bladeCLI) throws IOException {
		migrateWorkspaceIfNecessary(bladeCLI, null);
	}

	public void migrateWorkspaceIfNecessary(BladeCLI bladeCLI, String profileName) throws IOException {
		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(_settingsFile);

		if ((workspaceProvider != null) && workspaceProvider.isWorkspace(bladeCLI)) {
			File workspaceDirectory = workspaceProvider.getWorkspaceDir(_settingsFile);

			File pomFile = new File(workspaceDirectory, "pom.xml");

			boolean shouldPrompt = false;

			if (pomFile.exists()) {
				if (!_settingsFile.exists()) {
					shouldPrompt = true;
				}
				else {
					String profilePromptDisabled = _properties.getProperty("profile.prompt.disabled", "false");

					if (!Objects.equals(profilePromptDisabled, "true") && !Objects.equals(getProfileName(), "maven")) {
						shouldPrompt = true;
					}
				}
			}

			if (shouldPrompt) {
				if (!BladeUtil.isEmpty(profileName)) {
					setProfileName(profileName);
					save();

					return;
				}

				String question =
					"WARNING: blade commands will not function properly in a Maven workspace unless the blade " +
						"profile is set to \"maven\". Should the settings for this workspace be updated?";

				if (Prompter.confirm(question, bladeCLI.in(), bladeCLI.out(), Optional.of(true))) {
					setProfileName("maven");
					save();
				}
				else {
					question = "Should blade remember this setting for this workspace?";

					if (Prompter.confirm(question, bladeCLI.in(), bladeCLI.out(), Optional.of(true))) {
						_properties.setProperty("profile.prompt.disabled", "true");
						save();
					}
				}
			}
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