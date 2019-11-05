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

/**
 * @author Christopher Bryan Boyd
 */
public final class Versions {

	public Versions(String currentVersion, String releaseUpdatedVersion, String snapshotUpdatedVersion) {
		_currentVersion = currentVersion;
		_releasedUpdatedVersion = releaseUpdatedVersion;
		_snapshotUpdatedVersion = snapshotUpdatedVersion;
	}

	public String getCurrentVersion() {
		return _currentVersion;
	}

	public String getReleasedUpdatedVersion() {
		return _releasedUpdatedVersion;
	}

	public String getSnapshotUpdatedVersion() {
		return _snapshotUpdatedVersion;
	}

	private final String _currentVersion;
	private final String _releasedUpdatedVersion;
	private final String _snapshotUpdatedVersion;

}