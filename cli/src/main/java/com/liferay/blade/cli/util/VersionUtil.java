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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gregory Amerson
 */
public class VersionUtil {

	public static boolean isDXPVersion(String targetPlatformVersion) {
		Matcher matcher = _dxpVersionPattern.matcher(targetPlatformVersion);

		return matcher.matches();
	}

	private static final Pattern _dxpVersionPattern = Pattern.compile(
		"^[0-9]\\.[0-9]\\.\\d+(\\.((e|f)p)?[0-9]+(-[0-9]+)?)?$");

}