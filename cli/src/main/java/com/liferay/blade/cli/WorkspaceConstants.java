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

import java.util.Arrays;
import java.util.List;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class WorkspaceConstants {

	public static final String BUNDLE_ARTIFACT_NAME = "liferay.workspace.bundle.artifact.name";

	public static final String BUNDLE_URL = "liferay.workspace.bundle.url";

	public static final String DEFAULT_BUNDLE_ARTIFACT_NAME = "portal-tomcat-bundle";

	public static final String DEFAULT_EXT_DIR = "ext";

	public static final String DEFAULT_EXT_DIR_PROPERTY = "liferay.workspace.ext.dir";

	public static final String DEFAULT_LIFERAY_HOME_DIR = "bundles";

	public static final String DEFAULT_LIFERAY_HOME_DIR_PROPERTY = "liferay.workspace.home.dir";

	public static final String DEFAULT_MODULES_DIR = "modules";

	public static final String DEFAULT_MODULES_DIR_PROPERTY = "liferay.workspace.modules.dir";

	public static final String DEFAULT_PLUGINS_SDK_DIR = "plugins-sdk";

	public static final String DEFAULT_PLUGINS_SDK_DIR_PROPERTY = "liferay.workspace.plugins.sdk.dir";

	public static final String DEFAULT_THEMES_DIR = "themes";

	public static final String DEFAULT_THEMES_DIR_PROPERTY = "liferay.workspace.themes.dir";

	public static final String DEFAULT_WARS_DIR = "wars";

	public static final String DEFAULT_WARS_DIR_PROPERTY = "liferay.workspace.wars.dir";

	public static final List<String> originalLiferayVersions = Arrays.asList("7.0", "7.1", "7.2", "7.3");

}