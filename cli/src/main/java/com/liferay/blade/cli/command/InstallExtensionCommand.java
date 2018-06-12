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
		String pathArg = getArgs().getPath();

		if (StringUtil.isNullOrEmpty(pathArg)) {
			pathArg = ".";
		}
		if (pathArg.toLowerCase().startsWith("http") && BladeUtil.isValidURL(pathArg)) {
			if (pathArg.toLowerCase().contains("github")) {
				Path path = Files.createTempDirectory(null);
				try 
				{
					Path zip = path.resolve("master.zip");
					
					BladeUtil.downloadGithubProject(pathArg, zip);
					BladeUtil.unzip(zip.toFile(), path.toFile(), null);
					
					if (BladeUtil.isGradleBuildPath(path)) {
						_gradleAssemble(path);
						
						_installExtension(path);
					}	
				} catch (Exception e) {
					throw e;
				} finally {
					FileUtil.deleteDir(path);					
				}
			} else {
				throw new Exception("Only Github HTTP links are supported.");
			}
		} else {
			Path path = Paths.get(pathArg);
	
			if (Files.exists(path)) {
				Path extensionJarPath = Optional.of(
					path
				).filter(
					Files::exists
				).filter(
					Files::isDirectory
				).filter(
					BladeUtil::isGradleBuildPath
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
		
		if (_isTemplateMatch(extensionPath) || BladeUtil.isExtension(extensionPath)) {
			
			Path extensionsHome = Extensions.getDirectory();
			
			Path extensionName = extensionPath.getFileName();
			
			Path newExtensionPath = extensionsHome.resolve(extensionName);
			
			Files.copy(extensionPath, newExtensionPath);
			
			getBladeCLI().out("The extension " + extensionName + " has been installed successfully.");
			
		} else {
			throw new IOException("Unable to install, file " + extensionPath.getFileName() + 
			" is not a valid maven archetype or Blade extension.");
		}
	}

	private boolean _isTemplateMatch(Path path) {
		if (_customTemplatePathMatcher.matches(path) && Files.exists(path) && BladeUtil.isArchetype(path)) {
			return true;
		}
		return false;
	}
	
	private static final PathMatcher _customTemplatePathMatcher = FileSystems.getDefault().getPathMatcher(
			"glob:**/*.project.templates.*");
}