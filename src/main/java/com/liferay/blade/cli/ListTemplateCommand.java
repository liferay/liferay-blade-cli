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

/**
 * @author Christopher Bryan Boyd
 */
public class ListTemplateCommand {
	
	public static final String DESCRIPTION = "Lists all currently installed custom project template jars.";

	public ListTemplateCommand(BladeCLI blade, ListTemplateCommandArgs args) throws Exception {
		_blade = blade;
		_args = args;
		
	}

	public void execute() throws Exception {

		_blade.out("Currently installed custom project templates:");
		for (String jarName : Util.getTemplateJarNames()) {
			_blade.out(jarName);
			
		}
	}
	
	private final ListTemplateCommandArgs _args;
	private final BladeCLI _blade;
}
