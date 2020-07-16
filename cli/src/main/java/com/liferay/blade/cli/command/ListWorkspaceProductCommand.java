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

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.validator.WorkspaceProductComparator;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Simon Jiang
 */
public class ListWorkspaceProductCommand extends BaseCommand<ListWorkspaceProductArgs> {

	@Override
	public void execute() throws Exception {
		_printPromotedWorkspaceProducts();
	}

	@Override
	public Class<ListWorkspaceProductArgs> getArgsClass() {
		return ListWorkspaceProductArgs.class;
	}

	@SuppressWarnings("unchecked")
	private void _printPromotedWorkspaceProducts() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Map<String, Object> productInfos = BladeUtil.getProductInfos();

		List<String> promotedProductKeys = productInfos.entrySet(
		).stream(
		).filter(
			entry -> Objects.nonNull(productInfos.get(entry.getKey()))
		).filter(
			entry -> {
				ProductInfo productInfo = new ProductInfo((Map<String, String>)productInfos.get(entry.getKey()));

				return productInfo.isPromoted();
			}
		).map(
			Map.Entry::getKey
		).sorted(
			new WorkspaceProductComparator()
		).collect(
			Collectors.toList()
		);

		for (String productKey : promotedProductKeys) {
			bladeCLI.out(productKey);
		}
	}

}