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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

/**
 * @author David Truong
 */
public class NodeUtil {

	public static final String YO_GENERATOR_8_VERSION = "8.x";

	public static final String YO_GENERATOR_9_VERSION = "9.x";

	public static final String YO_GENERATOR_10_VERSION = "10.x";

	public static Path downloadNode() throws IOException {
		Path bladeCachePath = BladeUtil.getBladeCachePath();

		Path nodeDirPath = bladeCachePath.resolve("node");

		if (!Files.exists(nodeDirPath) || !_containsFiles(nodeDirPath)) {
			Files.createDirectories(nodeDirPath);

			String nodeURL = _getNodeURL();

			Path downloadPath = bladeCachePath.resolve(nodeURL.substring(nodeURL.lastIndexOf("/") + 1));

			if (!Files.exists(downloadPath)) {
				BladeUtil.downloadLink(nodeURL, downloadPath);
			}

			FileUtil.unpack(downloadPath, nodeDirPath, 1);

			if (OSDetector.isWindows()) {
				Path nodePath;

				try (Stream<Path> paths = Files.list(nodeDirPath)) {
					nodePath = paths.findFirst(
					).get();
				}

				try (Stream<Path> nodePaths = Files.list(nodePath)) {
					nodePaths.forEach(
						x -> {
							try {
								Files.move(
									x, nodeDirPath.resolve(x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch (IOException ioe) {
								throw new RuntimeException(ioe);
							}
						});
				}

				Files.delete(nodePath);
			}
			else {
				Files.setPosixFilePermissions(
					nodeDirPath.resolve("bin/node"), PosixFilePermissions.fromString("rwxrwxr--"));
				Files.setPosixFilePermissions(
					nodeDirPath.resolve("bin/npm"), PosixFilePermissions.fromString("rwxrwxr--"));
			}
		}

		return nodeDirPath;
	}

	public static int runYo(String liferayVersion, File dir, String[] args) throws Exception {
		return runYo(liferayVersion, dir, args, false);
	}

	public static int runYo(String liferayVersion, File dir, String[] args, boolean isQuiet) throws Exception {
		Path nodeDirPath = downloadNode();

		Path yoDirPath = _installYo(liferayVersion);

		ProcessBuilder processBuilder = new ProcessBuilder();

		processBuilder.directory(dir);

		Map<String, String> env = processBuilder.environment();

		List<String> commands = new ArrayList<>();

		if (OSDetector.isWindows()) {
			commands.add("cmd.exe");
			commands.add("/c");

			Path nodePath = nodeDirPath.resolve("node.exe");

			Path yoPath = yoDirPath.resolve(
				"node_modules" + File.separator + "yo" + File.separator + "lib" + File.separator + "cli.js");

			commands.add(nodePath.toString());
			commands.add(yoPath.toString());

			for (String arg : args) {
				commands.add(arg);
			}
		}
		else {
			env.put("PATH", env.get("PATH") + ":/bin:/usr/local/bin");

			Path nodePath = nodeDirPath.resolve("bin/node");
			Path yoPath = yoDirPath.resolve("node_modules/.bin/yo");

			commands.add("/bin/sh");
			commands.add("-c");

			StringBuilder command = new StringBuilder();

			command.append("\"");
			command.append(nodePath.toString());
			command.append("\" \"");
			command.append(yoPath.toString());
			command.append("\" ");

			for (String arg : args) {
				command.append("\"");
				command.append(arg);
				command.append("\" ");
			}

			commands.add(command.toString());
		}

		processBuilder.command(commands);

		if (!isQuiet) {
			processBuilder.inheritIO();
		}

		if ((dir != null) && dir.exists()) {
			processBuilder.directory(dir);
		}

		Process process = processBuilder.start();

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		return process.waitFor();
	}

	private static boolean _containsFiles(Path path) throws IOException {
		try (Stream<Path> files = Files.list(path)) {
			if (files.count() > 0) {
				return true;
			}

			return false;
		}
	}

	private static boolean _contentEquals(Path path1, Path path2) throws Exception {
		return FileUtils.contentEqualsIgnoreEOL(path1.toFile(), path2.toFile(), "UTF-8");
	}

	private static String _getNodeURL() {
		String nodeVersion = _getNodeVersion();

		if ((nodeVersion == null) || nodeVersion.equals("")) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://nodejs.org/dist/v");
		sb.append(nodeVersion);
		sb.append("/node-v");
		sb.append(nodeVersion);
		sb.append('-');

		String os = "linux";

		if (OSDetector.isApple()) {
			os = "darwin";
		}
		else if (OSDetector.isWindows()) {
			os = "win";
		}

		sb.append(os);
		sb.append("-x");

		String bitmode = OSDetector.getBitmode();

		if (bitmode.equals("32")) {
			bitmode = "86";
		}

		sb.append(bitmode);

		if (OSDetector.isWindows()) {
			sb.append(".zip");
		}
		else {
			sb.append(".tar.gz");
		}

		return sb.toString();
	}

	private static String _getNodeVersion() {
		return _nodeVersion;
	}

	private static File _getNpmDir(File nodeDir) {
		File nodeModulesDir = new File(nodeDir, "node_modules");

		if (!nodeModulesDir.exists()) {
			nodeModulesDir = new File(nodeDir, "lib" + File.separator + "node_modules");
		}

		return new File(nodeModulesDir, "npm");
	}

	private static Path _installYo(String liferayVersion) throws Exception {
		Path bladeCachePath = BladeUtil.getBladeCachePath();

		Path nodeDirPath = bladeCachePath.resolve("node");

		String yoGeneratorVersion = NodeUtil.YO_GENERATOR_10_VERSION;

		if (liferayVersion.equals("7.0") || liferayVersion.equals("7.1")) {
			yoGeneratorVersion = NodeUtil.YO_GENERATOR_8_VERSION;
		}

		Path yoDirPath = bladeCachePath.resolve("yo-" + yoGeneratorVersion);

		Files.createDirectories(yoDirPath);

		Path newPackageJsonPath = yoDirPath.resolve("new_package.json");

		InputStream inputStream = NodeUtil.class.getResourceAsStream("dependencies/yo-" + yoGeneratorVersion + ".json");

		Files.copy(inputStream, newPackageJsonPath, StandardCopyOption.REPLACE_EXISTING);

		Path packageJsonPath = yoDirPath.resolve("package.json");

		boolean skipInstall = false;

		if (Files.exists(packageJsonPath)) {
			skipInstall = _contentEquals(packageJsonPath, newPackageJsonPath);
		}

		Path nodeModulesDirPath = yoDirPath.resolve("node_modules");

		skipInstall = skipInstall && Files.exists(nodeModulesDirPath);

		if (!skipInstall) {
			FileUtils.deleteQuietly(nodeModulesDirPath.toFile());

			Files.move(newPackageJsonPath, packageJsonPath, StandardCopyOption.REPLACE_EXISTING);

			File npmDir = _getNpmDir(nodeDirPath.toFile());

			Process process;

			if (OSDetector.isWindows()) {
				process = BladeUtil.startProcess(
					nodeDirPath.toString() + File.separator + "node.exe " + npmDir + File.separator + "bin" +
						File.separator + "npm-cli.js install --scripts-prepend-node-path",
					yoDirPath.toFile());
			}
			else {
				process = BladeUtil.startProcess(
					nodeDirPath.toString() + File.separator + "bin" + File.separator + "node " + npmDir +
						File.separator + "bin" + File.separator + "npm-cli.js install",
					yoDirPath.toFile());
			}

			int returnCode = process.waitFor();

			if (returnCode != 0) {
				throw new RuntimeException("Problem occurred while downloading yo");
			}
		}

		return yoDirPath;
	}

	private static String _nodeVersion = "8.4.0";

}