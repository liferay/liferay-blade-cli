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

package com.liferay.blade.cli.command;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class VersionCommandTest {

	@Test
	public void testVersionCommandFromJar() throws Exception {
		Runtime runtime = Runtime.getRuntime();

		Process process = runtime.exec(new String[] {"java", "-jar", _BLADE_JAR_PATH});

		process.waitFor();

		InputStream inputStream = process.getInputStream();

		byte[] bytes = new byte[inputStream.available()];

		inputStream.read(bytes, 0, bytes.length);

		String version = new String(bytes);

		Assert.assertFalse(version.isEmpty());
	}

	private static final String _BLADE_JAR_PATH = System.getProperty("bladeJarPath", "build/libs/blade.jar");

}