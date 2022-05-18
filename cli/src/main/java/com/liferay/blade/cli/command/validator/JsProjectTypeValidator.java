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