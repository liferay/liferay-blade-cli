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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Vernon Singleton
 * @author Gregory Amerson
 */
public class UpdateCommandTest {

	@Before
	public void setUp() throws Exception {
		_rootDir = temporaryFolder.getRoot();

		_extensionsDir = temporaryFolder.newFolder(".blade", "extensions");
	}

	@Test
	public void testCurrentMajorLessThanUpdatedMajor() {
		String currentVersion = "1.5.9.1.2.3.4.5.6.7.8.9";
		String updatedVersion = "2.1.1.4.5.6-snapshot";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentMajorMoreThanUpdatedMajor() {
		String currentVersion = "3.0.0.2018.10.23.1234";
		String updatedVersion = "2.5.9-SNAPSHOT";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentMinorLessThanUpdatedMinor() {
		String currentVersion = "12.1.9.SCHWIBBY";
		String updatedVersion = "12.2.1-snapshot";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentMinorMoreThanUpdatedMinor() {
		String currentVersion = "3.6.0.001810231234";
		String updatedVersion = "3.5.9-SCHNAPS";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentPatchLessThanUpdatedPatch() {
		String currentVersion = "123.10.10.SCHOOBY";
		String updatedVersion = "123.10.20-whiff";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentPatchMoreThanUpdatedPatch() {
		String currentVersion = "3.5.9.001810231234";
		String updatedVersion = "3.5.8.999999";

		Assert.assertFalse(
			"currentVersion = " + currentVersion + " should NOT be updated to " + updatedVersion,
			UpdateCommand.shouldUpdate(currentVersion, updatedVersion));
	}

	@Test
	public void testCurrentSnapshot() {
		String currentVersion = "3.4.0.SNAPSHOT201812060746";
		String updatedVersion = "3.4.0-20181206.074623-13";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be the latest snapshot " + updatedVersion +
				" but it is not",
			UpdateCommand.equal(currentVersion, updatedVersion));
	}

	@Ignore
	@Test
	public void testGetUpdateVersionReleases() throws IOException {
		String updateVersion = UpdateCommand.getUpdateVersion(false);

		Assert.assertNotNull(updateVersion);

		Assert.assertFalse("updateVersion does not look right.", updateVersion.isEmpty());
	}

	@Ignore
	@Test
	public void testTargetReleases() throws IOException {

		// assuming target is in releases and available

		UpdateArgs updateArgs = new UpdateArgs();

		updateArgs.setSnapshots(false);

		String url = UpdateCommand.getUpdateJarUrl(updateArgs);

		// expect: valid update url from the releases repo

		boolean ok = false;

		if (url.length() > 1) {
			ok = true;
		}

		Assert.assertTrue("url = " + url + " ... this does not look right.", ok);
		Assert.assertTrue("url is not from releases repo.  url = " + url, url.contains("liferay-public-releases/"));
	}

	@Test
	public void testTargetSnapshots() throws IOException {

		// assuming target is in snapshots and available

		UpdateArgs updateArgs = new UpdateArgs();

		updateArgs.setSnapshots(true);

		String url = UpdateCommand.getUpdateJarUrl(updateArgs);

		// expect: valid update url from the snapshots repo

		boolean ok = false;

		if (url.length() > 1) {
			ok = true;
		}

		Assert.assertTrue("url = " + url + " ... this does not look right.", ok);
		Assert.assertTrue("url is not from snapshots repo.  url = " + url, url.contains("liferay-public-snapshots/"));
	}

	@Test
	public void testTwoSnapshotVersions() {
		String currentVersion = "3.3.1.SNAPSHOT201811211846";
		String updateVersion = "3.3.1-20181128.214621-308";

		Assert.assertTrue(
			"currentVersion = " + currentVersion + " should be updated to " + updateVersion,
			UpdateCommand.shouldUpdate(currentVersion, updateVersion));
	}

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _extensionsDir = null;
	private File _rootDir = null;

}