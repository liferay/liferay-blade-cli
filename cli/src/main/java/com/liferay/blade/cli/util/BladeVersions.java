/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.util.Optional;

/**
 * @author Christopher Bryan Boyd
 */
public final class BladeVersions {

	public BladeVersions(
		String currentVersion, Optional<String> releaseUpdateVersion, Optional<String> snapshotUpdateVersion) {

		_currentVersion = currentVersion;
		_snapshotUpdateVersion = snapshotUpdateVersion;

		_releasedUpdateVersion = releaseUpdateVersion;
	}

	public String getCurrentVersion() {
		return _currentVersion;
	}

	public Optional<String> getReleasedUpdateVersion() {
		return _releasedUpdateVersion;
	}

	public Optional<String> getSnapshotUpdateVersion() {
		return _snapshotUpdateVersion;
	}

	private final String _currentVersion;
	private final Optional<String> _releasedUpdateVersion;
	private final Optional<String> _snapshotUpdateVersion;

}