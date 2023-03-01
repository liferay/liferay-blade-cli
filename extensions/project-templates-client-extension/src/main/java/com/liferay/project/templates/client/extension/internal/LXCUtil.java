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

package com.liferay.project.templates.client.extension.internal;

import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.OSDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 */
public class LXCUtil {

	public static Path downloadLxc() throws Exception {
		Path bladeCachePath = BladeUtil.getBladeCachePath();
		String lxcChecksumURL = _getLxcURL() + ".checksum";

		Path checksumPath = bladeCachePath.resolve(lxcChecksumURL.substring(lxcChecksumURL.lastIndexOf("/") + 1));

		String lxcURL = _getLxcURL();

		Path downloadPath = bladeCachePath.resolve(lxcURL.substring(lxcURL.lastIndexOf("/") + 1));

		Path lxcDirPath = bladeCachePath.resolve("lxc");

		if (Files.exists(checksumPath)) {
			Files.delete(checksumPath);
		}

		BladeUtil.downloadFile(lxcChecksumURL, lxcDirPath, checksumPath);

		if (Files.exists(lxcDirPath) && Files.exists(checksumPath) && Files.exists(downloadPath) &&
			!_validChecksum(checksumPath, downloadPath)) {

			Files.delete(checksumPath);
			Files.delete(downloadPath);
			FileUtil.deleteDir(lxcDirPath);
		}

		if (!Files.exists(lxcDirPath) || !_containsFiles(lxcDirPath) || !Files.exists(checksumPath) ||
			!Files.exists(downloadPath)) {

			Files.createDirectories(lxcDirPath);

			if (!Files.exists(checksumPath)) {
				BladeUtil.downloadFile(lxcChecksumURL, lxcDirPath, checksumPath);
			}

			if (!Files.exists(downloadPath)) {
				BladeUtil.downloadFile(lxcURL, lxcDirPath, downloadPath);
			}

			if (!_validChecksum(checksumPath, downloadPath)) {
				Files.delete(checksumPath);
				Files.delete(downloadPath);
				FileUtil.deleteDir(lxcDirPath);

				throw new IOException("Downloaded checksum failed, please try again");
			}

			FileUtil.unpack(downloadPath, lxcDirPath, 1);

			if (OSDetector.isWindows()) {
				Path lxcPath;

				try (Stream<Path> paths = Files.list(lxcDirPath)) {
					Optional<Path> findFirst = paths.findFirst();

					lxcPath = findFirst.get();
				}

				try (Stream<Path> paths = Files.list(lxcPath)) {
					paths.forEach(
						path -> {
							try {
								Files.move(
									path, lxcDirPath.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch (IOException ioException) {
								throw new RuntimeException(ioException);
							}
						});
				}

				Files.delete(lxcPath);
			}
			else {
				Files.setPosixFilePermissions(lxcDirPath.resolve("lxc"), PosixFilePermissions.fromString("rwxrwxr--"));
				Files.setPosixFilePermissions(lxcDirPath.resolve("lxc"), PosixFilePermissions.fromString("rwxrwxr--"));
			}
		}

		return lxcDirPath.resolve("lxc");
	}

	public static int run(Path dir, String[] args, Map<String, String> env, boolean quiet) throws Exception {
		Path lxcPath = downloadLxc();

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.directory(dir.toFile());

		List<String> commands = new ArrayList<>();

		if (OSDetector.isWindows()) {
			commands.add("cmd.exe");
			commands.add("/c");
			commands.add(lxcPath.toString());

			for (String arg : args) {
				commands.add(arg);
			}
		}
		else {
			commands.add("sh");
			commands.add("-c");

			StringBuilder command = new StringBuilder();

			command.append("\"");
			command.append(lxcPath.toString());
			command.append("\" ");

			for (String arg : args) {
				command.append("\"");
				command.append(arg);
				command.append("\" ");
			}

			commands.add(command.toString());
		}

		processBuilder.command(commands);

		if (!quiet) {
			processBuilder.inheritIO();
		}

		if ((dir != null) && Files.exists(dir)) {
			processBuilder.directory(dir.toFile());
		}

		env.forEach(processBuilder.environment()::put);

		Process process = processBuilder.start();

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		return process.waitFor();
	}

	private static boolean _containsFiles(Path path) throws Exception {
		try (Stream<Path> files = Files.list(path)) {
			if (files.count() > 0) {
				return true;
			}

			return false;
		}
	}

	private static String _createSha256(File file) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		try (InputStream fileInputStream = new FileInputStream(file)) {
			byte[] buffer = new byte[8192];
			int read = 0;

			while (read != -1) {
				read = fileInputStream.read(buffer);

				if (read > 0) {
					messageDigest.update(buffer, 0, read);
				}
			}
		}

		BigInteger bigInteger = new BigInteger(1, messageDigest.digest());

		return bigInteger.toString(16);
	}

	private static String _getLxcURL() {
		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/ipeychev/lxc-cli-release/releases/download/");
		sb.append(_lxcVersion);
		sb.append("/lxc-");

		String os = "linux";

		if (OSDetector.isApple()) {
			os = "macos";
		}
		else if (OSDetector.isWindows()) {
			os = "win";
		}

		sb.append(os);
		sb.append(".tgz");

		return sb.toString();
	}

	private static boolean _validChecksum(Path checksumPath, Path downloadPath) throws Exception {
		String content = FileUtil.read(checksumPath.toFile());

		String checksum = content.trim();

		String sha256 = _createSha256(downloadPath.toFile());

		return checksum.equals(sha256);
	}

	private static String _lxcVersion = "0.0.6";

}