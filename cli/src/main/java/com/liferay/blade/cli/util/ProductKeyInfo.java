/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Comparator;

/**
 * @author Drew Brokke
 */
public class ProductKeyInfo implements Comparable<ProductKeyInfo> {

	@Override
	public int compareTo(final ProductKeyInfo keyInfo) {
		return Comparator.comparing(
			ProductKeyInfo::getProductRank
		).thenComparing(
			ProductKeyInfo::isQuarterly
		).thenComparing(
			ProductKeyInfo::getMajorProductKeyVersion
		).thenComparing(
			ProductKeyInfo::getMinorProductKeyVersion
		).thenComparing(
			ProductKeyInfo::getMicroProductKeyVersion
		).reversed(
		).compare(
			this, keyInfo
		);
	}

	public ProductKeyVersion getMajorProductKeyVersion() {
		return _majorProductKeyVersion;
	}

	public ProductKeyVersion getMicroProductKeyVersion() {
		return _microProductKeyVersion;
	}

	public ProductKeyVersion getMinorProductKeyVersion() {
		return _minorProductKeyVersion;
	}

	public String getProduct() {
		return _product;
	}

	public int getProductRank() {
		return _productRank;
	}

	public boolean isQuarterly() {
		return _quarterly;
	}

	public void setMajorProductKeyVersion(ProductKeyVersion majorProductKeyVersion) {
		_majorProductKeyVersion = majorProductKeyVersion;
	}

	public void setMicroProductKeyVersion(ProductKeyVersion microProductKeyVersion) {
		_microProductKeyVersion = microProductKeyVersion;
	}

	public void setMinorProductKeyVersion(ProductKeyVersion minorProductKeyVersion) {
		_minorProductKeyVersion = minorProductKeyVersion;
	}

	public void setProduct(String product) {
		_product = product;
	}

	public void setProductRank(int productRank) {
		_productRank = productRank;
	}

	public void setQuarterly(boolean quarterly) {
		_quarterly = quarterly;
	}

	private ProductKeyVersion _majorProductKeyVersion = ProductKeyVersion.BLANK;
	private ProductKeyVersion _microProductKeyVersion = ProductKeyVersion.BLANK;
	private ProductKeyVersion _minorProductKeyVersion = ProductKeyVersion.BLANK;
	private String _product;
	private int _productRank = -1;
	private boolean _quarterly = false;

}