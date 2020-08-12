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

package com.liferay.blade.cli;

import com.liferay.blade.cli.util.CombinedClassLoader;
import com.liferay.blade.cli.util.FileUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 */
public class ExtensionsClassLoaderSupplier implements AutoCloseable, Supplier<ClassLoader> {

	public ExtensionsClassLoaderSupplier(Path extensionsPath) {
		_extensionsPath = extensionsPath;
	}

	@Override
	public void close() throws Exception {
		if ((_serviceLoaderClassLoader != null) && (_serviceLoaderClassLoader instanceof Closeable)) {
			Closeable closeable = (Closeable)_serviceLoaderClassLoader;

			closeable.close();
		}

		if ((_tempExtensionsDirectory != null) && Files.exists(_tempExtensionsDirectory)) {
			try {
				FileUtil.deleteDirIfExists(_tempExtensionsDirectory);
			}
			catch (IOException ioe) {
			}
		}
	}

	@Override
	public ClassLoader get() {
		try {
			if (_serviceLoaderClassLoader == null) {
				_tempExtensionsDirectory = Files.createTempDirectory("extensions");

				FileUtil.copyDir(_extensionsPath, _tempExtensionsDirectory);

				_extractBladeExtensions(_tempExtensionsDirectory);

				URL[] jarUrls = _getJarUrls(_tempExtensionsDirectory);

				Thread thread = Thread.currentThread();

				ClassLoader currentClassLoader = thread.getContextClassLoader();

				ClassLoader urlClassLoader = new URLClassLoader(jarUrls, getClass().getClassLoader());

				_serviceLoaderClassLoader = new CombinedClassLoader(currentClassLoader, urlClassLoader);
			}

			return _serviceLoaderClassLoader;
		}
		catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}

	private static URL _convertUriToUrl(URI uri) {
		try {
			return uri.toURL();
		}
		catch (MalformedURLException murle) {
		}

		return null;
	}

	private static URL[] _getJarUrls(Path jarsPath) throws IOException {
		try (Stream<Path> files = Files.list(jarsPath)) {
			return files.filter(
				path -> {
					String file = path.toString();

					return file.endsWith(".jar");
				}
			).map(
				Path::toUri
			).map(
				ExtensionsClassLoaderSupplier::_convertUriToUrl
			).filter(
				url -> url != null
			).collect(
				Collectors.toSet()
			).toArray(
				new URL[0]
			);
		}
	}

	private void _extractBladeExtensions(Path extensionsDirectory) throws IOException {
		try (InputStream inputStream = Extensions.class.getResourceAsStream("/blade-extensions-versions.properties")) {
			if (inputStream == null) {
				return;
			}

			Properties properties = new Properties();

			properties.load(inputStream);

			Set<Object> keySet = properties.keySet();

			ClassLoader classLoader = Extensions.class.getClassLoader();

			try {
				Set<String> extensions = new HashSet<>();

				for (Object key : keySet) {
					String extension = key.toString() + "-" + properties.getProperty(key.toString()) + ".jar";

					if (!extension.startsWith("com.liferay.project.templates")) {
						if (classLoader.getResource(extension) != null) {
							extensions.add(extension);
						}
						else {
							System.err.println("Warning: Unable to locate " + extension);
						}
					}
				}

				for (String extension : extensions) {
					try (InputStream extensionInputStream = classLoader.getResourceAsStream(extension)) {
						Path extensionPath = extensionsDirectory.resolve(extension);

						Files.copy(extensionInputStream, extensionPath, StandardCopyOption.REPLACE_EXISTING);
					}
					catch (Throwable th) {
						StringBuilder sb = new StringBuilder();

						sb.append("Error encountered while loading custom extensions.");
						sb.append(System.lineSeparator());
						sb.append(th.getMessage());
						sb.append(System.lineSeparator());
						sb.append("Not loading extension " + extension + ".");
						sb.append(System.lineSeparator());

						String errorString = sb.toString();

						System.err.println(errorString);
					}
				}
			}
			catch (NoSuchElementException nsee) {
				StringBuilder sb = new StringBuilder();

				sb.append("Error encountered while loading custom extensions.");
				sb.append(System.lineSeparator());
				sb.append(nsee.getMessage());
				sb.append(System.lineSeparator());
				sb.append("Only built-in commands will be recognized.");
				sb.append(System.lineSeparator());

				String errorString = sb.toString();

				System.err.println(errorString);
			}
			catch (Throwable th) {
				String errorMessage = "Error encountered while loading custom extensions." + System.lineSeparator();

				throw new RuntimeException(errorMessage, th);
			}
		}
	}

	private final Path _extensionsPath;
	private ClassLoader _serviceLoaderClassLoader = null;
	private Path _tempExtensionsDirectory;

}