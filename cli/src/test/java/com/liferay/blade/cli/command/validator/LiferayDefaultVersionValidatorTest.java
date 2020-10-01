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

		Assert.assertTrue(values.size() == 10);
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