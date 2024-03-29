/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class ReleaseUtil {

	public static ReleaseEntry getReleaseEntry(String releaseKey) {
		return _releaseUtil._releaseEntryMap.getOrDefault(releaseKey, _EMPTY_RELEASE_ENTRY);
	}

	public static ReleaseProperties getReleaseProperties(String releaseKey) {
		if (releaseKey == null) {
			return _EMPTY_RELEASE_PROPERTIES;
		}

		return _releaseUtil._releasePropertiesMap.computeIfAbsent(releaseKey, _releaseUtil::_createReleaseProperties);
	}

	public static void refreshReleases() {
		System.out.println("Checking for new releases...");

		_releaseUtil = new ReleaseUtil(0);
	}

	public static Stream<ReleaseEntry> releaseEntriesStream() {
		return _releaseUtil._releaseEntries.stream();
	}

	public static <T> T withReleaseEntriesStream(Function<Stream<ReleaseEntry>, T> function) {
		return function.apply(releaseEntriesStream());
	}

	public static <T> T withReleaseEntry(String releaseKey, Function<ReleaseEntry, T> function) {
		return function.apply(getReleaseEntry(releaseKey));
	}

	public static class ReleaseEntry {

		public String getProduct() {
			return _product;
		}

		public String getProductGroupVersion() {
			return _productGroupVersion;
		}

		public String getProductVersion() {
			return _productVersion;
		}

		public String getReleaseKey() {
			return _releaseKey;
		}

		public String getTargetPlatformVersion() {
			return _targetPlatformVersion;
		}

		public String getUrl() {
			return _url;
		}

		public boolean isPromoted() {
			return _promoted;
		}

		@JsonProperty("product")
		private String _product;

		@JsonProperty("productGroupVersion")
		private String _productGroupVersion;

		@JsonProperty("productVersion")
		private String _productVersion;

		@JsonProperty("promoted")
		private boolean _promoted;

		@JsonProperty("releaseKey")
		private String _releaseKey;

		@JsonProperty("targetPlatformVersion")
		private String _targetPlatformVersion;

		@JsonProperty("url")
		private String _url;

	}

	public static class ReleaseProperties {

		public String getAppServerTomcatVersion() {
			return _appServerTomcatVersion;
		}

		public String getBuildTimestamp() {
			return _buildTimestamp;
		}

		public String getBundleChecksumSHA512() {
			return _bundleChecksumSHA512;
		}

		public String getBundleUrl() {
			return _bundleUrl;
		}

		public String getGitHashLiferayDocker() {
			return _gitHashLiferayDocker;
		}

		public String getGitHashLiferayPortalEE() {
			return _gitHashLiferayPortalEE;
		}

		public String getLiferayDockerImage() {
			return _liferayDockerImage;
		}

		public String getLiferayDockerTags() {
			return _liferayDockerTags;
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

		private ReleaseProperties() {
			this(new Properties());
		}

		private ReleaseProperties(Properties properties) {
			this(
				properties.getProperty("app.server.tomcat.version"), properties.getProperty("build.timestamp"),
				properties.getProperty("bundle.checksum.sha512"), properties.getProperty("bundle.url"),
				properties.getProperty("git.hash.liferay-docker"), properties.getProperty("git.hash.liferay-portal-ee"),
				properties.getProperty("liferay.docker.image"), properties.getProperty("liferay.docker.tags"),
				properties.getProperty("liferay.product.version"), properties.getProperty("release.date"),
				properties.getProperty("target.platform.version"));
		}

		private ReleaseProperties(
			String appServerTomcatVersion, String buildTimestamp, String bundleChecksumSHA512, String bundleUrl,
			String gitHashLiferayDocker, String gitHashLiferayPortalEE, String liferayDockerImage,
			String liferayDockerTags, String liferayProductVersion, String releaseDate, String targetPlatformVersion) {

			_appServerTomcatVersion = appServerTomcatVersion;
			_buildTimestamp = buildTimestamp;
			_bundleChecksumSHA512 = bundleChecksumSHA512;
			_bundleUrl = bundleUrl;
			_gitHashLiferayDocker = gitHashLiferayDocker;
			_gitHashLiferayPortalEE = gitHashLiferayPortalEE;
			_liferayDockerImage = liferayDockerImage;
			_liferayDockerTags = liferayDockerTags;
			_liferayProductVersion = liferayProductVersion;
			_releaseDate = releaseDate;
			_targetPlatformVersion = targetPlatformVersion;
		}

		private final String _appServerTomcatVersion;
		private final String _buildTimestamp;
		private final String _bundleChecksumSHA512;
		private final String _bundleUrl;
		private final String _gitHashLiferayDocker;
		private final String _gitHashLiferayPortalEE;
		private final String _liferayDockerImage;
		private final String _liferayDockerTags;
		private final String _liferayProductVersion;
		private final String _releaseDate;
		private final String _targetPlatformVersion;

	}

	private ReleaseUtil(int maxAge) {
		File releasesJsonFile = new File(_workspaceCacheDir, "releases.json");

		_releaseEntries = ResourceUtil.readJson(
			ReleaseEntries.class, ResourceUtil.getLocalFileResolver(releasesJsonFile, maxAge, ChronoUnit.DAYS),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir, "https://releases.liferay.com/releases.json", "releases.json"),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir, "https://releases-cdn.liferay.com/releases.json", "releases.json"),
			ResourceUtil.getLocalFileResolver(releasesJsonFile), ResourceUtil.getClassLoaderResolver("/releases.json"));

		if (_releaseEntries == null) {
			throw new RuntimeException("Could not find releases.json");
		}

		_releaseEntryMap.clear();

		for (ReleaseEntry releaseEntry : _releaseEntries) {
			_releaseEntryMap.put(releaseEntry.getReleaseKey(), releaseEntry);
		}
	}

	private ReleaseProperties _createReleaseProperties(String releaseKey) {
		ReleaseEntry releaseEntry = _releaseEntryMap.get(releaseKey);

		if (releaseEntry == null) {
			throw new RuntimeException(
				String.format(
					"%s is not a valid product key. Must be one of %s", releaseKey, _releaseEntryMap.keySet()));
		}

		String product = releaseEntry.getProduct();

		File productReleasePropertiesCacheDir = new File(
			new File(_workspaceCacheDir, "releaseProperties"), String.format("%s/%s", product, releaseKey));

		String releasesCDNUrl = releaseEntry.getUrl() + "/release.properties";

		String releasesUrl = releasesCDNUrl.replaceFirst("releases-cdn", "releases");

		Properties properties = ResourceUtil.readProperties(
			ResourceUtil.getLocalFileResolver(new File(productReleasePropertiesCacheDir, "release.properties")),
			ResourceUtil.getURLResolver(productReleasePropertiesCacheDir, releasesCDNUrl, "release.properties"),
			ResourceUtil.getURLResolver(productReleasePropertiesCacheDir, releasesUrl, "release.properties"));

		return new ReleaseProperties(properties);
	}

	private static final ReleaseEntry _EMPTY_RELEASE_ENTRY = new ReleaseEntry();

	private static final ReleaseProperties _EMPTY_RELEASE_PROPERTIES = new ReleaseProperties();

	private static ReleaseUtil _releaseUtil = new ReleaseUtil(7);

	private final ReleaseEntries _releaseEntries;
	private final Map<String, ReleaseEntry> _releaseEntryMap = new HashMap<>();
	private final Map<String, ReleaseProperties> _releasePropertiesMap = new HashMap<>();
	private final File _workspaceCacheDir = new File(System.getProperty("user.home"), ".liferay/workspace");

	private static class ReleaseEntries extends ArrayList<ReleaseEntry> {
	}

}