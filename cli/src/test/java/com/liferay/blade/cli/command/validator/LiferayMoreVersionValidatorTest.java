/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LiferayMoreVersionValidatorTest {

	@Test
	public void testGet() throws Exception {
		List<String> expectedReleaseKeys = ReleaseUtil.getReleaseEntryStream(
		).map(
			ReleaseEntry::getReleaseKey
		).collect(
			Collectors.toList()
		);

		LiferayMoreVersionValidator lmvv = new LiferayMoreVersionValidator();

		List<String> vals = lmvv.get();

		Assert.assertEquals(vals.toString(), expectedReleaseKeys.size(), vals.size());

		for (int i = 0; i < expectedReleaseKeys.size(); i++) {
			String actual = vals.get(i);
			String expected = expectedReleaseKeys.get(i);

			Assert.assertEquals(expected, actual);
		}
	}

}