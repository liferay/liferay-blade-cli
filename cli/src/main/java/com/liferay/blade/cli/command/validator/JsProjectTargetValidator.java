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
			boolean jsBatchModel = createArgs.isJsBatchModel();

			if (!jsBatchModel) {
				return true;
			}

			if (Objects.isNull(createArgs.getJsProjectTarget())) {
				return false;
			}
		}

		return true;
	}

}