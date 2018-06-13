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

import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.powermock.api.easymock.PowerMock;
import org.powermock.reflect.Whitebox;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class InstallExtensionCommandTest {

	@Before
	public void setUp() throws Exception {
		Whitebox.setInternalState(Extensions.class, "_USER_HOME_DIR", temporaryFolder.getRoot());
	}

	@Test
	public void testInstallCustomExtension() throws Exception {
		String[] args = {"extension install", _sampleCommandJarFile.getAbsolutePath()};

		String output = TestUtil.runBlade(args);

		PowerMock.verifyAll();

		Assert.assertTrue("Expected output to contain \"successful\"\n" + output, output.contains(" successful"));

		Assert.assertTrue(output.contains(_sampleCommandJarFile.getName()));

		File root = temporaryFolder.getRoot();

		File extensionJar = new File(root, ".blade/extensions/" + _sampleCommandJarFile.getName());

		Assert.assertTrue(extensionJar.getAbsolutePath() + " does not exist", extensionJar.exists());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static final File _sampleCommandJarFile = new File(System.getProperty("sampleCommandJarFile"));

}