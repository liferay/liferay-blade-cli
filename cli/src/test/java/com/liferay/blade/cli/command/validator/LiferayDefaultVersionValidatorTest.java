/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LiferayDefaultVersionValidatorTest {

	@Test
	public void testPromoted() throws Exception {
		LiferayDefaultVersionValidator ldvv = new LiferayDefaultVersionValidator();

		List<String> values = ldvv.get();

		Assert.assertTrue(values.toString(), values.size() < 20);
	}

	@Test
	public void testSort() throws Exception {
		LiferayDefaultVersionValidator ldvv = new LiferayDefaultVersionValidator();

		List<String> vals = ldvv.get();

		String first = vals.get(0);

		Assert.assertTrue(first, first.startsWith("dxp"));

		String second = vals.get(1);

		Assert.assertTrue(second, second.startsWith("dxp"));

		String last = vals.get(vals.size() - 1);

		Assert.assertTrue(last, last.startsWith("commerce"));
	}

}