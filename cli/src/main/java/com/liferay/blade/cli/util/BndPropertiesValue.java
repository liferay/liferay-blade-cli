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