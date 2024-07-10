/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Simon Jiang
 */
public class LiferayMoreVersionValidator implements ValidatorSupplier {

	@Override
	public List<String> get() {
		return ReleaseUtil.getReleaseEntryStream(
		).map(
			ReleaseEntry::getReleaseKey
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		ReleaseEntry releaseEntry = BladeUtil.getReleaseEntry(value);

		if (releaseEntry == null) {
			throw new ParameterException(value + " is not a valid value.");
		}
	}

}