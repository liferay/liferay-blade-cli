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
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class ReleaseUtil {

	public static ReleaseEntry getReleaseEntry(String releaseKey) {
		if (_releaseUtil == null) {
			populateReleases(_DEFAULT_MAX_AGE);
		}

		return _releaseUtil._releaseEntryMap.getOrDefault(releaseKey, _EMPTY_RELEASE_ENTRY);
	}

	public static void populateReleases(int maxAge) {
		_releaseUtil = new ReleaseUtil(maxAge);
	}

	public static Stream<ReleaseEntry> releaseEntriesStream() {
		if (_releaseUtil == null) {
			populateReleases(_DEFAULT_MAX_AGE);
		}

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

	private ReleaseUtil(int maxAge) {
		File releasesJsonFile = new File(_workspaceCacheDir, "releases.json");

		_releaseEntries = ResourceUtil.readJson(
			ReleaseEntries.class, ResourceUtil.getLocalFileResolver(System.getenv("BLADE_LOCAL_RELEASES_JSON_FILE")),
			ResourceUtil.getLocalFileResolver(releasesJsonFile, maxAge, ChronoUnit.DAYS),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir, "https://releases-cdn.liferay.com/releases.json", "releases.json"),
			ResourceUtil.getURLResolver(
				_workspaceCacheDir, "https://releases.liferay.com/releases.json", "releases.json"),
			ResourceUtil.getClassLoaderResolver("/releases.json"));

		if (_releaseEntries == null) {
			throw new RuntimeException("Could not find releases.json");
		}

		_releaseEntryMap.clear();

		for (ReleaseEntry releaseEntry : _releaseEntries) {
			_releaseEntryMap.put(releaseEntry.getReleaseKey(), releaseEntry);
		}
	}

	private static final int _DEFAULT_MAX_AGE = 7;

	private static final ReleaseEntry _EMPTY_RELEASE_ENTRY = new ReleaseEntry();

	private static ReleaseUtil _releaseUtil;

	private final ReleaseEntries _releaseEntries;
	private final Map<String, ReleaseEntry> _releaseEntryMap = new HashMap<>();
	private final File _workspaceCacheDir = new File(System.getProperty("user.home"), ".liferay/workspace");

	private static class ReleaseEntries extends ArrayList<ReleaseEntry> {
	}

}