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

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.util.StringUtil;

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
 */
public class InstallTemplateCommand extends BaseCommand<InstallTemplateCommandArgs> {

	public InstallTemplateCommand()  {
	}

	public void execute() throws Exception {
		String arg = _args.getPath();

		Path path = StringUtil.isNullOrEmpty(arg) ? Paths.get(".") : Paths.get(arg);

		if (Files.exists(path)) {
			Path templatePath = Optional.of(
				path
			).filter(
				Files::exists
			).filter(
				Files::isDirectory
			).filter(
				Util::isGradleBuildPath
			).map(
				this::_gradleAssemble
			).orElse(
				path
			);

			_installTemplatePath(templatePath);
		}
		else {
			throw new Exception("Template path must exist");
		}
	}

	@Override
	public Class<InstallTemplateCommandArgs> getArgsClass() {
		return InstallTemplateCommandArgs.class;
	}

	private Path _gradleAssemble(Path projectPath) {
		GradleExec gradle = new GradleExec(_blade);

		try {
			Set<File> outputFiles = GradleTooling.getOutputFiles(_blade.getCacheDir(), projectPath.toFile());

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

	private void _installTemplate(Path templatePath) throws IOException {
		Path templatesHome = Util.getCustomTemplatesPath();

		Path templateName = templatePath.getFileName();

		Path newTemplatePath = templatesHome.resolve(templateName);

		Files.copy(templatePath, newTemplatePath);

		_blade.out("The template " + newTemplatePath.getFileName() + " has been installed successfully.");
	}

	private void _installTemplatePath(Path outputFile) throws Exception, IOException {
		if (_isTemplateMatch(outputFile)) {
			_installTemplate(outputFile);
		}
		else {
			throw new Exception();
		}
	}

	private boolean _isTemplateMatch(Path path) {
		return _customTemplatePathMatcher.matches(path);
	}

	private static final PathMatcher _customTemplatePathMatcher = FileSystems.getDefault().getPathMatcher(
		"glob:**/*.project.templates.*");

}