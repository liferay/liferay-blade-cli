/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.command.SamplesCommand;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.protocol.RedirectLocations;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;

import org.gradle.internal.impldep.com.google.common.base.Strings;

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
		InetSocketAddress remoteAddress = new InetSocketAddress(host, port);

		return _canConnect(localAddress, remoteAddress);
	}

	public static Path downloadFile(String urlString, Path cacheDirPath, String targetFileName) throws Exception {
		URL downladURL = new URL(urlString);

		URI downladURI = downladURL.toURI();

		if (Objects.equals(downladURI.getScheme(), "file")) {
			return Paths.get(downladURI);
		}

		try (CloseableHttpClient closeableHttpClient = getHttpClient(downladURL.toURI(), null, null)) {
			return _downloadFile(closeableHttpClient, downladURI, cacheDirPath, targetFileName);
		}
	}

	public static Path downloadGithubProject(String url, String target) throws Exception {
		String zipUrl = url + "/archive/master.zip";

		Path githubCacheDirPath = getBladeCachePath().resolve("github");

		return downloadFile(zipUrl, githubCacheDirPath, target);
	}

	public static File findParentFile(File dir, String[] fileNames, boolean checkParents) {
		if (dir == null) {
			return null;
		}
		else if (Objects.equals(dir.toString(), ".") || !dir.isAbsolute()) {
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

	public static Path getCurrentPath() {
		Path currentPath = Paths.get("");

		Path destinationNormalizePath = currentPath.normalize();

		return destinationNormalizePath.toAbsolutePath();
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

	public static CloseableHttpClient getHttpClient(URI uri, String userName, String password) {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();

		requestConfigBuilder.setCookieSpec(RequestConfig.DEFAULT.getCookieSpec());
		requestConfigBuilder.setRedirectsEnabled(true);

		httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());

		String scheme = uri.getScheme();

		String proxyHost = System.getProperty(scheme + ".proxyHost");
		String proxyPort = System.getProperty(scheme + ".proxyPort");

		String proxyUser = userName;

		if (Objects.isNull(proxyUser)) {
			proxyUser = System.getProperty(scheme + ".proxyUser");
		}

		String proxyPassword = password;

		if (Objects.isNull(proxyPassword)) {
			proxyPassword = System.getProperty(scheme + ".proxyPassword");
		}

		if ((proxyUser != null) && (proxyPassword != null)) {
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

			if ((proxyHost != null) && (proxyPort != null)) {
				credentialsProvider.setCredentials(
					new AuthScope(proxyHost, Integer.parseInt(proxyPort)),
					new UsernamePasswordCredentials(proxyUser, proxyPassword.toCharArray()));
			}

			httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
		}
		else {
			if ((proxyHost != null) && (proxyPort != null)) {
				httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
			}
		}

		httpClientBuilder.useSystemProperties();

		return httpClientBuilder.build();
	}

	public static Map<String, String> getInitTemplates(BladeCLI bladeCLI) throws IOException {
		Map<String, String> initTemplates = new HashMap<>();

		initTemplates.put("workspace", "Liferay Workspace built with Gradle or Maven.");

		Path extensions = bladeCLI.getExtensionsPath();

		try {
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				extensions, "*.project.templates.workspace*");

			for (Path path : directoryStream) {
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

	public static Properties getProperties(File file) {
		Properties properties = new Properties();

		try (InputStream inputStream = Files.newInputStream(file.toPath())) {
			properties.load(inputStream);
		}
		catch (Exception exception) {
		}

		return properties;
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

	public static boolean hasGradleWrapper(File dir) {
		File gradlew = new File(dir, _GRADLEW_UNIX_FILE_NAME);
		File gradleBat = new File(dir, _GRADLEW_WINDOWS_FILE_NAME);

		if (gradlew.exists() && gradleBat.exists()) {
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
		boolean retCode = false;

		if (Files.exists(path) && !Files.isDirectory(path)) {
			try (ZipFile zipFile = new ZipFile(path.toFile())) {
				Stream<? extends ZipEntry> stream = zipFile.stream();

				Collection<ZipEntry> entryCollection = stream.collect(Collectors.toSet());

				for (ZipEntry zipEntry : entryCollection) {
					if (!zipEntry.isDirectory()) {
						String entryName = zipEntry.getName();

						if (test.test(entryName)) {
							retCode = true;
						}
					}
				}
			}
			catch (Exception exception) {
			}
		}

		return retCode;
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
			sb.append(micro, 0, dashPosition);

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

		return connected;
	}

	private static void _checkResponseStatus(HttpResponse httpResponse) throws IOException {
		if (httpResponse.getCode() != HttpStatus.SC_OK) {
			throw new IOException(httpResponse.getReasonPhrase());
		}
	}

	private static Path _downloadFile(
			CloseableHttpClient closeableHttpClient, URI uri, Path cacheDirPath, String targetFileName)
		throws Exception {

		HttpHead httpHead = new HttpHead(uri);

		HttpContext httpContext = new BasicHttpContext();

		Date lastModifiedDate;

		try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpHead, httpContext)) {
			_checkResponseStatus(closeableHttpResponse);

			Header dispositionHeader = closeableHttpResponse.getFirstHeader("Content-Disposition");

			if (dispositionHeader == null) {
				RedirectLocations redirectLocations = (RedirectLocations)httpContext.getAttribute(
					HttpClientContext.REDIRECT_LOCATIONS);

				if ((redirectLocations != null) && (redirectLocations.size() > 0)) {
					uri = redirectLocations.get(redirectLocations.size() - 1);
				}
			}

			Header lastModifiedHeader = closeableHttpResponse.getFirstHeader(HttpHeaders.LAST_MODIFIED);

			if (lastModifiedHeader != null) {
				lastModifiedDate = DateUtils.parseDate(lastModifiedHeader.getValue());
			}
			else {
				lastModifiedDate = new Date();
			}
		}

		Files.createDirectories(cacheDirPath);

		Path targetPath = cacheDirPath.resolve(targetFileName);

		if (Files.exists(targetPath)) {
			FileTime fileTime = Files.getLastModifiedTime(targetPath);

			if (fileTime.toMillis() == lastModifiedDate.getTime()) {
				return targetPath;
			}

			Files.delete(targetPath);
		}

		HttpGet httpGet = new HttpGet(uri);

		try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
			_checkResponseStatus(closeableHttpResponse);

			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			try (InputStream inputStream = httpEntity.getContent();
				OutputStream outputStream = Files.newOutputStream(targetPath)) {

				byte[] buffer = new byte[10 * 1024];
				int read = -1;

				while ((read = inputStream.read(buffer)) >= 0) {
					outputStream.write(buffer, 0, read);
				}
			}
		}

		Files.setLastModifiedTime(targetPath, FileTime.fromMillis(lastModifiedDate.getTime()));

		return targetPath;
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

	private static final Pattern _microPattern = Pattern.compile("((([efs])p)|(ga)|(u))([0-9]+)(-[0-9]+)?");

}