/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.text.SimpleDateFormat;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Drew Brokke
 * @author Simon Jiang
 */
public class ProductKeyInfo implements Comparable<ProductKeyInfo> {

	public ProductKeyInfo(String productKey, Map<String, String> releaseMap) {
		_productKey = productKey;

		_releaseDate = _safeGet(releaseMap, "releaseDate", "");
		_liferayProductVersion = _safeGet(releaseMap, "liferayProductVersion", "");
		_product = _safeGet(releaseMap, "product", "");

		_promoted = Boolean.parseBoolean(_safeGet(releaseMap, "promoted", "false"));
		_quarterly = Boolean.parseBoolean(_safeGet(releaseMap, "quarterly", "false"));
	}

	@Override
	public int compareTo(ProductKeyInfo keyInfo) {
		return Comparator.comparing(
			(Function<ProductKeyInfo, Integer>)productKeyInfo -> {
				if (Objects.equals(productKeyInfo.getProduct(), "dxp")) {
					return 1;
				}

				return -1;
			}
		).thenComparing(
			productKeyInfo -> {
				if (productKeyInfo.isQuarterly()) {
					return 1;
				}

				return -1;
			}
		).thenComparing(
			productKeyInfo -> ProductKeyUtil.createProductKeyVersion(productKeyInfo.getLiferayProductVersion())
		).thenComparing(
			ProductKeyInfo::getReleaseDate
		).reversed(
		).compare(
			this, keyInfo
		);
	}

	public String getLiferayProductVersion() {
		return _liferayProductVersion;
	}

	public String getProduct() {
		return _product;
	}

	public String getProductKey() {
		return _productKey;
	}

	public Date getReleaseDate() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			return format.parse(_releaseDate);
		}
		catch (Exception exception) {
			return null;
		}
	}

	public boolean isPromoted() {
		return _promoted;
	}

	public boolean isQuarterly() {
		return _quarterly;
	}

	public void setProduct(String product) {
		_product = product;
	}

	public void setQuarterly(boolean quarterly) {
		_quarterly = quarterly;
	}

	private String _safeGet(Map<String, String> map, String key, String defVal) {
		return Optional.ofNullable(
			map
		).map(
			m -> m.get(key)
		).orElse(
			defVal
		);
	}

	private String _liferayProductVersion;
	private String _product;
	private String _productKey;
	private Boolean _promoted = false;
	private Boolean _quarterly = false;
	private String _releaseDate;

}