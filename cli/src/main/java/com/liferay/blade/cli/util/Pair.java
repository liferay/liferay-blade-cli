/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

/**
 * @author Alberto Chaparro
 */
public class Pair<F, S> {

	public Pair(final F l, final S r) {
		_first = l;
		_second = r;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final Pair<?, ?> other = (Pair<?, ?>)obj;

		if (_first == null) {
			if (other.first() != null) {
				return false;
			}
		}
		else if (!_first.equals(other.first())) {
			return false;
		}

		if (_second == null) {
			if (other.second() != null) {
				return false;
			}
		}
		else if (!_second.equals(other._second)) {
			return false;
		}

		return true;
	}

	public F first() {
		return _first;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

		int result = 1;

		result = (prime * result) + ((_first == null) ? 0 : _first.hashCode());

		result = (prime * result) + ((_second == null) ? 0 : _second.hashCode());

		return result;
	}

	public S second() {
		return _second;
	}

	@Override
	public String toString() {
		return "(" + _first + ", " + _second + ")";
	}

	private final F _first;
	private final S _second;

}