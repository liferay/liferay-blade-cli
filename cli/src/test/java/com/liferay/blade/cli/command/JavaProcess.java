/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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