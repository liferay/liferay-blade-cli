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

package com.liferay.blade.cli.command;

/**
 * @author Gregory Amerson
 */
public class JavaProcess {

	public JavaProcess(int id, String displayName) {
		_id = id;
		_displayName = displayName;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public int getId() {
		return _id;
	}

	private String _displayName;
	private int _id;

}