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
import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.Processes;

/**
 * @author Gregory Amerson
 */
public class ServerStartCommandTest {

	@Before
	public void setUp() throws Exception {
		_testWorkspaceDir = temporaryFolder.newFolder("testWorkspaceDir");
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
	public void testServerStartCommandTomcat() throws Exception {
		String[] initArgs = {"--base", _testWorkspaceDir.getPath(), "init", "-v", "7.1"};

		new BladeTest().run(initArgs);

		File gradleProperties = new File(_testWorkspaceDir, "gradle.properties");

		String contents = new String(Files.readAllBytes(gradleProperties.toPath()));

		StringBuilder sb = new StringBuilder();

		sb.append("liferay.workspace.bundle.url=");
		sb.append("https://releases-cdn.liferay.com/portal/7.1.1-ga2/");
		sb.append("liferay-ce-portal-tomcat-7.1.1-ga2-20181112144637000.tar.gz");
		sb.append(System.lineSeparator());

		String bundleUrl = sb.toString();

		contents = bundleUrl + contents;

		Files.write(gradleProperties.toPath(), bundleUrl.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

		String[] gwArgs = {"--base", _testWorkspaceDir.getPath(), "gw", "initBundle"};

		new BladeTest().run(gwArgs);

		File bundlesFolder = new File(_testWorkspaceDir, "bundles/tomcat-9.0.10");

		Assert.assertTrue(bundlesFolder.exists());

		Predicate<JavaProcess> tomcatFilter = process -> {
			String displayName = process.getDisplayName();

			return displayName.contains("org.apache.catalina.startup.Bootstrap");
		};

		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> tomcatProcess = _findProcess(javaProcesses, tomcatFilter);

		Assert.assertFalse(tomcatProcess.isPresent());

		String[] serverStartArgs = {"--base", _testWorkspaceDir.getPath(), "server", "start"};

		new BladeTest().run(serverStartArgs);

		Thread.sleep(1000);

		javaProcesses = JavaProcesses.list();

		tomcatProcess = _findProcess(javaProcesses, tomcatFilter);

		Assert.assertTrue("Expected tomcat process to be started", tomcatProcess.isPresent());

		JavaProcess javaProcess = tomcatProcess.get();

		PidProcess tomcatPidProcess = Processes.newPidProcess(javaProcess.getId());

		Assert.assertTrue("Expected tomcat process to be alive", tomcatPidProcess.isAlive());

		tomcatPidProcess.destroyForcefully();

		tomcatPidProcess.waitFor(1, TimeUnit.SECONDS);

		Assert.assertFalse("Expected tomcat proces to be destroyed.", tomcatPidProcess.isAlive());
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

	private Optional<JavaProcess> _findProcess(
		Collection<JavaProcess> javaProcesses, Predicate<JavaProcess> processFilter) {

		Stream<JavaProcess> stream = javaProcesses.stream();

		return stream.filter(
			processFilter
		).findFirst();
	}

	private File _testWorkspaceDir = null;

}