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

import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeTest;
import com.liferay.blade.cli.LiferayBundleDeployer;
import com.liferay.blade.cli.MockUtil;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.easymock.EasyMock;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.osgi.framework.dto.BundleDTO;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest(
	{
		Domain.class, GradleTooling.class, LiferayBundleDeployer.class, GradleExec.class, BladeCLI.class,
		BladeTest.class, BladeUtil.class, DeployCommand.class, TestUtil.class
	}
)
public class DeployCommandTest {

	@Test
	public void testInstallExistingJar() throws Exception {
		Collection<BundleDTO> bundles = Collections.emptyList();

		final AtomicLong atomicLong = new AtomicLong(1);

		File jar = _createFile("test.jar");

		MockUtil.stubGradleExec();

		MockUtil.stubDomain(true, false);

		MockUtil.stubDeployCommand();

		MockUtil.stubGradleTooling(jar);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(
			client.getBundleId(EasyMock.eq(bundles), EasyMock.anyString())
		).andAnswer(
			() -> atomicLong.get()
		).atLeastOnce();

		EasyMock.expect(
			client.getBundleId(EasyMock.anyString())
		).andAnswer(
			() -> atomicLong.get()
		).atLeastOnce();

		EasyMock.expect(
			client.getBundles()
		).andReturn(
			bundles
		).atLeastOnce();

		Path jarPath = jar.toPath();

		EasyMock.expect(
			client.install(EasyMock.eq(jarPath.toUri()))
		).andAnswer(
			() -> atomicLong.incrementAndGet()
		).once();

		client.reloadBundle(EasyMock.anyLong(), EasyMock.eq(jarPath.toUri()));

		EasyMock.expectLastCall(
		).andVoid(
		).once();

		EasyMock.replay(client);

		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(
			LiferayBundleDeployer.newInstance(EasyMock.anyString(), EasyMock.anyInt())
		).andReturn(
			client
		).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"--base", jar.getParentFile().getAbsolutePath(), "deploy"};

		String content = TestUtil.runBlade(args);

		PowerMock.verifyAll();

		Assert.assertTrue(content.contains(String.format("Updated bundle %s", atomicLong.get())));
	}

	@Test
	public void testInstallJar() throws Exception {
		Collection<BundleDTO> bundles = Collections.emptyList();

		final AtomicLong atomicLong = new AtomicLong(1);

		File jar = _createFile("test.jar");

		MockUtil.stubGradleExec();

		MockUtil.stubDomain(true, false);

		MockUtil.stubDeployCommand();

		MockUtil.stubGradleTooling(jar);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(
			client.getBundleId(EasyMock.anyString())
		).andAnswer(
			atomicLong::get
		).atLeastOnce();

		EasyMock.expect(
			client.getBundles()
		).andReturn(
			bundles
		).atLeastOnce();

		Path jarPath = jar.toPath();

		EasyMock.expect(
			client.install(jarPath.toUri())
		).andAnswer(
			() -> atomicLong.incrementAndGet()
		).once();

		client.start(EasyMock.eq(atomicLong.get()));

		EasyMock.expectLastCall(
		).andVoid(
		).once();

		EasyMock.replay(client);

		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(
			LiferayBundleDeployer.newInstance(EasyMock.anyString(), EasyMock.anyInt())
		).andReturn(
			client
		).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"--base", jar.getParentFile().getAbsolutePath(), "deploy"};

		String content = TestUtil.runBlade(args);

		PowerMock.verifyAll();

		Assert.assertTrue(content.contains(String.format("Installed bundle %s", atomicLong.get())));
	}

	@Test
	public void testInstallWar() throws Exception {
		File war = _createFile("test.war");

		MockUtil.stubGradleExec();

		MockUtil.stubDomain(false, false);

		MockUtil.stubDeployCommand();

		MockUtil.stubGradleTooling(war);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(
			client.install(EasyMock.eq(war.toURI()))
		).andReturn(
			1L
		).once();

		client.start(1);

		EasyMock.expectLastCall(
		).andVoid(
		).once();

		EasyMock.replay(client);

		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(
			LiferayBundleDeployer.newInstance(EasyMock.anyString(), EasyMock.anyInt())
		).andReturn(
			client
		).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"--base", war.getParentFile().getAbsolutePath(), "deploy"};

		BladeTest bl = new BladeTest();

		bl.run(args);

		PowerMock.verifyAll();
	}

	@Rule
	public final PowerMockRule rule = new PowerMockRule();

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _createFile(String fileName) throws IOException {
		final File testDir = temporaryFolder.newFolder();

		final File war = new File(testDir, fileName);

		Assert.assertTrue(war.createNewFile());

		return war;
	}

}