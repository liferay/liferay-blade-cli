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
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.StringPrintStream;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.ProcessResult;

import java.io.File;
import java.io.PrintStream;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest(InstallExtensionCommand.class)
public class GradlePrintErrorTest {

	@Test
	public void testGradleError() throws Exception {
		String[] args = {"extension", "install", "https://github.com/gamerson/blade-sample-command"};

		PrintStream outputPrintStream = StringPrintStream.newInstance();

		PrintStream errorPrintStream = StringPrintStream.newInstance();

		BladeTest bladeTest = new BladeTest(outputPrintStream, errorPrintStream, null, temporaryFolder.getRoot());

		PowerMock.expectNew(GradleExec.class, EasyMock.isA(BladeTest.class)).andReturn(new GradleExecSpecial(bladeTest));

		PowerMock.replay(GradleExec.class);

		TestUtil.runBlade(bladeTest, outputPrintStream, errorPrintStream, false, args);

		String error = errorPrintStream.toString();

		boolean buildFailedBoolean = error.contains("BUILD FAILED");

		String output = outputPrintStream.toString();

		boolean foobarBoolean = output.contains("foobar");

		Assert.assertTrue(buildFailedBoolean && foobarBoolean);

		PowerMock.verifyAll();
	}

	@Rule
	public final PowerMockRule rule = new PowerMockRule();

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