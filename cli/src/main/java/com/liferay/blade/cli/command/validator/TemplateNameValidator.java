/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class TemplateNameValidator implements ValidatorSupplier {

	@Override
	public List<String> get() {
		try {
			return new ArrayList<>(BladeUtil.getTemplateNames(BladeCLI.instance));
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		if (Objects.equals(value, "portlet")) {
			value = "mvc-portlet";
		}

		Collection<String> possibleValues = get();

		if (!possibleValues.contains(value)) {
			throw new ParameterException(value + " is not among the possible values.");
		}
	}

}