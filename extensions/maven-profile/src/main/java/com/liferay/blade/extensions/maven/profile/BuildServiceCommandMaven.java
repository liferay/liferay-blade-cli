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

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.extensions.maven.profile.internal.MavenExecutor;
import com.liferay.blade.extensions.maven.profile.internal.MavenUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 */
@BladeProfile("maven")
public class BuildServiceCommandMaven extends BaseCommand<BuildServiceArgsMaven> implements MavenExecutor {

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		File baseDir = baseArgs.getBase();

		if (MavenUtil.isWorkspace(baseDir)) {
			File pomXmlFile = MavenUtil.getPomXMLFile(baseDir);

			if (pomXmlFile.exists()) {
				if (!baseArgs.isQuiet()) {
					bladeCLI.out("Identifying Service Builder projects");
				}

				Path baseDirPath = baseDir.toPath();

				try (Stream<Path> pathStream = Files.walk(baseDirPath)) {
					List<Path> paths = pathStream.filter(
						path -> _pathHasFileName(path, "service.xml")
					).map(
						path -> baseDirPath.relativize(path)
					).map(
						path -> path.getParent()
					).collect(
						Collectors.toList()
					);

					if (!paths.isEmpty()) {
						StringBuilder sb = new StringBuilder();

						for (int x = 0; x < paths.size(); x++) {
							String pathString = String.valueOf(paths.get(x));

							sb.append(pathString);

							if ((x + 1) != paths.size()) {
								sb.append(",");
							}
						}

						String[] args = {"--projects", sb.toString(), "service-builder:build"};

						if (!baseArgs.isQuiet()) {
							bladeCLI.out("Executing " + String.join(" ", args));
						}

						execute(baseDir.getAbsolutePath(), args, true);
					}
					else {
						bladeCLI.error("No Service Builder projects could be found.");
					}
				}
			}
		}
		else {
			bladeCLI.error("'buildService' command is only supported inside a Liferay workspace project.");
		}
	}

	@Override
	public Class<BuildServiceArgsMaven> getArgsClass() {
		return BuildServiceArgsMaven.class;
	}

	private boolean _pathHasFileName(Path path, String expectedFileName) {
		String fileNameString = String.valueOf(path.getFileName());

		return fileNameString.equals(expectedFileName);
	}

}