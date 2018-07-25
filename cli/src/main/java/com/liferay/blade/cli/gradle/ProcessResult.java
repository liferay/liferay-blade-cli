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

import java.util.function.Supplier;

/**
 * @author Christopher Bryan Boyd
 */
public class ProcessResult implements Supplier<String> {

	public static String getProcessResultOutput(ProcessResult processResult) {
		StringBuilder sb = new StringBuilder();

		sb.append(processResult.getError());
		sb.append(System.lineSeparator());
		sb.append(processResult.getOutput());
		sb.append(System.lineSeparator());

		return sb.toString();
	}

	public ProcessResult(int returnCode, String output, String error) {
		_returnCode = returnCode;
		_output = output;
		_error = error;
	}

	@Override
	public String get() {
		return getProcessResultOutput(this);
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

	@Override
	public String toString() {
		return get();
	}

	private final String _error;
	private final String _output;
	private final int _returnCode;

}