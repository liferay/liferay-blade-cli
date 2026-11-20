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

		List<String> promotedValues = ldvv.get();

		Assert.assertFalse(promotedValues.isEmpty());

		LiferayMoreVersionValidator lmvv = new LiferayMoreVersionValidator();

		List<String> allValues = lmvv.get();

		Assert.assertTrue(promotedValues.toString(), promotedValues.size() < allValues.size());
	}

}