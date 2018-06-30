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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeIOTest;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import org.easymock.EasyMock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest(InstallExtensionCommand.class)
@RunWith(PowerMockRunner.class)
public class GradlePrintErrorTest {

	@Before
	public void setUp() throws Exception {
		Whitebox.setInternalState(BladeCLI.class, "USER_HOME_DIR", temporaryFolder.getRoot());

		BladeTest bladeTest = new BladeTest();

		File cacheDir = bladeTest.getCacheDir();

		if (cacheDir.exists()) {
			FileUtil.deleteDir(cacheDir.toPath());
		}
	}

	@Test
	public void testGradleError() throws Exception {
		String[] args = {"extension", "install", "https://github.com/gamerson/blade-sample-command"};

		BladeIOTest blade = BladeIOTest.getBlade();

		PowerMock.expectNew(GradleExec.class, EasyMock.isA(BladeTest.class)).andReturn(new GradleExecSpecial(blade));

		PowerMock.replay(GradleExec.class);

		Assert.assertTrue(blade.runBlade(args));

		String output = blade.getOutput() + System.lineSeparator() + blade.getError();

		boolean buildFailedBoolean = output.contains("BUILD FAILED");

		boolean foobarBoolean = output.contains("foobar");

		Assert.assertTrue(buildFailedBoolean && foobarBoolean);

		PowerMock.verifyAll();
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static class GradleExecSpecial extends GradleExec {

		public GradleExecSpecial(BladeCLI blade) {
			super(blade);
		}

		@Override
		public ProcessResult executeCommand(String cmd, File dir) throws Exception {
			cmd = cmd.replace("assemble", "foobar");

			return super.executeCommand(cmd, dir);
		}

	}

}