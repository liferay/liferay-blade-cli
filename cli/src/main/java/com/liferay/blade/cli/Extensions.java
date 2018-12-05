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
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class Extensions implements AutoCloseable {

	public static Collection<String> getBladeProfiles(Class<?> commandClass) {
		return Stream.of(
			commandClass.getAnnotationsByType(BladeProfile.class)
		).filter(
			Objects::nonNull
		).map(
			BladeProfile::value
		).collect(
			Collectors.toList()
		);
	}

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

		Collection<Class<? extends BaseArgs>> classes = argStream.map(
			BaseArgs::getClass
		).collect(
			Collectors.toSet()
		);

		Collection<String> commandNames = getCommandNames(classes);

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
					if (argsList.size() >= (x + strArray.length)) {
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

	public Extensions(BladeSettings bladeSettings, Path extensionsPath) {
		_bladeSettings = bladeSettings;
		_extensionsPath = extensionsPath;
	}

	@Override
	public void close() throws Exception {
		if (_serviceLoaderClassLoader != null) {
			_getServiceClassLoader().close();
		}

		if ((_tempExtensionsDirectory != null) && Files.exists(_tempExtensionsDirectory)) {
			try {
				FileUtil.deleteDirIfExists(_tempExtensionsDirectory);
			}
			catch (IOException ioe) {
			}
		}
	}

	public Map<String, BaseCommand<? extends BaseArgs>> getCommands() throws Exception {
		String profileName = _bladeSettings.getProfileName();

		return _getCommands(profileName);
	}

	public Path getPath() throws IOException {
		return _extensionsPath;
	}

	private static URL _convertUriToUrl(URI uri) {
		try {
			return uri.toURL();
		}
		catch (MalformedURLException murle) {
		}

		return null;
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

	private static URL[] _getJarUrls(Path jarsPath) throws IOException {
		try (Stream<Path> files = Files.list(jarsPath)) {
			return files.filter(
				path -> String.valueOf(
					path
				).endsWith(
					".jar"
				)
			).map(
				Path::toUri
			).map(
				Extensions::_convertUriToUrl
			).filter(
				url -> url != null
			).collect(
				Collectors.toSet()
			).toArray(
				new URL[0]
			);
		}
	}

	private void _addCommand(
			Map<String, BaseCommand<?>> map, BaseCommand<?> baseCommand, Class<? extends BaseArgs> argsClass)
		throws IllegalAccessException, InstantiationException {

		BaseArgs baseArgs = argsClass.newInstance();

		baseCommand.setArgs(baseArgs);

		Parameters parameters = argsClass.getAnnotation(Parameters.class);

		if (parameters == null) {
			throw new IllegalArgumentException(
				"Loaded base command class that does not have a Parameters annotation " + argsClass.getName());
		}

		String[] commandNames = parameters.commandNames();

		map.putIfAbsent(commandNames[0], baseCommand);
	}

	private void _extractBladeExtensions(Path extensionsDirectory) throws IOException {
		try (InputStream inputStream = Extensions.class.getResourceAsStream("/blade-extensions-versions.properties")) {
			Properties properties = new Properties();

			properties.load(inputStream);

			Set<Object> keySet = properties.keySet();

			for (Object key : keySet) {
				String extension = key.toString() + "-" + properties.getProperty(key.toString()) + ".jar";

				try (InputStream extensionInputStream = Extensions.class.getResourceAsStream(extension)) {
					Path extensionPath = extensionsDirectory.resolve(extension);

					Files.copy(inputStream, extensionPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	private Map<String, BaseCommand<? extends BaseArgs>> _getCommands(String profileName) throws Exception {
		if (_commands == null) {
			_commands = new HashMap<>();

			ClassLoader serviceLoaderClassLoader = _getServiceClassLoader();

			@SuppressWarnings("rawtypes")
			ServiceLoader<BaseCommand> serviceLoader = ServiceLoader.load(BaseCommand.class, serviceLoaderClassLoader);

			Collection<BaseCommand<?>> allCommands = new ArrayList<>();

			for (BaseCommand<?> baseCommand : serviceLoader) {
				baseCommand.setClassLoader(serviceLoaderClassLoader);

				allCommands.add(baseCommand);
			}

			Map<String, BaseCommand<?>> map = new HashMap<>();

			Collection<BaseCommand<?>> commandsToRemove = new ArrayList<>();

			if ((profileName != null) && (profileName.length() > 0)) {
				for (BaseCommand<?> baseCommand : allCommands) {
					Collection<String> profileNames = getBladeProfiles(baseCommand.getClass());

					Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();

					if (profileNames.contains(profileName)) {
						_addCommand(map, baseCommand, argsClass);

						commandsToRemove.add(baseCommand);
					}
				}
			}
			else {
				for (BaseCommand<?> baseCommand : allCommands) {
					Collection<String> profileNames = getBladeProfiles(baseCommand.getClass());

					if ((profileNames != null) && !profileNames.isEmpty()) {
						commandsToRemove.add(baseCommand);
					}
				}
			}

			allCommands.removeAll(commandsToRemove);

			for (BaseCommand<?> baseCommand : allCommands) {
				Class<? extends BaseArgs> argsClass = baseCommand.getArgsClass();

				_addCommand(map, baseCommand, argsClass);
			}

			for (Entry<String, BaseCommand<?>> entry : map.entrySet()) {
				_commands.put(entry.getKey(), entry.getValue());
			}
		}

		return _commands;
	}

	private URLClassLoader _getServiceClassLoader() throws IOException {
		if (_serviceLoaderClassLoader == null) {
			_tempExtensionsDirectory = Files.createTempDirectory("extensions");

			FileUtil.copyDir(getPath(), _tempExtensionsDirectory);

			_extractBladeExtensions(_tempExtensionsDirectory);

			URL[] jarUrls = _getJarUrls(_tempExtensionsDirectory);

			_serviceLoaderClassLoader = new URLClassLoader(jarUrls, getClass().getClassLoader());
		}

		return _serviceLoaderClassLoader;
	}

	private final BladeSettings _bladeSettings;
	private Map<String, BaseCommand<? extends BaseArgs>> _commands;
	private final Path _extensionsPath;
	private URLClassLoader _serviceLoaderClassLoader = null;
	private Path _tempExtensionsDirectory;

}