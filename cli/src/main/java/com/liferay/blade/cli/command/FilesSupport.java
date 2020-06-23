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

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

/**
 * @author Simon Jiang
 */
public interface FilesSupport {

	public default void copyFile(Path sourcePath, Path destinationPath) throws IOException {
		moveFile(sourcePath, destinationPath, false);
	}

	public default void moveFile(Path sourcePath, Path destinationPath) throws IOException {
		if ((sourcePath == null) || (destinationPath == null)) {
			return;
		}

		File sourceFile = sourcePath.toFile();

		if (!sourceFile.exists()) {
			return;
		}

		Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
	}

	public default void moveFile(Path sourcePath, Path destinationPath, boolean removeSourceFile) throws IOException {
		if ((sourcePath == null) || (destinationPath == null)) {
			return;
		}

		File sourceFile = sourcePath.toFile();

		if (!sourceFile.exists()) {
			return;
		}

		if (removeSourceFile) {
			Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			File srcFile = sourcePath.toFile();
			File destFile = destinationPath.toFile();

			if (srcFile.isDirectory()) {
				FileUtils.copyDirectory(sourcePath.toFile(), destinationPath.toFile(), true);
			}
			else {
				FileUtils.copyFile(srcFile, destFile);
			}
		}
	}

}