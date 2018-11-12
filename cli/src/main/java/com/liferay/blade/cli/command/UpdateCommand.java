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

import aQute.bnd.version.Version;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

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

	public static boolean equal(String currentVersion, String updateVersion) {
		Matcher matcher = _versionPattern.matcher(currentVersion);

		matcher.find();

		int currentMajor = Integer.parseInt(matcher.group(1));
		int currentMinor = Integer.parseInt(matcher.group(2));
		int currentPatch = Integer.parseInt(matcher.group(3));

		Version currentSemver = new Version(currentMajor, currentMinor, currentPatch);

		matcher = _versionPattern.matcher(updateVersion);

		matcher.find();

		int updateMajor = Integer.parseInt(matcher.group(1));
		int updateMinor = Integer.parseInt(matcher.group(2));
		int updatePatch = Integer.parseInt(matcher.group(3));

		Version updateSemver = new Version(updateMajor, updateMinor, updatePatch);

		return currentSemver.equals(updateSemver);
	}

	public static String getUpdateJarUrl(boolean snapshots) throws IOException {
		String url = _RELEASES_REPO_URL;

		if (snapshots) {
			url = _SNAPSHOTS_REPO_URL;
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

		if (hasUpdateUrlFromBladeDir()) {
			List<String> lines;

			try {
				lines = Files.readAllLines(Paths.get(_updateUrlFile.toURI()));

				url = lines.get(0);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return url;
	}

	public static String getUpdateVersion(boolean snapshots) throws IOException {
		String url = _RELEASES_REPO_URL;

		if (snapshots) {
			url = _SNAPSHOTS_REPO_URL;
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

		if (_updateUrlFile.exists() && _updateUrlFile.isFile()) {
			if (_updateUrlFile.length() > 0) {
				hasUpdate = true;
			}
		}

		return hasUpdate;
	}

	public static boolean shouldUpdate(String currentVersion, String updateVersion) {
		Matcher matcher = _versionPattern.matcher(currentVersion);

		matcher.find();

		int currentMajor = Integer.parseInt(matcher.group(1));
		int currentMinor = Integer.parseInt(matcher.group(2));
		int currentPatch = Integer.parseInt(matcher.group(3));

		Version currentSemver = new Version(currentMajor, currentMinor, currentPatch);

		matcher = _versionPattern.matcher(updateVersion);

		matcher.find();

		int updateMajor = Integer.parseInt(matcher.group(1));
		int updateMinor = Integer.parseInt(matcher.group(2));
		int updatePatch = Integer.parseInt(matcher.group(3));

		Version updateSemver = new Version(updateMajor, updateMinor, updatePatch);

		if (updateSemver.compareTo(currentSemver) > 0) {
			return true;
		}

		return false;
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

		String currentVersion = "0.0.0.0";

		boolean snapshots = updateArgs.isSnapshots();

		String updateVersion = "";

		try {
			updateVersion = getUpdateVersion(snapshots);

			try {
				currentVersion = VersionCommand.getBladeCLIVersion();

				bladeCLI.out("Current blade version " + currentVersion);
			}
			catch (IOException ioe) {
				bladeCLI.error("Could not determine current blade version, continuing with update.");
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

			String url = getUpdateJarUrl(snapshots);

			if (shouldUpdate) {
				bladeCLI.out("Updating from: " + url);

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
						Process process = BladeUtil.startProcess("jpm install -f " + url, baseDir);

						int errCode = process.waitFor();

						if (errCode == 0) {
							bladeCLI.out("Update completed successfully.");
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
						"Current blade version " + currentVersion + " is greater than the latest version " +
							updateVersion);
				}
				else {
					if (equal(currentVersion, updateVersion)) {
						bladeCLI.out("Current blade version " + currentVersion + " is the latest released version.");
					}
					else {
						bladeCLI.out(
							"Current blade version " + currentVersion + " is higher than the latest version " +
								updateVersion);
						bladeCLI.out("Not updating, since downgrades are not supported at this time.");
						bladeCLI.out("If you want to force a downgrade, use the following command:");
						bladeCLI.out("\tjpm install -f " + url);
					}
				}
			}
		}
		catch (IOException ioe) {
			bladeCLI.error("Could not determine if blade update is available.");

			if (updateArgs.isTrace()) {
				PrintStream error = bladeCLI.error();

				ioe.printStackTrace(error);
			}
			else {
				bladeCLI.error("For more information run update with '--trace' option.");
			}
		}
	}

	@Override
	public Class<UpdateArgs> getArgsClass() {
		return UpdateArgs.class;
	}

	private static final String _BASE_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/repositories/";

	private static final String _BLADE_CLI_CONTEXT = "com/liferay/blade/com.liferay.blade.cli/";

	private static final String _RELEASES_REPO_URL = _BASE_CDN_URL + "liferay-public-releases/" + _BLADE_CLI_CONTEXT;

	private static final String _SNAPSHOTS_REPO_URL = _BASE_CDN_URL + "liferay-public-snapshots/" + _BLADE_CLI_CONTEXT;

	private static final File _updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");
	private static final Pattern _versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}