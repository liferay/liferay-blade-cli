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

package com.liferay.blade.cli.util;

import java.io.PrintStream;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.fusesource.jansi.Ansi;

/**
 * @author Gregory Amerson
 */
public class AnsiLinePrinter {

	public static void println(PrintStream printStream, String line) {
		AtomicBoolean printed = new AtomicBoolean(false);

		Consumer<? super Ansi> println = ansi -> {
			printed.set(true);
			printStream.println(ansi);
		};

		_print(_BRIGHT_RED_LINE_PATTERNS, line, _toAnsiColor(Ansi::fgBrightRed), println);
		_print(_BRIGHT_GREEN_LINE_PATTERNS, line, _toAnsiColor(Ansi::fgBrightGreen), println);
		_print(_YELLOW_LINE_PATTERNS, line, _toAnsiColor(Ansi::fgYellow), println);
		_print(_GREEN_LINE_PATTERNS, line, _toAnsiColor(Ansi::fgGreen), println);

		if (!printed.get()) {
			printStream.println(line);
		}
	}

	private static void _print(
		Pattern[] patterns, String line, Function<? super String, ? extends Ansi> toAnsi,
		Consumer<? super Ansi> println) {

		Stream.of(
			patterns
		).map(
			pattern -> pattern.matcher(line)
		).filter(
			Matcher::matches
		).findAny(
		).map(
			matcher -> line
		).map(
			toAnsi
		).ifPresent(
			println
		);
	}

	private static Function<? super String, ? extends Ansi> _toAnsiColor(Function<Ansi, Ansi> colorizer) {
		return line -> {
			return colorizer.apply(
				Ansi.ansi()
			).a(
				line
			).reset();
		};
	}

	private static final Pattern[] _BRIGHT_GREEN_LINE_PATTERNS = {Pattern.compile(".*STOPPED.*")};

	private static final Pattern[] _BRIGHT_RED_LINE_PATTERNS =
		{Pattern.compile("^Error.*"), Pattern.compile(".*ERROR.*"), Pattern.compile(".*FATAL.*")};

	private static final Pattern[] _GREEN_LINE_PATTERNS =
		{Pattern.compile(".*STARTED.*"), Pattern.compile(".*STOPPED.*")};

	private static final Pattern[] _YELLOW_LINE_PATTERNS = {Pattern.compile(".*WARN.*")};

}