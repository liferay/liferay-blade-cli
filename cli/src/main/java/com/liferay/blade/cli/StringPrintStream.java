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
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.charset.Charset;

import java.util.Scanner;
import java.util.function.Supplier;

/**
 * @author Christopher Bryan Boyd
 */
public class StringPrintStream extends PrintStream implements Supplier<String> {

	public static StringPrintStream fromInputStream(InputStream inputStream) {
		StringPrintStream stringPrintStream = new StringPrintStream(
			new ByteArrayOutputStream(), Charset.defaultCharset());

		StringConverter.readInputStreamToPrintStream(inputStream, stringPrintStream);

		return stringPrintStream;
	}

	public static StringPrintStream newInstance() {
		return newInstance(Charset.defaultCharset());
	}

	public static StringPrintStream newInstance(Charset charset) {
		return new StringPrintStream(new ByteArrayOutputStream(), charset);
	}

	@Override
	public String get() {
		StringBuilder stringBuilder = new StringBuilder();
		String results = new String(_outputStream.toByteArray(), _charset);

		try (Scanner scanner = new Scanner(results)) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();

				if (line.startsWith("SLF4J:")) {
					continue;
				}
				

				if (line.contains("LC_ALL: cannot change locale")) {
					continue;
				}

				stringBuilder.append(line + System.lineSeparator());
			}
		}

		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return get();
	}

	private StringPrintStream(ByteArrayOutputStream outputStream, Charset charset) {
		super(outputStream);

		_outputStream = outputStream;
		_charset = charset;
	}

	private Charset _charset;
	private ByteArrayOutputStream _outputStream;

}