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

import aQute.lib.io.IO;

import java.io.File;

import java.nio.file.Path;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest({Util.class, UninstallExtensionCommand.class})
@RunWith(PowerMockRunner.class)
public class UninstallExtensionCommandTest {

	@After
	public void cleanUp() throws Exception {
		if (_TEST_DIR.exists()) {
			IO.delete(_TEST_DIR);
			Assert.assertFalse(_TEST_DIR.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		_TEST_DIR.mkdirs();

		File aFile = new File(_TEST_DIR, "afile");

		if (aFile.exists())aFile.delete();
		Assert.assertTrue(aFile.createNewFile() && aFile.delete());
	}

	@Test
	public void testUninstallTemplate() throws Exception {
		String jarName = "custom.blade.extension.jar";

		String[] args = {"extension", "uninstall", jarName};

		File extensionsDir = new File(_TEST_DIR, "extensions");

		extensionsDir.mkdirs();

		File testJar = new File(extensionsDir, jarName);

		Assert.assertTrue(testJar.createNewFile());

		PowerMock.mockStaticPartialNice(Util.class, "getExtensionsDirectory");

		IExpectationSetters<Path> extensionsDirMethod = EasyMock.expect(Util.getExtensionsDirectory());

		Path extensionsDirPath = extensionsDir.toPath();

		extensionsDirMethod.andReturn(extensionsDirPath).atLeastOnce();

		PowerMock.replay(Util.class);

		String output = TestUtil.runBlade(args);

		PowerMock.verifyAll();

		Assert.assertTrue(output.contains(" successful") && output.contains(jarName));

		Assert.assertTrue(!testJar.exists());
	}

	private static File _TEST_DIR = IO.getFile("build/test");

}