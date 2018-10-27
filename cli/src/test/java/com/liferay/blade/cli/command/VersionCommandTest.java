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

import com.liferay.blade.cli.BladeTestResults;
import com.liferay.blade.cli.TestUtil;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Vernon Singleton
 */
public class VersionCommandTest {

	@Test
	public void testBladeMajorLessThanUpdateMajor() {
		boolean ok;

		String bladeVersion = "1.5.9.1.2.3.4.5.6.7.8.9";
		String updateVersion = "2.1.1.4.5.6-snapshot";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertTrue(
			"bladeVersion = " + bladeVersion + " should be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladeMajorMoreThanUpdateMajor() {
		boolean ok;

		String bladeVersion = "3.0.0.2018.10.23.1234";
		String updateVersion = "2.5.9-SNAPSHOT";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertFalse(
			"bladeVersion = " + bladeVersion + " should NOT be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladeMinorLessThanUpdateMinor() {
		boolean ok;

		String bladeVersion = "12.1.9.SCHWIBBY";
		String updateVersion = "12.2.1-snapshot";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertTrue(
			"bladeVersion = " + bladeVersion + " should be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladeMinorMoreThanUpdateMinor() {
		boolean ok;

		String bladeVersion = "3.6.0.001810231234";
		String updateVersion = "3.5.9-SCHNAPS";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertFalse(
			"bladeVersion = " + bladeVersion + " should NOT be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladePatchLessThanUpdatePatch() {
		boolean ok;

		String bladeVersion = "123.10.10.SCHOOBY";
		String updateVersion = "123.10.20-whiff";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertTrue(
			"bladeVersion = " + bladeVersion + " should be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladePatchMoreThanUpdatePatch() {
		boolean ok;

		String bladeVersion = "3.5.9.001810231234";
		String updateVersion = "3.5.8.999999";

		ok = BladeUtil.shouldUpdate(bladeVersion, updateVersion);

		Assert.assertFalse(
			"bladeVersion = " + bladeVersion + " should NOT be updated to " + updateVersion + " but ok = " + ok, ok);
	}

	@Test
	public void testBladeVersionWithManifest() throws InterruptedException, IOException {
		boolean ok;

		Process ps = Runtime.getRuntime().exec(new String[] {"java", "-jar", "build/libs/blade.jar", "version"});

		ps.waitFor();

		InputStream is = ps.getInputStream();

		byte[] b = new byte[is.available()];

		is.read(b, 0, b.length);

		String version = new String(b);

		try (PrintWriter out = new PrintWriter("out0.txt")) {
			out.println("testBladeVersionWithManifest: version = " + version);
		}

		if (version.length() > 1) {
			ok = true;
		}
		else {
			ok = false;
		}

		Assert.assertTrue("version = " + version + " ... this does not look right.", ok);
	}

	@Test
	public void testBladeVersionWithNoManifest() throws Exception {
		BladeTestResults bladeTestResults = TestUtil.runBlade(false, "version");

		String errors = bladeTestResults.getErrors();

		String expectedErrorMessage = "Could not locate version.";

		Assert.assertTrue(
			"Expected error message '" + expectedErrorMessage + "' was not returned",
			errors.contains(expectedErrorMessage));
	}

	// This test should pass once blade is in the liferay-public-releases context of nexus

	@Ignore
	@Test
	public void testGetUpdateJarUrl() throws IOException {
		boolean ok;

		// use liferay-public-releases context

		String jarUrl = BladeUtil.getUpdateJarUrl(false);

		try (PrintWriter out = new PrintWriter("out3.txt")) {
			out.println("testGetUpdateJarUrl: jarUrl = " + jarUrl);
		}

		if (jarUrl.length() > 1) {
			ok = true;
		}
		else {
			ok = false;
		}

		Assert.assertTrue("jarUrl = " + jarUrl + " ... this does not look right.", ok);
	}

	@Test
	public void testGetUpdateJarUrlFromSnapshots() throws IOException {
		boolean ok;

		// use liferay-public-snapshots context

		String jarUrl = BladeUtil.getUpdateJarUrl(true);

		try (PrintWriter out = new PrintWriter("out1.txt")) {
			out.println("testGetUpdateSnapshotVersion: jarUrl = " + jarUrl);
		}

		if (jarUrl.length() > 1) {
			ok = true;
		}
		else {
			ok = false;
		}

		Assert.assertTrue("jarUrl = " + jarUrl + " ... this does not look right.", ok);
	}

	@Test
	public void testGetUpdateJarUrlFromUrlInBladeDir() throws IOException {
		boolean ok;

		if (BladeUtil.hasUpdateUrlFromBladeDir()) {
			String jarUrl = BladeUtil.getUpdateJarUrl(false);

			try (PrintWriter out = new PrintWriter("out2.txt")) {
				out.println("testGetUpdateUsingUrlFromBladeDirVersion: jarUrl = " + jarUrl);
			}

			if (jarUrl.length() > 1) {
				ok = true;
			}
			else {
				ok = false;
			}

			Assert.assertTrue("jarUrl = " + jarUrl + " ... this does not look right.", ok);
		}
	}

	// This test should pass once blade is in the liferay-public-releases context of nexus

	@Ignore
	@Test
	public void testGetUpdateVersion() throws IOException {
		boolean ok;

		// use liferay-public-releases context

		String version = BladeUtil.getUpdateVersion(false);

		try (PrintWriter out = new PrintWriter("out3.txt")) {
			out.println("testGetUpdateVersion: version = " + version);
		}

		if (version.length() > 1) {
			ok = true;
		}
		else {
			ok = false;
		}

		Assert.assertTrue("version = " + version + " ... this does not look right.", ok);
	}

}