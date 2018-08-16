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

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.StringPrintStream;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.PidUtil;
import org.zeroturnaround.process.Processes;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerCommandsTest {

	@Before
	public void setUp() throws Exception {
		_workspaceDir = temporaryFolder.newFolder("build", "test", "workspace");
	}

	@Test
	public void testServerCommands() throws Exception {
		TestUtil.runBlade(new String[] {"--base", _workspaceDir.getPath(), "init", "-v", "7.1"});

		TestUtil.runBlade(new String[] {"--base", _workspaceDir.getPath(), "gw", "initBundle"});

		_testServerStart();
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

	private void _testServerStart() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "server", "start"};

		StringPrintStream outputPrintStream = StringPrintStream.newInstance();

		StringPrintStream errorPrintStream = StringPrintStream.newInstance();

		BladeTest bladeTest = new BladeTest(outputPrintStream, errorPrintStream);

		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					bladeTest.run(args);
				}
				catch (Exception e) {
				}
			}

		};

		thread.setDaemon(true);

		thread.run();

		Thread.sleep(1);

		ServerStartCommand serverStartCommand = (ServerStartCommand)bladeTest.getCommand();

		Collection<Process> processes = serverStartCommand.getProcesses();

		Assert.assertFalse("Expected server start process to have started.", processes.isEmpty());

		Iterator<Process> iterator = processes.iterator();

		Process process = iterator.next();

		Assert.assertTrue("Expected server start process to be alive.", process.isAlive());

		int pid = PidUtil.getPid(process);

		PidProcess pidProcess = Processes.newPidProcess(pid);

		pidProcess.destroyForcefully();

		Assert.assertFalse("Expected server start process to be destroyed.", pidProcess.isAlive());
	}

	private File _workspaceDir = null;

}