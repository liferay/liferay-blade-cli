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

package com.liferay.blade.cli;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author Christopher Bryan Boyd
 */
public class InstallExtensionCommand extends BaseCommand<InstallExtensionCommandArgs> {

	public static final String DESCRIPTION = "Installs an extension into Blade.";

	public InstallExtensionCommand() {
	}

	public void execute() throws Exception {
		String arg = _args.getPath();

		Path pathArg;

		if (Objects.isNull(arg) || arg.trim().length() == 0) {
			pathArg = Paths.get(".");
		}
		else {
			pathArg = Paths.get(arg);
		}

		// Possibly handle git and github links here

		if (Files.exists(pathArg)) {
			if (pathArg.toFile().isDirectory()) {
				_gradleDeploy(_blade, pathArg);
			} else {
				_installExtension(pathArg);
			}
		}
		else {
			throw new Exception("Path to extension does not exist: " + pathArg);
		}
	}

	@Override
	public Class<InstallExtensionCommandArgs> getArgsClass() {
		return InstallExtensionCommandArgs.class;
	}

	private void _gradleDeploy(BladeCLI blade, Path pathToProject) throws Exception {
		GradleExec gradle = new GradleExec(blade);

		Set<File> outputFiles = GradleTooling.getOutputFiles(blade.getCacheDir(), blade.getBase());

		gradle.executeGradleCommand("assemble -x check");

		Iterator<File> i = outputFiles.iterator();

		if (i.hasNext()) {
			Path outputFile = i.next().toPath();

			if (Files.exists(outputFile)) {
				_installExtension(outputFile);
			}
		}
	}

	private void _installExtension(Path extensionPath) throws IOException {
		Path extensionsHome = Util.getExtensionsDirectory();

		Path extensionName = extensionPath.getFileName();

		Path newExtensionPath = extensionsHome.resolve(extensionName);

		Files.copy(extensionPath, newExtensionPath);

		_blade.out("The extension " + extensionName + " has been installed successfully.");
	}

}