/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import com.liferay.release.util.ReleaseEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Drew Brokke
 */
public class ReleaseUtil extends com.liferay.release.util.ReleaseUtil {

	public static ReleaseEntry getReleaseEntry(String version) {
		return getReleaseEntry(null, version);
	}

	public static ReleaseEntry getReleaseEntry(String product, String version) {
		Predicate<ReleaseEntry> productGroupVersionPredicate = releaseEntry -> Objects.equals(
			releaseEntry.getProductGroupVersion(), version);
		Predicate<ReleaseEntry> productPredicate = releaseEntry -> Objects.equals(releaseEntry.getProduct(), product);
		Predicate<ReleaseEntry> targetPlatformVersionPredicate = releaseEntry -> Objects.equals(
			releaseEntry.getTargetPlatformVersion(), version);

		List<Predicate<ReleaseEntry>> predicates = new ArrayList<>();

		predicates.add(releaseEntry -> Objects.equals(releaseEntry.getReleaseKey(), version));

		predicates.add(productPredicate.and(targetPlatformVersionPredicate));

		predicates.add(productPredicate.and(productGroupVersionPredicate));

		predicates.add(targetPlatformVersionPredicate);

		predicates.add(productGroupVersionPredicate);

		for (Predicate<ReleaseEntry> predicate : predicates) {
			Optional<ReleaseEntry> first = getReleaseEntryStream(
			).filter(
				predicate
			).findFirst();

			if (first.isPresent()) {
				return first.get();
			}
		}

		return null;
	}

}