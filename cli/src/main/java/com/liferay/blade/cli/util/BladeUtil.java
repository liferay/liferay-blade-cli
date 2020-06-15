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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.command.SamplesCommand;
import com.liferay.blade.cli.command.validator.WorkspaceProductComparator;
import com.liferay.portal.tools.bundle.support.commands.DownloadCommand;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.CodeSource;
import java.security.ProtectionDomain;

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
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;

/**
 * @author Gregory Amerson
 * @author David Truong
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
		else if (Objects.equals(".", dir.toString()) || !dir.isAbsolute()) {
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
		catch (URISyntaxException urise) {
			throw new RuntimeException(urise);
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

	public static Map<String, ProductInfo> getProductInfo() {
		try {
			DownloadCommand downloadCommand = new DownloadCommand();

			downloadCommand.setCacheDir(_workspaceCacheDir);
			downloadCommand.setPassword(null);
			downloadCommand.setToken(false);
			downloadCommand.setUrl(new URL(_PRODUCT_INFO_URL));
			downloadCommand.setUserName(null);
			downloadCommand.setQuiet(true);

			downloadCommand.execute();

			Path downloadPath = downloadCommand.getDownloadPath();

			Gson gson = new Gson();

			try (JsonReader jsonReader = new JsonReader(Files.newBufferedReader(downloadPath))) {
				TypeToken<Map<String, ProductInfo>> typeToken = new TypeToken<Map<String, ProductInfo>>() {
				};

				return gson.fromJson(jsonReader, typeToken.getType());
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return Collections.emptyMap();
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
		Path extensionsPath = bladeCLI.getExtensionsPath();

		Collection<File> templatesFiles = new HashSet<>();

		templatesFiles.add(extensionsPath.toFile());

		Extensions extensions = bladeCLI.getExtensions();

		Path extensionTemplates = extensions.getTemplatesPath();

		templatesFiles.add(extensionTemplates.toFile());

		return ProjectTemplates.getTemplates(templatesFiles);
	}

	public static List<String> getWorkspaceProductKey() {
		Map<String, ProductInfo> productInfoMap = getProductInfo();

		Set<Map.Entry<String, ProductInfo>> entries = productInfoMap.entrySet();

		return entries.stream(
		).filter(
			entry -> {
				ProductInfo productInfo = entry.getValue();

				return productInfo.getTargetPlatformVersion() != null;
			}
		).map(
			Map.Entry::getKey
		).sorted(
			new WorkspaceProductComparator()
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
			env.put("PATH", env.get("PATH") + ":/bin:/usr/local/bin");

			commands.add("sh");
			commands.add("-c");
		}

		commands.add(cmd);

		processBuilder.command(commands);
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
				catch (InterruptedException ie) {
					Thread currentThread = Thread.currentThread();

					currentThread.interrupt();

					break;
				}
			}
		}
	}

	public static void writePropertyValue(File propertyFile, String key, String value) throws Exception {
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<>(
			PropertiesConfiguration.class);

		Parameters parameters = new Parameters();

		PropertiesBuilderParameters properties = parameters.properties();

		properties.setFile(propertyFile);

		builder.configure(properties);

		Configuration config = builder.getConfiguration();

		config.setProperty(key, value);

		builder.save();
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

	private static final String _DEFAULT_WORKSPACE_CACHE_DIR_NAME = ".liferay/workspace";

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

	private static final String _PRODUCT_INFO_URL =
		"https://releases-cdn.liferay.com/tools/workspace/.product_info.json";

	private static File _workspaceCacheDir = new File(
		System.getProperty("user.home"), _DEFAULT_WORKSPACE_CACHE_DIR_NAME);

}