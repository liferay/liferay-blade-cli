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

import aQute.lib.io.IO;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;
import com.liferay.project.templates.internal.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class InitCommand {

	public InitCommand(BladeCLI blade, InitCommandArgs options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		String name = _options.getName();

		File destDir = name != null ? new File(_blade.getBase(), name) : _blade.getBase();

		File temp = null;

		boolean pluginsSDK = _isPluginsSDK(destDir);

		_trace("Using destDir " + destDir);

		if (destDir.exists() && !destDir.isDirectory()) {
			_addError(destDir.getAbsolutePath() + " is not a directory.");
			return;
		}

		boolean mavenBuild = "maven".equals(_options.getBuild());

		if (destDir.exists()) {
			if (pluginsSDK) {
				if (!_isPluginsSDK70(destDir)) {
					if (_options.isUpgrade()) {
						if (mavenBuild) {
							_addError("Unsupport to upgrade plugins sdk in liferay maven workpace now.");
							return;
						}

						_trace(
							"Found plugins-sdk 6.2, upgraded to 7.0, moving contents to new subdirectory and initing " +
								"workspace.");

						for (String fileName : _SDK_6_GA5_FILES) {
							File file = new File(destDir, fileName);

							if (file.exists()) {
								file.delete();
							}
						}
					}
					else {
						_addError(
							"Unable to run blade init in plugins sdk 6.2, please add -u (--upgrade) if you want to " +
								"upgrade to 7.0");
						return;
					}
				}

				_trace("Found plugins-sdk, moving contents to new subdirectory and initing workspace.");

				temp = Files.createTempDirectory("orignal-sdk").toFile();

				_moveContentsToDirectory(destDir, temp);
			}
			else if (destDir.list().length > 0) {
				if (_options.isForce()) {
					_trace("Files found, initing anyways.");
				}
				else {
					_addError(
						destDir.getAbsolutePath() +
							" contains files, please move them before continuing or use -f (--force) option to init " +
								"workspace anyways.");
					return;
				}
			}
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		if (name == null) {
			name = destDir.getName();
		}

		File destParentDir = destDir.getParentFile();

		projectTemplatesArgs.setDestinationDir(destParentDir);

		if (_options.isForce() || _options.isUpgrade()) {
			projectTemplatesArgs.setForce(true);
		}

		projectTemplatesArgs.setGradle(!mavenBuild);
		projectTemplatesArgs.setMaven(mavenBuild);
		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setTemplate("workspace");

		new ProjectTemplates(projectTemplatesArgs);

		if (mavenBuild) {
			FileUtil.deleteFiles(destDir.toPath(), "gradle.properties", "gradle-local.properties");
		}

		if (pluginsSDK) {
			if (_options.isUpgrade() && !mavenBuild) {
				GradleExec gradleExec = new GradleExec(_blade);

				gradleExec.executeGradleCommand("upgradePluginsSDK");
			}

			File gitFile = new File(temp, ".git");

			if (gitFile.exists()) {
				File destGitFile = new File(destDir, ".git");

				_moveContentsToDirectory(gitFile, destGitFile);

				IO.deleteWithException(gitFile);
			}

			File pluginsSdkDir = new File(destDir, "plugins-sdk");

			_moveContentsToDirectory(temp, pluginsSdkDir);

			IO.deleteWithException(temp);
		}
	}

	private void _addError(String msg) {
		_blade.addErrors("init", Collections.singleton(msg));
	}

	private boolean _isPluginsSDK(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		List<String> names = Arrays.asList(dir.list());

		if ((names != null) && names.contains("portlets") && names.contains("hooks") && names.contains("layouttpl") &&
			names.contains("themes") && names.contains("build.properties") && names.contains("build.xml") &&
			names.contains("build-common.xml") && names.contains("build-common-plugin.xml")) {

			return true;
		}

		return false;
	}

	private boolean _isPluginsSDK70(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		File buildProperties = new File(dir, "build.properties");
		Properties properties = new Properties();

		InputStream in = null;

		try {
			in = new FileInputStream(buildProperties);

			properties.load(in);

			String sdkVersionValue = (String)properties.get("lp.version");

			if (sdkVersionValue.equals("7.0.0")) {
				return true;
			}
		}
		catch (Exception e) {
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
				}
			}
		}

		return false;
	}

	private void _moveContentsToDirectory(File src, File dest) throws IOException {
		Path source = src.toPath().toAbsolutePath();
		Path target = dest.toPath().toAbsolutePath();

		Files.walkFileTree(
			source,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					String dirName = dir.toFile().getName();

					if (!dirName.equals(src.getName())) {
						Files.delete(dir);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path targetDir = target.resolve(source.relativize(dir));

					if (!Files.exists(targetDir)) {
						Files.createDirectory(targetDir);
					}

					if (Util.isWindows() && !dir.toFile().canWrite()) {
						Files.setAttribute(dir, "dos:readonly", false);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Path targetFile = target.resolve(source.relativize(file));

					if (!Files.exists(targetFile)) {
						Files.copy(file, targetFile);
					}

					if (Util.isWindows() && !file.toFile().canWrite()) {
						Files.setAttribute(file, "dos:readonly", false);
					}

					Files.delete(file);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _trace(String msg) {
		_blade.trace("%s: %s", "init", msg);
	}

	private static final String[] _SDK_6_GA5_FILES = {
		"app-servers.gradle", "build.gradle", "build-plugins.gradle", "build-themes.gradle", "sdk.gradle",
		"settings.gradle", "util.gradle", "versions.gradle"
	};

	private final BladeCLI _blade;
	private final InitCommandArgs _options;

}