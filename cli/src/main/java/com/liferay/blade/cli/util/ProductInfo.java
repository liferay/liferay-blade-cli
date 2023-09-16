/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Map;
import java.util.Optional;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ProductInfo {

	public ProductInfo(Map<String, String> productMap) {
		_appServerTomcatVersion = _safeGet(productMap, "appServerTomcatVersion", "");
		_bundleUrl = _safeGet(productMap, "bundleUrl", "");
		_liferayDockerImage = _safeGet(productMap, "liferayDockerImage", "");
		_liferayProductVersion = _safeGet(productMap, "liferayProductVersion", "");
		_releaseDate = _safeGet(productMap, "releaseDate", "");
		_targetPlatformVersion = _safeGet(productMap, "targetPlatformVersion", "");
		_promoted = Boolean.parseBoolean(_safeGet(productMap, "promoted", "false"));
	}

	public String getAppServerTomcatVersion() {
		return _appServerTomcatVersion;
	}

	public String getBundleUrl() {
		return _bundleUrl;
	}

	public String getLiferayDockerImage() {
		return _liferayDockerImage;
	}

	public String getLiferayProductVersion() {
		return _liferayProductVersion;
	}

	public String getReleaseDate() {
		return _releaseDate;
	}

	public String getTargetPlatformVersion() {
		return _targetPlatformVersion;
	}

	public boolean isPromoted() {
		return _promoted;
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

	private String _appServerTomcatVersion;
	private String _bundleUrl;
	private final String _liferayDockerImage;
	private final String _liferayProductVersion;
	private Boolean _promoted = false;
	private final String _releaseDate;
	private String _targetPlatformVersion;

}