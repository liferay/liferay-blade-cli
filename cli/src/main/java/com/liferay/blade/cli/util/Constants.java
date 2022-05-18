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