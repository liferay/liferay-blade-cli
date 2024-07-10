/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

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
		return ReleaseUtil.getReleaseEntryStream(
		).filter(
			ReleaseEntry::isPromoted
		).map(
			ReleaseEntry::getReleaseKey
		).collect(
			Collectors.toList()
		);
	}

}