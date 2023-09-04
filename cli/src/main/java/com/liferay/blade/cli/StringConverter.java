/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author Christopher Bryan Boyd
 */
public class StringConverter {

	public static String fromInputStream(final InputStream inputStream, Charset charset) throws IOException {
		try (InputStream is = inputStream; ByteArrayOutputStream outputStream = _getOutputStream(is)) {
			return new String(outputStream.toByteArray(), charset);
		}
	}

	public static String frommInputStream(InputStream inputStream) throws IOException {
		return fromInputStream(inputStream, Charset.defaultCharset());
	}

	public static void readInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) {
		readInputStreamToOutputStream(inputStream, outputStream, _DEFAULT_BUFFER_SIZE);
	}

	public static void readInputStreamToOutputStream(
		InputStream inputStream, OutputStream outputStream, int bufferSize) {

		try {
			byte[] buffer = new byte[bufferSize];

			int length;

			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void readInputStreamToPrintStream(InputStream inputStream, PrintStream printStream) {
		readInputStreamToPrintStream(inputStream, printStream, _DEFAULT_BUFFER_SIZE);
	}

	public static void readInputStreamToPrintStream(InputStream inputStream, PrintStream printStream, int bufferSize) {
		try {
			final char[] buffer = new char[bufferSize];
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			int length;

			while ((length = reader.read(buffer, 0, buffer.length)) != -1) {
				printStream.append(CharBuffer.wrap(buffer), 0, length);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static ByteArrayOutputStream _getOutputStream(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		readInputStreamToOutputStream(inputStream, outputStream);

		return outputStream;
	}

	private static final int _DEFAULT_BUFFER_SIZE = 1024;

}