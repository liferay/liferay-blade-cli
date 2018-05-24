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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Christopher Bryan Boyd
 */
public class UninstallTemplateCommand {
	
	public static final String DESCRIPTION = "Uninstalls a custo project template from Blade.";

	public UninstallTemplateCommand(BladeCLI blade, UninstallTemplateCommandArgs args) throws Exception {
		_blade = blade;
		_args = args;
		
	}

	public void execute() throws Exception {

		String name = _args.getName();
		
		if (Objects.nonNull(name) && name.length() > 0 && name.contains("project.templates") && name.endsWith(".jar")) {
			_removeTemplate(name);
			_blade.out("The template " + name + " has been uninstalled successfully.");
		}
		else {
			throw new Exception("Invalid template specified: " + name);
		}
	}
	
	private static void _removeTemplate(String name) throws IOException {
		Path templatesHome = Util.getTemplatesDirectory();
		
		Path templatePath = templatesHome.resolve(name);
		
		Files.delete(templatePath);
	}

	private final UninstallTemplateCommandArgs _args;
	private final BladeCLI _blade;
}
