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

package com.liferay.blade.cli;

/**
 * @author Christopher Bryan Boyd
 */
public class BladeTestResults {

	public BladeTestResults(BladeCLI bladeCLI, String output, String errors) {
		_bladeCLI = bladeCLI;
		_output = output;
		_errors = errors;
	}

	public BladeCLI getBladeCLI() {
		return _bladeCLI;
	}

	public String getErrors() {
		return _errors;
	}

	public String getOutput() {
		return _output;
	}

	private final BladeCLI _bladeCLI;
	private final String _errors;
	private final String _output;

}