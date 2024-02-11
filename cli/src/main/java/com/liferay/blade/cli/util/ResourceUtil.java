/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Objects;
import java.util.Properties;

import org.gradle.api.GradleException;

/**
 * @author Drew Brokke
 */
public class ResourceUtil {

	public static Resolver getClassLoaderResolver(String resourcePath) {
		return () -> Objects.requireNonNull(
			ResourceUtil.class.getResourceAsStream(resourcePath),
			"Unable to get resource from class path: " + resourcePath);
	}

	public static Resolver getLocalFileResolver(File file) {
		return () -> {
			if (!file.exists()) {
				throw new Exception("Unable to get resource from local file: " + file.getAbsolutePath());
			}

			return Files.newInputStream(file.toPath());
		};
	}

	public static Resolver getLocalFileResolver(String filePath) {
		if (Objects.isNull(filePath)) {
			return _nullResolver;
		}

		return getLocalFileResolver(new File(filePath));
	}

	public static Resolver getURLResolver(File cacheDir, String url, String targetFileName) {
		return () -> {
			try {
				return Files.newInputStream(BladeUtil.downloadFile(url, cacheDir.toPath(), targetFileName));
			}
			catch (Exception exception) {
				throw new Exception(
					String.format("Unable to get resource from URL %s: %s", url, exception.getMessage()), exception);
			}
		};
	}

	public static File readFile(Path path, Resolver... resolvers) {
		return _withInputStream(
			inputStream -> {
				Files.copy(inputStream, path);

				return path.toFile();
			},
			resolvers);
	}

	public static <T> T readJson(Class<T> clazz, Resolver... resolvers) {
		return _withInputStream(inputStream -> _gson.fromJson(new InputStreamReader(inputStream), clazz), resolvers);
	}

	public static Properties readProperties(Resolver... resolvers) {
		return _withInputStream(
			inputStream -> {
				Properties properties = new Properties();

				properties.load(inputStream);

				return properties;
			},
			resolvers);
	}

	@FunctionalInterface
	public interface Resolver {

		public InputStream resolve() throws Exception;

	}

	@FunctionalInterface
	public interface Transformer<T> {

		public T transform(InputStream inputStream) throws Exception;

	}

	private static <T> T _withInputStream(Transformer<T> transformer, Resolver... resolvers) {
		for (Resolver resolver : resolvers) {
			try (InputStream inputStream = resolver.resolve()) {
				if (inputStream != null) {
					return transformer.transform(inputStream);
				}
			}
			catch (Exception exception) {
				System.out.println(exception.getMessage());
			}
		}

		throw new GradleException("Unable to get resource");
	}

	private static final Gson _gson = new Gson();
	private static final Resolver _nullResolver = () -> null;

}