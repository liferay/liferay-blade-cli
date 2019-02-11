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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetAddress;
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
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class ServerStartCommandTest {

	@Before
	public void setUp() throws Exception {
		_testWorkspaceDir = temporaryFolder.newFolder("testWorkspaceDir");

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");

		_killTomcat();

		_killWildfly();
	}

	@Test
	public void testServerInitCustomEnvironment() throws Exception {
		String[] initArgs = {"--base", _testWorkspaceDir.getPath(), "init"};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, initArgs);

		File prodConfigFile = new File(_testWorkspaceDir, "configs/prod/portal-ext.properties");

		Properties portalExtProperties = new Properties();

		portalExtProperties.load(new FileInputStream(prodConfigFile));

		portalExtProperties.put("foo.bar", "foobar");

		Path prodFilePath = prodConfigFile.toPath();

		try (OutputStream stream = Files.newOutputStream(prodFilePath)) {
			portalExtProperties.store(stream, "");
		}

		String[] serverInitArgs = {"--base", _testWorkspaceDir.getPath(), "server init", "--environment", "prod"};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverInitArgs);

		File bundlesFolder = new File(_testWorkspaceDir, "bundles");

		boolean bundlesFolderExists = bundlesFolder.exists();

		Assert.assertTrue(bundlesFolderExists);

		File bundleConfigFile = new File(bundlesFolder, "portal-ext.properties");

		boolean bundleConfigFileExists = bundleConfigFile.exists();

		Assert.assertTrue(bundleConfigFileExists);

		portalExtProperties.load(new FileInputStream(bundleConfigFile));

		String fooBarProperty = portalExtProperties.getProperty("foo.bar");

		Assert.assertEquals("foobar", fooBarProperty);
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
		boolean useDebugging = false;

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServer(useDebugging);

		_findAndTerminateTomcat(useDebugging);
	}

	@Test
	public void testServerStartCommandTomcatDebug() throws Exception {
		boolean useDebugging = true;

		_initBladeWorkspace();

		_addTomcatBundleToGradle();

		_initServerBundle();

		_verifyTomcatBundlePath();

		_startServer(useDebugging);

		_findAndTerminateTomcat(useDebugging);
	}

	@Test
	public void testServerStartCommandWildfly() throws Exception {
		boolean useDebugging = false;

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_startServer(useDebugging);

		_findAndTerminateWildfly(useDebugging);
	}

	@Test
	public void testServerStartCommandWildflyDebug() throws Exception {
		boolean useDebugging = true;

		_initBladeWorkspace();

		_addWildflyBundleToGradle();

		_initServerBundle();

		_verifyWildflyBundlePath();

		_startServer(useDebugging);

		_findAndTerminateWildfly(useDebugging);
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

	private static boolean _isDebugPortListening(int debugPort) {
		InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

		try (Socket socket = new Socket(loopbackAddress, debugPort)) {
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}

	private static void _terminateProcess(PidProcess tomcatPidProcess) throws InterruptedException, IOException {
		tomcatPidProcess.destroyForcefully();

		tomcatPidProcess.waitFor(1, TimeUnit.SECONDS);

		String processName = tomcatPidProcess.getDescription();

		Assert.assertFalse("Expected " + processName + " process to be destroyed.", tomcatPidProcess.isAlive());
	}

	private void _addBundleToGradle(String bundleFileName) throws Exception {
		File gradleProperties = new File(_testWorkspaceDir, "gradle.properties");

		String contents = new String(Files.readAllBytes(gradleProperties.toPath()));

		StringBuilder sb = new StringBuilder();

		sb.append(_liferayWorkspaceBundleKey);
		sb.append("=");
		sb.append(_liferayWorkspaceBundleUrl);
		sb.append(bundleFileName);
		sb.append(System.lineSeparator());

		String bundleUrl = sb.toString();

		contents = bundleUrl + contents;

		Files.write(gradleProperties.toPath(), bundleUrl.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void _addTomcatBundleToGradle() throws Exception {
		_addBundleToGradle(_liferayWorkspaceBundleTomcat);
	}

	private void _addWildflyBundleToGradle() throws Exception {
		_addBundleToGradle(_liferayWorkspaceBundleWildfly);
	}

	private boolean _commandExists(String... args) {
		try {
			TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, args);
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

	private void _findAndTerminateServer(Predicate<JavaProcess> processFilter, boolean debugFlag, int debugPort)
		throws Exception {

		PidProcess serverProcess = _findServerProcess(processFilter);

		boolean debugPortListening = _isDebugPortListening(debugPort);

		Assert.assertEquals("Debug port not in a correct state", debugFlag, debugPortListening);

		_terminateProcess(serverProcess);

		if (debugFlag) {
			debugPortListening = _isDebugPortListening(debugPort);

			Assert.assertFalse("Debug port should no longer be listening", debugPortListening);
		}
	}

	private void _findAndTerminateTomcat(boolean debugFlag) throws Exception {
		_findAndTerminateServer(_tomcatFilter, debugFlag, _debugPortTomcat);
	}

	private void _findAndTerminateWildfly(boolean debugFlag) throws Exception {
		_findAndTerminateServer(_wildflyFilter, debugFlag, _debugPortWildfly);
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

	private void _initBladeWorkspace() throws Exception {
		String[] initArgs = {"--base", _testWorkspaceDir.getPath(), "init", "-v", "7.1"};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, initArgs);
	}

	private void _initServerBundle() throws Exception {
		String[] gwArgs = {"--base", _testWorkspaceDir.getPath(), "server", "init"};

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, gwArgs);
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

	private void _killWildfly() throws Exception {
		Collection<JavaProcess> javaProcesses = JavaProcesses.list();

		Optional<JavaProcess> wildflyProcess = _findProcess(javaProcesses, _wildflyFilter);

		if (wildflyProcess.isPresent()) {
			JavaProcess javaProcess = wildflyProcess.get();

			PidProcess wildflyPidProcess = Processes.newPidProcess(javaProcess.getId());

			Assert.assertTrue("Expected wildfly process to be alive", wildflyPidProcess.isAlive());

			wildflyPidProcess.destroyForcefully();

			wildflyPidProcess.waitFor(1, TimeUnit.SECONDS);

			Assert.assertFalse("Expected wildfly proces to be destroyed.", wildflyPidProcess.isAlive());
		}
	}

	private String _printDisplayNames(Collection<JavaProcess> javaProcesses) {
		StringBuilder sb = new StringBuilder();

		for (JavaProcess javaProcess : javaProcesses) {
			sb.append(javaProcess.getDisplayName() + System.lineSeparator());
		}

		return sb.toString();
	}

	private void _startServer(boolean debugFlag) throws Exception, InterruptedException {
		String[] serverStartArgs = {"--base", _testWorkspaceDir.getPath(), "server", "start"};

		Collection<String> serverStartArgsCollection = Arrays.asList(serverStartArgs);

		serverStartArgsCollection = new ArrayList<>(serverStartArgsCollection);

		if (debugFlag) {
			serverStartArgsCollection.add("--debug");
		}

		serverStartArgs = serverStartArgsCollection.toArray(new String[0]);

		TestUtil.runBlade(_testWorkspaceDir, _extensionsDir, serverStartArgs);

		Thread.sleep(1000);
	}

	private void _verifyBundlePath(String folderName) {
		Path workspacePath = _testWorkspaceDir.toPath();

		Path bundlesPath = Paths.get("bundles", folderName);

		bundlesPath = workspacePath.resolve(bundlesPath);

		boolean bundlesPathExists = Files.exists(bundlesPath);

		Assert.assertTrue("Bundles folder " + bundlesPath + " must exist", bundlesPathExists);
	}

	private void _verifyTomcatBundlePath() {
		_verifyBundlePath(_bundleFolderNameTomcat);
	}

	private void _verifyWildflyBundlePath() {
		_verifyBundlePath(_bundleFolderNameWildfly);
	}

	private static String _bundleFolderNameTomcat = "tomcat-9.0.10";
	private static String _bundleFolderNameWildfly = "wildfly-11.0.0";
	private static int _debugPortTomcat = 8000;
	private static int _debugPortWildfly = 8787;
	private static String _liferayWorkspaceBundleKey = "liferay.workspace.bundle.url";
	private static String _liferayWorkspaceBundleTomcat = "liferay-ce-portal-tomcat-7.1.1-ga2-20181112144637000.tar.gz";
	private static String _liferayWorkspaceBundleUrl = "https://releases-cdn.liferay.com/portal/7.1.1-ga2/";
	private static String _liferayWorkspaceBundleWildfly =
		"liferay-ce-portal-wildfly-7.1.1-ga2-20181112144637000.tar.gz";

	private File _extensionsDir = null;
	private File _testWorkspaceDir = null;

	private Predicate<JavaProcess> _tomcatFilter = process -> {
		String displayName = process.getDisplayName();

		return displayName.contains("org.apache.catalina.startup.Bootstrap");
	};

	private Predicate<JavaProcess> _wildflyFilter = process -> {
		String displayName = process.getDisplayName();

		return displayName.contains("jboss-modules");
	};

}