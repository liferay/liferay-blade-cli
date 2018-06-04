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

import com.liferay.blade.cli.Util;
import com.liferay.blade.cli.util.StringUtil;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Optional;

/**
 * @author Christopher Bryan Boyd
 */
public class UninstallTemplate extends BaseCommand<UninstallTemplateArgs> {

	public UninstallTemplate() {
	}

	public void execute() throws Exception {
		Optional<String> templateName = Optional.of(
			_args.getName()
		).filter(
			name -> !StringUtil.isNullOrEmpty(name)
		).filter(
			name -> name.endsWith(".jar")
		);

		if (templateName.isPresent()) {
			try {
				String template = templateName.get();

				_removeTemplate(template);

				_blade.out("The template " + template + " has been uninstalled successfully.");
			}
			catch (IOException ioe) {
				_blade.err(ioe.getMessage());
			}
		}
		else {
			_blade.err("Invalid template name specified.");
		}
	}

	private static void _removeTemplate(String name) throws IOException {
		Path templatesHome = Util.getCustomTemplatesPath();

		Path templatePath = templatesHome.resolve(name);

		Files.delete(templatePath);
	}

	@Override
	public Class<UninstallTemplateArgs> getArgsClass() {
		return UninstallTemplateArgs.class;
	}

}