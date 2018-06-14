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

package com.liferay.blade.cli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Properties;

/**
 * @author Christopher Bryan Boyd
 */
public class WorkspaceMetadata {

	public WorkspaceMetadata(File workspaceMetadataFile) {
		_workspaceMetadataFile = workspaceMetadataFile;

		load();
	}

	public String getProfileName() {
		return _properties.getProperty("blade.profile.name");
	}

	public void load() {
		try {
			_properties.load(new FileInputStream(_workspaceMetadataFile));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void save() {
		try {
			_properties.store(new FileOutputStream(_workspaceMetadataFile), null);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setProfileName(String profileName) {
		_properties.setProperty("blade.profile.name", profileName);
	}

	private Properties _properties = new Properties();
	private final File _workspaceMetadataFile;

}