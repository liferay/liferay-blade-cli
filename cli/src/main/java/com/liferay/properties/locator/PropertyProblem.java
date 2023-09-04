/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.properties.locator;

import com.liferay.blade.cli.util.Pair;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public class PropertyProblem implements Comparable<PropertyProblem> {

	public PropertyProblem(String propertyName, PropertyProblemType type) {
		_propertyName = propertyName;
		_type = type;
	}

	public PropertyProblem(
		String propertyName, PropertyProblemType type, String message, List<Pair<String, String>> replacements) {

		_propertyName = propertyName;
		_type = type;
		_message = message;
		_replacements = replacements;
	}

	public void appendMessage(String message) {
		_message += message;
	}

	@Override
	public int compareTo(PropertyProblem o) {
		return _propertyName.compareTo(o.getPropertyName());
	}

	public String getMessage() {
		return _message;
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

	public PropertyProblem setMessage(String message) {
		_message = message;

		return this;
	}

	public PropertyProblem setReplacements(List<Pair<String, String>> replacements) {
		_replacements = replacements;

		return this;
	}

	public PropertyProblem setType(PropertyProblemType type) {
		_type = type;

		return this;
	}

	private String _message;
	private final String _propertyName;
	private List<Pair<String, String>> _replacements;
	private PropertyProblemType _type;

}