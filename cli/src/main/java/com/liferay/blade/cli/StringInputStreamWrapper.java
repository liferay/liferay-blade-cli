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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Supplier;

/**
 * @author Christopher Bryan Boyd
 */
public class StringInputStreamWrapper implements AutoCloseable, Supplier<String> {
	
	@Override
	public String get() {
		try {
			
			ByteArrayOutputStream result = _getOutputStream();
			
			return new String(result.toByteArray(), _charset);
		} 
		catch (Exception e) {
			_closeSafe();
			throw new RuntimeException(e);
		}

	}

	private void _closeSafe() {
		try {
			
			close();
		} 
		catch (Exception ignoredException) {
			
		}
	}

	private static void _readInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) {
		_readInputStreamToOutputStream(inputStream, outputStream, _DEFAULT_BUFFER_SIZE);
	}
	
	private static void _readInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream, int bufferSize) {
		try {
			byte[] buffer = new byte[bufferSize];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private StringInputStreamWrapper(InputStream inputStream, Charset charset) {

		_inputStream = inputStream;
		_charset = charset;
	}

	@Override
	public String toString() {
		return get();
	}
	
	private ByteArrayOutputStream _getOutputStream() {
		if (_outputStream == null) {
			_outputStream = new ByteArrayOutputStream();
			
			_readInputStreamToOutputStream(_inputStream, _outputStream);
		}
		return _outputStream;
	}
	
	private ByteArrayOutputStream _outputStream;
	private Charset _charset;
	private InputStream _inputStream;
	private boolean _closed = false;
	private static final int _DEFAULT_BUFFER_SIZE = 1024;
	
	@Override
	public void close() throws Exception {
		if (!_closed) {
			_closed = true;
			_close(_inputStream);
			_close(_outputStream);
		}
	}

	private static void _close(Closeable closeable) {
		if (closeable != null) {

			try { 
				closeable.close();
			} 
			catch (IOException ignored) {
				
			}
		}
	}
	
	public static String get(InputStream inputStream) throws Exception {
		
		return get(inputStream, Charset.defaultCharset());
	}

	public static String get(InputStream inputStream, Charset charset) throws Exception {

		try (StringInputStreamWrapper streamWrapper = newInstance(inputStream, charset)) {
			
			return streamWrapper.get();
		}
	}
	
	public static StringInputStreamWrapper newInstance(InputStream inputStream) {
		return newInstance(inputStream, Charset.defaultCharset());
	}

	public static StringInputStreamWrapper newInstance(InputStream inputStream, Charset charset) {
		return new StringInputStreamWrapper(inputStream, charset);
	}
}
