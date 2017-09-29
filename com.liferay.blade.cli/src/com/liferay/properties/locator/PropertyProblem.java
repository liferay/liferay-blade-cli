/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
		return _propertyName + " has been " + _type.toString().toLowerCase() + ".  " + _message;
	}

	private String _message;
	private final String _propertyName;
	private List<Pair<String, String>> _replacements;
	private PropertyProblemType _type;

}