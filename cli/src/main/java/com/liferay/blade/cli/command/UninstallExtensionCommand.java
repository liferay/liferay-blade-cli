/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class UninstallExtensionCommand extends BaseCommand<UninstallExtensionArgs> {

	public UninstallExtensionCommand() {
	}

	@Override
	public void execute() throws Exception {
		UninstallExtensionArgs uninstallExtensionArgs = getArgs();

		String name = uninstallExtensionArgs.getName();

		if (Objects.nonNull(name) && (name.length() > 0) && name.endsWith(".jar")) {
			_removeExtension(name);
		}
		else {
			throw new Exception("Invalid extension specified: " + name);
		}
	}

	@Override
	public Class<UninstallExtensionArgs> getArgsClass() {
		return UninstallExtensionArgs.class;
	}

	private void _removeExtension(String name) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path extensionsHome = bladeCLI.getExtensionsPath();

		Path extensionPath = extensionsHome.resolve(name);

		Files.delete(extensionPath);

		BaseArgs baseArgs = bladeCLI.getArgs();

		if (!baseArgs.isQuiet()) {
			bladeCLI.out("The extension " + name + " has been uninstalled successfully.");
		}
	}

}