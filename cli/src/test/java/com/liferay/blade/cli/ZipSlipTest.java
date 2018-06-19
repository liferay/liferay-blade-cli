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

import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

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
@PrepareForTest({ZipFile.class, BladeUtil.class})
@RunWith(PowerMockRunner.class)
public class ZipSlipTest {

	@Test
	public void testNonZipSlip() throws Exception {
		_testZip("foo.txt");
	}

	@Test(expected = ZipException.class)
	public void testZipSlip() throws Exception {
		_testZip("../../foo.txt");
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testZip(String fileName) throws Exception {
		ZipFile zip = PowerMock.createMock(ZipFile.class);

		zip.close();

		EasyMock.expectLastCall().andVoid();

		InputStream in = EasyMock.createNiceMock(InputStream.class);

		in.read(EasyMock.isA(byte[].class));

		EasyMock.expectLastCall().andReturn(-1);

		EasyMock.replay(in);

		EasyMock.expect(zip.getInputStream(EasyMock.isA(ZipEntry.class))).andStubReturn(in);

		ZipEntry e = EasyMock.createNiceMock(ZipEntry.class);

		EasyMock.expect(e.getName()).andStubReturn(fileName);

		EasyMock.replay(e);

		Vector<ZipEntry> vector = new Vector<>();

		vector.add(e);

		Enumeration<? extends ZipEntry> entries = vector.elements();

		zip.entries();

		EasyMock.expectLastCall().andStubReturn(entries);

		IExpectationSetters<ZipFile> expectation = PowerMock.expectNew(
			ZipFile.class, new Class<?>[] {File.class}, EasyMock.isA(File.class));

		expectation.andStubReturn(zip);

		FileOutputStream out = PowerMock.createNiceMock(FileOutputStream.class);

		PowerMock.expectNew(
			FileOutputStream.class, new Class<?>[] {File.class}, EasyMock.isA(File.class)).andStubReturn(out);

		PowerMock.replayAll();

		File testFile = new File(temporaryFolder.getRoot(), "test.zip");

		File testDir = temporaryFolder.newFolder("a", "b", "c", "d", "e");

		BladeUtil.unzip(testFile, testDir);
	}

}