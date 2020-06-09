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

import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Simon Jiang
 */
public class LiferayMoreVersionValidator extends LiferayDefaultVersionValidator {

	@Override
	public Collection<String> get() {
		Map<String, ProductInfo> productInfoMap = BladeUtil.getProductInfo();

		List<String> possibleLiferayProducts = new CopyOnWriteArrayList<>();

		possibleLiferayProducts.addAll(WorkspaceConstants.originalLiferayVersions);

		productInfoMap.forEach(
			(productKey, productInfo) -> {
				if (!BladeUtil.isEmpty(productInfo.getTargetPlatformVersion())) {
					possibleLiferayProducts.add(productKey);
				}
			});

		Collections.sort(possibleLiferayProducts);

		return possibleLiferayProducts;
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		Collection<String> possibleValues = get();

		if (!possibleValues.contains(value)) {
			throw new ParameterException(name + " is not a valid value.");
		}
	}

}