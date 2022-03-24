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
import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.command.SamplesCommand;
import com.liferay.blade.cli.command.validator.WorkspaceProductComparator;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;

import groovy.json.JsonSlurper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import org.gradle.internal.impldep.com.google.common.base.Strings;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author David Truong
 * @author Simon Jiang
 */
public class BladeUtil {

	public static final String APP_SERVER_PARENT_DIR_PROPERTY = "app.server.parent.dir";

	public static final String APP_SERVER_TYPE_PROPERTY = "app.server.type";

	public static void addGradleWrapper(File destinationDir) throws Exception {
		InputStream inputStream = SamplesCommand.class.getResourceAsStream("/wrapper.zip");

		FileUtil.unzip(inputStream, destinationDir);

		File gradlewFile = new File(destinationDir, "gradlew");

		gradlewFile.setExecutable(true);
	}

	public static boolean canConnect(String host, int port) {
		InetSocketAddress localAddress = new InetSocketAddress(0);
		InetSocketAddress remoteAddress = new InetSocketAddress(host, Integer.valueOf(port));

		return _canConnect(localAddress, remoteAddress);
	}

	public static int compareVersions(Version v1, Version v2) {
		if (v2 == v1) {

			// quicktest

			return 0;
		}

		int result = v1.getMajor() - v2.getMajor();

		if (result != 0) {
			return result;
		}

		result = v1.getMinor() - v2.getMinor();

		if (result != 0) {
			return result;
		}

		result = v1.getMicro() - v2.getMicro();

		if (result != 0) {
			return result;
		}

		String s1 = v1.getQualifier();

		return s1.compareTo(v2.getQualifier());
	}

	public static void downloadGithubProject(String url, Path target) throws IOException {
		String zipUrl = url + "/archive/master.zip";

		Path githubCacheDirPath = getBladeCachePath().resolve("github");

		downloadLink(zipUrl, githubCacheDirPath.toFile(), target);
	}

	public static void downloadLink(String link, File cacheDir, Path target) throws IOException {
		try {
			DownloadCommand downloadCommand = new DownloadCommand();

			downloadCommand.setCacheDir(cacheDir);
			downloadCommand.setPassword(null);
			downloadCommand.setToken(false);
			downloadCommand.setUrl(new URL(link));
			downloadCommand.setUserName(null);
			downloadCommand.setQuiet(true);

			downloadCommand.execute();

			Files.move(downloadCommand.getDownloadPath(), target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception exception) {
			throw new IOException(MessageFormat.format("Can not download for link {0}", link), exception);
		}
	}

	public static File findParentFile(File dir, String[] fileNames, boolean checkParents) {
		if (dir == null) {
			return null;
		}
		else if (Objects.equals(".", dir.toString()) || !dir.isAbsolute()) {
			try {
				dir = dir.getCanonicalFile();
			}
			catch (Exception exception) {
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

	public static Path getBladeCachePath() {
		File userHome = new File(System.getProperty("user.home"));

		Path userHomePath = userHome.toPath();

		return userHomePath.resolve(".blade" + File.separator + "cache");
	}

	public static Path getBladeJarPath() {
		try {
			ProtectionDomain protectionDomain = BladeCLI.class.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL location = codeSource.getLocation();

			File file = new File(location.toURI());

			return file.toPath();
		}
		catch (URISyntaxException uriSyntaxException) {
			throw new RuntimeException(uriSyntaxException);
		}
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

			return new File(gradleRoot, _GRADLEW_UNIX_FILE_NAME);
		}

		return null;
	}

	public static Map<String, String> getInitTemplates(BladeCLI bladeCLI) throws IOException {
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
		catch (IOException ioException) {
		}

		return initTemplates;
	}

	public static String getManifestProperty(Path pathToJar, String propertyName) throws IOException {
		File file = pathToJar.toFile();

		try (JarFile jar = new JarFile(file)) {
			Manifest manifest = jar.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			return attributes.getValue(propertyName);
		}
	}

	public static Map<String, Object> getProductInfos() {
		return getProductInfos(false, null);
	}

	@SuppressWarnings("unchecked")
	public static synchronized Map<String, Object> getProductInfos(boolean trace, PrintStream printStream) {
		if (!_productInfoMap.isEmpty()) {
			return _productInfoMap;
		}

		JsonSlurper jsonSlurper = new JsonSlurper();

		try {
			DownloadCommand downloadCommand = new DownloadCommand();

			downloadCommand.setCacheDir(_workspaceCacheDir);
			downloadCommand.setConnectionTimeout(5000);
			downloadCommand.setPassword(null);
			downloadCommand.setToken(false);
			downloadCommand.setUrl(new URL(_PRODUCT_INFO_URL));
			downloadCommand.setUserName(null);
			downloadCommand.setQuiet(true);

			downloadCommand.execute();

			try (BufferedReader reader = Files.newBufferedReader(downloadCommand.getDownloadPath())) {
				_productInfoMap = (Map<String, Object>)jsonSlurper.parse(reader);
			}
		}
		catch (Exception exception1) {
			if (trace && (printStream != null)) {
				exception1.printStackTrace(printStream);
			}

			try (InputStream resourceAsStream = BladeUtil.class.getResourceAsStream("/.product_info.json")) {
				_productInfoMap = (Map<String, Object>)jsonSlurper.parse(resourceAsStream);
			}
			catch (Exception exception2) {
				if (trace && (printStream != null)) {
					exception2.printStackTrace(printStream);
				}
			}
		}

		return _productInfoMap;
	}

	public static Properties getProperties(File file) {
		try (InputStream inputStream = new FileInputStream(file)) {
			Properties properties = new Properties();

			properties.load(inputStream);

			return properties;
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static Collection<String> getTemplateNames(BladeCLI blade) throws Exception {
		Map<String, String> templates = getTemplates(blade);

		return templates.keySet();
	}

	public static Map<String, String> getTemplates(BladeCLI bladeCLI) throws Exception {
		Path extensionsPath = bladeCLI.getExtensionsPath();

		Collection<File> templatesFiles = new HashSet<>();

		templatesFiles.add(extensionsPath.toFile());

		Extensions extensions = bladeCLI.getExtensions();

		Path extensionTemplates = extensions.getTemplatesPath();

		templatesFiles.add(extensionTemplates.toFile());

		return ProjectTemplates.getTemplates(templatesFiles);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getWorkspaceProductKeys(boolean promoted) {
		Map<String, Object> productInfos = getProductInfos();

		return productInfos.entrySet(
		).stream(
		).filter(
			entry -> Objects.nonNull(productInfos.get(entry.getKey()))
		).map(
			entry -> new Pair<>(entry.getKey(), new ProductInfo((Map<String, String>)productInfos.get(entry.getKey())))
		).filter(
			pair -> {
				ProductInfo productInfo = pair.second();

				return Objects.nonNull(productInfo.getTargetPlatformVersion()) &&
					   (!promoted || (promoted && productInfo.isPromoted()));
			}
		).sorted(
			new WorkspaceProductComparator()
		).map(
			Pair::first
		).collect(
			Collectors.toList()
		);
	}

	public static boolean hasGradleWrapper(File dir) {
		File gradlew = new File(dir, _GRADLEW_UNIX_FILE_NAME);
		File gradlebat = new File(dir, _GRADLEW_WINDOWS_FILE_NAME);

		if (gradlew.exists() && gradlebat.exists()) {
			return true;
		}

		File parent = dir.getParentFile();

		if ((parent != null) && parent.exists()) {
			return hasGradleWrapper(parent);
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

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
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
		catch (IOException ioException) {
			return false;
		}
	}

	public static String read(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	public static void readProcessStream(final InputStream inputStream, final PrintStream printStream) {
		Thread thread = new Thread(
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

		thread.setDaemon(true);
		thread.start();
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
			catch (Exception exception) {
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
			env.put("PATH", env.get("PATH") + ":/bin:/usr/local/bin");

			commands.add("sh");
			commands.add("-c");
		}

		commands.add(cmd);

		processBuilder.command(commands);
	}

	public static String simplifyTargetPlatformVersion(String targetPlatformVersion) {
		if (targetPlatformVersion == null) {
			return null;
		}

		String[] segments = targetPlatformVersion.split("\\.");

		StringBuilder sb = new StringBuilder();

		sb.append(segments[0]);
		sb.append('.');
		sb.append(segments[1]);
		sb.append('.');

		String micro = segments[2];

		int dashPosition = micro.indexOf("-");

		if (dashPosition > 0) {
			sb.append(micro.substring(0, dashPosition));

			if (segments.length == 3) {
				sb.append(".");
				sb.append(micro.substring(dashPosition + 1));
			}
		}
		else {
			sb.append(micro);
		}

		if (segments.length > 3) {
			sb.append(".");

			String qualifier = segments[3];

			Matcher matcher = _microPattern.matcher(qualifier);

			if (matcher.matches() && (matcher.groupCount() >= 5)) {
				qualifier = matcher.group(5);
			}

			if (!Strings.isNullOrEmpty(qualifier)) {
				sb.append(qualifier);
			}
		}

		return sb.toString();
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

	public static void tail(Path path, PrintStream printStream) throws IOException {
		try (BufferedReader input = new BufferedReader(new FileReader(path.toFile()))) {
			String currentLine = null;

			while (true) {
				if ((currentLine = input.readLine()) != null) {
					printStream.println(currentLine);

					continue;
				}

				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException interruptedException) {
					Thread currentThread = Thread.currentThread();

					currentThread.interrupt();

					break;
				}
			}
		}
	}

	public static void writePropertyValue(File propertyFile, String key, String value) throws Exception {
		String property = System.lineSeparator() + key + "=" + value;

		Files.write(propertyFile.toPath(), property.getBytes(), StandardOpenOption.APPEND);
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
		catch (IOException ioException) {
		}

		if (connected) {
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

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME = ".liferay/workspace";

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

	private static final String _PRODUCT_INFO_URL = "https://releases.liferay.com/tools/workspace/.product_info.json";

	private static final Pattern _microPattern = Pattern.compile("(((e|f|s)p)|(ga))([0-9]+)(-[0-9]+)?");
	private static Map<String, Object> _productInfoMap = Collections.emptyMap();
	private static File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);

}