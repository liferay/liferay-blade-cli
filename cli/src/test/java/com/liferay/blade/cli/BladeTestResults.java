/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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