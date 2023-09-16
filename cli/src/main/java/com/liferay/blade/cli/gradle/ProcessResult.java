/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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