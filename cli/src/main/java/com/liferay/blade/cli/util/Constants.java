/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

/**
 * @author Terry Jia
 */
public class Constants {

	public static final String DEFAULT_JAVA_SRC = "src/main/java/";

	public static final String DEFAULT_PLUGINS_SDK_PORTLET_SRC = "docroot/WEB-INF/src/";

	public static final String[] DEFAULT_POSSIBLE_PLATFORM_VALUES = {
		"portal-7.4", "dxp-7.4", "portal-7.3", "dxp-7.3", "portal-7.2", "dxp-7.2", "portal-7.1", "dxp-7.1"
	};

	public static final String[] DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES = {
		"Angular", "Plain JavaScript", "React", "Shared bundle", "Vue.js"
	};

	public static final String[] DEFAULT_POSSIBLE_TARGET_VALUES = {
		"Liferay Platform Project", "Liferay Remote App Project"
	};

	public static final String DEFAULT_RESOURCES_SRC = "src/main/resources/";

	public static final String DEFAULT_WEBAPP_SRC = "src/main/webapp/";

}