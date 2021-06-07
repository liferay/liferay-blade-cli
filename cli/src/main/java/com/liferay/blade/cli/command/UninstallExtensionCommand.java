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