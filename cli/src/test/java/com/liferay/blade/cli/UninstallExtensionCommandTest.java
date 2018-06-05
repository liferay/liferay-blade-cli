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

import com.liferay.blade.cli.command.UninstallExtension;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.nio.file.Path;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest({BladeUtil.class, UninstallExtension.class})
@RunWith(PowerMockRunner.class)
public class UninstallExtensionCommandTest {

	@Test
	public void testUninstallTemplate() throws Exception {
		String jarName = "custom.blade.extension.jar";

		String[] args = {"extension", "uninstall", jarName};

		File extensionsDir = new File(tempFolder.getRoot(), "extensions");

		extensionsDir.mkdirs();

		File testJar = new File(extensionsDir, jarName);

		Assert.assertTrue(testJar.createNewFile());

		PowerMock.mockStaticPartialNice(BladeUtil.class, "getExtensionsDirectory");

		IExpectationSetters<Path> extensionsDirMethod = EasyMock.expect(Extensions.getDirectory());

		Path extensionsDirPath = extensionsDir.toPath();

		extensionsDirMethod.andReturn(extensionsDirPath).atLeastOnce();

		PowerMock.replay(BladeUtil.class);

		String output = TestUtil.runBlade(args);

		PowerMock.verifyAll();

		Assert.assertTrue(output.contains(" successful") && output.contains(jarName));

		Assert.assertTrue(!testJar.exists());
	}

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

}