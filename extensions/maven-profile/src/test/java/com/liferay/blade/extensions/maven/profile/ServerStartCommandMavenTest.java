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
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.command.JavaProcess;
import com.liferay.blade.cli.command.JavaProcesses;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.zeroturnaround.process.PidProcess;
import org.zeroturnaround.process.ProcessUtil;
import org.zeroturnaround.process.Processes;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ServerStartCommandMavenTest {

	@Before
	public void setUp() throws Exception {
		File testWorkspaceFile = temporaryFolder.newFolder("testWorkspaceDir");

		_testWorkspaceDir = testWorkspaceFile.toPath();

		File extensionsFile = temporaryFolder.newFolder(".blade", "extensions");

		_extensionsDir = extensionsFile.toPath();
	}

	@Test
	public void testServerRunCommandTomcat() throws Exception {
		_initBladeWorkspace();

		_verifyMavenFiles();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_runServer();

		_findAndTerminateTomcat();
	}

	@Test
	public void testServerRunCommandTomcatDebug() throws Exception {
		_initBladeWorkspace();

		_verifyMavenFiles();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_runServerDebug();

		_findAndTerminateTomcat();
	}

	@Test
	public void testServerStartCommandTomcat() throws Exception {
		_initBladeWorkspace();

		_verifyMavenFiles();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServer();

		_findAndTerminateTomcat();
	}

	@Test
	public void testServerStartCommandTomcatDebug() throws Exception {
		_initBladeWorkspace();

		_verifyMavenFiles();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServerDebug();

		_findAndTerminateTomcat();
	}

	@Rule
	public RetryRule retryRule = new RetryRule(1);

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static boolean _isDebugPortListening(int debugPort) {
		InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

		try (Socket socket = new Socket(loopbackAddress, debugPort)) {
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}

	private static boolean _isServerRunning() {
		for (JavaProcess process : JavaProcesses.list()) {
			String displayName = process.getDisplayName();

			if (displayName.contains("wildfly") || displayName.contains("catalina")) {
				return true;
			}
		}

		return false;
	}

	private static void _terminateProcess(PidProcess tomcatPidProcess)
		throws InterruptedException, IOException, TimeoutException {

		ProcessUtil.destroyForcefullyAndWait(tomcatPidProcess, 1, TimeUnit.MINUTES);

		String processName = tomcatPidProcess.getDescription();

		Assert.assertFalse("Expected " + processName + " process to be destroyed.", tomcatPidProcess.isAlive());

		Assert.assertFalse(_isServerRunning());
	}

	private void _findAndTerminateServer(Predicate<JavaProcess> processFilter) throws Exception {
		PidProcess serverProcess = _findServerProcess(processFilter);

		boolean debugPortListening = false;

		if (_useDebug) {
			debugPortListening = _isDebugPortListening(_DEBUG_PORT_TOMCAT);

			Assert.assertEquals("Debug port not in a correct state", _useDebug, debugPortListening);
		}

		_terminateProcess(serverProcess);

		if (_useDebug) {
			debugPortListening = _isDebugPortListening(_DEBUG_PORT_TOMCAT);

			Assert.assertFalse("Debug port should no longer be listening", debugPortListening);
		}
	}

	private void _findAndTerminateTomcat() throws Exception {
		_findAndTerminateServer(
			process -> {
				String displayName = process.getDisplayName();

				return displayName.contains("org.apache.catalina.startup.Bootstrap");
			});
	}

	private Optional<JavaProcess> _findProcess(
		Collection<JavaProcess> javaProcesses, Predicate<JavaProcess> processFilter) {

		Stream<JavaProcess> stream = javaProcesses.stream();

		return stream.filter(
			processFilter
		).findFirst();
	}

	private PidProcess _findServerProcess(Predicate<JavaProcess> processFilter)
		throws InterruptedException, IOException {

		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> optionalProcess = _findProcess(javaProcesses, processFilter);

		Assert.assertTrue(
			"Expected to find server process:\n" + _printDisplayNames(javaProcesses), optionalProcess.isPresent());

		JavaProcess javaProcess = optionalProcess.get();

		String processName = javaProcess.getDisplayName();

		PidProcess pidProcess = Processes.newPidProcess(javaProcess.getId());

		Assert.assertTrue("Expected " + processName + " process to be alive", pidProcess.isAlive());

		return pidProcess;
	}

	private String[] _getDebugArgs(String[] serverStartArgs) {
		Collection<String> serverStartArgsCollection = Arrays.asList(serverStartArgs);

		serverStartArgsCollection = new ArrayList<>(serverStartArgsCollection);

		serverStartArgsCollection.add("--debug");
		serverStartArgsCollection.add("--port");
		serverStartArgsCollection.add(String.valueOf(_DEBUG_PORT_TOMCAT));

		return serverStartArgsCollection.toArray(new String[0]);
	}

	private void _initBladeWorkspace() throws Exception {
		String[] initArgs = {
			"--base", _testWorkspaceDir.toString(), "init", "-f", "-v", BladeTest.PRODUCT_VERSION_PORTAL_71, "-P",
			"maven"
		};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, initArgs);

		String workspacePath = _testWorkspaceDir.toString();

		TestUtil.updateMavenRepositories(workspacePath);
	}

	private void _initServerBundle() throws InterruptedException, IOException {
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

		processBuilder.directory(_testWorkspaceDir.toFile());

		Process process = processBuilder.start();

		process.waitFor();
	}

	private String _printDisplayNames(Collection<JavaProcess> javaProcesses) {
		StringBuilder sb = new StringBuilder();

		for (JavaProcess javaProcess : javaProcesses) {
			sb.append(javaProcess.getDisplayName() + System.lineSeparator());
		}

		return sb.toString();
	}

	private void _runServer() throws Exception, InterruptedException {
		String[] serverRunArgs = {"--base", _testWorkspaceDir.toString(), "server", "run"};

		CompletableFuture.runAsync(() -> TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverRunArgs));

		Thread.sleep(1000);
	}

	private void _runServerDebug() throws Exception, InterruptedException {
		_useDebug = true;

		String[] serverRunArgs = {"--base", _testWorkspaceDir.toString(), "server", "run"};

		final String[] serverRunArgsFinal = _getDebugArgs(serverRunArgs);

		CompletableFuture.runAsync(() -> TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverRunArgsFinal));

		Thread.sleep(1000);
	}

	private void _startServer() throws Exception, InterruptedException {
		String[] serverStartArgs = {"--base", _testWorkspaceDir.toString(), "server", "start"};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverStartArgs);

		Thread.sleep(1000);
	}

	private void _startServerDebug() throws Exception, InterruptedException {
		_useDebug = true;

		String[] serverStartArgs = {"--base", _testWorkspaceDir.toString(), "server", "start"};

		final String[] serverStartArgsFinal = _getDebugArgs(serverStartArgs);

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverStartArgsFinal);

		Thread.sleep(1000);
	}

	private void _verifyMavenFiles() {
		Path pomXml = _testWorkspaceDir.resolve("pom.xml");

		Assert.assertTrue(Files.exists(pomXml));

		Path bladeSettings = _testWorkspaceDir.resolve(".blade.properties");

		Assert.assertTrue(Files.exists(bladeSettings));
	}

	private void _verifyTomcatBundlePath() {
		Path bundles = _testWorkspaceDir.resolve("bundles");

		Assert.assertTrue(Files.exists(bundles));
	}

	private static final int _DEBUG_PORT_TOMCAT = 8000;

	private Path _extensionsDir = null;
	private Path _testWorkspaceDir = null;
	private boolean _useDebug = false;

}