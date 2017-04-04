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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;

import org.junit.After;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class ConvertServiceBuilderCommandTest {

	public static final String SB_PROJECT_NAME = "sample-service-builder-portlet";

	@After
	public void cleanUp() throws Exception {
		IO.delete(workspaceDir.getParentFile());
	}

	@Test
	public void testConvertServiceBuilder() throws Exception {
		File testdir = IO.getFile("generated/testMigrateServiceBuilder");

		if (testdir.exists()) {
			IO.deleteWithException(testdir);
			assertFalse(testdir.exists());
		}

		Util.unzip(new File("test-projects/plugins-sdk-with-git.zip"), testdir);

		assertTrue(testdir.exists());

		File projectDir = new File(testdir, "plugins-sdk-with-git");

		String[] args = {"-b", projectDir.getPath(), "init", "-u"};

		new bladenofail().run(args);

		args = new String[] {"-b", projectDir.getPath(), "convert", SB_PROJECT_NAME};

		new bladenofail().run(args);

		File sbWar = new File(projectDir, "wars/sample-service-builder-portlet");

		assertTrue(sbWar.exists());

		assertFalse(new File(sbWar, "build.xml").exists());

		assertTrue(new File(sbWar, "build.gradle").exists());

		assertFalse(new File(sbWar, "docroot").exists());

		args = new String[] {"-b", projectDir.getPath(), "convert", SB_PROJECT_NAME};

		new bladenofail().run(args);

		File moduleDir = new File(projectDir, "modules");

		File newSbDir = new File(moduleDir, SB_PROJECT_NAME + "-sb");

		File sbServiceDir = new File(newSbDir, SB_PROJECT_NAME + "-sb-service");
		File sbApiDir = new File(newSbDir, SB_PROJECT_NAME + "-sb-api");

		assertTrue(sbServiceDir.exists());
		assertTrue(sbApiDir.exists());

		assertTrue(new File(sbServiceDir, "service.xml").exists());
		assertTrue(new File(sbServiceDir, "src/main/resources/service.properties").exists());
		assertTrue(new File(sbServiceDir, "src/main/resources/META-INF/portlet-model-hints.xml").exists());
		assertTrue(new File(sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/service/impl/FooLocalServiceImpl.java").exists());
		assertTrue(new File(sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/service/impl/FooServiceImpl.java").exists());
		assertTrue(new File(sbServiceDir, "src/main/java/com/liferay/sampleservicebuilder/model/impl/FooImpl.java").exists());

		File bndBnd = new File(sbApiDir, "bnd.bnd");

		assertTrue(bndBnd.exists());

		String bndContent = new String(IO.read(bndBnd));

		assertTrue(bndContent.indexOf("com.liferay.sampleservicebuilder.util.comparator") > 0);
	}

	private final File workspaceDir = IO.getFile("generated/test/workspace");

}