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

import com.google.gson.annotations.SerializedName;

/**
 * @author Simon Jiang
 */
public class ProductInfo {

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

	public Boolean isInitialVersion() {
		return _initialVersion;
	}

	@SerializedName("appServerTomcatVersion")
	private String _appServerTomcatVersion;

	@SerializedName("bundleUrl")
	private String _bundleUrl;

	@SerializedName("initialVersion")
	private Boolean _initialVersion = false;

	@SerializedName("liferayDockerImage")
	private String _liferayDockerImage;

	@SerializedName("liferayProductVersion")
	private String _liferayProductVersion;

	@SerializedName("releaseDate")
	private String _releaseDate;

	@SerializedName("targetPlatformVersion")
	private String _targetPlatformVersion;

}