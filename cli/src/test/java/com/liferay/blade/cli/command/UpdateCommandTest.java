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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Vernon Singleton
 * @author Gregory Amerson
 */
public class UpdateCommandTest {

	@Test
	public void testBladeMinorMoreThanUpdateMinor() {
		String currentVersion = "3.6.0.001810231234";
		String updatedVersion = "3.5.9-SCHNAPS";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testBladePatchLessThanUpdatePatch() {
		String currentVersion = "123.10.10.SCHOOBY";
		String updatedVersion = "123.10.20-whiff";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testBladePatchMoreThanUpdatePatch() {
		String currentVersion = "3.5.9.001810231234";
		String updatedVersion = "3.5.8.999999";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testBladeVersionWithManifest() throws InterruptedException, IOException {
		boolean ok;

		Process ps = Runtime.getRuntime().exec(new String[] {"java", "-jar", "build/libs/blade.jar"});

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

		String expectedErrorMessage = "Could not determine version.";

		Assert.assertEquals(expectedErrorMessage, errors.trim());
	}

	@Ignore
	@Test
	public void testGetUpdateJarUrl() throws IOException {
		boolean ok;

		// use liferay-public-releases context

		String jarUrl = UpdateCommand.getUpdateJarUrl(false);

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

		String jarUrl = UpdateCommand.getUpdateJarUrl(true);

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

		if (UpdateCommand.hasUpdateUrlFromBladeDir()) {
			String jarUrl = UpdateCommand.getUpdateJarUrl(false);

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

		String version = UpdateCommand.getUpdateVersion(false);

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

	@Test
	public void testUpdateVersioneMajorLessThanUpdateMajor() {
		String currentVersion = "1.5.9.1.2.3.4.5.6.7.8.9";
		String updatedVersion = "2.1.1.4.5.6-snapshot";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testUpdateVersionMajorMoreThanUpdateMajor() {
		String currentVersion = "3.0.0.2018.10.23.1234";
		String updatedVersion = "2.5.9-SNAPSHOT";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	// This test should pass once blade is in the liferay-public-releases context of nexus

	@Test
	public void testUpdateVersionMinorLessThanUpdateMinor() {
		String currentVersion = "12.1.9.SCHWIBBY";
		String updatedVersion = "12.2.1-snapshot";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

}