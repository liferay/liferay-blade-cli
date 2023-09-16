/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
		return line -> colorizer.apply(
			Ansi.ansi()
		).a(
			line
		).reset();
	}

	private static final Pattern[] _BRIGHT_GREEN_LINE_PATTERNS = {Pattern.compile(".*STOPPED.*")};

	private static final Pattern[] _BRIGHT_RED_LINE_PATTERNS = {
		Pattern.compile("^Error.*"), Pattern.compile(".*ERROR.*"), Pattern.compile(".*FATAL.*")
	};

	private static final Pattern[] _GREEN_LINE_PATTERNS = {Pattern.compile(".*STARTED.*")};

	private static final Pattern[] _YELLOW_LINE_PATTERNS = {Pattern.compile(".*WARN.*")};

}