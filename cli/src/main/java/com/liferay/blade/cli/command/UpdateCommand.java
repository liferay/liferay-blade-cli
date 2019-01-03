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
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
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

		if (currentVersion.contains("SNAPSHOT")) {
			if (updateVersion.contains("-")) {
				matcher = _bladeSnapshotPattern.matcher(currentVersion);

				matcher.find();

				Long currentSnapshot = Long.parseLong(matcher.group(4));

				matcher = _nexusSnapshotPattern.matcher(updateVersion);

				matcher.find();

				Long updateSnapshot = Long.parseLong(matcher.group(4) + matcher.group(5));

				if (updateSnapshot > currentSnapshot) {
					return false;
				}

				if (updateSnapshot < currentSnapshot) {
					return false;
				}

				if (updateSnapshot == currentSnapshot) {
					return true;
				}
			}
		}

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

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Element lastVersionElement = versionElements.last();

		String version = lastVersionElement.text();

		if (snapshots) {
			connection.url(url + "/" + version + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			String snapshotVersion = valueElement.text();

			return url + "/" + version + "/com.liferay.blade.cli-" + snapshotVersion + ".jar";
		}
		else {
			return url + "/" + version + "/com.liferay.blade.cli-" + version + ".jar";
		}
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

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Element lastVersion = versionElements.last();

		String updateVersion = null;

		if (snapshots) {
			connection.url(url + lastVersion.text() + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			updateVersion = valueElement.text();
		}
		else {
			updateVersion = lastVersion.text();
		}

		return updateVersion;
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

		if (currentVersion.contains("SNAPSHOT")) {
			if (updateVersion.contains("-")) {
				matcher = _bladeSnapshotPattern.matcher(currentVersion);

				matcher.find();

				Long currentSnapshot = Long.parseLong(matcher.group(4));

				matcher = _nexusSnapshotPattern.matcher(updateVersion);

				matcher.find();

				Long updateSnapshot = Long.parseLong(matcher.group(4) + matcher.group(5));

				if (updateSnapshot > currentSnapshot) {
					return true;
				}
			}
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

		boolean toSnapshots = updateArgs.isSnapshots();

		String updateVersion = "";

		try {
			updateVersion = getUpdateVersion(toSnapshots);

			try {
				currentVersion = VersionCommand.getBladeCLIVersion();

				bladeCLI.out("Current blade version " + currentVersion);
			}
			catch (IOException ioe) {
				bladeCLI.error("Could not determine current blade version, continuing with update.");
			}

			boolean shouldUpdate = shouldUpdate(currentVersion, updateVersion);

			if (currentVersion.contains("SNAPSHOT")) {
				if (toSnapshots) {
					shouldUpdate = true;

					bladeCLI.out("Current version is a SNAPSHOT version. Updating from the snapshots repository.");
				}
				else {
					if (equal(currentVersion, updateVersion)) {
						shouldUpdate = true;
					}
				}
			}

			if (currentVersion.contains("SNAPSHOT")) {
				if (!toSnapshots) {
					if (shouldUpdate) {
						bladeCLI.out("Updating from a snapshot to the newest released version.");
					}
				}
			}

			String url = getUpdateJarUrl(toSnapshots);

			if (shouldUpdate) {
				bladeCLI.out("Updating from: " + url);

				if (BladeUtil.isWindows()) {
					Path batPath = Files.createTempFile("jpm_install", ".bat");

					StringBuilder sb = new StringBuilder();

					ClassLoader classLoader = UpdateCommand.class.getClassLoader();

					try (InputStream inputStream = classLoader.getResourceAsStream("jpm_install.bat");
						Scanner scanner = new Scanner(inputStream)) {

						while (scanner.hasNextLine()) {
							String line = scanner.nextLine();

							if (line.contains("%s")) {
								line = String.format(line, url);
							}

							sb.append(line);

							sb.append(System.lineSeparator());
						}
					}

					String batContents = sb.toString();

					Files.write(batPath, batContents.getBytes());

					Runtime runtime = Runtime.getRuntime();

					runtime.exec("cmd /c start \"\" \"" + batPath + "\"");
				}
				else {
					BaseArgs args = bladeCLI.getArgs();

					File baseDir = new File(args.getBase());

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
				if (toSnapshots) {
					if (currentVersion.contains("SNAPSHOT")) {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" is greater than the latest snapshot version " + updateVersion);
					}
					else {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" (released) is greater than the latest snapshot version " + updateVersion);
					}
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

	private static final Pattern _bladeSnapshotPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).SNAPSHOT(\\d+)");
	private static final Pattern _nexusSnapshotPattern = Pattern.compile(
		"(\\d+)\\.(\\d+)\\.(\\d+)-(\\d+)\\.(\\d\\d\\d\\d)\\d\\d-\\d+");
	private static final File _updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");
	private static final Pattern _versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}