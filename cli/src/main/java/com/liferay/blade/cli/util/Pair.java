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