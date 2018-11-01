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
		boolean has = false;

		final File updateUrlFile = new File(System.getProperty("user.home"), ".blade/update.url");

		if (updateUrlFile.exists() && !updateUrlFile.isDirectory()) {
			if (updateUrlFile.length() > 0) {
				has = true;
			}
		}

		return has;
	}

	public static boolean shouldUpdate(String bladeVersion, String updateVersion) {
		boolean should = false;

		Matcher matcher = _pattern.matcher(bladeVersion);

		matcher.find();

		String bladeMajor = matcher.group(1);
		String bladeMinor = matcher.group(2);
		String bladePatch = matcher.group(3);

		matcher = _pattern.matcher(updateVersion);

		matcher.find();

		String updateMajor = matcher.group(1);
		String updateMinor = matcher.group(2);
		String updatePatch = matcher.group(3);

		if (Integer.parseInt(updateMajor) > Integer.parseInt(bladeMajor)) {
			should = true;
		}
		else {
			if (Integer.parseInt(updateMajor) < Integer.parseInt(bladeMajor)) {
				should = false;
			}
			else {
				if (Integer.parseInt(updateMinor) > Integer.parseInt(bladeMinor)) {
					should = true;
				}
				else {
					if (Integer.parseInt(updateMinor) < Integer.parseInt(bladeMinor)) {
						should = false;
					}
					else {
						if (Integer.parseInt(updatePatch) > Integer.parseInt(bladePatch)) {
							should = true;
						}
					}
				}
			}
		}

		if (bladeVersion.contains("SNAPSHOT")) {
			should = true;
		}

		return should;
	}

	public UpdateCommand() {
	}

	public UpdateCommand(BladeCLI bladeCLI) {
		setBlade(bladeCLI);
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		UpdateArgs updateArgs = getArgs();

		boolean snapshots = updateArgs.isSnapshots();

		String oldUrl = "https://releases.liferay.com/tools/blade-cli/latest/blade.jar";

		String url = "";

		boolean available = false;

		available = isUpdateAvailable();

		if (available) {

			// Just because there is an update available does not mean that there is
			// a url to a released blade.jar yet, or maybe the snapshot repo is empty.

			try {
				url = getUpdateJarUrl(snapshots);
			}
			catch (IOException ioe) {
				if (snapshots) {
					bladeCLI.out("No jar is available from " + _SNAPSHOTS_REPO_URL);
				}
				else {
					bladeCLI.out("No jar is available from " + _RELEASES_REPO_URL);
				}

				url = oldUrl;
			}
		}
		else {
			url = oldUrl;
		}

		bladeCLI.out("Updating to " + url);

		if (BladeUtil.isWindows()) {
			bladeCLI.out(
				"blade update cannot execute successfully because of Windows file locking. Please use following " +
					"command:");
			bladeCLI.out("\tjpm install -f " + url);
		}
		else {
			BaseArgs baseArgs = bladeCLI.getBladeArgs();

			File baseDir = new File(baseArgs.getBase());

			Process process = BladeUtil.startProcess("jpm install -f " + url, baseDir);

			int errCode = process.waitFor();

			if (errCode == 0) {
				bladeCLI.out("Update completed successfully");
			}
			else {
				bladeCLI.error("blade exited with code: " + errCode);
			}
		}
	}

	@Override
	public Class<UpdateArgs> getArgsClass() {
		return UpdateArgs.class;
	}

	public boolean isUpdateAvailable() throws IOException {
		BladeCLI bladeCLI = getBladeCLI();

		boolean available = false;

		VersionCommand versionCommand = new VersionCommand(bladeCLI);

		String bladeVersion = versionCommand.getBladeCLIVersion();

		boolean fromSnapshots = bladeVersion.contains("SNAPSHOT");

		String updateVersion = getUpdateVersion(fromSnapshots);

		boolean shouldUpdate = shouldUpdate(bladeVersion, updateVersion);

		if (shouldUpdate) {
			String updateJarUrl = getUpdateJarUrl(fromSnapshots);

			if ("".equals(updateJarUrl)) {
				bladeCLI.out("No update url available.");
			}
			else {
				available = true;
			}
		}

		return available;
	}

	private static final String _BASE_CDN_URL = "https://repository-cdn.liferay.com/nexus/content/repositories/";

	private static final String _BLADE_CLI_CONTEXT = "'com/liferay/blade/com.liferay.blade.cli/";

	private static final String _RELEASES_REPO_URL = _BASE_CDN_URL + "liferay-public-releases/" + _BLADE_CLI_CONTEXT;

	private static final String _SNAPSHOTS_REPO_URL = _BASE_CDN_URL + "liferay-public-snapshots/" + _BLADE_CLI_CONTEXT;

	private static final Pattern _pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}