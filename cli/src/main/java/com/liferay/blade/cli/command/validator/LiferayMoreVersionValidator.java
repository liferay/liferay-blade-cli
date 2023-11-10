/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductKeyUtil;
import com.liferay.project.templates.extensions.util.VersionUtil;

import java.util.ArrayList;
import java.util.List;
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

		Set<String> allTargetPlatformVersions = BladeUtil.getWorkspaceProductTargetPlatformVersions(false);

		possibleValues.addAll(WorkspaceConstants.originalLiferayVersions);

		if ((!possibleValues.contains(value) && !allTargetPlatformVersions.contains(value)) ||
			(!ProductKeyUtil.verifyPortalDxpWorkspaceProduct(value) && !VersionUtil.isLiferayVersion(value) &&
			 !ProductKeyUtil.verifyCommerceWorkspaceProduct(value))) {

			throw new ParameterException(value + " is not a valid value.");
		}
	}

}