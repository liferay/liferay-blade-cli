/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class WorkspaceProductComparatorTest {

	@Test
	public void testSortByReleaseDate() throws Exception {
		List<Pair<String, ProductInfo>> pairs = new ArrayList<>();

		Map<String, String> map = new HashMap<>();

		map.put("releaseDate", "10/4/2019");

		pairs.add(new Pair<>("dxp-7.2-sp1", new ProductInfo(map)));

		map = new HashMap<>();

		map.put("releaseDate", "12/22/2018");

		pairs.add(new Pair<>("dxp-7.2-sp2", new ProductInfo(map)));

		map = new HashMap<>();

		map.put("releaseDate", "5/31/2019");

		pairs.add(new Pair<>("dxp-7.2-sp3", new ProductInfo(map)));

		map = new HashMap<>();

		map.put("releaseDate", "1/31/2011");

		pairs.add(new Pair<>("portal-7.1-ga1", new ProductInfo(map)));

		map = new HashMap<>();

		map.put("releaseDate", "1/31/2012");

		pairs.add(new Pair<>("portal-7.1-ga2", new ProductInfo(map)));

		map = new HashMap<>();

		map.put("releaseDate", "6/29/2020");

		pairs.add(new Pair<>("portal-7.3-ga1", new ProductInfo(map)));

		map.put("releaseDate", "6/29/2022");

		pairs.add(new Pair<>("dxp-2022.q3.1", new ProductInfo(map)));

		map.put("releaseDate", "6/29/2023");

		pairs.add(new Pair<>("dxp-2023.q2.1", new ProductInfo(map)));

		map.put("releaseDate", "7/29/2023");

		pairs.add(new Pair<>("dxp-2023.q3.1", new ProductInfo(map)));

		map.put("releaseDate", "8/29/2023");

		pairs.add(new Pair<>("dxp-2023.q3.2", new ProductInfo(map)));

		String[] actuals = pairs.stream(
		).sorted(
			new WorkspaceProductComparator()
		).map(
			Pair::first
		).collect(
			Collectors.toList()
		).toArray(
			new String[0]
		);

		String[] expecteds = {
			"dxp-2023.q3.2", "dxp-2023.q3.1", "dxp-2023.q2.1", "dxp-2022.q3.1", "dxp-7.2-sp1", "dxp-7.2-sp3",
			"dxp-7.2-sp2", "portal-7.3-ga1", "portal-7.1-ga2", "portal-7.1-ga1"
		};

		Assert.assertArrayEquals(expecteds, actuals);
	}

}