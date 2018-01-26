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

import static org.junit.Assert.assertTrue;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Christopher Boyd
 */
@PrepareForTest(
	{GradleTooling.class, GradleExec.class, BladeCLI.class, BladeNoFail.class, Util.class, DeployCommand.class}
)
@RunWith(PowerMockRunner.class)
public class DeployTest {

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testDeployWar() throws Exception {
		File testDir = tempFolder.newFolder();

		File war = new File(testDir, "test.war");

		assertTrue(war.createNewFile());

		PowerMock.mockStaticNice(Util.class);

		IExpectationSetters<Boolean> canConnect = EasyMock.expect(
			Util.canConnect(EasyMock.anyString(), EasyMock.anyInt()));

		canConnect.andStubReturn(true);

		PowerMock.replay(Util.class);

		PowerMock.mockStatic(GradleTooling.class);

		IExpectationSetters<Set<File>> outputFiles = EasyMock.expect(
			GradleTooling.getOutputFiles(EasyMock.isA(File.class), EasyMock.isA(File.class)));

		outputFiles.andStubReturn(new HashSet<>(Arrays.asList(war)));

		PowerMock.replay(GradleTooling.class);

		GradleExec gradle = EasyMock.createNiceMock(GradleExec.class);

		EasyMock.expect(gradle.executeGradleCommand(EasyMock.anyString())).andStubReturn(0);

		EasyMock.replay(gradle);

		PowerMock.mockStatic(GradleExec.class);

		IExpectationSetters<GradleExec> newGradleExec = PowerMock.expectNew(
			GradleExec.class, EasyMock.isA(BladeNoFail.class));

		newGradleExec.andStubReturn(gradle);

		PowerMock.replay(GradleExec.class);

		GogoTelnetClient client = EasyMock.createNiceMock(GogoTelnetClient.class);

		PowerMock.mockStatic(GogoTelnetClient.class);

		IExpectationSetters<GogoTelnetClient> newGogoTelnetClient = PowerMock.expectNew(
			GogoTelnetClient.class, EasyMock.isA(String.class), EasyMock.isA(Integer.class));

		newGogoTelnetClient.andStubReturn(client);

		EasyMock.expect(client.send(EasyMock.anyString()));

		String[] args = { "-b",testDir.getAbsolutePath(), "deploy" };

		BladeNoFail bl= new BladeNoFail();

		bl.run(args);

		PowerMock.verifyAll();
	}

	@Test
	void testDeployJar() throws Exception {

	}
}
