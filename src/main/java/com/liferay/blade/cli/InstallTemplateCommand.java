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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

/**
 * @author Christopher Bryan Boyd
 */
public class InstallTemplateCommand {
	
	public static final String DESCRIPTION = "Installs a custom project template into Blade.";

	public InstallTemplateCommand(BladeCLI blade, InstallTemplateCommandArgs args) throws Exception {
		_blade = blade;
		_args = args;
	}

	public void execute() throws Exception {

		String arg = _args.getPath();
		
		Path pathArg;
		if (Objects.isNull(arg) || arg.trim().length() == 0) {
			pathArg = Paths.get(".");
		} else
		{
			pathArg = Paths.get(arg);
		}
		// Possibly handle git and github links here
		if (Files.exists(pathArg)) {
			if (Files.isDirectory(pathArg)) {
				_gradleDeploy(_blade, pathArg);
			} else {
				// Install Jar Directly if it is valid
				// Or Error if it's not
				installTemplatePath(pathArg);
			}
		}
		else {
			throw new Exception("Path to template does not exist: " + pathArg);
		}
	}
	
	private void _gradleDeploy(BladeCLI blade, Path pathToProject) throws Exception {
		GradleExec gradle = new GradleExec(blade);

		Set<File> outputFiles = GradleTooling.getOutputFiles(blade.getCacheDir(), blade.getBase());

		gradle.executeGradleCommand("assemble -x check");
		
		Iterator<File> i = outputFiles.iterator();
		if (i.hasNext()) {
			Path outputFile = i.next().toPath();
			
			if (Files.exists(outputFile)) {

				installTemplatePath(outputFile);
			}
		}
	}

	private void installTemplatePath(Path outputFile) throws IOException, Exception {
		if (_isTemplateMatch(outputFile)) {
			_installTemplate(outputFile);
		} else {
			throw new Exception();
		}
	}
	
	private boolean _isTemplateMatch(Path path) {
		return _pathMatcher.matches(path);
	}

	private void _installTemplate(Path templatePath) throws IOException {
		Path templatesHome = Util.getTemplatesDirectory();
		
		Path templateName = templatePath.getFileName();
		
		Path newTemplatePath = templatesHome.resolve(templateName);
		
		Files.copy(templatePath, newTemplatePath);
		
		_blade.out("The template " + newTemplatePath.getFileName() + " has been installed successfully.");
	}
	
	private static final FileSystem _fileSystem = FileSystems.getDefault();
	private static final PathMatcher _pathMatcher = _fileSystem.getPathMatcher("glob:**/*.project.templates.*");

	private final InstallTemplateCommandArgs _args;
	private final BladeCLI _blade;
}
