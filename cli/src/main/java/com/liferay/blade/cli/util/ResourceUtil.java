/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URI;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;

import java.util.Date;
import java.util.Objects;

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

/**
 * @author Drew Brokke
 */
public class ResourceUtil {

	public static Resolver getClassLoaderResolver(String resourcePath) {
		return () -> {
			_print("Trying to get resource from class path: %s", resourcePath);

			return Objects.requireNonNull(
				ResourceUtil.class.getResourceAsStream(resourcePath),
				"Unable to get resource from class path: " + resourcePath);
		};
	}

	public static Resolver getLocalFileResolver(File file) {
		return () -> {
			_print("Trying to get resource from local file: %s", file.getAbsolutePath());

			_checkFileExists(file);

			return Files.newInputStream(file.toPath());
		};
	}

	public static Resolver getLocalFileResolver(File file, long maxAge, TemporalUnit temporalUnit) {
		return () -> {
			_print(
				"Trying to get resource from local file with max age of %s %s: %s", maxAge, temporalUnit,
				file.getAbsolutePath());

			_checkFileExists(file);

			BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

			FileTime fileTime = basicFileAttributes.lastModifiedTime();

			Duration age = Duration.between(fileTime.toInstant(), Instant.now());

			if (age.compareTo(Duration.of(maxAge, temporalUnit)) > 0) {
				throw new Exception(
					String.format("Cached file %s is older than max age of %s %s", file, maxAge, temporalUnit));
			}

			return Files.newInputStream(file.toPath());
		};
	}

	public static Resolver getLocalFileResolver(String file) {
		if (StringUtil.isNullOrEmpty(file)) {
			return () -> null;
		}

		return getLocalFileResolver(new File(file));
	}

	public static Resolver getURIResolver(File cacheDir, URI uri, String targetFileName) {
		return () -> {
			_print("Trying to get resource from URL %s", uri);

			URL url = uri.toURL();

			try {
				Path path = _downloadFile(url.toString(), cacheDir.toPath(), targetFileName);

				Files.setLastModifiedTime(path, FileTime.from(Instant.now()));

				return Files.newInputStream(path);
			}
			catch (Exception exception) {
				throw new Exception(
					String.format("Unable to get resource from URL %s: %s", url, exception.getMessage()), exception);
			}
		};
	}

	public static Resolver getURLResolver(File cacheDir, String url, String targetFileName) {
		return getURIResolver(cacheDir, URI.create(url), targetFileName);
	}

	public static <T> T readJson(Class<T> clazz, Resolver... resolvers) {
		return _withInputStream(inputStream -> _objectMapper.readValue(inputStream, clazz), resolvers);
	}

	public static void setTrace(boolean trace) {
		ResourceUtil._trace = trace;
	}

	@FunctionalInterface
	public interface Resolver {

		public InputStream resolve() throws Exception;

	}

	@FunctionalInterface
	public interface Transformer<T> {

		public T transform(InputStream inputStream) throws Exception;

	}

	private static void _checkFileExists(File file) throws Exception {
		if (!file.exists()) {
			throw new FileNotFoundException("Unable to get resource from local file: " + file.getAbsolutePath());
		}
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

	private static Path _downloadFile(String urlString, Path cacheDirPath, String targetFileName) throws Exception {
		URL downladURL = new URL(urlString);

		URI downladURI = downladURL.toURI();

		if (Objects.equals(downladURI.getScheme(), "file")) {
			return Paths.get(downladURI);
		}

		try (CloseableHttpClient closeableHttpClient = _getHttpClient(downladURL.toURI(), null, null)) {
			return _downloadFile(closeableHttpClient, downladURI, cacheDirPath, targetFileName);
		}
	}

	private static CloseableHttpClient _getHttpClient(URI uri, String userName, String password) {
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

	private static void _print(String message, Object... args) {
		if (_trace) {
			System.out.printf(message + "%n", args);
		}
	}

	private static <T> T _withInputStream(Transformer<T> transformer, Resolver... resolvers) {
		InputStream inputStream1 = null;

		for (Resolver resolver : resolvers) {
			try {
				inputStream1 = resolver.resolve();
			}
			catch (Exception exception) {
				_print(exception.getMessage());
			}

			if (inputStream1 != null) {
				break;
			}
		}

		if (inputStream1 == null) {
			_print("Resource not found");

			return null;
		}

		try (InputStream inputStream2 = inputStream1) {
			_print("Found resource");

			return transformer.transform(inputStream2);
		}
		catch (Exception exception) {
			throw new RuntimeException("Unable to read resource", exception);
		}
	}

	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static boolean _trace;

}