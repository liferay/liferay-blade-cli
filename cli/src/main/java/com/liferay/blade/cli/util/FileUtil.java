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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * @author Gregory Amerson
 */
public class FileUtil {

	public static String collect(InputStream inputStream) throws IOException {
		return _collect(new BufferedReader(new InputStreamReader(inputStream, _UTF_8)));
	}

	public static File copy(InputStream in, File file) throws IOException {
		_copy(in, file.toPath());

		return file;
	}

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

	public static void deleteDirIfExists(Path dirPath) throws IOException {
		if (Files.exists(dirPath)) {
			deleteDir(dirPath);
		}
	}

	public static boolean exists(String path) {
		if (path == null) {
			return false;
		}

		File file = new File(path);

		return file.exists();
	}

	public static String getManifestProperty(File file, String name) throws IOException {
		try (JarFile jarFile = new JarFile(file)) {
			Manifest manifest = jarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			return attributes.getValue(name);
		}
	}

	public static String read(File file) throws IOException {
		return _collect(file.toPath(), _UTF_8);
	}

	public static void unpack(Path path, Path destinationDirPath, int stripComponents) throws IOException {
		String fileName = String.valueOf(path.getFileName());

		if (fileName.endsWith(".gz") || fileName.endsWith(".tar") || fileName.endsWith(".tgz")) {
			_untar(path, destinationDirPath, stripComponents);
		}
		else if (fileName.endsWith(".zip")) {
			unzip(path.toFile(), destinationDirPath.toFile());
		}
		else {
			throw new UnsupportedOperationException("Unsupported format for " + fileName);
		}
	}

	public static void unzip(File srcFile, File destDir) throws IOException {
		unzip(srcFile, destDir, null);
	}

	public static void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
		try (final ZipFile zip = new ZipFile(srcFile)) {
			final Enumeration<? extends ZipEntry> entries = zip.entries();

			boolean foundStartEntry = false;

			if (entryToStart == null) {
				foundStartEntry = true;
			}

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				String entryName = entry.getName();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entryName);

					continue;
				}

				if (entry.isDirectory() || ((entryToStart != null) && !entryName.startsWith(entryToStart))) {
					continue;
				}

				if (entryToStart != null) {
					entryName = entryName.replaceFirst(entryToStart, "");
				}

				final File f = new File(destDir, entryName);

				if (!BladeUtil.isSafelyRelative(f, destDir)) {
					throw new ZipException(
						"Entry " + f.getName() + " is outside of the target destination: " + destDir);
				}

				if (f.exists()) {
					Files.delete(f.toPath());

					if (f.exists()) {
						throw new IOException("Could not delete " + f.getAbsolutePath());
					}
				}

				final File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					final String msg = "Could not create dir: " + dir.getPath();

					throw new IOException(msg);
				}

				try (final InputStream in = zip.getInputStream(entry);
					final FileOutputStream out = new FileOutputStream(f)) {

					final byte[] bytes = new byte[1024];

					int count = in.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = in.read(bytes);
					}

					out.flush();
				}
			}
		}
	}

	public static void unzip(InputStream inputStream, File destinationDir) throws IOException {
		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
			ZipEntry zipEntry = null;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {
					continue;
				}

				String entryName = zipEntry.getName();

				final File f = new File(destinationDir, entryName);

				if (!BladeUtil.isSafelyRelative(f, destinationDir)) {
					throw new ZipException(
						"Entry " + f.getName() + " is outside of the target destination: " + destinationDir);
				}

				if (f.exists()) {
					Files.delete(f.toPath());

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

	private static String _collect(Path path, Charset encoding) throws IOException {
		return _collect(_reader(path, encoding));
	}

	private static String _collect(Reader reader) throws IOException {
		StringWriter stringWriter = new StringWriter();

		_copy(reader, stringWriter);

		return stringWriter.toString();
	}

	private static Path _copy(InputStream in, Path path) throws IOException {
		try (FileChannel out = _writeChannel(path)) {
			_copy(in, out);
		}

		return path;
	}

	private static WritableByteChannel _copy(InputStream inputStream, WritableByteChannel writableByteChannel)
		throws IOException {

		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(4096 * 16);

			byte[] buffer = byteBuffer.array();

			for (int size; (size = inputStream.read(buffer, byteBuffer.position(), byteBuffer.remaining())) > 0;) {
				byteBuffer.position(byteBuffer.position() + size);
				byteBuffer.flip();

				writableByteChannel.write(byteBuffer);

				byteBuffer.compact();
			}

			for (byteBuffer.flip(); byteBuffer.hasRemaining();) {
				writableByteChannel.write(byteBuffer);
			}

			return writableByteChannel;
		}
		finally {
			inputStream.close();
		}
	}

	private static Writer _copy(Reader reader, Writer writer) throws IOException {
		try {
			char[] buffer = new char[4096 * 16];

			for (int size; (size = reader.read(buffer, 0, buffer.length)) > 0;) {
				writer.write(buffer, 0, size);
			}

			return writer;
		}
		finally {
			reader.close();
		}
	}

	private static FileChannel _readChannel(Path path) throws IOException {
		return FileChannel.open(path, _readOptions);
	}

	private static BufferedReader _reader(Path path, Charset encoding) throws IOException {
		return _reader(_readChannel(path), encoding);
	}

	private static BufferedReader _reader(ReadableByteChannel in, Charset encoding) throws IOException {
		return new BufferedReader(Channels.newReader(in, encoding.newDecoder(), -1));
	}

	private static Path _stripComponents(Path path, int stripComponents, boolean directory) {
		if (stripComponents > 0) {
			int nameCount = path.getNameCount();

			if (stripComponents < nameCount) {
				return path.subpath(stripComponents, nameCount);
			}
			else if (directory && (stripComponents == nameCount)) {
				return path.relativize(path);
			}
		}

		return path;
	}

	private static void _untar(Path tarPath, Path destinationDirPath, int stripComponents) throws IOException {
		try (InputStream inputStream = Files.newInputStream(tarPath);
			TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
				new GzipCompressorInputStream(inputStream))) {

			TarArchiveEntry tarArchiveEntry = null;

			while ((tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
				Path destinationPath = destinationDirPath.resolve(
					_stripComponents(
						Paths.get(tarArchiveEntry.getName()), stripComponents, tarArchiveEntry.isDirectory()));

				if (tarArchiveEntry.isDirectory()) {
					Files.createDirectories(destinationPath);

					continue;
				}

				Files.copy(tarArchiveInputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

				Date lastModifiedDate = tarArchiveEntry.getLastModifiedDate();

				Files.setLastModifiedTime(destinationPath, FileTime.fromMillis(lastModifiedDate.getTime()));
			}
		}
	}

	private static FileChannel _writeChannel(Path path) throws IOException {
		return FileChannel.open(path, _writeOptions);
	}

	private static final Charset _UTF_8 = Charset.forName("UTF-8");

	private static final EnumSet<StandardOpenOption> _readOptions = EnumSet.of(StandardOpenOption.READ);
	private static final EnumSet<StandardOpenOption> _writeOptions = EnumSet.of(
		StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

}