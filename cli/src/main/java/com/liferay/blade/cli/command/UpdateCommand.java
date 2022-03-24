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
import com.liferay.blade.cli.util.BladeVersions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.math.BigInteger;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.MessageDigest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

	public UpdateCommand() {
	}

	public UpdateCommand(BladeCLI bladeCLI) {
		setBlade(bladeCLI);
	}

	@Override
	public void execute() {
		UpdateArgs updateArgs = getArgs();

		if (_hasUpdateUrlFromBladeDir()) {
			try {
				updateArgs.setUrl(new URL(_getUpdateUrlFromBladeDir()));
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		BladeCLI bladeCLI = getBladeCLI();

		String currentVersion = "0.0.0.0";

		Optional<String> updateVersion = Optional.empty();

		boolean checkOnly = updateArgs.isCheckOnly();

		boolean release = updateArgs.isRelease();

		boolean snapshots = updateArgs.isSnapshots();

		Optional<String> updateReleaseVersion = Optional.empty();

		Optional<String> updateSnapshotVersion = Optional.empty();

		String updateUrl = null;

		if (updateArgs.getUrl() != null) {
			updateUrl = String.valueOf(updateArgs.getUrl());
		}

		try {
			BladeVersions versions = _getVersions();

			currentVersion = versions.getCurrentVersion();

			currentVersion = currentVersion.toUpperCase();

			updateSnapshotVersion = versions.getSnapshotUpdateVersion();

			updateReleaseVersion = versions.getReleasedUpdateVersion();

			boolean releaseShouldUpdate = _shouldUpdate(currentVersion, updateReleaseVersion, updateUrl, true);

			boolean snapshotShouldUpdate = _shouldUpdate(currentVersion, updateSnapshotVersion, updateUrl, false);

			boolean shouldUpdate;

			if (snapshots) {
				updateVersion = updateSnapshotVersion;
			}
			else if (release) {
				updateVersion = updateReleaseVersion;
			}
			else if (currentVersion.contains("SNAPSHOT")) {
				updateArgs.setSnapshots(true);

				updateVersion = updateSnapshotVersion;
			}
			else {
				updateArgs.setRelease(true);

				updateVersion = updateReleaseVersion;
			}

			if (Objects.equals(updateVersion, updateReleaseVersion)) {
				shouldUpdate = releaseShouldUpdate;
			}
			else if (Objects.equals(updateVersion, updateSnapshotVersion)) {
				shouldUpdate = snapshotShouldUpdate;
			}
			else {
				shouldUpdate = false;
			}

			if (updateUrl != null) {
				bladeCLI.out("Custom URL specified: " + updateUrl);
			}

			if (checkOnly) {
				String versionTag;

				if (Objects.equals(updateVersion, updateSnapshotVersion)) {
					versionTag = "(snapshot)";
				}
				else if (Objects.equals(updateVersion, updateReleaseVersion)) {
					versionTag = "(release)";
				}
				else {
					versionTag = "(custom)";
				}

				String currentVersionOutput = currentVersion.replace("SNAPSHOT", "");

				bladeCLI.out("Current blade version: " + currentVersionOutput + " " + versionTag);

				if ((updateReleaseVersion == null) || !releaseShouldUpdate) {
					String message = "No new release updates are available for this version of blade.";

					bladeCLI.out(message);
				}
				else if (releaseShouldUpdate) {
					_releaseUpdateVersion = updateReleaseVersion;

					if (updateReleaseVersion.isPresent()) {
						bladeCLI.out("A new release update is available for blade: " + updateReleaseVersion.get());
					}

					if (updateArgs.isRelease() || currentVersion.contains("SNAPSHOT")) {
						bladeCLI.out("Pass the -r flag to 'blade update' to switch to release branch.");
					}
				}

				if ((updateSnapshotVersion == null) || !snapshotShouldUpdate) {
					String message = "No new snapshot updates are available for this version of blade.";

					bladeCLI.out(message);
				}
				else if (snapshotShouldUpdate) {
					_snapshotUpdateVersion = updateSnapshotVersion;

					if (updateSnapshotVersion.isPresent()) {
						bladeCLI.out("A new snapshot update is available for blade: " + updateSnapshotVersion.get());
					}

					if (updateArgs.isSnapshots() && !currentVersion.contains("SNAPSHOT")) {
						bladeCLI.out("Pass the -s flag to 'blade update' to switch to snapshots branch.");
					}
				}

				return;
			}

			String url = _getUpdateJarUrl(updateArgs);

			if (url == null) {
				String message;

				if (updateArgs.isSnapshots()) {
					message = "No new snapshot updates are available for this version of blade.";
				}
				else {
					message = "No new release updates are available for this version of blade.";
				}

				if (updateUrl != null) {
					bladeCLI.out("Custom URL specified: " + updateUrl);
				}

				bladeCLI.out(message);

				return;
			}

			if (shouldUpdate) {
				_performUpdate(url);
			}
			else {
				if (snapshots) {
					if (currentVersion.contains("SNAPSHOT")) {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" is greater than the latest snapshot version.");
					}
					else {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" (released) is greater than the latest snapshot version.");
					}
				}
				else {
					if (_equal(currentVersion, updateVersion) || _doesMD5Match(updateArgs)) {
						String updateVersionValue = updateVersion.get();

						if (updateVersionValue.contains("-")) {
							bladeCLI.out(
								"Current blade version " + currentVersion + " is the latest snapshot version.");

							if (releaseShouldUpdate && updateReleaseVersion.isPresent()) {
								bladeCLI.out(
									"A new release update is available for blade: " + updateReleaseVersion.get());
								bladeCLI.out("Run `blade update -r` to install.");
							}
						}
						else {
							bladeCLI.out(
								"Current blade version " + currentVersion + " is the latest released version.");
						}
					}
					else {
						bladeCLI.out(
							"Current blade version " + currentVersion + " is greater than the latest version.");

						if (releaseShouldUpdate) {
							bladeCLI.out("A new release update is available for blade: " + updateReleaseVersion);
							bladeCLI.out("Run `blade update -r` to install.");
						}
					}
				}
			}
		}
		catch (IOException ioException) {
			bladeCLI.error("Could not determine if blade update is available.");

			if (updateArgs.isTrace()) {
				PrintStream error = bladeCLI.error();

				ioException.printStackTrace(error);
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

	public Optional<String> getReleaseUpdateVersion() {
		return _releaseUpdateVersion;
	}

	public Optional<String> getSnapshotUpdateVersion() {
		return _snapshotUpdateVersion;
	}

	private static boolean _doesMD5Match(String url, boolean snapshot) {
		UpdateArgs updateArgs = new UpdateArgs();

		if (url != null) {
			try {
				updateArgs.setUrl(new URL(url));
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		if (snapshot) {
			updateArgs.setSnapshots(true);
		}
		else {
			updateArgs.setRelease(true);
		}

		return _doesMD5Match(updateArgs);
	}

	private static boolean _doesMD5Match(UpdateArgs updateArgs) {
		try {
			String bladeJarMD5 = _getMD5(BladeUtil.getBladeJarPath());

			String updateJarMD5 = _readTextFileFromURL(_getUpdateJarMD5Url(updateArgs));

			return Objects.equals(updateJarMD5.toUpperCase(), bladeJarMD5);
		}
		catch (Exception exception) {
		}

		return false;
	}

	private static String _getMD5(Path path) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			byte[] data = Files.readAllBytes(path);

			messageDigest.update(data);

			BigInteger md5Int = new BigInteger(1, messageDigest.digest());

			String md5 = md5Int.toString(16);

			return md5.toUpperCase();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static String _getUpdateJarMD5Url(UpdateArgs updateArgs) throws IOException {
		String url = null;

		if (updateArgs.getUrl() != null) {
			url = String.valueOf(updateArgs.getUrl());
		}

		boolean release = updateArgs.isRelease();

		boolean snapshots = updateArgs.isSnapshots();

		String currentVersion = VersionCommand.getBladeCLIVersion();

		if (!release && !snapshots && currentVersion.contains("SNAPSHOT")) {
			snapshots = true;
		}

		if (url == null) {
			if (snapshots) {
				url = _SNAPSHOTS_REPO_URL;
			}
			else if (release) {
				url = _RELEASES_REPO_URL;
			}
		}

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Iterator<Element> iterator = versionElements.iterator();

		Collection<Element> elements = new HashSet<>();

		while (iterator.hasNext()) {
			Element versionElement = iterator.next();

			String nodeString = String.valueOf(versionElement.childNode(0));

			if (nodeString.contains("SNAPSHOT")) {
				if (!snapshots) {
					elements.add(versionElement);
				}
			}
			else {
				if (snapshots) {
					elements.add(versionElement);
				}
			}
		}

		versionElements.removeAll(elements);

		Element lastVersionElement = versionElements.last();

		String version = lastVersionElement.text();

		if (Objects.equals(url, _SNAPSHOTS_REPO_URL) || snapshots) {
			connection.url(url + "/" + version + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			String snapshotVersion = valueElement.text();

			return url + "/" + version + "/com.liferay.blade.cli-" + snapshotVersion + ".jar.md5";
		}

		return url + "/" + version + "/com.liferay.blade.cli-" + version + ".jar.md5";
	}

	private static String _readTextFileFromURL(String urlString) {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(urlString);

			try (Scanner scanner = new Scanner(url.openStream())) {
				while (scanner.hasNextLine()) {
					sb.append(scanner.nextLine() + System.lineSeparator());
				}
			}

			String returnValue = sb.toString();

			return returnValue.trim();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private boolean _equal(String currentVersion, Optional<String> updateVersionOpt) {
		if (!updateVersionOpt.isPresent()) {
			return false;
		}

		String updateVersion = updateVersionOpt.get();

		if (currentVersion.contains("SNAPSHOT") && updateVersion.contains("-")) {
			Long currentSnapshot = _getBladeSnapshotVersion(currentVersion);

			Long updateSnapshot = _getNexusSnapshotVersion(updateVersion);

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

		Version currentSemver = _getVersionObject(currentVersion);

		Version updateSemver = _getVersionObject(updateVersion);

		return currentSemver.equals(updateSemver);
	}

	private Long _getBladeSnapshotVersion(String currentVersion) {
		Matcher matcher = _bladeSnapshotPattern.matcher(currentVersion);

		matcher.find();

		return Long.parseLong(matcher.group(4));
	}

	private Long _getNexusSnapshotVersion(String updateVersion) {
		Matcher matcher = _nexusSnapshotPattern.matcher(updateVersion);

		matcher.find();

		return Long.parseLong(matcher.group(4) + matcher.group(5));
	}

	private String _getUpdateJarUrl(UpdateArgs updateArgs) throws IOException {
		String url = null;

		if (updateArgs.getUrl() != null) {
			url = String.valueOf(updateArgs.getUrl());
		}

		boolean release = updateArgs.isRelease();

		boolean snapshots = updateArgs.isSnapshots();

		String currentVersion = VersionCommand.getBladeCLIVersion();

		if (!release && !snapshots && currentVersion.contains("SNAPSHOT")) {
			snapshots = true;
		}

		if (url == null) {
			if (snapshots) {
				url = _SNAPSHOTS_REPO_URL;
			}
			else if (release) {
				url = _RELEASES_REPO_URL;
			}
		}

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Iterator<Element> iterator = versionElements.iterator();

		Collection<Element> elements = new HashSet<>();

		while (iterator.hasNext()) {
			Element versionElement = iterator.next();

			String nodeString = String.valueOf(versionElement.childNode(0));

			if (nodeString.contains("SNAPSHOT")) {
				if (!snapshots) {
					elements.add(versionElement);
				}
			}
			else {
				if (snapshots) {
					elements.add(versionElement);
				}
			}
		}

		versionElements.removeAll(elements);

		Element lastVersion = versionElements.last();

		if (lastVersion == null) {
			return null;
		}

		String version = lastVersion.text();

		if (Objects.equals(url, _SNAPSHOTS_REPO_URL) || snapshots) {
			connection.url(url + "/" + version + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			String snapshotVersion = valueElement.text();

			return url + "/" + version + "/com.liferay.blade.cli-" + snapshotVersion + ".jar";
		}

		return url + "/" + version + "/com.liferay.blade.cli-" + version + ".jar";
	}

	private String _getUpdateUrlFromBladeDir() {
		String url = "no url";

		if (_hasUpdateUrlFromBladeDir()) {
			List<String> lines;

			try {
				lines = Files.readAllLines(Paths.get(_updateUrlFile.toURI()));

				url = lines.get(0);
			}
			catch (IOException ioException) {
			}
		}

		return url;
	}

	private Optional<String> _getUpdateVersion(boolean snapshotsArg) {
		Optional<String> updateVersion = Optional.empty();

		UpdateArgs updateArgs = getArgs();

		String url = _RELEASES_REPO_URL;

		if (snapshotsArg) {
			url = _SNAPSHOTS_REPO_URL;
		}

		if (_hasUpdateUrlFromBladeDir()) {
			url = _getUpdateUrlFromBladeDir();
		}

		URL urlArg = updateArgs.getUrl();

		if (urlArg != null) {
			url = urlArg.toExternalForm();
		}

		try {
			Connection connection = Jsoup.connect(url + "maven-metadata.xml");

			connection = connection.parser(Parser.xmlParser());

			Document document = connection.get();

			Elements versionElements = document.select("version");

			Iterator<Element> it = versionElements.iterator();

			Collection<Element> elements = new HashSet<>();

			while (it.hasNext()) {
				Element versionElement = it.next();

				String nodeString = String.valueOf(versionElement.childNode(0));

				if (nodeString.contains("SNAPSHOT")) {
					if (!snapshotsArg) {
						elements.add(versionElement);
					}
				}
				else {
					if (snapshotsArg) {
						elements.add(versionElement);
					}
				}
			}

			versionElements.removeAll(elements);

			Element lastVersion = versionElements.last();

			updateVersion = null;

			if (snapshotsArg) {
				if (lastVersion != null) {
					connection.url(url + lastVersion.text() + "/maven-metadata.xml");

					document = connection.get();

					Elements valueElements = document.select("snapshotVersion > value");

					Element valueElement = valueElements.get(0);

					updateVersion = Optional.ofNullable(valueElement.text());
				}
				else {
					return Optional.empty();
				}
			}
			else {
				updateVersion = Optional.ofNullable(
					lastVersion
				).map(
					e -> e.text()
				);
			}
		}
		catch (Exception exception) {
			if (updateArgs.isTrace()) {
				BladeCLI bladeCLI = getBladeCLI();

				bladeCLI.error("Could not get update information from " + url);

				exception.printStackTrace(bladeCLI.error());
			}
		}

		return updateVersion;
	}

	private Version _getVersionObject(String version) {
		Matcher matcher = _versionPattern.matcher(version);

		matcher.find();

		int currentMajor = Integer.parseInt(matcher.group(1));
		int currentMinor = Integer.parseInt(matcher.group(2));
		int currentPatch = Integer.parseInt(matcher.group(3));

		return new Version(currentMajor, currentMinor, currentPatch);
	}

	private BladeVersions _getVersions() {
		String currentVersion = null;

		try {
			currentVersion = VersionCommand.getBladeCLIVersion();
		}
		catch (IOException ioException) {
			System.err.println("Could not determine current blade version, continuing with update.");
		}

		return new BladeVersions(currentVersion, _getUpdateVersion(false), _getUpdateVersion(true));
	}

	private boolean _hasUpdateUrlFromBladeDir() {
		boolean hasUpdate = false;

		if (_updateUrlFile.exists() && _updateUrlFile.isFile() && (_updateUrlFile.length() > 0)) {
			hasUpdate = true;
		}

		return hasUpdate;
	}

	private void _performUpdate(String url) throws IOException {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.out("Updating from: " + url);

		if (BladeUtil.isWindows()) {
			_updateWindows(url);
		}
		else {
			_updateUnix(url);
		}
	}

	private boolean _shouldUpdate(
		String currentVersion, Optional<String> updateVersionOpt, String url, boolean release) {

		if (!updateVersionOpt.isPresent()) {
			return false;
		}

		String updateVersion = updateVersionOpt.get();

		boolean updatedVersionIsSnapshot = updateVersion.contains("-");

		boolean currentVersionIsSnapshot = currentVersion.contains("SNAPSHOT");

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
		else if (currentVersionIsSnapshot && !updatedVersionIsSnapshot && release &&
				 (updateSemver.compareTo(currentSemver) == 0)) {

			return true;
		}

		boolean md5Match = _doesMD5Match(url, updatedVersionIsSnapshot);

		if (!md5Match && updatedVersionIsSnapshot && currentVersionIsSnapshot) {
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

		return false;
	}

	private void _updateUnix(String url) {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File baseDir = args.getBase();

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
		catch (Exception exception) {
			bladeCLI.error("Problem running jpm install.");
			bladeCLI.error(exception);
		}
	}

	private void _updateWindows(String url) throws IOException {
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

	private static final String _BASE_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/repositories/";

	private static final String _BLADE_CLI_CONTEXT = "com/liferay/blade/com.liferay.blade.cli/";

	private static final String _RELEASES_REPO_URL = _BASE_CDN_URL + "liferay-public-releases/" + _BLADE_CLI_CONTEXT;

	private static final String _SNAPSHOTS_REPO_URL = _BASE_CDN_URL + "liferay-public-snapshots/" + _BLADE_CLI_CONTEXT;

	private static final Pattern _bladeSnapshotPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).SNAPSHOT(\\d+)");
	private static final Pattern _nexusSnapshotPattern = Pattern.compile(
		"(\\d+)\\.(\\d+)\\.(\\d+)-(\\d+)\\.(\\d\\d\\d\\d)\\d\\d-\\d+");
	private static final File _updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");
	private static final Pattern _versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

	private Optional<String> _releaseUpdateVersion = Optional.empty();
	private Optional<String> _snapshotUpdateVersion = Optional.empty();

}