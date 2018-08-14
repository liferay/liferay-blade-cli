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

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.project.templates.ProjectTemplates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeUtil {

	public static final String APP_SERVER_PARENT_DIR_PROPERTY = "app.server.parent.dir";

	public static final String APP_SERVER_TYPE_PROPERTY = "app.server.type";

	public static boolean canConnect(String host, int port) {
		InetSocketAddress localAddress = new InetSocketAddress(0);
		InetSocketAddress remoteAddress = new InetSocketAddress(host, Integer.valueOf(port));

		return _canConnect(localAddress, remoteAddress);
	}

	public static void copy(InputStream in, File outputDir) throws Exception {
		try (Jar jar = new Jar("dot", in)) {
			Map<String, Resource> resources = jar.getResources();

			for (Entry<String, Resource> e : resources.entrySet()) {
				String path = e.getKey();

				Resource r = e.getValue();

				File dest = Processor.getFile(outputDir, path);

				if ((dest.lastModified() < r.lastModified()) || (r.lastModified() <= 0)) {
					File dp = dest.getParentFile();

					if (!dp.exists() && !dp.mkdirs()) {
						throw new Exception("Could not create directory " + dp);
					}

					IO.copy(r.openInputStream(), dest);
				}
			}
		}
	}

	public static boolean dependencyManagerEnable(File dir) {
		if (!isWorkspace(dir)) {
			return false;
		}

		File settingGradle = getSettingGradleFile(dir);

		Properties gradleProperties = getGradleProperties(dir);

		String targetPlatformKey = "liferay.workspace.target.platform.version";

		boolean containsTargetPlatformProperty = gradleProperties.containsKey(targetPlatformKey);

		try {
			String settingScript = read(settingGradle);

			Matcher matcher = WorkspaceConstants.patternGradleWorkspacePlugin.matcher(settingScript);

			if (!containsTargetPlatformProperty || !matcher.find()) {
				return false;
			}

			String pluginVersion = matcher.group(1);

			ComparableVersion currentVersion = new ComparableVersion(pluginVersion);

			ComparableVersion minSupportVersion = new ComparableVersion("1.9.2");

			int result = currentVersion.compareTo(minSupportVersion);

			if (result >= 0) {
				return true;
			}
		}
		catch (Exception e) {
		}

		return false;
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

	public static String getBundleVersion(Path pathToJar) throws IOException {
		return getManifestProperty(pathToJar, "Bundle-Version");
	}

	public static Properties getGradleProperties(File dir) {
		File file = getGradlePropertiesFile(dir);

		return getProperties(file);
	}

	public static File getGradlePropertiesFile(File dir) {
		File gradlePropertiesFile = new File(getWorkspaceDir(dir), _GRADLE_PROPERTIES_FILE_NAME);

		return gradlePropertiesFile;
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

	public static File getSettingGradleFile(File dir) {
		File settingGradleFile = new File(getWorkspaceDir(dir), _SETTINGS_GRADLE_FILE_NAME);

		return settingGradleFile;
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

	public static File getWorkspaceDir(BladeCLI blade) {
		BaseArgs args = blade.getBladeArgs();

		return getWorkspaceDir(new File(args.getBase()));
	}

	public static File getWorkspaceDir(File dir) {
		File gradleParent = findParentFile(
			dir, new String[] {_SETTINGS_GRADLE_FILE_NAME, _GRADLE_PROPERTIES_FILE_NAME}, true);

		if ((gradleParent != null) && gradleParent.exists()) {
			return gradleParent;
		}

		File mavenParent = findParentFile(dir, new String[] {"pom.xml"}, true);

		if (_isWorkspacePomFile(new File(mavenParent, "pom.xml"))) {
			return mavenParent;
		}

		FilenameFilter gradleFilter =
			(file, name) -> _SETTINGS_GRADLE_FILE_NAME.equals(name) || _GRADLE_PROPERTIES_FILE_NAME.equals(name);

		File[] matches = dir.listFiles(gradleFilter);

		if (Objects.nonNull(matches) && (matches.length > 0)) {
			return dir;
		}
		else {
			File mavenPom = new File(dir, "pom.xml");

			if (mavenPom.exists() && _isWorkspacePomFile(mavenPom)) {
				return dir;
			}
		}

		try {
			if (dir.exists() && dir.isDirectory() && !isDirEmpty(dir.toPath())) {
				for (File file : dir.listFiles()) {
					if (file.isDirectory()) {
						File[] gradleChildren = file.listFiles(gradleFilter);

						if (Objects.nonNull(gradleChildren) && (gradleChildren.length > 0)) {
							return file;
						}
						else {
							File childPom = new File(file, "pom.xml");

							if (childPom.exists() && _isWorkspacePomFile(childPom)) {
								return file;
							}
						}
					}
				}
			}
		}
		catch (Throwable th) {
			throw new RuntimeException("Error while detecting a workspace directory", th);
		}

		return null;
	}

	public static boolean hasGradleWrapper(File dir) {
		if (new File(dir, "gradlew").exists() && new File(dir, "gradlew.bat").exists()) {
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

	public static boolean isWindows() {
		String osName = System.getProperty("os.name");

		osName = osName.toLowerCase();

		return osName.contains("windows");
	}

	public static boolean isWorkspace(BladeCLI blade) {
		File dirToCheck;

		if (blade == null) {
			dirToCheck = new File(".").getAbsoluteFile();
		}
		else {
			BaseArgs args = blade.getBladeArgs();

			dirToCheck = new File(args.getBase());
		}

		return isWorkspace(dirToCheck);
	}

	public static boolean isWorkspace(File dir) {
		File workspaceDir = getWorkspaceDir(dir);

		if (Objects.isNull(dir)) {
			return false;
		}

		File gradleFile = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);

		if (!gradleFile.exists()) {
			File pomFile = new File(workspaceDir, "pom.xml");

			if (_isWorkspacePomFile(pomFile)) {
				return true;
			}

			return false;
		}

		try {
			String script = read(gradleFile);

			Matcher matcher = WorkspaceConstants.patternWorkspacePlugin.matcher(script);

			if (matcher.find()) {
				return true;
			}
			else {
				//For workspace plugin < 1.0.5

				gradleFile = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);

				script = read(gradleFile);

				matcher = WorkspaceConstants.patternWorkspacePlugin.matcher(script);

				return matcher.find();
			}
		}
		catch (Exception e) {
			return false;
		}
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

	public static Process startProcess(File workingDir, String command) throws Exception {
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

	public static void unzip(File srcFile, File destDir) throws IOException {
		unzip(srcFile, destDir, null);
	}

	public static void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
		try (final ZipFile zip = new ZipFile(srcFile)) {
			final Enumeration<? extends ZipEntry> entries = zip.entries();

			boolean foundStartEntry = false;

			if (entryToStart == null) {
				foundStartEntry = true;
			}

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				String entryName = entry.getName();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entryName);
					continue;
				}

				if (entry.isDirectory() || ((entryToStart != null) && !entryName.startsWith(entryToStart))) {
					continue;
				}

				if (entryToStart != null) {
					entryName = entryName.replaceFirst(entryToStart, "");
				}

				final File f = new File(destDir, entryName);

				if (!_isSafelyRelative(f, destDir)) {
					throw new ZipException(
						"Entry " + f.getName() + " is outside of the target destination: " + destDir);
				}

				if (f.exists()) {
					IO.delete(f);

					if (f.exists()) {
						throw new IOException("Could not delete " + f.getAbsolutePath());
					}
				}

				final File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					final String msg = "Could not create dir: " + dir.getPath();

					throw new IOException(msg);
				}

				try (final InputStream in = zip.getInputStream(entry);
					final FileOutputStream out = new FileOutputStream(f)) {

					final byte[] bytes = new byte[1024];

					int count = in.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = in.read(bytes);
					}

					out.flush();
				}
			}
		}
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

	private static boolean _isSafelyRelative(File file, File destDir) {
		Path destPath = destDir.toPath();

		destPath = destPath.toAbsolutePath();

		destPath = destPath.normalize();

		Path path = file.toPath();

		path = path.toAbsolutePath();

		path = path.normalize();

		return path.startsWith(destPath);
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

	private static boolean _isWorkspacePomFile(File pomFile) {
		boolean pom = false;

		if ((pomFile != null) && "pom.xml".equals(pomFile.getName()) && pomFile.exists()) {
			pom = true;
		}

		if (pom) {
			try {
				String content = read(pomFile);

				if (content.contains("portal.tools.bundle.support")) {
					return true;
				}
			}
			catch (Exception e) {
			}
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

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME = "gradle.properties";

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

}