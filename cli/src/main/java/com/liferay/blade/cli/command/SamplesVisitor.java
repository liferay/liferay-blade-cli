/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Christopher Bryan Boyd
 */
public class SamplesVisitor extends SimpleFileVisitor<Path> {

	public Collection<Path> getPaths() {
		return Collections.unmodifiableCollection(_paths);
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		super.preVisitDirectory(dir, attrs);

		if (Files.exists(dir.resolve("src"))) {
			_paths.add(dir);

			return FileVisitResult.SKIP_SUBTREE;
		}

		return FileVisitResult.CONTINUE;
	}

	private final Collection<Path> _paths = new HashSet<>();

}