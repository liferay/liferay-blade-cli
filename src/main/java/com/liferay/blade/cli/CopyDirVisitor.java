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

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Gregory Amerson
 */
public class CopyDirVisitor extends SimpleFileVisitor<Path> {
	private final Path fromPath;
	private final Path toPath;
	private final CopyOption copyOption;

	public CopyDirVisitor(Path fromPath, Path toPath, CopyOption copyOption) {
		this.fromPath = fromPath;
		this.toPath = toPath;
		this.copyOption = copyOption;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Path targetPath = toPath.resolve(fromPath.relativize(dir));

		if (!Files.exists(targetPath)) {
			Files.createDirectory(targetPath);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);

		return FileVisitResult.CONTINUE;
	}
}
