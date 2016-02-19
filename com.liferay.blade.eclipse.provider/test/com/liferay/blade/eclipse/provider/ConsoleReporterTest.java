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

package com.liferay.blade.eclipse.provider;

import static org.junit.Assert.assertEquals;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConsoleReporterTest {

	@Test
	public void reportLongFormatTest() throws Exception {
		String expectString =
				"   ____________________________________________________________\n" +
				"   | Title| Summary| Type      | Ticket| File            | Line|\n" +
				"   |===========================================================|\n" +
				"1. | test | summary| java      | 100   | file1.java      | 10  |\n" +
				"2. | test1| summary| properties| 101   | file2.properties| 12  |\n";

		Problem problem = new Problem();
		problem.title = "test";
		problem.summary = "summary";
		problem.ticket = "100";
		problem.type = "java";
		problem.file = new File("file1.java");
		problem.lineNumber = 10;

		Problem problem1 = new Problem();
		problem1.title = "test1";
		problem1.summary = "summary";
		problem1.ticket = "101";
		problem1.type = "properties";
		problem1.file = new File("file2.properties");
		problem1.lineNumber = 12;

		List<Problem> problems = new ArrayList<>();
		problems.add(problem);
		problems.add(problem1);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		System.setOut(printStream);

		ConsoleReporter reporter = new ConsoleReporter();
		reporter.beginReporting(Migration.DETAIL_LONG, baos);

		for (Problem p : problems) {
			reporter.report(p);
		}

		reporter.endReporting();

		String realString = baos.toString().replace("\r", "");

		assertEquals(expectString, realString);
	}

	@Test
	public void reportShortFormatTest() throws Exception {
		String expectString =
				"   ___________________________________________\n" +
				"   | Title| Type      | File            | Line|\n" +
				"   |==========================================|\n" +
				"1. | test | java      | file1.java      | 10  |\n" +
				"2. | test1| properties| file2.properties| 12  |\n";

		Problem problem = new Problem();
		problem.title = "test";
		problem.type = "java";
		problem.file = new File("file1.java");
		problem.lineNumber = 10;

		Problem problem1 = new Problem();
		problem1.title = "test1";
		problem1.type = "properties";
		problem1.file = new File("file2.properties");
		problem1.lineNumber = 12;

		List<Problem> problems = new ArrayList<>();
		problems.add(problem);
		problems.add(problem1);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		System.setOut(printStream);

		ConsoleReporter reporter = new ConsoleReporter();
		reporter.beginReporting(Migration.DETAIL_SHORT, baos);

		for (Problem p : problems) {
			reporter.report(p);
		}

		reporter.endReporting();

		baos = new ByteArrayOutputStream();
		printStream = new PrintStream(baos);
		System.setOut(printStream);

		reporter.beginReporting(Migration.DETAIL_SHORT, baos);

		for (Problem p : problems) {
			reporter.report(p);
		}

		reporter.endReporting();

		String realString = baos.toString().replace("\r", "");

		assertEquals(expectString, realString);
	}

}