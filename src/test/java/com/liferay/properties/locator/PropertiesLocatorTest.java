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

package com.liferay.properties.locator;

import java.io.File;

import java.nio.file.Files;

import java.util.SortedSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class PropertiesLocatorTest {

	@BeforeClass
	public static void readSystemProperties() throws Exception {
		Assert.assertNotNull("Expecting liferay.home system property to not be null", _liferayHome);
	}

	@Test
	public void testPropertiesLocatorAPI() throws Exception {
		PropertiesLocatorArgs args = new PropertiesLocatorArgs();

		args.setBundleDir(_liferayHome);
		args.setPropertiesFile(new File("test-resources/6.2-fix-pack-131/portal.properties"));
		args.setQuiet(true);

		PropertiesLocator propertiesLocator = new PropertiesLocator(args);

		SortedSet<PropertyProblem> problems = propertiesLocator.getProblems();

		Assert.assertNotNull(problems);

		Assert.assertEquals(problems.toString(), 627, problems.size());
	}

	@Test
	public void testPropertiesLocatorOutputFile() throws Exception {
		File outputFile = new File(_buildDir, "testProperties.out");

		String[] args = {
			"-p", "test-resources/6.2-fix-pack-131/portal.properties", "-d", _liferayHome.getAbsolutePath(), "-o",
			outputFile.getAbsolutePath()
		};

		PropertiesLocator.main(args);

		String expectedOutput = new String(Files.readAllBytes(new File("test-resources/checkProperties.out").toPath()));
		String testOutput = new String(Files.readAllBytes(outputFile.toPath()));

		Assert.assertEquals(expectedOutput.replaceAll("\\r", ""), testOutput.replaceAll("\\r", ""));
	}

	private static final File _buildDir = new File(System.getProperty("buildDir"));
	private static final File _liferayHome = new File(System.getProperty("liferay.home"));

}