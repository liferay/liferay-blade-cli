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
import com.liferay.blade.cli.util.StringUtil;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Jiang
 */
public class WorkspaceProductComparator implements Comparator<String> {

	@Override
	public int compare(String a, String b) {
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
		else if (!StringUtil.equals(_getProductMainVersion(a), _getProductMainVersion(b))) {
			Version aProductMainVerson = Version.parseVersion(_getProductMainVersion(a));
			Version bProductMainVerson = Version.parseVersion(_getProductMainVersion(b));

			return -1 * aProductMainVerson.compareTo(bProductMainVerson);
		}
		else {
			String aProductMicroVersion = _getProductMicroVersion(a);
			String bProductMicroVersion = _getProductMicroVersion(b);

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