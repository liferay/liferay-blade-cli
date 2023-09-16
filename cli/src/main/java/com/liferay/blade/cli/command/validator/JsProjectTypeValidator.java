/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.command.CreateArgs;
import com.liferay.blade.cli.util.Constants;
import com.liferay.blade.cli.util.ListUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Jiang
 */
public class JsProjectTypeValidator implements ValidatorFunctionPredicate<CreateArgs> {

	@Override
	public List<String> apply(CreateArgs createArgs) {
		if (Objects.equals(createArgs.getJsProjectTarget(), Constants.DEFAULT_POSSIBLE_TARGET_VALUES[1])) {
			return Arrays.asList(Constants.DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES[2]);
		}

		return Arrays.asList(Constants.DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES);
	}

	@Override
	public boolean test(CreateArgs createArgs) {
		if (Objects.equals(createArgs.getTemplate(), "js-widget")) {
			boolean jsInteractiveModel = createArgs.isJsInteractiveModel();

			if (jsInteractiveModel) {
				return true;
			}

			String jsProjectType = createArgs.getJsProjectType();

			if (Objects.isNull(jsProjectType)) {
				return false;
			}

			if (Objects.equals(createArgs.getJsProjectTarget(), Constants.DEFAULT_POSSIBLE_TARGET_VALUES[1])) {
				if (!Objects.equals(jsProjectType, Constants.DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES[2])) {
					return false;
				}
			}
			else {
				if (!ListUtil.contains(Arrays.asList(Constants.DEFAULT_POSSIBLE_PROJECT_TYPE_VALUES), jsProjectType)) {
					return false;
				}
			}
		}

		return true;
	}

}