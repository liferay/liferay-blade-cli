/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.io.IOException;

import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;

/**
 * @author Gregory Amerson
 */
public class CopyDirVisitor extends SimpleFileVisitor<Path> {

	public CopyDirVisitor(Path fromPath, Path toPath, CopyOption copyOption) {
		_fromPath = fromPath;
		_toPath = toPath;
		_copyOption = copyOption;

		_deleteSource = false;
	}

	public CopyDirVisitor(Path fromPath, Path toPath, CopyOption copyOption, boolean deleteSource) {
		_fromPath = fromPath;
		_toPath = toPath;
		_copyOption = copyOption;
		_deleteSource = deleteSource;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path file, IOException ioException) throws IOException {
		if (_deleteSource) {
			FileUtils.forceDelete(file.toFile());
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		Path targetPath = _toPath.resolve(_fromPath.relativize(dir));

		if (!Files.exists(targetPath)) {
			Files.createDirectory(targetPath);
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.copy(file, _toPath.resolve(_fromPath.relativize(file)), _copyOption);

		return FileVisitResult.CONTINUE;
	}

	private final CopyOption _copyOption;
	private boolean _deleteSource;
	private final Path _fromPath;
	private final Path _toPath;

}