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
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.ProcessesUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class Extensions implements Closeable {

	public static Collection<String> getCommandNames(Collection<Class<? extends BaseArgs>> argsClass) {
		Stream<Class<? extends BaseArgs>> stream = argsClass.stream();

		return stream.map(
			clazz -> clazz.getAnnotation(Parameters.class)
		).filter(
			Objects::nonNull
		).map(
			Parameters::commandNames
		).map(
			Arrays::asList
		).flatMap(
			List::stream
		).collect(
			Collectors.toList()
		);
	}

	public static String[] sortArgs(Map<String, BaseCommand<? extends BaseArgs>> commands, String[] args)
		throws Exception {

		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		Collection<String> addLast = new ArrayList<>();

		Collection<BaseArgs> argList = new HashSet<>();

		Collection<BaseCommand<? extends BaseArgs>> values = commands.values();

		Stream<BaseCommand<? extends BaseArgs>> valuesStream = values.stream();

		valuesStream.map(
			command -> command.getArgs()
		).forEach(
			argList::add
		);

		Stream<BaseArgs> argStream = argList.stream();

		Collection<String> commandNames = getCommandNames(
			argStream.map(
				BaseArgs::getClass
			).collect(
				Collectors.toSet()
			));

		Stream<String> commandNamesStream = commandNames.stream();

		Collection<String> spaceCommandCollection = commandNamesStream.filter(
			x -> x.contains(" ")
		).collect(
			Collectors.toSet()
		);

		Collection<String[]> spaceCommandSplitCollection = new ArrayList<>();

		Stream<String> spaceCommandStream = spaceCommandCollection.stream();

		spaceCommandStream.map(
			x -> x.split(" ")
		).forEach(
			spaceCommandSplitCollection::add
		);

		Collection<String> flagsWithArgs = _getFlags(BaseArgs.class, true);

		Collection<String> flagsWithoutArgs = _getFlags(BaseArgs.class, false);

		for (int x = 0; x < argsList.size(); x++) {
			String s = argsList.get(x);

			if (flagsWithArgs.contains(s)) {
				addLast.add(argsList.remove(x));
				addLast.add(argsList.remove(x));
				x--;
			}
		}

		for (int x = 0; x < argsList.size(); x++) {
			String s = argsList.get(x);

			if (flagsWithoutArgs.contains(s)) {
				addLast.add(argsList.remove(x));
			}
		}

		for (int x = 0; x < argsList.size(); x++) {
			if (!spaceCommandSplitCollection.isEmpty()) {
				String[] foundStrArray = null;

				for (String[] strArray : spaceCommandSplitCollection) {
					if ((argsList.size() >= (x + strArray.length)) && (foundStrArray == null)) {
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

							if (len > 0) {
								newCommand.append(" ");
							}

							newCommand.append(argsList.remove(x));
						}
					}

					argsList.add(x, newCommand.toString());
				}
			}
		}

		argsList.addAll(addLast);

		return argsList.toArray(new String[0]);
	}

	public Extensions(ClassLoader classLoader) {
		_serviceLoaderClassLoader = classLoader;
	}

	@Override
	public void close() throws IOException {
		if ((_embeddedTemplatesPath != null) && Files.exists(_embeddedTemplatesPath)) {
			try {
				FileUtil.deleteDirIfExists(_embeddedTemplatesPath);
			}
			catch (Exception e) {
			}
		}
	}

	public Map<String, BaseCommand<? extends BaseArgs>> getCommands() throws Exception {
		return _getCommands(null);
	}

	public Map<String, BaseCommand<? extends BaseArgs>> getCommands(String profileName) throws Exception {
		if (profileName == null) {
			profileName = "gradle";
		}

		return _getCommands(profileName);
	}

	public Path getTemplatesPath() throws IOException {
		if (_embeddedTemplatesPath == null) {
			long pid = ProcessesUtil.getAProcessId();

			_embeddedTemplatesPath = Files.createTempDirectory("templates-" + pid + "-");

			try (InputStream inputStream = Extensions.class.getResourceAsStream(
					"/blade-extensions-versions.properties")) {

				if (inputStream != null) {
					Properties properties = new Properties();

					properties.load(inputStream);

					Set<String> templates = new HashSet<>();
					ClassLoader classLoader = Extensions.class.getClassLoader();

					for (Object key : properties.keySet()) {
						String jarResource = key.toString() + "-" + properties.getProperty(key.toString()) + ".jar";

						if (jarResource.startsWith("com.liferay.project.templates") &&
							(classLoader.getResource(jarResource) != null)) {

							templates.add(jarResource);
						}
					}

					for (String template : templates) {
						try (InputStream extensionInputStream = classLoader.getResourceAsStream(template)) {
							Path extensionPath = _embeddedTemplatesPath.resolve(template);

							Files.copy(extensionInputStream, extensionPath, StandardCopyOption.REPLACE_EXISTING);
						}
						catch (Throwable th) {
							StringBuilder sb = new StringBuilder();

							sb.append("Error encountered while loading embedded custom template.");
							sb.append(System.lineSeparator());
							sb.append(th.getMessage());
							sb.append(System.lineSeparator());
							sb.append("Not loading template " + template + ".");
							sb.append(System.lineSeparator());

							String errorString = sb.toString();

							System.err.println(errorString);
						}
					}
				}
			}
			catch (Throwable th) {
				String errorMessage = "Error encountered while loading custom extensions." + System.lineSeparator();

				System.err.println(errorMessage);

				System.err.println(th.getMessage());
			}
		}

		return _embeddedTemplatesPath;
	}

	private static Collection<String> _getFlags(Class<? extends BaseArgs> clazz, boolean withArguments) {
		Collection<String> flags = new ArrayList<>();

		for (Field field : clazz.getDeclaredFields()) {
			Parameter annotation = field.getAnnotation(Parameter.class);

			if (annotation != null) {
				String[] names = annotation.names();

				if ((names != null) && (names.length > 0)) {
					Class<?> type = field.getType();

					if ((withArguments && !type.equals(boolean.class)) ||
						(!withArguments && type.equals(boolean.class))) {

						for (String name : names) {
							flags.add(name);
						}
					}
				}
			}
		}

		return flags;
	}

	private Map<String, BaseCommand<? extends BaseArgs>> _getCommands(String profileName) throws Exception {
		if (_commands == null) {
			ClassLoader serviceLoaderClassLoader = _serviceLoaderClassLoader;

			_commands = BladeCLI.getCommandMapByClassLoader(profileName, serviceLoaderClassLoader);
		}

		return _commands;
	}

	private Map<String, BaseCommand<? extends BaseArgs>> _commands;
	private Path _embeddedTemplatesPath = null;
	private ClassLoader _serviceLoaderClassLoader = null;

}