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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.ProtectionDomain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 * @author Gregory Amerson
 */
public class UpdateCommand extends BaseCommand<UpdateArgs> {

	public static final String RELEASES_REPO_URL =
		UpdateCommand._BASE_CDN_URL + "liferay-public-releases/" + UpdateCommand._BLADE_CLI_CONTEXT;

	public static final String SNAPSHOTS_REPO_URL =
		UpdateCommand._BASE_CDN_URL + "liferay-public-snapshots/" + UpdateCommand._BLADE_CLI_CONTEXT;

	public static boolean equal(String currentVersion, String updateVersion) {
		Matcher matcher = _versionPattern.matcher(currentVersion);

		matcher.find();

		int currentMajor = Integer.parseInt(matcher.group(1));
		int currentMinor = Integer.parseInt(matcher.group(2));
		int currentPatch = Integer.parseInt(matcher.group(3));

		matcher = _versionPattern.matcher(updateVersion);

		matcher.find();

		int updateMajor = Integer.parseInt(matcher.group(1));
		int updateMinor = Integer.parseInt(matcher.group(2));
		int updatePatch = Integer.parseInt(matcher.group(3));

		if (currentVersion.contains("SNAPSHOT") && updateVersion.contains("-")) {
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

		Version currentSemver = new Version(currentMajor, currentMinor, currentPatch);

		Version updateSemver = new Version(updateMajor, updateMinor, updatePatch);

		return currentSemver.equals(updateSemver);
	}

	public static Path getRunningJarFile() {
		try {
			ProtectionDomain protectionDomain = BladeCLI.class.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL location = codeSource.getLocation();

			URI locationUri = location.toURI();

			File runningJarFile = new File(locationUri);

			return runningJarFile.toPath();
		}
		catch (URISyntaxException urise) {
			throw new RuntimeException(urise);
		}
	}

	public static String getUpdateJarMD5Url(UpdateArgs updateArgs) throws IOException {
		String url = null;

		if (updateArgs.getUrl() != null) {
			URL updateUrlVar = updateArgs.getUrl();

			url = updateUrlVar.toString();
		}

		boolean snapshots = updateArgs.isSnapshots();

		boolean release = updateArgs.isRelease();

		if (url == null) {
			if (snapshots) {
				url = SNAPSHOTS_REPO_URL;
			}
			else if (release) {
				url = RELEASES_REPO_URL;
			}
			else {
				String currentVersion = VersionCommand.getBladeCLIVersion();

				if (currentVersion.contains("SNAPSHOT")) {
					url = SNAPSHOTS_REPO_URL;
				}
				else {
					url = RELEASES_REPO_URL;
				}
			}
		}

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Element lastVersionElement = versionElements.last();

		String version = lastVersionElement.text();

		if (Objects.equals(url, SNAPSHOTS_REPO_URL)) {
			connection.url(url + "/" + version + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			String snapshotVersion = valueElement.text();

			return url + "/" + version + "/com.liferay.blade.cli-" + snapshotVersion + ".jar.md5";
		}

		return url + "/" + version + "/com.liferay.blade.cli-" + version + ".jar.md5";
	}

	public static String getUpdateJarUrl(UpdateArgs updateArgs) throws IOException {
		String url = null;

		if (updateArgs.getUrl() != null) {
			URL updateUrlVar = updateArgs.getUrl();

			url = updateUrlVar.toString();
		}

		boolean snapshots = updateArgs.isSnapshots();

		boolean release = updateArgs.isRelease();

		if (url == null) {
			if (snapshots) {
				url = SNAPSHOTS_REPO_URL;
			}
			else if (release) {
				url = RELEASES_REPO_URL;
			}
			else {
				String currentVersion = VersionCommand.getBladeCLIVersion();

				if (currentVersion.contains("SNAPSHOT")) {
					url = SNAPSHOTS_REPO_URL;
				}
				else {
					url = RELEASES_REPO_URL;
				}
			}
		}

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Element lastVersionElement = versionElements.last();

		String version = lastVersionElement.text();

		if (Objects.equals(url, SNAPSHOTS_REPO_URL)) {
			connection.url(url + "/" + version + "/maven-metadata.xml");

			document = connection.get();

			Elements valueElements = document.select("snapshotVersion > value");

			Element valueElement = valueElements.get(0);

			String snapshotVersion = valueElement.text();

			return url + "/" + version + "/com.liferay.blade.cli-" + snapshotVersion + ".jar";
		}

		return url + "/" + version + "/com.liferay.blade.cli-" + version + ".jar";
	}

	public static String getUpdateVersion(boolean snapshotsArg) throws IOException {
		String url = RELEASES_REPO_URL;

		if (snapshotsArg) {
			url = SNAPSHOTS_REPO_URL;
		}

		if (_hasUpdateUrlFromBladeDir()) {
			url = _getUpdateUrlFromBladeDir();
		}

		Connection connection = Jsoup.connect(url + "maven-metadata.xml");

		connection = connection.parser(Parser.xmlParser());

		Document document = connection.get();

		Elements versionElements = document.select("version");

		Iterator<Element> it = versionElements.iterator();

		Collection<Element> elements = new HashSet<>();

		while (it.hasNext()) {
			Element versionElement = it.next();

			Node node = versionElement.childNode(0);

			String nodeString = node.toString();

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

		String updateVersion = null;

		if (snapshotsArg) {
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

	public static boolean shouldUpdate(String currentVersion, String updateVersion) {
		return shouldUpdate(currentVersion, updateVersion, null);
	}

	public static boolean shouldUpdate(String currentVersion, String updateVersion, String url) {
		boolean snapshot = currentVersion.contains("SNAPSHOT");

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

		UpdateArgs updateArgs = new UpdateArgs();

		if (url != null) {
			try {
				updateArgs.setUrl(new URL(url));
			}
			catch (MalformedURLException murle) {
				throw new RuntimeException(murle);
			}
		}

		updateArgs.setSnapshots(snapshot);

		if ((updateSemver.compareTo(currentSemver) > 0) && !_doesMD5Match(updateArgs)) {
			return true;
		}

		if (snapshot && updateVersion.contains("-")) {
			matcher = _bladeSnapshotPattern.matcher(currentVersion);

			matcher.find();

			Long currentSnapshot = Long.parseLong(matcher.group(4));

			matcher = _nexusSnapshotPattern.matcher(updateVersion);

			matcher.find();

			Long updateSnapshot = Long.parseLong(matcher.group(4) + matcher.group(5));

			if ((updateSnapshot > currentSnapshot) && !_doesMD5Match(updateArgs)) {
				return true;
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

		boolean snapshotsArg = updateArgs.isSnapshots();

		boolean releaseArg = updateArgs.isRelease();

		boolean checkUpdateOnly = updateArgs.isCheckOnly();

		String releaseUpdateVersion = "";

		String snapshotUpdateVersion = "";

		String updateUrl = null;

		if (_hasUpdateUrlFromBladeDir()) {
			try {
				updateArgs.setUrl(new URL(_getUpdateUrlFromBladeDir()));
			}
			catch (MalformedURLException murle) {
				throw new RuntimeException(murle);
			}
		}

		if (updateArgs.getUrl() != null) {
			URL updateUrlVar = updateArgs.getUrl();

			updateUrl = updateUrlVar.toString();
		}

		String url = null;

		try {
			url = getUpdateJarUrl(updateArgs);

			releaseUpdateVersion = getUpdateVersion(false);

			snapshotUpdateVersion = getUpdateVersion(true);

			try {
				currentVersion = VersionCommand.getBladeCLIVersion();
			}
			catch (IOException ioe) {
				if (!checkUpdateOnly) {
					bladeCLI.error("Could not determine current blade version, continuing with update.");
				}
				else {
					throw new RuntimeException(ioe);
				}
			}

			String updateVersion;

			if (snapshotsArg) {
				updateVersion = snapshotUpdateVersion;
			}
			else if (releaseArg) {
				updateVersion = releaseUpdateVersion;
			}
			else if (currentVersion.contains("SNAPSHOT")) {
				updateVersion = snapshotUpdateVersion;
			}
			else {
				updateVersion = releaseUpdateVersion;
			}

			boolean shouldUpdate = shouldUpdate(currentVersion, updateVersion, updateUrl);

			if (checkUpdateOnly) {
				if (_hasUpdateUrlFromBladeDir()) {
					bladeCLI.out("Custom URL specified: " + updateUrl);
				}
				else {
				}

				bladeCLI.out("Current blade version: " + currentVersion);
				bladeCLI.out("Latest Release Version: " + releaseUpdateVersion);
				bladeCLI.out("Latest Snapshot Version: " + snapshotUpdateVersion);

				if (shouldUpdate) {
					String versionTag;

					if (Objects.equals(updateVersion, snapshotUpdateVersion)) {
						versionTag = "(Snapshot)";
					}
					else if (Objects.equals(updateVersion, releaseUpdateVersion)) {
						versionTag = "(Release)";
					}
					else {
						versionTag = "(Custom)";
					}

					bladeCLI.out(
						"A new update is available for this version of blade: " + updateVersion + " " + versionTag);
				}

				return;
			}

			if (shouldUpdate) {
				_performUpdate(url);
			}
			else {
				if (snapshotsArg) {
					if (currentVersion.contains("SNAPSHOT")) {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" is greater than the latest snapshot version " + releaseUpdateVersion);
					}
					else {
						bladeCLI.out(
							"Current blade version " + currentVersion +
								" (released) is greater than the latest snapshot version " + releaseUpdateVersion);
					}
				}
				else {
					if (equal(currentVersion, releaseUpdateVersion)) {
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

	private static boolean _doesMD5Match(UpdateArgs updateArgs) {
		try {
			Path currentJarPath = getRunningJarFile();

			String currentJarMD5 = _getMD5(currentJarPath);

			String updateJarMD5 = _readTextFileFromURL(getUpdateJarMD5Url(updateArgs));

			updateJarMD5 = updateJarMD5.toUpperCase();

			return Objects.equals(updateJarMD5, currentJarMD5);
		}
		catch (Exception e) {
		}

		return false;
	}

	private static String _getMD5(Path path) {
		try (FileChannel fileChannel = FileChannel.open(path)) {
			long fileChannelSize = fileChannel.size();

			MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannelSize);

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.update(buffer);

			byte[] digest = messageDigest.digest();

			String md5Sum = DatatypeConverter.printHexBinary(digest);

			buffer.clear();

			return md5Sum.toUpperCase();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String _getUpdateUrlFromBladeDir() {
		String url = "no url";

		if (_hasUpdateUrlFromBladeDir()) {
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

	private static boolean _hasUpdateUrlFromBladeDir() {
		boolean hasUpdate = false;

		if (_updateUrlFile.exists() && _updateUrlFile.isFile() && (_updateUrlFile.length() > 0)) {
			hasUpdate = true;
		}

		return hasUpdate;
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
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
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

	private void _updateUnix(String url) {
		BladeCLI bladeCLI = getBladeCLI();

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

	private static final Pattern _bladeSnapshotPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+).SNAPSHOT(\\d+)");
	private static final Pattern _nexusSnapshotPattern = Pattern.compile(
		"(\\d+)\\.(\\d+)\\.(\\d+)-(\\d+)\\.(\\d\\d\\d\\d)\\d\\d-\\d+");
	private static final File _updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");
	private static final Pattern _versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}