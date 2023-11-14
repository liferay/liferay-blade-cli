/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.ProductKeyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LiferayMoreVersionValidatorTest {

	@Test
	public void testSort() throws Exception {
		LiferayMoreVersionValidator lmvv = new LiferayMoreVersionValidator();

		List<String> vals = lmvv.get();

		String first = vals.get(0);

		Assert.assertTrue(first, first.startsWith("dxp"));

		String second = vals.get(1);

		Assert.assertTrue(second, second.startsWith("dxp"));

		String last = vals.get(vals.size() - 1);

		Assert.assertTrue(last, last.startsWith("commerce"));
	}

	@Test
	public void testWithRandom() throws Exception {
		List<String> randomLines = new ArrayList<>();

		try (Scanner scanner = new Scanner(
				FileUtil.collect(LiferayMoreVersionValidatorTest.class.getResourceAsStream("random.txt")))) {

			while (scanner.hasNextLine()) {
				randomLines.add(scanner.nextLine());
			}
		}

		List<String> sortedLines = new ArrayList<>();

		try (Scanner scanner = new Scanner(
				FileUtil.collect(LiferayMoreVersionValidatorTest.class.getResourceAsStream("sorted.txt")))) {

			while (scanner.hasNextLine()) {
				sortedLines.add(scanner.nextLine());
			}
		}

		List<String> sorted = randomLines.stream(
		).sorted(
			ProductKeyUtil.comparator
		).collect(
			Collectors.toList()
		);

		Assert.assertEquals(
			sortedLines.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			),
			sorted.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			));
	}

}