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

package com.liferay.ide.gradle.core.model;

import java.text.MessageFormat;

/**
 * @author Lovett Li
 * @author Vernon Singleton
 * @author Gregory Amerson
 */
public class GradleDependency {

	public GradleDependency(String singleLine) {
		_singleLine = singleLine;
		_configuration = null;
		_group = null;
		_name = null;
		_version = null;
		_lineNumber = -1;
		_lastLineNumber = -1;
	}

	public GradleDependency(
		String configuration, String group, String name, String version, int lineNumber, int lastLineNumber) {

		_configuration = configuration;
		_group = group;
		_name = name;
		_version = version;
		_lineNumber = lineNumber;
		_lastLineNumber = lastLineNumber;
		_singleLine = null;
	}

	public GradleDependency clone() {
		return new GradleDependency(_configuration, _group, _name, _version, _lineNumber, _lastLineNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		GradleDependency other = (GradleDependency)obj;

		if (_configuration == null) {
			if (other._configuration != null) {
				return false;
			}
		}
		else if (!_configuration.equals(other._configuration)) {
			return false;
		}

		if (_group == null) {
			if (other._group != null) {
				return false;
			}
		}
		else if (!_group.equals(other._group)) {
			return false;
		}

		if (_name == null) {
			if (other._name != null) {
				return false;
			}
		}
		else if (!_name.equals(other._name)) {
			return false;
		}

		if (_version == null) {
			if (other._version != null) {
				return false;
			}
		}
		else if (!_version.equals(other._version)) {
			return false;
		}

		return true;
	}

	public String getConfiguration() {
		return _configuration;
	}

	public String getGroup() {
		return _group;
	}

	public int getLastLineNumber() {
		return _lastLineNumber;
	}

	public int getLineNumber() {
		return _lineNumber;
	}

	public String getName() {
		return _name;
	}

	public String getSingleLine() {
		return _singleLine;
	}

	public String getVersion() {
		return _version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((_group == null) ? 0 : _group.hashCode());
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + ((_version == null) ? 0 : _version.hashCode());

		return result;
	}

	public void setVersion(String version) {
		_version = version;
	}

	@Override
	public String toString() {
		if (_singleLine != null) {
			return _singleLine;
		}

		return MessageFormat.format("{0} group: {1}, name: {2}, version: {3}", _configuration, _group, _name, _version);
	}

	private String _configuration;
	private String _group;
	private int _lastLineNumber;
	private int _lineNumber;
	private String _name;
	private String _singleLine;
	private String _version;

}