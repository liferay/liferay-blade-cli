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

import static com.liferay.blade.cli.MockUtil.*;

import static org.junit.Assert.assertTrue;

import aQute.bnd.osgi.Domain;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.easymock.EasyMock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.osgi.framework.dto.BundleDTO;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Christopher Bryan Boyd
 */
@PrepareForTest(
	{Domain.class, GradleTooling.class, LiferayBundleDeployer.class, GradleExec.class, BladeCLI.class, BladeNoFail.class, Util.class, DeployCommand.class}
)
@RunWith(PowerMockRunner.class)
public class DeployCommandTest {

	@Test
	public void testInstallExistingJar() throws Exception {
		Collection<BundleDTO> bundles = Collections.emptyList();

		final AtomicLong atomicLong = new AtomicLong(1);

		File jar = createFile("test.jar");

		stubGradleExec();

		stubDomain(true, false);

		stubUtil();

		stubGradleTooling(jar);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(client.getBundleId(EasyMock.eq(bundles), EasyMock.anyString())).andAnswer(() ->
		{
			return atomicLong.get();
		}).atLeastOnce();
		EasyMock.expect(client.getBundleId(EasyMock.anyString())).andAnswer(() ->
		{
			return atomicLong.get();
		}).atLeastOnce();

		EasyMock.expect(client.getBundles()).andReturn(bundles).atLeastOnce();

		EasyMock.expect(client.install(EasyMock.eq(jar.toPath().toUri()))).andAnswer(() -> {
			return atomicLong.incrementAndGet();

		}).once();

		client.reloadBundle(EasyMock.anyLong(), EasyMock.eq(jar.toPath().toUri()));

		EasyMock.expectLastCall().andVoid().once();

		EasyMock.replay(client);
		
		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(LiferayBundleDeployer._getDefault(EasyMock.anyString(), EasyMock.anyInt()))
		.andReturn(client).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"-b", jar.getParentFile().getAbsolutePath(), "deploy"};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream ps = new PrintStream(baos);

		BladeNoFail bl = new BladeNoFail(ps);

		bl.run(args);

		String content = baos.toString();

		PowerMock.verifyAll();

		assertTrue(content.contains(String.format("Updated bundle %s", atomicLong.get())));
	}

	@Test
	public void testInstallJar() throws Exception {
		Collection<BundleDTO> bundles = Collections.emptyList();

		final AtomicLong atomicLong = new AtomicLong(1);

		File jar = createFile("test.jar");

		stubGradleExec();

		stubDomain(true, false);

		stubUtil();

		stubGradleTooling(jar);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(client.getBundleId(EasyMock.anyString())).andAnswer(atomicLong::get).atLeastOnce();

		EasyMock.expect(client.getBundles()).andReturn(bundles).atLeastOnce();

		EasyMock.expect(client.install(jar.toPath().toUri())).andAnswer(() -> {
			return atomicLong.incrementAndGet();

		}).once();

		client.start(EasyMock.eq(atomicLong.get()));

		EasyMock.expectLastCall().andVoid().once();

		EasyMock.replay(client);

		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(LiferayBundleDeployer._getDefault(EasyMock.anyString(), EasyMock.anyInt()))
		.andReturn(client).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"-b", jar.getParentFile().getAbsolutePath(), "deploy"};

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream ps = new PrintStream(baos);

		BladeNoFail bl = new BladeNoFail(ps);

		bl.run(args);

		String content = baos.toString();

		PowerMock.verifyAll();

		assertTrue(content.contains(String.format("Installed bundle %s", atomicLong.get())));
	}

	@Test
	public void testInstallWar() throws Exception {
		File war = createFile("test.war");

		stubGradleExec();

		stubDomain(false, false);

		stubUtil();

		stubGradleTooling(war);

		LiferayBundleDeployer client = EasyMock.createNiceMock(LiferayBundleDeployer.class);

		EasyMock.expect(client.install(EasyMock.eq(war.toURI()))).andReturn(1L).once();

		client.start(1);

		EasyMock.expectLastCall().andVoid().once();

		EasyMock.replay(client);

		PowerMock.mockStatic(LiferayBundleDeployer.class);

		EasyMock.expect(LiferayBundleDeployer._getDefault(EasyMock.anyString(), EasyMock.anyInt()))
		.andReturn(client).once();

		PowerMock.replay(LiferayBundleDeployer.class);

		String[] args = {"-b", war.getParentFile().getAbsolutePath(), "deploy"};

		BladeNoFail bl = new BladeNoFail();

		bl.run(args);

		PowerMock.verifyAll();
	}

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	private File createFile(String fileName) throws IOException {
		final File testDir = tempFolder.newFolder();

		final File war = new File(testDir, fileName);

		assertTrue(war.createNewFile());

		return war;
	}

}