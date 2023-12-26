/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductKeyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Simon Jiang
 */
public class LiferayMoreVersionValidator implements ValidatorSupplier {

	@Override
	public List<String> get() {
		return BladeUtil.getWorkspaceProductKeys(false);
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		List<String> possibleValues = new ArrayList<>(get());

		Map<String, ProductKeyInfo> workspaceProductTargetPlatformVersions =
			BladeUtil.getWorkspaceProductTargetPlatformVersions(false);

		possibleValues.addAll(WorkspaceConstants.originalLiferayVersions);

		Set<String> allTargetPlatformVersions = workspaceProductTargetPlatformVersions.keySet();

		if (!possibleValues.contains(value) && !allTargetPlatformVersions.contains(value)) {
			ProductKeyInfo productKeyInfo = workspaceProductTargetPlatformVersions.get(value);

			if (!productKeyInfo.isQuarterly() &&
				!(Objects.equals(productKeyInfo.getProduct(), "dxp") ||
				  Objects.equals(productKeyInfo.getProduct(), "portal"))) {

				throw new ParameterException(value + " is not a valid value.");
			}
		}
	}

}