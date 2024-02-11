/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Drew Brokke
 */
public class ProductKeyUtil {

	public static final Comparator<String> comparator = ProductKeyUtil::compare;

	public static int compare(String productKey1, String productKey2) {
		ProductKeyInfo keyInfo1 = createProductKeyInfo(productKey1);

		return keyInfo1.compareTo(createProductKeyInfo(productKey2));
	}

	@SuppressWarnings("unchecked")
	public static ProductKeyInfo createProductKeyInfo(String productKey) {
		Map<String, Object> releasesInfos = BladeUtil.getReleaseKeyInfos();

		return new ProductKeyInfo(productKey, (Map<String, String>)releasesInfos.get(productKey));
	}

	public static ProductKeyVersion createProductKeyVersion(String versionString) {
		ProductKeyVersion productKeyVersion = new ProductKeyVersion();

		StringBuilder numberStringBuilder = new StringBuilder();

		for (char c : versionString.toCharArray()) {
			if (Character.isDigit(c)) {
				numberStringBuilder.append(c);
			}
		}

		if (numberStringBuilder.length() > 0) {
			productKeyVersion.setNumber(Integer.parseInt(numberStringBuilder.toString()));
		}

		return productKeyVersion;
	}

}