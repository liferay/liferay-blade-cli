/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.charset.Charset;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

	public static StringPrintStream newFilteredInstance(Collection<Predicate<String>> filters) {
		return new FilteringPrintStream(new ByteArrayOutputStream(), Charset.defaultCharset(), filters);
	}

	@SafeVarargs
	public static StringPrintStream newFilteredInstance(Predicate<String>... filters) {
		return new FilteringPrintStream(new ByteArrayOutputStream(), Charset.defaultCharset(), Arrays.asList(filters));
	}

	public static StringPrintStream newInstance() {
		return newInstance(Charset.defaultCharset());
	}

	public static StringPrintStream newInstance(Charset charset) {
		return new StringPrintStream(new ByteArrayOutputStream(), charset);
	}

	public static String toString(InputStream inputStream) {
		StringPrintStream stringPrintStream = fromInputStream(inputStream);

		return stringPrintStream.toString();
	}

	@Override
	public String get() {
		return new String(_outputStream.toByteArray(), _charset);
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

	private static class FilteringPrintStream extends StringPrintStream {

		public FilteringPrintStream(
			ByteArrayOutputStream outputStream, Charset charset, Collection<Predicate<String>> filters) {

			super(outputStream, charset);

			_filters = filters;
		}

		@Override
		public String get() {
			StringBuilder stringBuilder = new StringBuilder();
			String results = super.get();

			try (Scanner scanner = new Scanner(results)) {
				while (scanner.hasNext()) {
					String line = scanner.nextLine();

					Stream<Predicate<String>> filtersStream = _filters.stream();

					if (filtersStream.anyMatch(predicate -> predicate.test(line))) {
						continue;
					}

					stringBuilder.append(line + System.lineSeparator());
				}
			}

			return stringBuilder.toString();
		}

		private Collection<Predicate<String>> _filters;

	}

}