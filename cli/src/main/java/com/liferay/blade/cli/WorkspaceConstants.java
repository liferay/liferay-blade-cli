/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

	public static final String DEFAULT_LIFERAY_DOCKER_IMAGE_PROPERTY = "liferay.workspace.docker.image.liferay";

	public static final String DEFAULT_LIFERAY_HOME_DIR = "bundles";

	public static final String DEFAULT_LIFERAY_HOME_DIR_PROPERTY = "liferay.workspace.home.dir";

	public static final String DEFAULT_MODULES_DIR = "modules";

	public static final String DEFAULT_MODULES_DIR_PROPERTY = "liferay.workspace.modules.dir";

	public static final String DEFAULT_PLUGINS_SDK_DIR = "plugins-sdk";

	public static final String DEFAULT_PLUGINS_SDK_DIR_PROPERTY = "liferay.workspace.plugins.sdk.dir";

	public static final String DEFAULT_TARGET_PLATFORM_VERSION_PROPERTY = "liferay.workspace.target.platform.version";

	public static final String DEFAULT_THEMES_DIR = "themes";

	public static final String DEFAULT_THEMES_DIR_PROPERTY = "liferay.workspace.themes.dir";

	public static final String DEFAULT_WARS_DIR = "wars";

	public static final String DEFAULT_WARS_DIR_PROPERTY = "liferay.workspace.wars.dir";

	public static final String DEFAULT_WORKSPACE_PRODUCT_PROPERTY = "liferay.workspace.product";

	public static final List<String> originalLiferayVersions = Arrays.asList("7.0", "7.1", "7.2", "7.3", "7.4");

}