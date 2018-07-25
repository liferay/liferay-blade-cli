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
		try (InputStream is = inputStream;
			ByteArrayOutputStream outputStream = _getOutputStream(is)) {

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
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
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
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static ByteArrayOutputStream _getOutputStream(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		readInputStreamToOutputStream(inputStream, outputStream);

		return outputStream;
	}

	private static final int _DEFAULT_BUFFER_SIZE = 1024;

}