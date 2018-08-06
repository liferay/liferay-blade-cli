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
import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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
		_initWorkspace();

		_initBundle();

		_testServerStart();

		_testServerStop();
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

	private void _initBundle() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "gw", "initBundle"};

		TestUtil.runBlade(args);
	}

	private void _initWorkspace() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "init", "-v", "7.1"};

		TestUtil.runBlade(args);
	}

	private void _testServerStart() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "server", "start"};

		BladeTestResults results = TestUtil.runBlade(args);

		BladeCLI blade = results.getBlade();

		BaseCommand<?> serverStartCommand = blade.getCommand();

		((AutoCloseable)serverStartCommand).close();
	}

	private void _testServerStop() throws Exception {
		String[] args = {"--base", _workspaceDir.getPath(), "server", "stop"};

		TestUtil.runBlade(args);
	}

	private File _workspaceDir = null;

}