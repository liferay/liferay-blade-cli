/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class ProductKeyUtilTest {

	@Test
	public void testComparator() throws Exception {
		List<String> expectedKeys = Arrays.asList(
			"dxp-2023.q3.2", "dxp-2023.q2.1", "dxp-2022.q3.1", "dxp-7.2-sp3", "dxp-7.2-sp2", "dxp-7.2-sp1",
			"portal-7.3-ga1", "portal-7.1-ga2", "portal-7.1-ga1", "commerce-2.0.7-7.2", "commerce-2.0.7-7.1",
			"commerce-2.0.6");

		List<String> actualKeys = new ArrayList<>(expectedKeys);

		actualKeys.sort(null);

		actualKeys.sort(ProductKeyUtil.comparator);

		Assert.assertEquals(
			expectedKeys.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			),
			actualKeys.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			));
	}

}