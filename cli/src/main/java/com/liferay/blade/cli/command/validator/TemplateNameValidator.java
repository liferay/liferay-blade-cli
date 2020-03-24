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

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class TemplateNameValidator implements ValidatorSupplier {

	@Override
	public Collection<String> get() {
		try {
			return new ArrayList<>(BladeUtil.getTemplateNames(BladeCLI.instance));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		if (Objects.equals("portlet", value)) {
			value = "mvc-portlet";
		}

		Collection<String> possibleValues = get();

		if (!possibleValues.contains(value)) {
			throw new ParameterException(value + " is not among the possible values.");
		}
	}

}