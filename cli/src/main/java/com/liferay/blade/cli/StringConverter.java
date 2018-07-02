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
import java.io.OutputStream;

import java.nio.charset.Charset;

/**
 * @author Christopher Bryan Boyd
 */
public class StringConverter {

	public static String fromInputStream(InputStream inputStream, Charset charset) throws IOException {
		try (ByteArrayOutputStream outputStream = _getOutputStream(inputStream)) {
			return new String(outputStream.toByteArray(), charset);
		}
		finally {
			inputStream.close();
		}
	}

	public static String frommInputStream(InputStream inputStream) throws IOException {
		return fromInputStream(inputStream, Charset.defaultCharset());
	}

	private static ByteArrayOutputStream _getOutputStream(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		_readInputStreamToOutputStream(inputStream, outputStream);

		return outputStream;
	}

	private static void _readInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) {
		_readInputStreamToOutputStream(inputStream, outputStream, _DEFAULT_BUFFER_SIZE);
	}

	private static void _readInputStreamToOutputStream(
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

	private static final int _DEFAULT_BUFFER_SIZE = 1024;

}