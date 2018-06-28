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

package com.liferay.blade.cli.gradle;

/**
 * @author Christopher Bryan Boyd
 */
public class ProcessResult {

	public ProcessResult(int returnCode, String output, String error) {
		_returnCode = returnCode;
		_output = output;
		_error = error;
	}

	public String getError() {
		return _error;
	}

	public String getOutput() {
		return _output;
	}

	public int getResultCode() {
		return _returnCode;
	}

	private final String _error;
	private final String _output;
	private final int _returnCode;

}