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

package com.liferay.properties.locator;

import com.liferay.blade.cli.util.Pair;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public class PropertyProblem implements Comparable<PropertyProblem> {

	public PropertyProblem(String propertyName) {
		_propertyName = propertyName;

		_type = PropertyProblemType.MISSING;
	}

	public PropertyProblem(
		String propertyName, PropertyProblemType type, String message, List<Pair<String, String>> replacements) {

		_propertyName = propertyName;
		_type = type;
		_message = message;
		_replacements = replacements;
	}

	@Override
	public int compareTo(PropertyProblem o) {
		return _propertyName.compareTo(o.getPropertyName());
	}

	public String getPropertyName() {
		return _propertyName;
	}

	public List<Pair<String, String>> getReplacements() {
		return _replacements;
	}

	public PropertyProblemType getType() {
		return _type;
	}

	@Override
	public String toString() {
		String type = _type.toString();

		return _propertyName + " has been " + type.toLowerCase() + ".  " + _message;
	}

	private String _message;
	private final String _propertyName;
	private List<Pair<String, String>> _replacements;
	private PropertyProblemType _type;

}