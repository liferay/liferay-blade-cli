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
import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.StringUtil;
import com.liferay.project.templates.internal.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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
			if (pathArgLower.contains("github")) {
				Path path = Files.createTempDirectory(null);

				try {
					Path zip = path.resolve("master.zip");

					bladeCLI.out("Downloading github repository " + pathArg);

					BladeUtil.downloadGithubProject(pathArg, zip);

					bladeCLI.out("Unzipping github repository to " + path);

					BladeUtil.unzip(zip.toFile(), path.toFile(), null);

					if (_isGradleBuild(path)) {
						bladeCLI.out("Building extension...");

						Path extensionPath = _gradleAssemble(path);

						_installExtension(extensionPath);
					}
					else {
						bladeCLI.err("Path not a gradle build " + path);
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
				Path extensionJarPath = Optional.of(
					path
				).filter(
					Files::exists
				).filter(
					Files::isDirectory
				).filter(
					InstallExtensionCommand::_isGradleBuild
				).map(
					this::_gradleAssemble
				).orElse(
					path
				);

				_installExtension(extensionJarPath);
			}
			else {
				throw new Exception("Path to extension does not exist: " + pathArg);
			}
		}
	}

	@Override
	public Class<InstallExtensionArgs> getArgsClass() {
		return InstallExtensionArgs.class;
	}

	private static boolean _isArchetype(Path path) {
		return BladeUtil.searchJar(path, name -> name.endsWith("archetype-metadata.xml"));
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

		String search = String.valueOf(Paths.get("META-INF", "services", "com.liferay.blade.cli.command"));

		return BladeUtil.searchJar(path, name -> name.startsWith(search));
	}

	private static boolean _isGradleBuild(Path path) {
		if ((path != null) && Files.exists(path.resolve("build.gradle"))) {
			return true;
		}

		return false;
	}

	private static boolean _isTemplateMatch(Path path) {
		if (_customTemplatePathMatcher.matches(path) && Files.exists(path) && _isArchetype(path)) {
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

	private Path _gradleAssemble(Path projectPath) {
		BladeCLI bladeCLI = getBladeCLI();

		GradleExec gradle = new GradleExec(bladeCLI);

		try {
			Set<File> outputFiles = GradleTooling.getOutputFiles(bladeCLI.getCacheDir(), projectPath.toFile());

			gradle.executeGradleCommand("assemble -x check");

			Iterator<File> i = outputFiles.iterator();

			if (i.hasNext()) {
				File next = i.next();

				Path outputPath = next.toPath();

				if (Files.exists(outputPath)) {
					return outputPath;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void _installExtension(Path extensionPath) throws IOException {
		if (_isExtension(extensionPath)) {
			Path extensionsHome = Extensions.getDirectory();

			Path extensionName = extensionPath.getFileName();

			Path newExtensionPath = extensionsHome.resolve(extensionName);

			Files.copy(extensionPath, newExtensionPath);

			getBladeCLI().out("The extension " + extensionName + " has been installed successfully.");
		}
		else {
			throw new IOException(
				"Unable to install. " + extensionPath.getFileName() +
					" is not a valid blade extension, e.g. custom template or command");
		}
	}

	private static final PathMatcher _customTemplatePathMatcher = FileSystems.getDefault().getPathMatcher(
		"glob:**/*.project.templates.*");

}