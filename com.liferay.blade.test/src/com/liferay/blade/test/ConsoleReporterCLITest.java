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

package com.liferay.blade.test;

import static org.junit.Assert.assertEquals;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.Reporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class ConsoleReporterCLITest {

	@Test
	public void reportLongFormatTest() throws Exception {
		String expectString =
				"   ____________________________________________________\n" +
				"   | Title| Summary    | Type| Ticket  | File    | Line|\n" +
				"   |===================================================|\n" +
				"1. | bar  | bar summary| jsp | LPS-867 | Bar.java| 20  |\n" +
				"2. | foo  | foo summary| java| LPS-5309| Foo.java| 10  |\n";


		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		System.setOut(printStream);

		ServiceReference<Reporter> sr = context
			.getServiceReference(Reporter.class);
		Reporter reporter = context.getService(sr);
		reporter.beginReporting(Migration.DETAIL_LONG, baos);
		reporter.report(new Problem(
				"foo", "foo summary", "java", "LPS-5309", new File("Foo.java"), 10, 100,
				110, null, null, Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
		reporter.report(new Problem(
				"bar", "bar summary", "jsp", "LPS-867", new File("Bar.java"), 20, 200,
				220, null, null, Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
		reporter.endReporting();

		String realString = baos.toString().replace("\r", "");

		assertEquals(expectString, realString);
	}

	@Test
	public void reportShortFormatTest() throws Exception {
		String expectString =
				"   _____________________________\n" +
				"   | Title| Type| File    | Line|\n" +
				"   |============================|\n" +
				"1. | bar  | jsp | Bar.java| 20  |\n" +
				"2. | foo  | java| Foo.java| 10  |\n";

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baos);
		System.setOut(printStream);

		ServiceReference<Reporter> sr = context
				.getServiceReference(Reporter.class);
		Reporter reporter = context.getService(sr);
		reporter.beginReporting(Migration.DETAIL_SHORT, baos);
		reporter.report(new Problem(
				"foo", "foo summary", "java", "LPS-867", new File("Foo.java"), 10, 100,
				110, null, null, Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
		reporter.report(new Problem(
				"bar", "bar summary", "jsp", "LPS-5309", new File("Bar.java"), 20, 200,
				220, null, null, Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID, Problem.MARKER_ERROR));
		reporter.endReporting();

		String realString = baos.toString().replace("\r", "");

		assertEquals(expectString, realString);
	}

	private final BundleContext context = FrameworkUtil.getBundle(
		this.getClass()).getBundleContext();

}