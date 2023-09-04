/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.extensions.sample.profile;

import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.DeployArgs;

/**
 * @author Liferay
 */
@Parameters(commandDescription = "Overriden Deploy Command", commandNames = "deploy")
public class OverriddenArgs extends DeployArgs {
}