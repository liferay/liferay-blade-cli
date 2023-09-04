/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 */
public class CombinedClassLoader extends ClassLoader implements AutoCloseable {

	public CombinedClassLoader(ClassLoader... classLoaders) {
		for (ClassLoader classLoader : classLoaders) {
			_add(classLoader);
		}
	}

	@Override
	public void close() throws Exception {
		for (ClassLoader classLoader : _classLoaders) {
			try {
				if (classLoader instanceof Closeable) {
					Closeable closeable = (Closeable)classLoader;

					closeable.close();
				}
			}
			catch (Throwable th) {
			}
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/');

		path = path + ".class";

		URL url = findResource(path);

		if (url == null) {
			throw new ClassNotFoundException(name);
		}

		try {
			ByteBuffer byteCode = _loadResourceFromClasspath(url);

			return defineClass(name, byteCode, null);
		}
		catch (IOException ioException) {
			throw new ClassNotFoundException(name, ioException);
		}
	}

	@Override
	protected URL findResource(String name) {
		Stream<ClassLoader> urlStream = _classLoaders.stream();

		return urlStream.map(
			c -> c.getResource(name)
		).filter(
			Objects::nonNull
		).findAny(
		).orElse(
			null
		);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Stream<ClassLoader> urlStream = _classLoaders.stream();

		return Collections.enumeration(
			urlStream.map(
				c -> _getResources(c, name)
			).map(
				Collections::list
			).flatMap(
				Collection::stream
			).collect(
				Collectors.toList()
			));
	}

	private void _add(ClassLoader classLoader) {
		_classLoaders.add(classLoader);
	}

	private Enumeration<URL> _getResources(ClassLoader classLoader, String name) {
		try {
			return classLoader.getResources(name);
		}
		catch (IOException ioException) {
		}

		return null;
	}

	private ByteBuffer _loadResourceFromClasspath(URL url) throws IOException {
		try (InputStream inputStream = url.openStream()) {
			byte[] buffer = new byte[1024];

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(buffer.length);

			int bytesCount = -1;

			while ((bytesCount = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesCount);
			}

			byte[] output = byteArrayOutputStream.toByteArray();

			return ByteBuffer.wrap(output);
		}
	}

	private Collection<ClassLoader> _classLoaders = new ArrayList<>();

}