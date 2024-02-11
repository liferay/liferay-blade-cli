/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Comparator;

/**
 * @author Drew Brokke
 */
public class ProductKeyVersion implements Comparable<ProductKeyVersion> {

	public static final ProductKeyVersion BLANK = new ProductKeyVersion();

	@Override
	public int compareTo(final ProductKeyVersion version) {
		return Comparator.comparingInt(
			ProductKeyVersion::getNumber
		).compare(
			this, version
		);
	}

	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		_number = number;
	}

	private int _number = 0;

}