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

import com.liferay.blade.cli.TestUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class ServerStartCommandTest {

	@Before
	public void setUp() throws Exception {
		File testWorkspaceFile = temporaryFolder.newFolder("testWorkspaceDir");

		_testWorkspacePath = testWorkspaceFile.toPath();

		File extensionsFile = temporaryFolder.newFolder(".blade", "extensions");

		_extensionsPath = extensionsFile.toPath();
	}

	@Test
	public void testServerInitCustomEnvironment() throws Exception {
		_initBladeWorkspace();

		_customizeProdProperties();

		_initServerBundle("--environment", "prod");

		Path bundleConfigPath = _getBundleConfigPath();

		_validateBundleConfigFile(bundleConfigPath);
	}

	@Test
	public void testServerRunCommandTomcat() throws Exception {
		_findAndTerminateTomcat(false);

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_runServer();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerRunCommandTomcatDebug() throws Exception {
		_findAndTerminateTomcat(false);

		_debugPort = _DEFAULT_DEBUG_PORT_TOMCAT;

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_runServerDebug();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerRunCommandTomcatDebugCustomPort() throws Exception {
		_findAndTerminateTomcat(false);

		_debugPort = _getAvailablePort();

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_runServerDebug();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerRunCommandWildfly() throws Exception {
		_findAndTerminateWildfly(false);

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_runServer();

		_findAndTerminateWildfly(true);
	}

	@Test
	public void testServerRunCommandWildflyDebug() throws Exception {
		_findAndTerminateWildfly(false);

		_debugPort = _DEFAULT_DEBUG_PORT_WILDFLY;

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_runServerDebug();

		_findAndTerminateWildfly(true);
	}

	@Test
	public void testServerRunCommandWildflyDebugCustomPort() throws Exception {
		_findAndTerminateWildfly(false);

		_debugPort = _getAvailablePort();

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_runServerDebug();

		_findAndTerminateWildfly(true);
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
		_findAndTerminateTomcat(false);

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServer();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerStartCommandTomcatDebug() throws Exception {
		_findAndTerminateTomcat(false);

		_debugPort = _DEFAULT_DEBUG_PORT_TOMCAT;

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServerDebug();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerStartCommandTomcatDebugCustomPort() throws Exception {
		_findAndTerminateTomcat(false);

		_debugPort = _getAvailablePort();

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServerDebug();

		_findAndTerminateTomcat(true);
	}

	@Test
	public void testServerStartCommandWildfly() throws Exception {
		_findAndTerminateWildfly(false);

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_startServer();

		_findAndTerminateWildfly(true);
	}

	@Test
	public void testServerStartCommandWildflyDebug() throws Exception {
		_findAndTerminateWildfly(false);

		_debugPort = _DEFAULT_DEBUG_PORT_WILDFLY;

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_startServerDebug();

		_findAndTerminateWildfly(true);
	}

	@Test
	public void testServerStartCommandWildflyDebugCustomPort() throws Exception {
		_findAndTerminateWildfly(false);

		_debugPort = _getAvailablePort();

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_startServerDebug();

		_findAndTerminateWildfly(true);
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

	private static int _getAvailablePort() {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			serverSocket.setReuseAddress(true);

			return serverSocket.getLocalPort();
		}
		catch (IOException ioe) {
			throw new IllegalStateException("No available ports");
		}
	}

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

	private static void _terminateProcess(PidProcess tomcatPidProcess) throws Exception {
		ProcessUtil.destroyForcefullyAndWait(tomcatPidProcess, 1, TimeUnit.MINUTES);

		String processName = tomcatPidProcess.getDescription();

		Assert.assertFalse("Expected " + processName + " process to be destroyed.", tomcatPidProcess.isAlive());

		Assert.assertFalse(_isServerRunning());
	}

	private void _addBundleToGradle(String bundleFileName) throws Exception {
		Path gradlePropertiesPath = _testWorkspacePath.resolve("gradle.properties");

		StringBuilder sb = new StringBuilder();

		sb.append(_LIFERAY_WORKSPACE_BUNDLE_KEY);
		sb.append("=");
		sb.append(_LIFERAY_WORKSPACE_BUNDLE_URL);
		sb.append(bundleFileName);
		sb.append(System.lineSeparator());

		String bundleUrl = sb.toString();

		Files.write(gradlePropertiesPath, bundleUrl.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void _addTomcatBundleToGradle() throws Exception {
		_addBundleToGradle(_LIFERAY_WORKSPACE_BUNDLE_TOMCAT);
	}

	private void _addWildflyBundleToGradle() throws Exception {
		_addBundleToGradle(_LIFERAY_WORKSPACE_BUNDLE_WILDFLY);
	}

	private boolean _commandExists(String... args) {
		try {
			TestUtil.runBlade(_testWorkspacePath, _extensionsPath, args);
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

	private void _customizeProdProperties() throws FileNotFoundException, IOException {
		Path prodConfigPath = _testWorkspacePath.resolve(Paths.get("configs", "prod", "portal-ext.properties"));

		Properties portalExtProperties = new Properties();

		try (InputStream inputStream = Files.newInputStream(prodConfigPath)) {
			portalExtProperties.load(inputStream);
		}

		portalExtProperties.put("foo.bar", "foobar");

		try (OutputStream outputStream = Files.newOutputStream(prodConfigPath)) {
			portalExtProperties.store(outputStream, "");
		}
	}

	private void _findAndTerminateServer(Predicate<JavaProcess> processFilter, boolean assertFound) throws Exception {
		if (assertFound) {
			Assert.assertTrue(_isServerRunning());
		}

		Optional<PidProcess> serverProcess = _findServerProcess(processFilter, assertFound);

		if (!assertFound && !serverProcess.isPresent()) {
			return;
		}

		boolean debugPortListening = false;

		if (_useDebug) {
			debugPortListening = _isDebugPortListening(_debugPort);

			Assert.assertEquals("Debug port not in a correct state", _useDebug, debugPortListening);
		}

		_terminateProcess(serverProcess.get());

		if (_useDebug) {
			debugPortListening = _isDebugPortListening(_debugPort);

			Assert.assertFalse("Debug port should no longer be listening", debugPortListening);
		}
	}

	private void _findAndTerminateTomcat(boolean assertFound) throws Exception {
		_findAndTerminateServer(
			process -> {
				String displayName = process.getDisplayName();

				return displayName.contains("org.apache.catalina.startup.Bootstrap");
			},
			assertFound);
	}

	private void _findAndTerminateWildfly(boolean assertFound) throws Exception {
		_findAndTerminateServer(
			process -> {
				String displayName = process.getDisplayName();

				return displayName.contains("jboss-modules");
			},
			assertFound);
	}

	private Optional<JavaProcess> _findProcess(
		Collection<JavaProcess> javaProcesses, Predicate<JavaProcess> processFilter) {

		Stream<JavaProcess> stream = javaProcesses.stream();

		return stream.filter(
			processFilter
		).findFirst();
	}

	private Optional<PidProcess> _findServerProcess(Predicate<JavaProcess> processFilter, boolean assertFound)
		throws InterruptedException, IOException {

		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> optionalProcess = _findProcess(javaProcesses, processFilter);

		if (assertFound) {
			Assert.assertTrue(
				"Expected to find server process:\n" + _printDisplayNames(javaProcesses), optionalProcess.isPresent());
		}
		else {
			return Optional.ofNullable(null);
		}

		JavaProcess javaProcess = optionalProcess.get();

		String processName = javaProcess.getDisplayName();

		PidProcess pidProcess = Processes.newPidProcess(javaProcess.getId());

		Assert.assertTrue("Expected " + processName + " process to be alive", pidProcess.isAlive());

		return Optional.of(pidProcess);
	}

	private Path _getBundleConfigPath() {
		Path bundlesFolderPath = _testWorkspacePath.resolve("bundles");

		boolean bundlesFolderExists = Files.exists(bundlesFolderPath);

		Assert.assertTrue(bundlesFolderExists);

		Path bundleConfigPath = bundlesFolderPath.resolve("portal-ext.properties");

		boolean bundleConfigFileExists = Files.exists(bundleConfigPath);

		Assert.assertTrue(bundleConfigFileExists);

		return bundleConfigPath;
	}

	private String[] _getDebugArgs(String[] serverStartArgs) {
		Collection<String> serverStartArgsCollection = Arrays.asList(serverStartArgs);

		serverStartArgsCollection = new ArrayList<>(serverStartArgsCollection);

		serverStartArgsCollection.add("--debug");
		serverStartArgsCollection.add("--port");
		serverStartArgsCollection.add(String.valueOf(_debugPort));

		serverStartArgs = serverStartArgsCollection.toArray(new String[0]);

		return serverStartArgs;
	}

	private void _initBladeWorkspace() throws Exception {
		String[] initArgs = {"--base", _testWorkspacePath.toString(), "init", "-v", "7.1"};

		TestUtil.runBlade(_testWorkspacePath, _extensionsPath, initArgs);
	}

	private void _initServerBundle(String... additionalArgs) throws Exception {
		String[] serverInitArgs = {"--base", _testWorkspacePath.toString(), "server", "init"};

		if ((additionalArgs != null) && (additionalArgs.length > 0)) {
			Collection<String> serverInitArgsCollection = Arrays.asList(serverInitArgs);

			Collection<String> additionalArgsCollection = Arrays.asList(additionalArgs);

			serverInitArgsCollection = new ArrayList<>(serverInitArgsCollection);

			serverInitArgsCollection.addAll(additionalArgsCollection);

			serverInitArgs = serverInitArgsCollection.toArray(new String[0]);
		}

		TestUtil.runBlade(_testWorkspacePath, _extensionsPath, serverInitArgs);
	}

	private String _printDisplayNames(Collection<JavaProcess> javaProcesses) {
		StringBuilder sb = new StringBuilder();

		for (JavaProcess javaProcess : javaProcesses) {
			sb.append(javaProcess.getDisplayName() + System.lineSeparator());
		}

		return sb.toString();
	}

	private void _runServer() throws Exception, InterruptedException {
		Assert.assertFalse(_isServerRunning());

		String[] serverRunArgs = {"--base", _testWorkspacePath.toString(), "server", "run"};

		CountDownLatch latch = new CountDownLatch(1);

		CompletableFuture.runAsync(
			() -> {
				latch.countDown();

				TestUtil.runBlade(_testWorkspacePath, _extensionsPath, serverRunArgs);
			},
			_executorService);

		latch.await();

		int retries = 0;

		while (!_isServerRunning() && (retries < 12)) {
			Thread.sleep(5000);

			retries++;
		}

		Assert.assertTrue("Expected a new process", _isServerRunning());
	}

	private void _runServerDebug() throws Exception, InterruptedException {
		_useDebug = true;

		Assert.assertFalse(_isServerRunning());

		String[] serverRunArgs = {"--base", _testWorkspacePath.toString(), "server", "run"};

		final String[] serverRunDebugArgs = _getDebugArgs(serverRunArgs);

		CountDownLatch latch = new CountDownLatch(1);

		CompletableFuture.runAsync(
			() -> {
				latch.countDown();

				TestUtil.runBlade(_testWorkspacePath, _extensionsPath, serverRunDebugArgs);
			},
			_executorService);

		latch.await();

		int retries = 0;

		while (!_isServerRunning() && (retries < 12)) {
			Thread.sleep(5000);

			retries++;
		}

		Assert.assertTrue("Expected a new process", _isServerRunning());
	}

	private void _startServer() throws Exception, InterruptedException {
		Assert.assertFalse(_isServerRunning());

		String[] serverStartArgs = {"--base", _testWorkspacePath.toString(), "server", "start"};

		CountDownLatch latch = new CountDownLatch(1);

		CompletableFuture.runAsync(
			() -> {
				latch.countDown();

				TestUtil.runBlade(_testWorkspacePath, _extensionsPath, serverStartArgs);
			},
			_executorService);

		latch.await();

		int retries = 0;

		while (!_isServerRunning() && (retries < 12)) {
			Thread.sleep(5000);
			retries++;
		}

		Assert.assertTrue("Expected a new process", _isServerRunning());
	}

	private void _startServerDebug() throws Exception, InterruptedException {
		_useDebug = true;

		Assert.assertFalse(_isServerRunning());

		String[] serverStartArgs = {"--base", _testWorkspacePath.toString(), "server", "start"};

		final String[] serverStartArgsFinal = _getDebugArgs(serverStartArgs);

		CountDownLatch latch = new CountDownLatch(1);

		CompletableFuture.runAsync(
			() -> {
				latch.countDown();

				TestUtil.runBlade(_testWorkspacePath, _extensionsPath, serverStartArgsFinal);
			},
			_executorService);

		latch.await();

		int retries = 0;

		while (!_isServerRunning() && (retries < 12)) {
			Thread.sleep(5000);

			retries++;
		}

		Assert.assertTrue("Expected a new process", _isServerRunning());
	}

	private void _validateBundleConfigFile(Path bundleConfigPath) throws FileNotFoundException, IOException {
		Properties runtimePortalExtProperties = new Properties();

		try (InputStream inputStream = Files.newInputStream(bundleConfigPath)) {
			runtimePortalExtProperties.load(inputStream);
		}

		String fooBarProperty = runtimePortalExtProperties.getProperty("foo.bar");

		Assert.assertEquals("foobar", fooBarProperty);
	}

	private void _verifyBundlePath(String folderName) {
		Path bundlesPath = _testWorkspacePath.resolve(Paths.get("bundles", folderName));

		boolean bundlesPathExists = Files.exists(bundlesPath);

		Assert.assertTrue("Bundles folder " + bundlesPath + " must exist", bundlesPathExists);
	}

	private void _verifyTomcatBundlePath() {
		_verifyBundlePath(_BUNDLE_FOLDER_NAME_TOMCAT);
	}

	private void _verifyWildflyBundlePath() {
		_verifyBundlePath(_BUNDLE_FOLDER_NAME_WILDFLY);
	}

	private static final String _BUNDLE_FOLDER_NAME_TOMCAT = "tomcat-9.0.10";

	private static final String _BUNDLE_FOLDER_NAME_WILDFLY = "wildfly-11.0.0";

	private static final int _DEFAULT_DEBUG_PORT_TOMCAT = 8000;

	private static final int _DEFAULT_DEBUG_PORT_WILDFLY = 8787;

	private static final String _LIFERAY_WORKSPACE_BUNDLE_KEY = "liferay.workspace.bundle.url";

	private static final String _LIFERAY_WORKSPACE_BUNDLE_TOMCAT =
		"liferay-ce-portal-tomcat-7.1.1-ga2-20181112144637000.tar.gz";

	private static final String _LIFERAY_WORKSPACE_BUNDLE_URL = "https://releases-cdn.liferay.com/portal/7.1.1-ga2/";

	private static final String _LIFERAY_WORKSPACE_BUNDLE_WILDFLY =
		"liferay-ce-portal-wildfly-7.1.1-ga2-20181112144637000.tar.gz";

	private int _debugPort = -1;
	private ExecutorService _executorService = Executors.newSingleThreadExecutor();
	private Path _extensionsPath = null;
	private Path _testWorkspacePath = null;
	private boolean _useDebug = false;

}