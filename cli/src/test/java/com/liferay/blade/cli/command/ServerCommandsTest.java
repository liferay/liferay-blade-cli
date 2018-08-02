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
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;
import java.util.Objects;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import org.junit.Assert;
import org.junit.Before;
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
@PrepareForTest(
	{
		BladeCLI.class, BladeTest.class, BladeUtil.class, ServerStartCommand.class, ServerStopCommand.class,
		TestUtil.class
	}
)
@RunWith(PowerMockRunner.class)
public class ServerCommandsTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");
	}

	@Test
	public void testServerCommands() throws Exception {
		_initWorkspace();

		_initBundle();

		_testServerStart();

		_testServerStop();

		PowerMock.verifyAll();
	}

	@Test
	public void testServerStartCommandExists() throws Exception {
		Assert.assertTrue(_commandExists("server", "start"));
		Assert.assertTrue(_commandExists("server start"));
		Assert.assertFalse(_commandExists("server", "startx"));
		Assert.assertFalse(_commandExists("server startx"));
		Assert.assertFalse(_commandExists("serverx", "start"));
		Assert.assertFalse(_commandExists("serverx start"));
	}

	@Test
	public void testServerStopCommandExists() throws Exception {
		Assert.assertTrue(_commandExists("server", "stop"));
		Assert.assertTrue(_commandExists("server stop"));
		Assert.assertFalse(_commandExists("server", "stopx"));
		Assert.assertFalse(_commandExists("server stopx"));
		Assert.assertFalse(_commandExists("serverx", "stopx"));
		Assert.assertFalse(_commandExists("serverx stop"));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static boolean _commandExists(String... args) {
		try {
			TestUtil.runBlade(args);
		}
		catch (Throwable throwable) {
			String message = throwable.getMessage();

			if (Objects.nonNull(message) && !message.contains("No such command")) {
				return true;
			}

			return false;
		}

		return false;
	}

	private Process _buildMockProcess() {
		Process mockProcess = EasyMock.createNiceMock(Process.class);

		EasyMock.replay(mockProcess);

		Object[] args = EasyMock.getCurrentArguments();

		String executable = String.valueOf(args[0]);

		String[] executableSplit = executable.split(" ");

		String executableName = executableSplit[0];

		File binFolder = (File)args[1];

		Path executablePath = binFolder.toPath();

		executablePath = executablePath.resolve(executableName).normalize();

		Assert.assertTrue(Files.exists(executablePath));

		return mockProcess;
	}

	private void _initBundle() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "gw", "initBundle"};

		TestUtil.runBlade(args);
	}

	private void _initWorkspace() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "7.1"};

		TestUtil.runBlade(args);
	}

	private void _testServerStart() throws Exception {
		PowerMock.mockStaticPartialNice(
			BladeUtil.class, "startProcess", String.class, File.class, Map.class, PrintStream.class, PrintStream.class);

		IExpectationSetters<Process> startProcessExpect = EasyMock.expect(
			BladeUtil.startProcess(
				EasyMock.anyString(), EasyMock.isA(File.class), EasyMock.isA(Map.class),
				EasyMock.isA(PrintStream.class), EasyMock.isA(PrintStream.class)));

		startProcessExpect.andAnswer(() -> _buildMockProcess()).once();

		PowerMock.replay(BladeUtil.class);

		String[] args = {"--base", _workspaceDir.getPath(), "server", "start"};

		TestUtil.runBlade(args);

		PowerMock.verify(BladeUtil.class);
	}

	private void _testServerStop() throws Exception {
		PowerMock.mockStaticPartialNice(BladeUtil.class, "startProcess", String.class, File.class, Map.class);

		IExpectationSetters<Process> startProcessExpect = EasyMock.expect(
			BladeUtil.startProcess(EasyMock.anyString(), EasyMock.isA(File.class), EasyMock.isA(Map.class)));

		startProcessExpect.andAnswer(() -> _buildMockProcess()).once();

		PowerMock.replay(BladeUtil.class);

		String[] args = {"--base", _workspaceDir.getPath(), "server", "stop"};

		TestUtil.runBlade(args);

		PowerMock.verify(BladeUtil.class);
	}

	private File _workspaceDir = null;

}