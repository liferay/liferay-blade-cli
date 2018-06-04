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

import com.beust.jcommander.Parameters;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class Commands {

	public static Map<String, BaseCommand<? extends BaseArgs>> get() throws Exception {
		if (_commands == null) {
			_commands = new HashMap<>();

			URL[] jarUrls = _getJarUrls(Extensions.getDirectory());

			URLClassLoader serviceLoaderClassloader = new URLClassLoader(jarUrls, Commands.class.getClassLoader());

			@SuppressWarnings("rawtypes")
			ServiceLoader<BaseCommand> serviceLoader = ServiceLoader.load(BaseCommand.class, serviceLoaderClassloader);

			for (BaseCommand<?> baseCommand : serviceLoader) {
				Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();

				Parameters parameters = argsClass.getAnnotation(Parameters.class);

				if (parameters == null) {
					throw new IllegalArgumentException(
						"Loaded base command class that doesn't have a Parameters annotation " + argsClass.getName());
				}

				String[] commandNames = parameters.commandNames();

				_commands.put(commandNames[0], baseCommand);
			}
		}

		return _commands;
	}

	private static Map<String, BaseCommand<? extends BaseArgs>> _commands;

	private static URL[] _getJarUrls(Path jarsPath) throws IOException {
		return Files.list(
			jarsPath
		).filter(
			path -> {
				String pathString = path.toString();

				return pathString.endsWith(".jar");
			}
		).map(
			Path::toUri
		).map(
			uri -> {
				try {
					return uri.toURL();
				}
				catch (MalformedURLException e) {
				}

				return null;
			}
		).filter(
			url -> url != null
		).collect(
			Collectors.toSet()
		).toArray(
			new URL[0]
		);
	}

}
