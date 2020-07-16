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

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ProductInfo {

	public ProductInfo(Map<String, String> productMap) {
		_appServerTomcatVersion = productMap.get("appServerTomcatVersion");
		_bundleUrl = productMap.get("bundleUrl");
		_liferayDockerImage = productMap.get("liferayDockerImage");
		_liferayProductVersion = productMap.get("liferayProductVersion");
		_releaseDate = productMap.get("releaseDate");
		_targetPlatformVersion = productMap.get("targetPlatformVersion");
		_promoted = Boolean.parseBoolean(productMap.get("promoted"));
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

	private String _appServerTomcatVersion;
	private String _bundleUrl;
	private final String _liferayDockerImage;
	private final String _liferayProductVersion;
	private Boolean _promoted = false;
	private final String _releaseDate;
	private String _targetPlatformVersion;

}