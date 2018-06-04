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

import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;

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
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class Extensions {

	public static Map<BaseArgs, BaseCommand<?>> getExtensions() {
		if (_EXTENSIONS == null) {
			_EXTENSIONS = new HashMap<>();
			loadExtensions();
		}

		return _EXTENSIONS;
	}

	public static void loadExtensions() {
		if (!_EXTENSIONS_LOADED) {
		try {
			loadExtensionsFromClasspath();
			_EXTENSIONS_LOADED = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	private static URLClassLoader buildURLClassLoader(URL[] urls) {
		return URLClassLoader.newInstance(urls, null);
	}

	public static void loadBuiltinCommands() {
		if (!_BUILTIN_COMMANDS_LOADED) {
			try {
				loadBuiltinCommandsFromClasspath();
				_BUILTIN_COMMANDS_LOADED = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<BaseArgs, BaseCommand<? extends BaseArgs>> getBuiltinCommands() {
		if (_BUILTIN_COMMANDS == null) {
			_BUILTIN_COMMANDS = new HashMap<>();
			loadBuiltinCommands();
		}

		return _BUILTIN_COMMANDS;
	}

	public static void loadBuiltinCommandsFromClasspath() throws Exception {
		getBuiltinCommands().clear();

		ServiceLoader<BaseCommand> loader = ServiceLoader.load(BaseCommand.class);

		for (BaseCommand<?> baseCommand : loader) {
			Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();
			BaseArgs args = argsClass.newInstance();

			getBuiltinCommands().put(args, baseCommand.getClass().newInstance());
		}
	}

	private static URL[] getJarUrls(Path extensionsPath) throws Exception {
		Collection<URL> urls = new HashSet<>();

		for (Path extension :
				Files.list(extensionsPath).filter(p -> p.toString().endsWith(".jar")).collect(Collectors.toSet())) {

			URL url = extension.toUri().toURL();

			urls.add(url);
		}

		return urls.toArray(new URL[0]);
	}

	public static Path getDirectory() {
		try {
			Path home = Paths.get(_USER_HOME);

			Path dotBlade = home.resolve(".blade");

			if (Files.notExists(dotBlade)) {
				Files.createDirectory(dotBlade);
			}

			else

			if (!Files.isDirectory(dotBlade)) {
				throw new Exception(".blade is not a directory!");
			}

			Path extensions = dotBlade.resolve("extensions");

			if (Files.notExists(extensions)) {
				Files.createDirectory(extensions);
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

	public static void loadExtensionsFromClasspath() throws Exception {
		getExtensions().clear();
		URL[] urls = getJarUrls(getDirectory());

		URLClassLoader extensionsClassLoader = buildURLClassLoader(urls);

		ServiceLoader<BaseCommand> loader = ServiceLoader.load(
			BaseCommand.class, extensionsClassLoader);

		for (BaseCommand<?> baseCommand : loader) {
			Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();
			BaseArgs args = argsClass.newInstance();

			getExtensions().put(args, baseCommand);
		}
	}

	public static void resetBuiltinCommands() {
		_BUILTIN_COMMANDS = null;
		_BUILTIN_COMMANDS_LOADED = false;
	}

	public static void resetExtensions() {
		_EXTENSIONS = null;
		_EXTENSIONS_LOADED = false;
	}

	private static Map<BaseArgs, BaseCommand<? extends BaseArgs>> _BUILTIN_COMMANDS = null;

	private static boolean _BUILTIN_COMMANDS_LOADED = false;

	private static Map<BaseArgs, BaseCommand<? extends BaseArgs>> _EXTENSIONS = null;

	private static boolean _EXTENSIONS_LOADED = false;

	public static String[] sort(String[] args) {
		List<String> argsList = Arrays.asList(args);

		Collection<String> addLast = new ArrayList<>();

		Collection<BaseArgs> argList = new HashSet<>();
		Stream.of(getBuiltinCommands().keySet(), getExtensions().keySet()).forEach(argList::addAll);

		Collection<Class<? extends BaseArgs>> classes =
			argList.stream().map(BaseArgs::getClass).collect(Collectors.toSet());
		Collection<String> spaceCommandCollection = Util.getCommandNames(
			classes).stream().filter(x -> x.contains(" ")).collect(Collectors.toSet());
		Collection<String[]> spaceCommandSplitCollection = new ArrayList<>();
		spaceCommandCollection.stream().map(x -> x.split(" ")).forEach(spaceCommandSplitCollection::add);

		Collection<String> flagsWithoutArgs = Util.getFlagsWithoutArguments(BaseArgs.class);
		Collection<String> flagsWithArgs = Util.getFlagsWithArguments(BaseArgs.class);

		for (int x = 0; x < argsList.size(); x++) {
			String s = argsList.get(x);

			if (flagsWithArgs.contains(s)) {
				addLast.add(argsList.remove(x));
				addLast.add(argsList.remove(x));
			} else if (flagsWithoutArgs.contains(s)) {
				addLast.add(argsList.remove(x));
			}
			else {
				if (spaceCommandSplitCollection.size() > 0) {
					String[] foundStrArray = null;

					for (String[] strArray : spaceCommandSplitCollection) {
						if (argsList.size() == (x + strArray.length)) {
						} else

						if (argsList.size() > x + (strArray.length - 1)) {
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

}
