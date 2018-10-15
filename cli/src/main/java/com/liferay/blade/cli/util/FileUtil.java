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

import aQute.lib.io.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

/**
 * @author Gregory Amerson
 */
public class FileUtil {

	public static void copyDir(Path source, Path target) throws IOException {
		if (!Files.exists(target)) {
			Files.createDirectories(target);
		}

		Files.walkFileTree(source, new CopyDirVisitor(source, target, StandardCopyOption.REPLACE_EXISTING));
	}

	public static void deleteDir(Path dirPath) throws IOException {
		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dirPath, IOException ioe) throws IOException {
					Files.delete(dirPath);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Files.delete(path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static void unzip(InputStream inputStream, File destinationDir) throws IOException {
		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
			ZipEntry zipEntry = null;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String entryName = zipEntry.getName();

				if (zipEntry.isDirectory()) {
					continue;
				}

				final File f = new File(destinationDir, entryName);

				if (!BladeUtil.isSafelyRelative(f, destinationDir)) {
					throw new ZipException(
						"Entry " + f.getName() + " is outside of the target destination: " + destinationDir);
				}

				if (f.exists()) {
					IO.delete(f);

					if (f.exists()) {
						throw new IOException("Could not delete " + f.getAbsolutePath());
					}
				}

				final File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					final String msg = "Could not create dir: " + dir.getPath();

					throw new IOException(msg);
				}

				try (final FileOutputStream out = new FileOutputStream(f)) {
					final byte[] bytes = new byte[1024];

					int count = zipInputStream.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = zipInputStream.read(bytes);
					}

					out.flush();
				}

				zipInputStream.closeEntry();
			}
		}
	}

}