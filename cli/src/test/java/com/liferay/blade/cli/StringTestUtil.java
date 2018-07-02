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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public class StringTestUtil {

	public static String merge(Iterable<String> strings, char separator) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;

		for (String s : strings) {
			if (!first) {
				sb.append(separator);
			}

			first = false;

			sb.append(s);
		}

		return sb.toString();
	}

	public static List<String> readLines(InputStream inputStream) throws IOException {
		List<String> lines = new ArrayList<>();

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

}