/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.command.CreateArgs;
import com.liferay.blade.cli.util.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Jiang
 */
public class JsProjectTargetValidator implements ValidatorFunctionPredicate<CreateArgs> {

	@Override
	public List<String> apply(CreateArgs t) {
		return Arrays.asList(Constants.DEFAULT_POSSIBLE_TARGET_VALUES);
	}

	@Override
	public boolean test(CreateArgs createArgs) {
		if (Objects.equals(createArgs.getTemplate(), "js-widget")) {
			boolean jsInteractiveModel = createArgs.isJsInteractiveModel();

			if (jsInteractiveModel) {
				return true;
			}

			if (Objects.isNull(createArgs.getJsProjectTarget())) {
				return false;
			}
		}

		return true;
	}

}