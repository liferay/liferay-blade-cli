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

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProductInfo;
import com.liferay.blade.cli.util.StringUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class WorkspaceProductComparator implements Comparator<Pair<String, ProductInfo>> {

	@Override
	public int compare(Pair<String, ProductInfo> aPair, Pair<String, ProductInfo> bPair) {
		String aKey = aPair.first();
		String bKey = bPair.first();

		if (aKey.startsWith("dxp") && !bKey.startsWith("dxp")) {
			return -1;
		}
		else if (aKey.startsWith("portal") && bKey.startsWith("dxp")) {
			return 1;
		}
		else if (aKey.startsWith("portal") && bKey.startsWith("commerce")) {
			return -1;
		}
		else if (aKey.startsWith("commerce") && !bKey.startsWith("commerce")) {
			return 1;
		}
		else if (!StringUtil.equals(_getProductMainVersion(aKey), _getProductMainVersion(bKey))) {
			Version aProductMainVerson = Version.parseVersion(_getProductMainVersion(aKey));
			Version bProductMainVerson = Version.parseVersion(_getProductMainVersion(bKey));

			return -1 * aProductMainVerson.compareTo(bProductMainVerson);
		}
		else {
			String aProductMicroVersion = _getProductMicroVersion(aKey);
			String bProductMicroVersion = _getProductMicroVersion(bKey);

			if (BladeUtil.isEmpty(aProductMicroVersion)) {
				return 1;
			}
			else if (BladeUtil.isEmpty(bProductMicroVersion)) {
				return -1;
			}
			else if (Version.isVersion(aProductMicroVersion) && Version.isVersion(bProductMicroVersion)) {
				Version aMicroVersion = Version.parseVersion(aProductMicroVersion);
				Version bMicroVersion = Version.parseVersion(bProductMicroVersion);

				return -1 * aMicroVersion.compareTo(bMicroVersion);
			}
			else {
				ProductInfo aProductInfo = aPair.second();
				ProductInfo bProductInfo = bPair.second();

				try {
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.ENGLISH);

					LocalDate aDate = LocalDate.parse(aProductInfo.getReleaseDate(), dateTimeFormatter);
					LocalDate bDate = LocalDate.parse(bProductInfo.getReleaseDate(), dateTimeFormatter);

					return bDate.compareTo(aDate);
				}
				catch (Exception e) {
					String aMicroVersionPrefix = aProductMicroVersion.substring(0, 2);
					String bMicroVersionPrefix = bProductMicroVersion.substring(0, 2);

					if (!aMicroVersionPrefix.equalsIgnoreCase(bMicroVersionPrefix)) {
						return -1 * aMicroVersionPrefix.compareTo(bMicroVersionPrefix);
					}

					String aMicroVersionString = aProductMicroVersion.substring(2);
					String bMicroVersionString = bProductMicroVersion.substring(2);

					return Integer.parseInt(bMicroVersionString) - Integer.parseInt(aMicroVersionString);
				}
			}
		}
	}

	private String _getProductMainVersion(String productKey) {
		Matcher aMatcher = _versionPattern.matcher(productKey.substring(productKey.indexOf('-') + 1));

		if (aMatcher.find()) {
			return aMatcher.group(1);
		}

		return "";
	}

	private String _getProductMicroVersion(String productKey) {
		String[] prodcutKeyArrays = StringUtil.split(productKey, "-");

		if (prodcutKeyArrays.length > 2) {
			return prodcutKeyArrays[2];
		}

		return null;
	}

	private static final Pattern _versionPattern = Pattern.compile("([0-9\\.]+).*");

}