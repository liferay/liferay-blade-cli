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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LiferayDefaultVersionValidator extends LiferayMoreVersionValidator {

	@Override
	public List<String> get() {
		List<String> more = super.get();

		return more.stream(
		).filter(
			_promoted::contains
		).collect(
			Collectors.toList()
		);
	}

	private static final List<String> _promoted = new ArrayList<>();

	{
		_promoted.add("dxp-7.2-sp1");
		_promoted.add("dxp-7.1-sp4");
		_promoted.add("dxp-7.0-sp13");
		_promoted.add("portal-7.3-ga4");
		_promoted.add("portal-7.2-ga2");
		_promoted.add("portal-7.1-ga4");
		_promoted.add("portal-7.0-ga7");
		_promoted.add("commerce-2.0.7-7.2");
	}

}