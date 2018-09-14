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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.gradle.ProcessResult;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.Prompter;
import com.liferay.blade.cli.util.StringUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class InstallExtensionCommand extends BaseCommand<InstallExtensionArgs> {

	public InstallExtensionCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		InstallExtensionArgs args = getArgs();

		String pathArg = args.getPath();

		if (StringUtil.isNullOrEmpty(pathArg)) {
			pathArg = ".";
		}

		String pathArgLower = pathArg.toLowerCase();

		if (pathArgLower.startsWith("http") && _isValidURL(pathArg)) {
			if (pathArgLower.contains("//github.com/")) {
				Path path = Files.createTempDirectory(null);

				try {
					Path zip = path.resolve("master.zip");

					File dir = path.toFile();

					bladeCLI.out("Downloading github repository " + pathArg);

					BladeUtil.downloadGithubProject(pathArg, zip);

					bladeCLI.out("Unzipping github repository to " + path);

					BladeUtil.unzip(zip.toFile(), dir, null);

					File[] directories = dir.listFiles(File::isDirectory);

					if ((directories != null) && (directories.length > 0)) {
						Path directory = directories[0].toPath();

						if (_isGradleBuild(directory)) {
							bladeCLI.out("Building extension...");

							Set<Path> extensionPaths = _gradleAssemble(directory);

							if (!extensionPaths.isEmpty()) {
								for (Path extensionPath : extensionPaths) {
									_installExtension(extensionPath);
								}
							}
							else {
								bladeCLI.err("Unable to get output of gradle build " + directory);
							}
						}
						else {
							bladeCLI.err("Path not a gradle build " + directory);
						}
					}
				}
				catch (Exception e) {
					throw e;
				}
				finally {
					FileUtil.deleteDir(path);
				}
			}
			else {
				throw new Exception("Only github http(s) links are supported");
			}
		}
		else {
			Path path = Paths.get(pathArg);

			if (Files.exists(path)) {
				Path gradleBuildPath = Optional.of(
					path
				).filter(
					Files::exists
				).filter(
					Files::isDirectory
				).filter(
					InstallExtensionCommand::_isGradleBuild
				).orElse(
					null
				);

				if (gradleBuildPath != null) {
					Set<Path> paths = _gradleAssemble(path);

					if (!paths.isEmpty()) {
						Iterator<Path> pathsIterator = paths.iterator();

						path = pathsIterator.next();
					}
				}
			}

			if (path == null) {
				throw new Exception("Path to extension does not exist: " + pathArg);
			}
			else {
				_installExtension(path);
			}
		}
	}

	@Override
	public Class<InstallExtensionArgs> getArgsClass() {
		return InstallExtensionArgs.class;
	}

	private static Set<Path> _getExistingPaths(Set<File> outputFiles) {
		Stream<File> stream = outputFiles.stream();

		return stream.map(
			File::toPath
		).filter(
			Files::exists
		).collect(
			Collectors.toSet()
		);
	}

	private static boolean _isArchetype(Path path) {
		return BladeUtil.searchZip(path, name -> name.endsWith("archetype-metadata.xml"));
	}

	private static boolean _isCustomTemplate(Path path) {
		if (_isTemplateMatch(path) && _isArchetype(path)) {
			return true;
		}

		return false;
	}

	private static boolean _isExtension(Path path) {
		if (_isCustomTemplate(path)) {
			return true;
		}

		return BladeUtil.searchZip(
			path, name -> name.startsWith("META-INF/services/com.liferay.blade.cli.command.BaseCommand"));
	}

	private static boolean _isGradleBuild(Path path) {
		if ((path != null) && Files.exists(path.resolve("build.gradle"))) {
			return true;
		}

		return false;
	}

	private static boolean _isTemplateMatch(Path path) {
		if (Files.exists(path) && (_customTemplatePathMatcher.matches(path) || _isArchetype(path))) {
			return true;
		}

		return false;
	}

	private static boolean _isValidURL(String urlString) {
		try {
			new URL(urlString).toURI();

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	private Set<Path> _gradleAssemble(Path projectPath) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradleExec = new GradleExec(bladeCLI);

		Path cachePath = bladeCLI.getCachePath();

		Set<File> outputFiles = GradleTooling.getOutputFiles(cachePath.toFile(), projectPath.toFile());

		ProcessResult processResult = gradleExec.executeTask("assemble -x check", projectPath.toFile());

		int resultCode = processResult.getResultCode();

		if (resultCode > 0) {
			String output = processResult.get();

			throw new Exception("Gradle command returned error code " + resultCode + System.lineSeparator() + output);
		}

		return _getExistingPaths(outputFiles);
	}

	private void _installExtension(Path extensionPath) throws IOException {
		Path extensionName = extensionPath.getFileName();

		if (_isExtension(extensionPath)) {
			BladeCLI bladeCLI = getBladeCLI();

			Path extensionsPath = bladeCLI.getExtensionsPath();

			Path extensionInstallPath = extensionsPath.resolve(extensionName);

			boolean exists = Files.exists(extensionInstallPath);

			String newExtensionVersion = BladeUtil.getBundleVersion(extensionPath);

			if (exists) {
				bladeCLI.out(
					String.format(
						"The extension %s already exists with version %s.\n", extensionName,
						BladeUtil.getBundleVersion(extensionInstallPath)));

				String message = String.format("Overwrite existing extension with version %s?", newExtensionVersion);

				boolean overwrite = Prompter.confirm(message, bladeCLI.in(), bladeCLI.out(), Optional.of(false));

				if (!overwrite) {
					return;
				}
			}

			Files.copy(
				extensionPath, extensionInstallPath, StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES);

			bladeCLI.out(String.format("The extension %s has been installed successfully.", extensionName));
		}
		else {
			throw new IOException(
				String.format("Unable to install. %s is not a valid blade extension.", extensionName));
		}
	}

	private static final PathMatcher _customTemplatePathMatcher = FileSystems.getDefault().getPathMatcher(
		"glob:**/*.project.templates.*");

}