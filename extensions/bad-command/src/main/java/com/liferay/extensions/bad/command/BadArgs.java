/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.bad.command;

import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;

/**
 * @author Liferay
 */
@Parameters(commandDescription = "Bad Command, results in an error", commandNames = "bad")
public class BadArgs extends BaseArgs {
}