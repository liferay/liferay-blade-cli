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