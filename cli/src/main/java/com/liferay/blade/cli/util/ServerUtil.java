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

package com.liferay.blade.cli.util;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerUtil {

	public static Optional<Path> findServerFolder(Path dir, String serverType) throws IOException {
		BiPredicate<Path, BasicFileAttributes> binFolderMatcher = (path, attrs) -> {
			Path fileName = path.getFileName();

			String fileNameString = String.valueOf(fileName);

			boolean match = false;

			if (fileNameString.startsWith(serverType) && Files.isDirectory(path)) {
				match = true;
			}

			if (match) {
				if ("tomcat".equals(serverType)) {
					Path executable = path.resolve(Paths.get("bin", getTomcatExecutable()));

					match = Files.exists(executable);
				}
				else if ("jboss".equals(serverType) || "wildfly".equals(serverType)) {
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