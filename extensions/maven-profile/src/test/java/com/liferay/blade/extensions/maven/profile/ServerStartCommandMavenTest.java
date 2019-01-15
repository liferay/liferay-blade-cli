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

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.command.JavaProcess;
import com.liferay.blade.cli.command.JavaProcesses;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
public class ServerStartCommandMavenTest {

	@Before
	public void setUp() throws Exception {
		_testWorkspaceDir = temporaryFolder.newFolder("testWorkspaceDir");

		_killTomcat();
	}

	@Test
	public void testServerStartCommandTomcat() throws Exception {
		String[] initArgs = {"--base", _testWorkspaceDir.getPath(), "init", "-v", "7.1", "-P", "maven"};

		new BladeTest().run(initArgs);

		File pomXml = new File(_testWorkspaceDir, "pom.xml");

		Assert.assertTrue(pomXml.exists());

		File bladeSettings = new File(_testWorkspaceDir, ".blade/settings.properties");

		Assert.assertTrue(bladeSettings.exists());

		List<String> commands = new ArrayList<>();

		if (BladeUtil.isWindows()) {
			commands.add("cmd.exe");
			commands.add("/c");
			commands.add(".\\mvnw.cmd");
		}
		else {
			commands.add("./mvnw");
		}

		commands.add("bundle-support:init");

		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		processBuilder.directory(_testWorkspaceDir);

		Process process = processBuilder.start();

		process.waitFor();

		File bundles = new File(_testWorkspaceDir, "bundles");

		Assert.assertTrue(bundles.exists());

		String[] serverStartArgs = {"--base", _testWorkspaceDir.getPath(), "server", "start"};

		try {
			new BladeTest().run(serverStartArgs);
		}
		catch (Exception e) {
		}

		Thread.sleep(1000);

		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> tomcatProcess = _findProcess(javaProcesses, _tomcatFilter);

		Assert.assertTrue(
			"Expected to find tomcat process:\n" + _printDisplayNames(javaProcesses), tomcatProcess.isPresent());

		JavaProcess javaProcess = tomcatProcess.get();

		PidProcess tomcatPidProcess = Processes.newPidProcess(javaProcess.getId());

		Assert.assertTrue("Expected tomcat process to be alive", tomcatPidProcess.isAlive());

		tomcatPidProcess.destroyForcefully();

		tomcatPidProcess.waitFor(1, TimeUnit.SECONDS);

		Assert.assertFalse("Expected tomcat proces to be destroyed.", tomcatPidProcess.isAlive());
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Optional<JavaProcess> _findProcess(
		Collection<JavaProcess> javaProcesses, Predicate<JavaProcess> processFilter) {

		Stream<JavaProcess> stream = javaProcesses.stream();

		return stream.filter(
			processFilter
		).findFirst();
	}

	private void _killTomcat() throws Exception {
		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> tomcatProcess = _findProcess(javaProcesses, _tomcatFilter);

		if (tomcatProcess.isPresent()) {
			JavaProcess javaProcess = tomcatProcess.get();

			PidProcess tomcatPidProcess = Processes.newPidProcess(javaProcess.getId());

			Assert.assertTrue("Expected tomcat process to be alive", tomcatPidProcess.isAlive());

			tomcatPidProcess.destroyForcefully();

			tomcatPidProcess.waitFor(1, TimeUnit.SECONDS);

			Assert.assertFalse("Expected tomcat process to be destroyed.", tomcatPidProcess.isAlive());
		}
	}

	private String _printDisplayNames(Collection<JavaProcess> javaProcesses) {
		StringBuilder sb = new StringBuilder();

		for (JavaProcess javaProcess : javaProcesses) {
			sb.append(javaProcess.getDisplayName() + System.lineSeparator());
		}

		return sb.toString();
	}

	private File _testWorkspaceDir = null;

	private Predicate<JavaProcess> _tomcatFilter = process -> {
		String displayName = process.getDisplayName();

		return displayName.contains("org.apache.catalina.startup.Bootstrap");
	};

}