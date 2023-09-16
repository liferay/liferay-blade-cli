/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.gradle;

/**
 * @author Christopher Bryan Boyd
 */
public class GradleExecutionException extends RuntimeException {

	public GradleExecutionException(String message, int returnCode) {
		super(message);

		_returnCode = returnCode;
	}

	public int getReturnCode() {
		return _returnCode;
	}

	private final int _returnCode;

}