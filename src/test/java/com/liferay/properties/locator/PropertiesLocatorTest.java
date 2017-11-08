/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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