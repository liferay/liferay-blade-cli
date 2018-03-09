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

import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;

import java.util.Set;

/**
 * @author Gregory Amerson
 */
public class OutputsCommand {

	public OutputsCommand(BladeCLI blade, OutputsCommandArgs args) throws Exception {
		_blade = blade;
	}

	public void execute() throws Exception {
		final File base = _blade.getBase();

		final Path basePath = base.toPath();

		final Path basePathRoot = basePath.getRoot();

		final Set<File> outputs = GradleTooling.getOutputFiles(_blade.getCacheDir(), base);

		for (File output : outputs) {
			Path outputPath = output.toPath();

			Path outputPathRoot = outputPath.getRoot();

			Object print = null;

			PrintStream out = _blade.out();

			if ((basePathRoot != null) && (outputPathRoot != null)) {
				print = basePath.relativize(outputPath);
			}
			else {
				out.println(outputPath);
			}

			out.println(print);
		}
	}

	private BladeCLI _blade;

}