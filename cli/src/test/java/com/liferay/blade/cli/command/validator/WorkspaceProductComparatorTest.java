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
			"dxp-7.2-sp1", "dxp-7.2-sp3", "dxp-7.2-sp2", "portal-7.3-ga1", "portal-7.1-ga2", "portal-7.1-ga1"
		};

		Assert.assertArrayEquals(expecteds, actuals);
	}

}