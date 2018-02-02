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

package com.liferay.blade.cli.gradle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Objects;

import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class LiferayBundleDeployerTest {

	@Test
	public void testWarString() throws IOException {
		Path war = Paths.get("test.war");

		String expectedWar = String.format(_warDeployPattern, war.toUri().toASCIIString(),
		war.getFileName().toString().subSequence(0, war.getFileName().toString().indexOf('.')));
		String result = LiferayBundleDeployerImpl.getWarString(war);
		assertTrue(Objects.equals(expectedWar, result));
	}

	private static final String _warDeployPattern = "webbundle:%s?Web-ContextPath=/%s";

}