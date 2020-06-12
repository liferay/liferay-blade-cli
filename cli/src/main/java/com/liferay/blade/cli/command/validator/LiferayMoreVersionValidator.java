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

import aQute.bnd.version.Version;

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Simon Jiang
 */
public class LiferayMoreVersionValidator implements ValidatorSupplier {

	public Comparator<? super String> comparator() {
		return (a, b) -> {
			if (a.startsWith("dxp") && !b.startsWith("dxp")) {
				return -1;
			}
			else if (a.startsWith("portal") && b.startsWith("dxp")) {
				return 1;
			}
			else if (a.startsWith("portal") && b.startsWith("commerce")) {
				return -1;
			}
			else if (a.startsWith("commerce") && !b.startsWith("commerce")) {
				return 1;
			}
			else {
				Matcher aMatcher = _versionPattern.matcher(a.substring(a.indexOf('-') + 1));
				Matcher bMatcher = _versionPattern.matcher(b.substring(b.indexOf('-') + 1));

				aMatcher.find();
				bMatcher.find();

				Version versionA = new Version(aMatcher.group(1));
				Version versionB = new Version(bMatcher.group(1));

				return -1 * versionA.compareTo(versionB);
			}
		};
	}

	@Override
	public List<String> get() {
		Map<String, ProductInfo> productInfoMap = BladeUtil.getProductInfo();

		Set<Map.Entry<String, ProductInfo>> entries = productInfoMap.entrySet();

		return entries.stream(
		).filter(
			entry -> {
				ProductInfo productInfo = entry.getValue();

				return productInfo.getTargetPlatformVersion() != null;
			}
		).map(
			Map.Entry::getKey
		).sorted(
			comparator()
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public void validate(String name, String value) throws ParameterException {
		List<String> possibleValues = new ArrayList<>(get());

		possibleValues.addAll(WorkspaceConstants.originalLiferayVersions);

		if (!possibleValues.contains(value)) {
			throw new ParameterException(name + " is not a valid value.");
		}
	}

	private static final Pattern _versionPattern = Pattern.compile("([0-9\\.]+).*");

}