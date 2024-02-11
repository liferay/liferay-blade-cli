/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Properties;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ReleaseInfo {

	public ReleaseInfo(ProductKeyInfo productKeyInfo, Properties releaseProperties) {
		_productKeyInfo = productKeyInfo;

		_appServerTomcatVersion = releaseProperties.getProperty("app.server.tomcat.version");
		_bundleUrl = releaseProperties.getProperty("bundle.url");
		_liferayDockerImage = releaseProperties.getProperty("liferay.docker.image");
		_liferayProductVersion = releaseProperties.getProperty("liferay.product.version");
		_releaseDate = releaseProperties.getProperty("release.date");
		_targetPlatformVersion = releaseProperties.getProperty("target.platform.version");
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

	public ProductKeyInfo getProductKey() {
		return _productKeyInfo;
	}

	public String getReleaseDate() {
		return _releaseDate;
	}

	public String getTargetPlatformVersion() {
		return _targetPlatformVersion;
	}

	private String _appServerTomcatVersion;
	private String _bundleUrl;
	private final String _liferayDockerImage;
	private final String _liferayProductVersion;
	private ProductKeyInfo _productKeyInfo;
	private final String _releaseDate;
	private String _targetPlatformVersion;

}