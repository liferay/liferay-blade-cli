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

package com.liferay.blade.cli.util;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.internal.util.ProjectTemplatesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.gradle.internal.impldep.bsh.commands.dir;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeUtil {

	public static final String APP_SERVER_PARENT_DIR_PROPERTY = "app.server.parent.dir";

	public static final String APP_SERVER_TYPE_PROPERTY = "app.server.type";

	public static final String CDN_NEXUS_CONTEXT = "https://repository-cdn.liferay.com/nexus/content/repositories/";

	public static final String GROUP_ID = "com.liferay.blade.cli";

	public static final String RELEASE_CONTEXT =
		CDN_NEXUS_CONTEXT + "liferay-public-releases/com/liferay/blade/" + GROUP_ID + "/";

	public static final String SNAPSHOT_CONTEXT =
		CDN_NEXUS_CONTEXT + "liferay-public-snapshots/com/liferay/blade/" + GROUP_ID + "/";

	public static boolean canConnect(String host, int port) {
		InetSocketAddress localAddress = new InetSocketAddress(0);
		InetSocketAddress remoteAddress = new InetSocketAddress(host, Integer.valueOf(port));

		return _canConnect(localAddress, remoteAddress);
	}

	public static void downloadGithubProject(String url, Path target) throws IOException {
		String zipUrl = url + "/archive/master.zip";

		downloadLink(zipUrl, target);
	}

	public static void downloadLink(String link, Path target) throws IOException {
		if (_isURLAvailable(link)) {
			LinkDownloader downloader = new LinkDownloader(link, target);

			downloader.run();
		}
		else {
			throw new RuntimeException("url '" + link + "' is not accessible.");
		}
	}

	public static File findParentFile(File dir, String[] fileNames, boolean checkParents) {
		if (dir == null) {
			return null;
		}
		else if (".".equals(dir.toString()) || !dir.isAbsolute()) {
			try {
				dir = dir.getCanonicalFile();
			}
			catch (Exception e) {
				dir = dir.getAbsoluteFile();
			}
		}

		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			if (file.exists()) {
				return dir;
			}
		}

		if (checkParents) {
			return findParentFile(dir.getParentFile(), fileNames, checkParents);
		}

		return null;
	}

	public static List<Properties> getAppServerProperties(File dir) {
		File projectRoot = findParentFile(dir, _APP_SERVER_PROPERTIES_FILE_NAMES, true);

		List<Properties> properties = new ArrayList<>();

		for (String fileName : _APP_SERVER_PROPERTIES_FILE_NAMES) {
			File file = new File(projectRoot, fileName);

			if (file.exists()) {
				properties.add(getProperties(file));
			}
		}

		return properties;
	}

	public static String getBladeVersion(BladeCLI bladeCLI) throws IOException {
		String version = "";

		Class<? extends BladeCLI> clazz = bladeCLI.getClass();

		ClassLoader cl = clazz.getClassLoader();

		Enumeration<URL> e = cl.getResources("META-INF/MANIFEST.MF");

		while (e.hasMoreElements()) {
			URL u = e.nextElement();

			Manifest m = new Manifest(u.openStream());

			Attributes mainAttributes = m.getMainAttributes();

			String bsn = mainAttributes.getValue(Constants.BUNDLE_SYMBOLICNAME);

			if ((bsn != null) && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = mainAttributes;

				version = attrs.getValue(Constants.BUNDLE_VERSION);
			}
		}

		return version;
	}

	public static String getBundleVersion(Path pathToJar) throws IOException {
		return getManifestProperty(pathToJar, "Bundle-Version");
	}

	public static File getGradleWrapper(File dir) {
		File gradleRoot = findParentFile(dir, new String[] {_GRADLEW_UNIX_FILE_NAME, _GRADLEW_WINDOWS_FILE_NAME}, true);

		if (gradleRoot != null) {
			if (isWindows()) {
				return new File(gradleRoot, _GRADLEW_WINDOWS_FILE_NAME);
			}
			else {
				return new File(gradleRoot, _GRADLEW_UNIX_FILE_NAME);
			}
		}

		return null;
	}

	public static Map<String, String> getInitTemplates(BladeCLI bladeCLI) {
		Map<String, String> initTemplates = new HashMap<>();

		initTemplates.put("workspace", "Liferay Workspace built with Gradle or Maven.");

		Path extensions = bladeCLI.getExtensionsPath();

		try {
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				extensions, "*.project.templates.workspace*");

			Iterator<Path> iterator = directoryStream.iterator();

			while (iterator.hasNext()) {
				Path path = iterator.next();

				String fileName = String.valueOf(path.getFileName());

				String template = ProjectTemplatesUtil.getTemplateName(fileName);

				String bundleDescription = FileUtil.getManifestProperty(path.toFile(), "Bundle-Description");

				initTemplates.put(template, bundleDescription);
			}
		}
		catch (IOException ioe) {
		}

		return initTemplates;
	}

	public static String getManifestProperty(Path pathToJar, String propertyName) throws IOException {
		File file = pathToJar.toFile();

		try (JarFile jar = new JarFile(file)) {
			Manifest manifest = jar.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			return attributes.getValue("Bundle-Version");
		}
	}

	public static Properties getProperties(File file) {
		try (InputStream inputStream = new FileInputStream(file)) {
			Properties properties = new Properties();

			properties.load(inputStream);

			return properties;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static Collection<String> getTemplateNames(BladeCLI blade) throws Exception {
		Map<String, String> templates = getTemplates(blade);

		return templates.keySet();
	}

	public static Map<String, String> getTemplates(BladeCLI bladeCLI) throws Exception {
		Path extensions = bladeCLI.getExtensionsPath();

		Collection<File> templatesFiles = new HashSet<>();

		templatesFiles.add(extensions.toFile());

		return ProjectTemplates.getTemplates(templatesFiles);
	}

	public static String getUpdateJarUrl(boolean snapshots) throws IOException {
		String url = _nexusContext;

		if (snapshots) {
			url = SNAPSHOT_CONTEXT;
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
		String url = _nexusContext;

		if (snapshots) {
			url = SNAPSHOT_CONTEXT;
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

	public static boolean hasGradleWrapper(File dir) {
		if (new File(dir, _GRADLEW_UNIX_FILE_NAME).exists() && new File(dir, _GRADLEW_WINDOWS_FILE_NAME).exists()) {
			return true;
		}
		else {
			File parent = dir.getParentFile();

			if ((parent != null) && parent.exists()) {
				return hasGradleWrapper(parent);
			}
		}

		return false;
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

	public static boolean isDirEmpty(final Path directory) throws IOException {
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
			Iterator<Path> iterator = directoryStream.iterator();

			return !iterator.hasNext();
		}
	}

	public static boolean isEmpty(List<?> list) {
		if ((list == null) || list.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(Object[] array) {
		if ((array == null) || (array.length == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(String string) {
		if ((string == null) || string.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	public static boolean isNotEmpty(Object[] array) {
		return !isEmpty(array);
	}

	public static boolean isSafelyRelative(File file, File destDir) {
		Path destPath = destDir.toPath();

		destPath = destPath.toAbsolutePath();

		destPath = destPath.normalize();

		Path path = file.toPath();

		path = path.toAbsolutePath();

		path = path.normalize();

		return path.startsWith(destPath);
	}

	public static boolean isWindows() {
		String osName = System.getProperty("os.name");

		osName = osName.toLowerCase();

		return osName.contains("windows");
	}

	public static boolean isZipValid(File file) {
		try (ZipFile zipFile = new ZipFile(file)) {
			return true;
		}
		catch (IOException ioe) {
			return false;
		}
	}

	public static String read(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	public static void readProcessStream(final InputStream inputStream, final PrintStream printStream) {
		Thread t = new Thread(
			new Runnable() {

				@Override
				public void run() {
					try (Scanner scanner = new Scanner(inputStream)) {
						while (scanner.hasNextLine()) {
							String line = scanner.nextLine();

							if (line != null) {
								AnsiLinePrinter.println(printStream, line);
							}
						}
					}
				}

			});

		t.start();
	}

	public static boolean searchZip(Path path, Predicate<String> test) {
		if (Files.exists(path) && !Files.isDirectory(path)) {
			try (ZipFile zipFile = new ZipFile(path.toFile())) {
				Stream<? extends ZipEntry> stream = zipFile.stream();

				Collection<ZipEntry> entryCollection = stream.collect(Collectors.toSet());

				for (ZipEntry zipEntry : entryCollection) {
					if (!zipEntry.isDirectory()) {
						String entryName = zipEntry.getName();

						if (test.test(entryName)) {
							return true;
						}
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static void setShell(ProcessBuilder processBuilder, String cmd) {
		Map<String, String> env = processBuilder.environment();

		List<String> commands = new ArrayList<>();

		if (isWindows()) {
			commands.add("cmd.exe");
			commands.add("/c");
		}
		else {
			env.put("PATH", env.get("PATH") + ":/usr/local/bin");

			commands.add("sh");
			commands.add("-c");
		}

		commands.add(cmd);

		processBuilder.command(commands);
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

	public static Process startProcess(String command, File workingDir) throws Exception {
		return startProcess(command, workingDir, null);
	}

	public static Process startProcess(String command, File dir, Map<String, String> environment) throws Exception {
		ProcessBuilder processBuilder = _buildProcessBuilder(command, dir, environment, true);

		Process process = processBuilder.start();

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		return process;
	}

	public static Process startProcess(
			String command, File dir, Map<String, String> environment, PrintStream out, PrintStream err)
		throws Exception {

		ProcessBuilder processBuilder = _buildProcessBuilder(command, dir, environment, false);

		Process process = processBuilder.start();

		readProcessStream(process.getInputStream(), out);
		readProcessStream(process.getErrorStream(), err);

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		return process;
	}

	public static Process startProcess(String command, File dir, PrintStream out, PrintStream err) throws Exception {
		return startProcess(command, dir, null, out, err);
	}

	public static boolean updateAvailable(BladeCLI bladeCLI) throws IOException {
		boolean available = false;

		String bladeVersion = getBladeVersion(bladeCLI);

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

	private static ProcessBuilder _buildProcessBuilder(
		String command, File dir, Map<String, String> environment, boolean inheritIO) {

		ProcessBuilder processBuilder = new ProcessBuilder();

		Map<String, String> env = processBuilder.environment();

		if (environment != null) {
			env.putAll(environment);
		}

		if ((dir != null) && dir.exists()) {
			processBuilder.directory(dir);
		}

		setShell(processBuilder, command);

		if (inheritIO) {
			processBuilder.inheritIO();
		}

		return processBuilder;
	}

	private static boolean _canConnect(InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		boolean connected = false;

		try (Socket socket = new Socket()) {
			socket.bind(localAddress);
			socket.connect(remoteAddress, 3000);
			socket.getInputStream();

			connected = true;
		}
		catch (IOException ioe) {
		}

		if (connected) {
			return true;
		}

		return false;
	}

	private static boolean _isURLAvailable(String urlString) throws IOException {
		URL url = new URL(urlString);

		HttpURLConnection.setFollowRedirects(false);

		HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

		httpURLConnection.setRequestMethod("HEAD");

		int responseCode = httpURLConnection.getResponseCode();

		if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_MOVED_TEMP)) {
			return true;
		}

		return false;
	}

	private static final String[] _APP_SERVER_PROPERTIES_FILE_NAMES = {
		"app.server." + System.getProperty("user.name") + ".properties",
		"app.server." + System.getenv("COMPUTERNAME") + ".properties",
		"app.server." + System.getenv("HOST") + ".properties",
		"app.server." + System.getenv("HOSTNAME") + ".properties", "app.server.properties",
		"build." + System.getProperty("user.name") + ".properties",
		"build." + System.getenv("COMPUTERNAME") + ".properties", "build." + System.getenv("HOST") + ".properties",
		"build." + System.getenv("HOSTNAME") + ".properties", "build.properties"
	};

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

	private static String _nexusContext = RELEASE_CONTEXT;
	private static final Pattern _pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");

}