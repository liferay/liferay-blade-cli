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
 * @author Gregory Amerson
 */
public class CamelCaseUtil {

	public static String fromCamelCase(String s, char delimiter) {
		StringBuilder sb = new StringBuilder();

		boolean upperCase = false;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((i > 0) && Character.isUpperCase(c)) {
				if (!upperCase || ((i < (s.length() - 1)) && !Character.isUpperCase(s.charAt(i + 1)))) {
					sb.append(delimiter);
				}

				c = Character.toLowerCase(c);

				upperCase = true;
			}
			else {
				upperCase = false;
			}

			sb.append(c);
		}

		return sb.toString();
	}

}