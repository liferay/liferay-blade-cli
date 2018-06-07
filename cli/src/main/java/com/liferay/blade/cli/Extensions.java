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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class Extensions {

	public static Path getDirectory() {
		try {
			Path home = Paths.get(_USER_HOME);

			Path dotBlade = home.resolve(".blade");

			if (Files.notExists(dotBlade)) {
				Files.createDirectories(dotBlade);
			}

			else

			if (!Files.isDirectory(dotBlade)) {
				throw new Exception(".blade is not a directory!");
			}

			Path extensions = dotBlade.resolve("extensions");

			if (Files.notExists(extensions)) {
				Files.createDirectories(extensions);
			}

			else

			if (!Files.isDirectory(extensions)) {
				throw new Exception(".blade/extensions is not a directory!");
			}

			return extensions;
		}
		catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException(e);
		}
	}

	private static final String _USER_HOME = System.getProperty("user.home");

	public static String[] sortArgs(Map<String, BaseCommand<? extends BaseArgs>> commands, String[] args) throws Exception {
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		Collection<String> addLast = new ArrayList<>();

		Collection<BaseArgs> argList = new HashSet<>();

		commands.values().stream().map(command -> command.getArgs()).forEach(argList::add);

		Collection<Class<? extends BaseArgs>> classes =
			argList.stream().map(BaseArgs::getClass).collect(Collectors.toSet());
		Collection<String> spaceCommandCollection = Extensions.getCommandNames(
			classes).stream().filter(x -> x.contains(" ")).collect(Collectors.toSet());
		Collection<String[]> spaceCommandSplitCollection = new ArrayList<>();
		spaceCommandCollection.stream().map(x -> x.split(" ")).forEach(spaceCommandSplitCollection::add);

		Collection<String> flagsWithoutArgs = _getFlagsWithoutArguments(BaseArgs.class);
		Collection<String> flagsWithArgs = _getFlagsWithArguments(BaseArgs.class);

		for (int x = 0; x < argsList.size(); x++) {
			String s = argsList.get(x);

			if (flagsWithArgs.contains(s)) {
				addLast.add(argsList.remove(x));
				addLast.add(argsList.remove(x));
			}
			else if (flagsWithoutArgs.contains(s)) {
				addLast.add(argsList.remove(x));
			}
			else {
				if (spaceCommandSplitCollection.size() > 0) {
					String[] foundStrArray = null;

					for (String[] strArray : spaceCommandSplitCollection) {
						if (argsList.size() == (x + strArray.length)) {
						}
						else if (argsList.size() > x + (strArray.length - 1)) {
							if (foundStrArray == null) {
								boolean mismatch = false;

								if (strArray.length == 0) {
									mismatch = true;
								}

								for (int y = 0; y < strArray.length; y++) {
									if (Objects.equals(strArray[y], argsList.get(x + y))) {
										continue;
									}

									mismatch = true;
								}

								if (!mismatch) {
									foundStrArray = strArray;

									break;
								}
							}
						}
					}

					if (foundStrArray != null) {
						Collection<String> commandParts = new ArrayList<>();

						for (int y = 0; y < foundStrArray.length; y++) {
							if (Objects.equals(foundStrArray[y], argsList.get(x + y))) {
								commandParts.add(foundStrArray[y]);
							}
						}

						StringBuilder newCommand = new StringBuilder();

						for (String commandPart : commandParts) {
							if (Objects.equals(commandPart, argsList.get(x))) {
								int len = newCommand.length();

								if (len > 0)newCommand.append(" ");

								newCommand.append(argsList.remove(x));
							}
						}

						argsList.add(x, newCommand.toString());
					}
				}
			}
		}

		argsList.addAll(addLast);

		return argsList.toArray(new String[0]);
	}

	public static Collection<String> getCommandNames(Collection<Class<? extends BaseArgs>> argsClass) {
		return argsClass.stream().map(clazz -> clazz.getAnnotation(Parameters.class)).filter(Objects::nonNull).map(x -> Arrays.asList(x.commandNames())).flatMap(List::stream).collect(Collectors.toList());
	}

	private Map<String, BaseCommand<? extends BaseArgs>> _commands;

	public Map<String, BaseCommand<? extends BaseArgs>> getCommands() throws Exception {
		if (_commands == null) {
			_commands = new HashMap<>();

			URL[] jarUrls = _getJarUrls(Extensions.getDirectory());

			URLClassLoader serviceLoaderClassloader = new URLClassLoader(jarUrls, this.getClass().getClassLoader());

			@SuppressWarnings("rawtypes")
			ServiceLoader<BaseCommand> serviceLoader = ServiceLoader.load(BaseCommand.class, serviceLoaderClassloader);

			for (BaseCommand<?> baseCommand : serviceLoader) {
				Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();

				BaseArgs baseArgs = argsClass.newInstance();

				baseCommand.setArgs(baseArgs);

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

	private static Collection<String> _getFlagsWithArguments(Class<? extends BaseArgs> clazz) {
		Collection<String> flags = new ArrayList<>();

		for (Field field : clazz.getDeclaredFields()) {
			Parameter annotation = field.getAnnotation(Parameter.class);

			if (annotation != null && (annotation.names() != null && annotation.names().length > 0)) {
				if (!field.getType().equals(boolean.class)) {
					for (String fName : annotation.names()) {
						flags.add(fName);
					}
				}
			}
		}

		return flags;
	}

	private static Collection<String> _getFlagsWithoutArguments(Class<? extends BaseArgs> clazz) {
		Collection<String> flags = new ArrayList<>();

		for (Field field : clazz.getDeclaredFields()) {
			Parameter annotation = field.getAnnotation(Parameter.class);

			if (annotation != null && (annotation.names() != null && annotation.names().length > 0)) {
				if (field.getType().equals(boolean.class)) {
					for (String fName : annotation.names()) {
						flags.add(fName);
					}
				}
			}
		}

		return flags;
	}

}
