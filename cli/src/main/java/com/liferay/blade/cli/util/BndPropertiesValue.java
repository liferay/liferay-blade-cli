/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

/**
 * @author Simon Jiang
 */
public class BndPropertiesValue {

	public BndPropertiesValue() {
	}

	public BndPropertiesValue(String value) {
		_formatedValue = value;
		_originalValue = value;
	}

	public BndPropertiesValue(String formatedValue, String originalValue) {
		_formatedValue = formatedValue;
		_originalValue = originalValue;
	}

	public String getFormatedValue() {
		return _formatedValue;
	}

	public int getKeyIndex() {
		return _keyIndex;
	}

	public String getOriginalValue() {
		return _originalValue;
	}

	public boolean isMultiLine() {
		return _multiLine;
	}

	public void setFormatedValue(String formatedValue) {
		_formatedValue = formatedValue;
	}

	public void setKeyIndex(int keyIndex) {
		_keyIndex = keyIndex;
	}

	public void setMultiLine(boolean multiLine) {
		_multiLine = multiLine;
	}

	public void setOriginalValue(String originalValue) {
		_originalValue = originalValue;
	}

	private String _formatedValue;
	private int _keyIndex;
	private boolean _multiLine;
	private String _originalValue;

}