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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Gregory Amerson
 */
public class UpdateCommand extends BaseCommand<UpdateArgs> {

	public static final String BASE_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/repositories/";

	public static final String BLADE_CLI_CONTEXT = "com/liferay/blade/com.liferay.blade.cli/";

	public static final String RELEASES_REPO_URL = BASE_CDN_URL + "liferay-public-releases/" + BLADE_CLI_CONTEXT;

	public static final String SNAPSHOTS_REPO_URL = BASE_CDN_URL + "liferay-public-snapshots/" + BLADE_CLI_CONTEXT;

	public static boolean equal(String currentVersion, String updateVersion) {
		boolean equal = false;

		Matcher matcher = _pattern.matcher(currentVersion);

		matcher.find();

		String currentMajor = matcher.group(1);
		String currentMinor = matcher.group(2);
		String currentPatch = matcher.group(3);

		matcher = _pattern.matcher(updateVersion);

		matcher.find();

		String updateMajor = matcher.group(1);
		String updateMinor = matcher.group(2);
		String updatePatch = matcher.group(3);

		if (Integer.parseInt(updateMajor) == Integer.parseInt(currentMajor)) {
			if (Integer.parseInt(updateMinor) == Integer.parseInt(currentMinor)) {
				if (Integer.parseInt(updatePatch) == Integer.parseInt(currentPatch)) {
					equal = true;
				}
			}
		}

		return equal;
	}

	public static String getUpdateJarUrl(boolean snapshots) throws IOException {
		String url = RELEASES_REPO_URL;

		if (snapshots) {
			url = SNAPSHOTS_REPO_URL;
		}

		if (hasUpdateUrlFromBladeDir()) {
			url = getUpdateUrlFromBladeDir();
		}

		String version = "";

		String nextUrl = "";

		String jarUrl = "";

		Document versionsDocument;

		Connection connection = Jsoup.connect(url);

		versionsDocument = connection.get();

		Elements tdOrPreElements = versionsDocument.select("td,pre");

		for (Element potentialVersion : tdOrPreElements.select("a")) {
			String bladeDir;

			boolean prependUrl = false;

			String href = potentialVersion.attr("href");

			if (href.contains(url)) {
				String hrefSubstring = href.substring(url .length());

				bladeDir = hrefSubstring.replaceAll("/", "");
			}
			else {
				bladeDir = href.replaceAll("/", "");

				prependUrl = true;
			}

			if (bladeDir.matches("\\d+\\..*")) {
				if (prependUrl) {
					nextUrl = url + potentialVersion.attr("href");
				}
				else {
					nextUrl = potentialVersion.attr("href");
				}
			}
		}

		if ("".equals(nextUrl)) {
			throw new IOException("No directory found at url = " + url);
		}

		version = "nextUrl = " + nextUrl + "\n";

		connection = Jsoup.connect(nextUrl);

		Document jarsDocument = connection.get();

		tdOrPreElements = jarsDocument.select("td,pre");

		for (Element potentialJar : tdOrPreElements.select("a")) {
			String bladeJar;

			boolean prependUrl = false;

			String href = potentialJar.attr("href");

			if (href.contains(nextUrl)) {
				String hrefSubstring = href.substring(nextUrl .length());

				bladeJar = hrefSubstring.replaceAll("/", "");
			}
			else {
				bladeJar = href.replaceAll("/", "");

				prependUrl = true;
			}

			version = version + "\na jar = " + potentialJar.attr("href");

			if (bladeJar.matches(".*\\.jar")) {
				if (prependUrl) {
					jarUrl = nextUrl + potentialJar.attr("href");
				}
				else {
					jarUrl = potentialJar.attr("href");
				}
			}
		}

		if ("".equals(jarUrl)) {
			throw new IOException("No jar found at nextUrl = " + nextUrl);
		}

		return jarUrl;
	}

	public static String getUpdateUrlFromBladeDir() {
		String url = "no url";

		final File updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");

		if (hasUpdateUrlFromBladeDir()) {
			List<String> lines;

			try {
				lines = Files.readAllLines(Paths.get(updateUrlFile.getPath()), StandardCharsets.UTF_8);

				url = lines.get(0);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return url;
	}

	public static String getUpdateVersion(boolean snapshots) throws IOException {
		String url = RELEASES_REPO_URL;

		if (snapshots) {
			url = SNAPSHOTS_REPO_URL;
		}

		if (hasUpdateUrlFromBladeDir()) {
			url = getUpdateUrlFromBladeDir();
		}

		String version = "";

		Document versionsDocument;

		Connection connection = Jsoup.connect(url);

		versionsDocument = connection.get();

		Elements tdOrPreElements = versionsDocument.select("td,pre");

		for (Element potentialVersion : tdOrPreElements.select("a")) {
			String bladeDir;

			String href = potentialVersion.attr("href");

			if (href.contains(url)) {
				String hrefSubstring = href.substring(url.length());

				bladeDir = hrefSubstring.replaceAll("/", "");
			}
			else {
				bladeDir = href.replaceAll("/", "");
			}

			if (bladeDir.matches("\\d+\\..*")) {
				version = bladeDir;
			}
		}

		return version;
	}

	public static boolean hasUpdateUrlFromBladeDir() {
		boolean hasUpdate = false;

		final File updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");

		if (updateUrlFile.exists() && updateUrlFile.isFile()) {
			if (updateUrlFile.length() > 0) {
				hasUpdate = true;
			}
		}

		return hasUpdate;
	}

	public static boolean shouldUpdate(String currentVersion, String updateVersion) {
		boolean should = false;

		Matcher matcher = _pattern.matcher(currentVersion);

		matcher.find();

		String currentMajor = matcher.group(1);
		String currentMinor = matcher.group(2);
		String currentPatch = matcher.group(3);

		matcher = _pattern.matcher(updateVersion);

		matcher.find();

		String updateMajor = matcher.group(1);
		String updateMinor = matcher.group(2);
		String updatePatch = matcher.group(3);

		if (Integer.parseInt(updateMajor) > Integer.parseInt(currentMajor)) {
			should = true;
		}
		else {
			if (Integer.parseInt(updateMajor) < Integer.parseInt(currentMajor)) {
				should = false;
			}
			else {
				if (Integer.parseInt(updateMinor) > Integer.parseInt(currentMinor)) {
					should = true;
				}
				else {
					if (Integer.parseInt(updateMinor) < Integer.parseInt(currentMinor)) {
						should = false;
					}
					else {
						if (Integer.parseInt(updatePatch) > Integer.parseInt(currentPatch)) {
							should = true;
						}
					}
				}
			}
		}

		return should;
	}

	public UpdateCommand() {
	}

	public UpdateCommand(BladeCLI bladeCLI) {
		setBlade(bladeCLI);
	}

	@Override
	public void execute() {
		BladeCLI bladeCLI = getBladeCLI();

		UpdateArgs updateArgs = getArgs();

		// String oldUrl = "https://releases.liferay.com/tools/blade-cli/latest/blade.jar";

		String url = "";

		String currentVersion = "0.0.0.0";

		boolean snapshots = updateArgs.isSnapshots();

		String updateVersion = "";

		try {
			updateVersion = getUpdateVersion(snapshots);

			try {
				currentVersion = VersionCommand.getBladeCLIVersion();

				bladeCLI.out("Current version is " + currentVersion);
			}
			catch (IOException ioe) {
				bladeCLI.out("Current blade.jar contains no manifest.");
				bladeCLI.out("Assuming blade.jar should be updated.");
			}

			boolean shouldUpdate = shouldUpdate(currentVersion, updateVersion);

			if (currentVersion.contains("SNAPSHOT")) {
				if (snapshots) {
					shouldUpdate = true;
					bladeCLI.out("Updating from the snapshots repository.");
				}
				else {
					if (equal(currentVersion, updateVersion)) {
						shouldUpdate = true;
					}
				}
			}

			if (currentVersion.contains("SNAPSHOT")) {
				if (!snapshots) {
					if (shouldUpdate) {
						bladeCLI.out("Updating from a snapshot to the newest released version.");
					}
				}
			}

			url = getUpdateJarUrl(snapshots);

			if (shouldUpdate) {
				bladeCLI.out("Updating to: " + url);

				if (BladeUtil.isWindows()) {
					bladeCLI.out(
						"blade update cannot execute successfully because of Windows file locking.  Please use the " +
							"following command:");
					bladeCLI.out("\tjpm install -f " + url);
				}
				else {
					BaseArgs baseArgs = bladeCLI.getBladeArgs();

					File baseDir = new File(baseArgs.getBase());

					try {

						// bladeCLI.out(
						// 	"Updating blade from " + currentVersion + " to the latest version, " + updateVersion +
						// 		", using " + url);

						Process process = BladeUtil.startProcess("jpm install -f " + url, baseDir);

						int errCode = process.waitFor();

						if (errCode == 0) {
							bladeCLI.out("Update completed successfully");
						}
						else {
							bladeCLI.error("blade exited with code: " + errCode);
						}
					}
					catch (Exception e) {
						bladeCLI.error("Problem running jpm install.");
						bladeCLI.error(e);
					}
				}
			}
			else {
				if (snapshots) {
					bladeCLI.out(
						"The current version of blade, " + currentVersion + ", is higher than the latest version, " +
							updateVersion + ", at " + SNAPSHOTS_REPO_URL);
				}
				else {
					if (equal(currentVersion, updateVersion)) {
						bladeCLI.out("Current version, " + currentVersion + ", is the latest released version.");
					}
					else {

						// This should never happen, but in case it does.

						bladeCLI.out(
							"The current version of blade, " + currentVersion +
								", is higher than the latest version, " + updateVersion + ", at " + RELEASES_REPO_URL);
						bladeCLI.out("Not updating since downgrades are not supported at this time.");

						bladeCLI.out("If you want to force a downgrade, you could use the following command:");
						bladeCLI.out("\tjpm install -f " + url);
					}
				}
			}
		}
		catch (IOException ioe) {
			if (snapshots) {
				bladeCLI.out("No jar is available from " + SNAPSHOTS_REPO_URL);
			}
			else {
				bladeCLI.out("No jar is available from " + RELEASES_REPO_URL);
			}

			bladeCLI.out("Not updating since no jar is available.");
		}
	}

	@Override
	public Class<UpdateArgs> getArgsClass() {
		return UpdateArgs.class;
	}

	private static final Pattern _pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}