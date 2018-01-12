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
		this.formatedValue = value;
		this.originalValue = value;
	}

	public BndPropertiesValue(String formatedValue, String originalValue) {
		this.formatedValue = formatedValue;
		this.originalValue = originalValue;
	}

	public String getFormatedValue() {
		return formatedValue;
	}

	public int getKeyIndex() {
		return keyIndex;
	}

	public String getOriginalValue() {
		return originalValue;
	}

	public boolean isMultiLine() {
		return isMultiLine;
	}

	public void setFormatedValue(String formatedValue) {
		this.formatedValue = formatedValue;
	}

	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	public void setMultiLine(boolean isMultiLine) {
		this.isMultiLine = isMultiLine;
	}

	public void setOriginalValue(String originalValue) {
		this.originalValue = originalValue;
	}

	private String formatedValue;
	private boolean isMultiLine;
	private int keyIndex;
	private String originalValue;

}