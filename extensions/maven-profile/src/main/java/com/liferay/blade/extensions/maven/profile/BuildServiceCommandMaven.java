/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
			File pomXMLFile = MavenUtil.getpomXMLFile(baseDir);

			if (pomXMLFile.exists()) {
				if (!baseArgs.isQuiet()) {
					bladeCLI.out("Identifying Service Builder projects");
				}

				Path baseDirPath = baseDir.toPath();

				try (Stream<Path> pathStream = Files.walk(baseDirPath)) {
					List<Path> paths = pathStream.filter(
						this::_pathHasFileName
					).map(
						baseDirPath::relativize
					).map(
						Path::getParent
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

	private boolean _pathHasFileName(Path path) {
		String fileNameString = String.valueOf(path.getFileName());

		return fileNameString.equals("service.xml");
	}

}