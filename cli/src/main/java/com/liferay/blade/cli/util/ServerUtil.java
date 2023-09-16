/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerUtil {

	public static Optional<Path> findAppServerPath(Path dir, String serverType) throws IOException {
		BiPredicate<Path, BasicFileAttributes> binFolderMatcher = (path, attrs) -> {
			String fileNameString = String.valueOf(path.getFileName());

			boolean match = false;

			if (fileNameString.startsWith(serverType) && Files.isDirectory(path)) {
				match = true;
			}

			if (match) {
				if (Objects.equals(serverType, "tomcat")) {
					Path executable = path.resolve(Paths.get("bin", getTomcatExecutable()));

					match = Files.exists(executable);
				}
				else if (Objects.equals(serverType, "jboss") || Objects.equals(serverType, "wildfly")) {
					Path executable = path.resolve(Paths.get("bin", getJBossWildflyExecutable()));

					match = Files.exists(executable);
				}
			}

			return match;
		};

		try (Stream<Path> stream = Files.find(dir, Integer.MAX_VALUE, binFolderMatcher)) {
			return stream.findFirst();
		}
	}

	public static String getJBossWildflyExecutable() {
		String executable = "./standalone.sh";

		if (BladeUtil.isWindows()) {
			executable = "standalone.bat";
		}

		return executable;
	}

	public static String getTomcatExecutable() {
		String executable = "./catalina.sh";

		if (BladeUtil.isWindows()) {
			executable = "catalina.bat";
		}

		return executable;
	}

}