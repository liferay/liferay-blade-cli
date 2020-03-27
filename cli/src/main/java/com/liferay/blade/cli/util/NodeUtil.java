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

/**
 * @author David Truong
 */
public class NodeUtil {

	public static final String YO_GENERATOR_8_VERSION = "8.x";

	public static final String YO_GENERATOR_9_VERSION = "9.x";

	public static Path downloadNode() throws IOException {
		Path bladeCachePath = BladeUtil.getBladeCachePath();

		Path nodeCachePath = bladeCachePath.resolve("node");

		if (!Files.exists(nodeCachePath)) {
			Files.createDirectories(nodeCachePath);

			String nodeURL = _getNodeURL();

			Path downloadPath = bladeCachePath.resolve(nodeURL.substring(nodeURL.lastIndexOf("/") + 1));

			if (!Files.exists(downloadPath)) {
				BladeUtil.downloadLink(nodeURL, downloadPath);
			}

			FileUtil.unpack(downloadPath, nodeCachePath, 1);

			if (OSDetector.isWindows()) {
				Path nodePath;

				try (Stream<Path> paths = Files.list(nodeCachePath)) {
					nodePath = paths.findFirst(
					).get();
				}

				try (Stream<Path> nodePaths = Files.list(nodePath)) {
					nodePaths.forEach(
						x -> {
							try {
								Files.move(
									x, nodeCachePath.resolve(x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
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
					nodeCachePath.resolve("bin/node"), PosixFilePermissions.fromString("rwxrwxr--"));
				Files.setPosixFilePermissions(
					nodeCachePath.resolve("bin/npm"), PosixFilePermissions.fromString("rwxrwxr--"));
			}
		}

		return nodeCachePath;
	}

	public static Path downloadYo() throws Exception {
		return downloadYo(YO_GENERATOR_9_VERSION);
	}

	public static Path downloadYo(String version) throws Exception {
		Path bladeCachePath = BladeUtil.getBladeCachePath();

		Path nodeDirPath = bladeCachePath.resolve("node");

		Path yoDirPath = bladeCachePath.resolve("yo-" + version);

		Path nodeModulesDirPath = yoDirPath.resolve("node_modules");

		if (!Files.exists(nodeModulesDirPath)) {
			Files.createDirectories(yoDirPath);

			InputStream inputStream = NodeUtil.class.getResourceAsStream(
				"dependencies" + File.separator + "yo-" + version + ".json");

			Path targetPath = yoDirPath.resolve("package.json");

			Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

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

	public static int runYo(String version, File dir, String[] args) throws Exception {
		Path nodeDirPath = downloadNode();

		Path yoDirPath = downloadYo(version);

		ProcessBuilder processBuilder = new ProcessBuilder();

		File cwd = new File(System.getProperty("user.dir"));

		processBuilder.directory(cwd);

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
		}
		else {
			env.put("PATH", env.get("PATH") + ":/bin:/usr/local/bin");

			Path nodePath = nodeDirPath.resolve("bin/node");
			Path yoPath = yoDirPath.resolve("node_modules/.bin/yo");

			commands.add("sh");
			commands.add("-c");
			commands.add(nodePath.toString());
			commands.add(yoPath.toString());
		}

		for (String arg : args) {
			commands.add(arg);
		}

		processBuilder.command(commands);
		processBuilder.inheritIO();

		if ((dir != null) && dir.exists()) {
			processBuilder.directory(dir);
		}

		Process process = processBuilder.start();

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		return process.waitFor();
	}

	public static int runYo(String[] args) throws Exception {
		return runYo(YO_GENERATOR_9_VERSION, null, args);
	}

	private static String _getNodeURL() {
		String nodeVersion = _getNodeVersion();

		if ((nodeVersion == null) || nodeVersion.equals("")) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("http://nodejs.org/dist/v");
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

	private static String _nodeVersion = "8.4.0";

}