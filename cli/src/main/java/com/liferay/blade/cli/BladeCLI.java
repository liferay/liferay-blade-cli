/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.WrappedParameter;

import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.CommandType;
import com.liferay.blade.cli.command.UpdateArgs;
import com.liferay.blade.cli.command.UpdateCommand;
import com.liferay.blade.cli.command.VersionCommand;
import com.liferay.blade.cli.command.validator.ParameterDepdendencyValidator;
import com.liferay.blade.cli.command.validator.ParameterPossibleValues;
import com.liferay.blade.cli.command.validator.ParameterValidator;
import com.liferay.blade.cli.command.validator.ValidatorFunctionPredicate;
import com.liferay.blade.cli.gradle.GradleExecutionException;
import com.liferay.blade.cli.util.CombinedClassLoader;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.Pair;
import com.liferay.blade.cli.util.ProcessesUtil;
import com.liferay.blade.cli.util.Prompter;
import com.liferay.blade.cli.util.ReleaseUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.lang3.StringUtils;

import org.fusesource.jansi.AnsiConsole;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeCLI {

	public static BladeCLI instance;

	public static Map<String, BaseCommand<? extends BaseArgs>> getCommandMapByClassLoader(
			String profileName, ClassLoader classLoader)
		throws IllegalAccessException, InstantiationException {

		Collection<BaseCommand<?>> allCommands = _getCommandsByClassLoader(classLoader);

		Map<String, BaseCommand<?>> commandMap = new TreeMap<>();

		Collection<BaseCommand<?>> commandsToRemove = new ArrayList<>();

		boolean profileNameIsPresent = false;

		if ((profileName != null) && (profileName.length() > 0)) {
			profileNameIsPresent = true;
		}

		for (BaseCommand<?> baseCommand : allCommands) {
			Collection<String> profileNames = _getBladeProfiles(baseCommand.getClass());

			if (profileNameIsPresent && profileNames.contains(profileName)) {
				_addCommand(commandMap, baseCommand);

				commandsToRemove.add(baseCommand);
			}
			else if ((profileNames != null) && !profileNames.isEmpty()) {
				commandsToRemove.add(baseCommand);
			}
		}

		allCommands.removeAll(commandsToRemove);

		for (BaseCommand<?> baseCommand : allCommands) {
			_addCommand(commandMap, baseCommand);
		}

		return commandMap;
	}

	public static void main(String[] args) {
		BladeCLI bladeCLI = new BladeCLI();

		try {
			bladeCLI.run(args);
		}
		catch (GradleExecutionException gradleExecutionException) {
			System.exit(gradleExecutionException.getReturnCode());
		}
		catch (Throwable th) {
			bladeCLI.error("Unexpected error occured.");

			th.printStackTrace(bladeCLI._error);

			System.exit(1);
		}
	}

	public BladeCLI() {
		this(System.out, System.err, System.in);
	}

	public BladeCLI(PrintStream out, PrintStream error, InputStream in) {
		AnsiConsole.systemInstall();

		_out = out;
		_error = error;
		_in = in;
		instance = this;
	}

	public void addErrors(String prefix, Collection<String> data) {
		PrintStream error = error();

		error.println("Error: " + prefix);

		data.forEach(error::println);
	}

	public PrintStream error() {
		return _error;
	}

	public void error(String message) {
		_error.println(message);
	}

	public void error(String string, String name, String message) {
		error(string + " [" + name + "]");
		error(message);
	}

	public void error(Throwable error) {
		error(error.getMessage());

		error.printStackTrace(error());
	}

	public BaseArgs getArgs() {
		return _args;
	}

	public BladeSettings getBladeSettings() throws IOException {
		File settingsBaseDir = _getSettingsBaseDir();

		File settingsFile = new File(settingsBaseDir, ".blade/settings.properties");

		if (settingsFile.exists() && Objects.equals(settingsFile.getName(), "settings.properties")) {
			_migrateBladeSettingsFile(settingsFile);
		}

		settingsFile = new File(settingsBaseDir, _BLADE_PROPERTIES);

		return new BladeSettings(settingsFile);
	}

	public BaseCommand<?> getCommand() {
		return _baseCommand;
	}

	public Extensions getExtensions() {
		if (_extensions == null) {
			ClassLoader classLoader = _getClassLoader();

			_extensions = new Extensions(classLoader);
		}

		return _extensions;
	}

	public Path getExtensionsPath() {
		Path userBladePath = getUserBladePath();

		Path extensions = userBladePath.resolve("extensions");

		try {
			if (Files.notExists(extensions)) {
				Files.createDirectories(extensions);
			}
			else if (!Files.isDirectory(extensions)) {
				throw new IOException(".blade/extensions is not a directory!");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return extensions;
	}

	public Path getUserBladePath() {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path userBladePath = userHomePath.resolve(".blade");

		try {
			if (Files.notExists(userBladePath)) {
				Files.createDirectories(userBladePath);
			}
			else if (!Files.isDirectory(userBladePath)) {
				throw new IOException(userBladePath + " is not a directory.");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return userBladePath;
	}

	public WorkspaceProvider getWorkspaceProvider(File dir) {
		try {
			Collection<WorkspaceProvider> providers = _getWorkspaceProviders();

			for (WorkspaceProvider provider : providers) {
				try {
					boolean workspace = provider.isWorkspace(dir);

					if (workspace) {
						return provider;
					}
				}
				catch (Throwable throwable) {
					throw new RuntimeException("_getWorkspaceProvider error", throwable);
				}
			}
		}
		catch (Throwable throwable) {
			throw new RuntimeException("_getWorkspaceProvider error", throwable);
		}

		return null;
	}

	public InputStream in() {
		return _in;
	}

	public boolean isWorkspace() {
		BaseArgs baseArgs = getArgs();

		if (getWorkspaceProvider(baseArgs.getBase()) != null) {
			return true;
		}

		return false;
	}

	public boolean isWorkspaceDir(File dir) {
		try {
			Collection<WorkspaceProvider> providers = _getWorkspaceProviders();

			for (WorkspaceProvider provider : providers) {
				try {
					boolean workspace = provider.isWorkspace(dir);

					if (workspace) {
						return true;
					}
				}
				catch (Throwable throwable) {
				}
			}
		}
		catch (Throwable throwable) {
		}

		return false;
	}

	public PrintStream out() {
		return _out;
	}

	public void out(String msg) {
		out().println(msg);
	}

	public void postRunCommand() {
		if (_shouldCheckForUpdates()) {
			try {
				_writeLastUpdateCheck();

				_printUpdateIfAvailable();
			}
			catch (IOException ioException) {
				error(ioException);
			}
		}
	}

	public void printUsage() {
		StringBuilder sb = new StringBuilder();

		CommandType ignoreCommandType = CommandType.WORKSPACE_ONLY;

		if (isWorkspace()) {
			ignoreCommandType = CommandType.NON_WORKSPACE;
		}

		JCommander jCommander = new JCommander();

		jCommander.setProgramName("blade");

		for (String command : _commands.keySet()) {
			BaseCommand<? extends BaseArgs> baseCommand = _commands.get(command);

			BaseArgs baseArgs = baseCommand.getArgs();

			CommandType commandType = baseArgs.getCommandType();

			if (commandType.equals(ignoreCommandType) || commandType.equals(CommandType.HIDDEN)) {
				continue;
			}

			jCommander.addCommand(baseArgs);
		}

		jCommander.usage(sb);

		try (Scanner scanner = new Scanner(sb.toString())) {
			StringBuilder simplifiedUsageString = new StringBuilder();

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.contains("Options:")) {
					while (scanner.hasNextLine()) {
						line = scanner.nextLine();

						if (line.equals("")) {
							break;
						}
					}
				}

				simplifiedUsageString.append(line + System.lineSeparator());
			}

			if (!isWorkspace()) {
				simplifiedUsageString.append("To see more command options, run \"blade init\" to create a workspace");
			}

			String output = simplifiedUsageString.toString();

			out(output);
		}
	}

	public void printUsage(String command) {
		_jCommander.usage(command);
	}

	public void printUsage(String command, String message) {
		out(message);

		_jCommander.usage(command);
	}

	public void run(String[] args) throws Exception {
		try {
			_removeOutDatedTempDir();

			Extensions extensions = getExtensions();

			String basePath = _extractBasePath(args);

			String profileName = _extractProfileName(args);

			File baseDir = new File(basePath);

			baseDir = baseDir.getAbsoluteFile();

			_args.setBase(baseDir);

			System.setOut(out());

			System.setErr(error());

			BladeSettings bladeSettings = getBladeSettings();

			if (profileName != null) {
				bladeSettings.setProfileName(profileName);
				bladeSettings.migrateWorkspaceIfNecessary(this, profileName);
			}
			else {
				bladeSettings.migrateWorkspaceIfNecessary(this);
			}

			_commands = extensions.getCommands(bladeSettings.getProfileName());

			args = Extensions.sortArgs(_commands, args);

			_jCommander = _buildJCommanderWithCommandMap(args, _commands);

			if ((args.length == 1) && args[0].equals("--help")) {
				printUsage();
			}
			else {
				try (CloseShieldInputStream closeShieldInputStream = new CloseShieldInputStream(in());
					BufferedReader reader = new BufferedReader(new InputStreamReader(closeShieldInputStream))) {

					ParameterException parameterException1 = null;

					try {
						_jCommander.parse(args);
					}
					catch (ParameterException parameterException2) {
						String parameterExceptionMessage = parameterException2.getMessage();

						if (parameterExceptionMessage.contains("Only one main parameter allowed")) {
							throw parameterException2;
						}

						parameterException1 = parameterException2;
					}

					String command = _jCommander.getParsedCommand();

					Map<String, JCommander> jCommands = _jCommander.getCommands();

					JCommander jCommander = jCommands.get(command);

					if (jCommander != null) {
						List<Object> objects = jCommander.getObjects();

						Object commandArgs = objects.get(0);

						BaseArgs baseArgs = (BaseArgs)commandArgs;

						if (baseArgs.isRefreshReleases()) {
							ReleaseUtil.refreshReleases();
						}

						_validateParameters(baseArgs);

						String parameterMessage = null;

						if (parameterException1 != null) {
							parameterMessage = parameterException1.getMessage();

							if (parameterMessage.contains("Main parameters are required") ||
								parameterMessage.contains(_MESSAGE_OPTIONS_ARE_REQUIRED) ||
								parameterMessage.contains(_MESSAGE_OPTION_IS_REQUIRED)) {

								System.out.println(
									"Error: The command " + command + " is missing required parameters.");
							}
							else {
								throw parameterException1;
							}
						}
						else {
							_validateParameterDependency((BaseArgs)commandArgs);

							_command = command;

							_args = (BaseArgs)commandArgs;

							_args.setProfileName(profileName);

							_args.setBase(baseDir);

							try {
								runCommand();

								postRunCommand();
							}
							catch (ParameterException parameterException2) {
								parameterException1 = parameterException2;
							}
						}

						while (parameterException1 != null) {
							parameterMessage = parameterException1.getMessage();

							List<String> fixedArgs = new ArrayList<>(Arrays.asList(args));

							if (parameterMessage.contains(_MESSAGE_OPTIONS_ARE_REQUIRED) ||
								parameterMessage.contains(_MESSAGE_OPTION_IS_REQUIRED)) {

								parameterMessage = parameterMessage.replace(_MESSAGE_OPTIONS_ARE_REQUIRED, "");
								parameterMessage = parameterMessage.replace(_MESSAGE_OPTION_IS_REQUIRED, "");

								Matcher matcher = _parameterDescriptionPattern.matcher(parameterMessage);

								String missingParameterDescription = null;

								if (matcher.matches()) {
									parameterMessage = matcher.group(1);
									missingParameterDescription = matcher.group(2);
								}

								String[] missingParameters = parameterMessage.split(", ");

								String value = null;

								for (String missingParameter : missingParameters) {
									missingParameter = _getMissingParameterUnformatted(missingParameter);

									value = _promptForMissingParameter(
										commandArgs, Optional.of(missingParameter),
										Optional.ofNullable(missingParameterDescription), reader, profileName);

									fixedArgs.add(1, missingParameter);

									fixedArgs.add(2, value);
								}

								args = fixedArgs.toArray(new String[0]);

								args = Extensions.sortArgs(_commands, args);
							}
							else if (parameterMessage.contains("Main parameters are required")) {
								String value = _promptForMissingParameter(
									commandArgs, Optional.empty(), Optional.empty(), reader, profileName);

								fixedArgs.add(value);

								args = fixedArgs.toArray(new String[0]);

								args = Extensions.sortArgs(_commands, args);
							}
							else {
								throw parameterException1;
							}

							try {
								parameterException1 = null;

								_jCommander = _buildJCommanderWithCommandMap(args, _commands);

								_jCommander.parse(args);
							}
							catch (ParameterException parameterException2) {
								parameterException1 = parameterException2;

								continue;
							}

							jCommands = _jCommander.getCommands();

							jCommander = jCommands.get(command);

							if (jCommander == null) {
								printUsage();

								break;
							}

							objects = jCommander.getObjects();

							commandArgs = objects.get(0);

							if (parameterException1 == null) {
								_command = command;

								_args = (BaseArgs)commandArgs;

								_args.setProfileName(profileName);

								_args.setBase(baseDir);

								try {
									_validateParameterDependency(_args);

									runCommand();

									postRunCommand();
								}
								catch (ParameterException parameterException2) {
									parameterException1 = parameterException2;
								}
							}
						}
					}
					else {
						printUsage();
					}
				}
				catch (MissingCommandException missingCommandException) {
					error("Error");

					StringBuilder stringBuilder = new StringBuilder("0. No such command");

					for (String arg : args) {
						stringBuilder.append(" " + arg);
					}

					error(stringBuilder.toString());

					printUsage();
				}
				catch (ParameterException parameterException) {
					error("Error");

					error(_jCommander.getParsedCommand() + ": " + parameterException.getMessage());
				}
			}
		}
		catch (GradleExecutionException gradleExecutionException) {
			throw gradleExecutionException;
		}
		catch (Throwable throwable) {
			error(throwable);
		}
		finally {
			if (_extensionsClassLoaderSupplier != null) {
				_extensionsClassLoaderSupplier.close();
			}

			if (_extensions != null) {
				_extensions.close();
			}
		}
	}

	public void runCommand() throws Exception {
		if (_args.isHelp()) {
			if (Objects.isNull(_command) || (_command.length() == 0)) {
				printUsage();
			}
			else {
				printUsage(_command);
			}
		}
		else {
			if (_args != null) {
				_runCommand();
			}
			else {
				_jCommander.usage();
			}
		}
	}

	public void trace(String s, Object... args) {
		if (_args.isTrace() && (_tracer != null)) {
			_tracer.format("# " + s + "%n", args);
			_tracer.flush();
		}
	}

	private static void _addCommand(Map<String, BaseCommand<?>> map, BaseCommand<?> baseCommand)
		throws IllegalAccessException, InstantiationException {

		Arrays.stream(
			_getCommandNames(baseCommand)
		).forEach(
			commandName -> map.putIfAbsent(commandName, baseCommand)
		);
	}

	private static Collection<String> _getBladeProfiles(Class<?> commandClass) {
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

	private static String[] _getCommandNames(BaseCommand<?> baseCommand)
		throws IllegalAccessException, InstantiationException {

		Class<? extends BaseArgs> baseArgsClass = baseCommand.getArgsClass();

		BaseArgs baseArgs = baseArgsClass.newInstance();

		baseCommand.setArgs(baseArgs);

		Parameters parameters = baseArgsClass.getAnnotation(Parameters.class);

		if (parameters == null) {
			throw new IllegalArgumentException(
				"Loaded base command class that does not have a Parameters annotation " + baseArgsClass.getName());
		}

		return parameters.commandNames();
	}

	@SuppressWarnings("rawtypes")
	private static Collection<BaseCommand<?>> _getCommandsByClassLoader(ClassLoader classLoader) {
		Collection<BaseCommand<?>> allCommands = new ArrayList<>();

		ServiceLoader<BaseCommand> serviceLoader = ServiceLoader.load(BaseCommand.class, classLoader);

		Iterator<BaseCommand> baseCommandIterator = serviceLoader.iterator();

		while (baseCommandIterator.hasNext()) {
			try {
				BaseCommand<?> baseCommand = baseCommandIterator.next();

				baseCommand.setClassLoader(classLoader);

				allCommands.add(baseCommand);
			}
			catch (Throwable throwable) {
				Class<?> throwableClass = throwable.getClass();

				System.err.println(
					"Exception thrown while loading extension." + System.lineSeparator() + "Exception: " +
						throwableClass.getName() + ": " + throwable.getMessage() + System.lineSeparator());

				Throwable cause = throwable.getCause();

				if (cause != null) {
					Class<?> throwableCauseClass = cause.getClass();

					System.err.print(
						throwableCauseClass.getName() + ": " + cause.getMessage() + System.lineSeparator());
				}
			}
		}

		return allCommands;
	}

	private JCommander _buildJCommanderWithCommandMap(
		String[] args, Map<String, BaseCommand<? extends BaseArgs>> commandMap) {

		JCommander.Builder builder = JCommander.newBuilder();

		builder.programName("blade");

		for (Map.Entry<String, BaseCommand<? extends BaseArgs>> entry : commandMap.entrySet()) {
			BaseCommand<? extends BaseArgs> value = entry.getValue();

			try {
				builder.addCommand(entry.getKey(), value.getArgs());
			}
			catch (ParameterException parameterException) {
				System.err.println(parameterException.getMessage());
			}
		}

		builder.defaultProvider(new BladeCLIDefaultProvider(args));

		JCommander build = builder.build();

		build.setParameterDescriptionComparator(
			new Comparator<ParameterDescription>() {

				@Override
				public int compare(
					ParameterDescription parameterDescription0, ParameterDescription parameterDescription1) {

					Parameter parameterAnnotation0 = parameterDescription0.getParameterAnnotation();
					Parameter parameterAnnotation1 = parameterDescription1.getParameterAnnotation();

					WrappedParameter wrappedParameter0 = parameterDescription0.getParameter();
					WrappedParameter wrappedParameter1 = parameterDescription1.getParameter();

					String[] names0 = wrappedParameter0.names();
					String[] names1 = wrappedParameter1.names();

					String name0 = Arrays.stream(
						names0
					).filter(
						name -> name.startsWith("-") && !name.startsWith("--")
					).findFirst(
					).orElse(
						(names0.length > 0) ? names0[0] : ""
					).toLowerCase(
					).replace(
						"-", ""
					);

					String name1 = Arrays.stream(
						names1
					).filter(
						name -> name.startsWith("-") && !name.startsWith("--")
					).findFirst(
					).orElse(
						(names1.length > 0) ? names1[0] : ""
					).toLowerCase(
					).replace(
						"-", ""
					);

					if ((parameterAnnotation0 != null) && (parameterAnnotation0.order() != -1) &&
						(parameterAnnotation1 != null) && (parameterAnnotation1.order() != -1)) {

						return Integer.compare(parameterAnnotation0.order(), parameterAnnotation1.order());
					}
					else if ((parameterAnnotation0 != null) && (parameterAnnotation0.order() != -1)) {
						return -1;
					}
					else if ((parameterAnnotation1 != null) && (parameterAnnotation1.order() != -1)) {
						return 1;
					}
					else if (!name0.isEmpty() || !name1.isEmpty()) {
						return name0.compareTo(name1);
					}

					String longestName0 = parameterDescription0.getLongestName();
					String longestName1 = parameterDescription1.getLongestName();

					return longestName0.compareTo(longestName1);
				}

			});

		return build;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> _buildMavenPossibleValuesMap(
		Class<? extends Supplier<List<String>>> supplierValidator) {

		try {
			Constructor<? extends Supplier<List<String>>> declaredConstructor =
				supplierValidator.getDeclaredConstructor();

			Supplier<List<String>> instance = declaredConstructor.newInstance();

			List<String> options = instance.get();

			Iterator<String> it = options.iterator();

			Map<String, String> optionsMap = new LinkedHashMap<>();

			for (int x = 1; it.hasNext(); x++) {
				String option = it.next();

				ReleaseUtil.ReleaseEntry releaseEntry = ReleaseUtil.getReleaseEntry(option);

				optionsMap.put(String.valueOf(x), releaseEntry.getTargetPlatformVersion());
			}

			return optionsMap;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Map<String, String> _buildPossibleValuesMap(Class<? extends Supplier<List<String>>> supplierValidator) {
		try {
			Supplier<List<String>> instance = supplierValidator.newInstance();

			List<String> options = instance.get();

			Iterator<String> it = options.iterator();

			Map<String, String> optionsMap = new LinkedHashMap<>();

			for (int x = 1; it.hasNext(); x++) {
				String option = it.next();

				optionsMap.put(String.valueOf(x), option);
			}

			return optionsMap;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _extractBasePath(String[] args) {
		String defaultBasePath = ".";

		if (args.length > 2) {
			for (int x = 0; x < args.length; x++) {
				String arg = args[x];

				if (arg.equals("--base")) {
					if (((x + 1) == args.length) || args[x + 1].startsWith("-")) {
						error("Error: The parameter for base path is mising.");

						System.exit(0);
					}

					defaultBasePath = args[x + 1];
				}
			}
		}

		return defaultBasePath;
	}

	private String _extractProfileName(String[] args) {
		List<String> argsList = new ArrayList<>();
		List<String> originalArgsList = Arrays.asList(args);

		argsList.addAll(originalArgsList);

		for (int x = 0; x < argsList.size(); x++) {
			String arg = argsList.get(x);

			if (Objects.equals(arg, "--base")) {
				argsList.remove(x);
				argsList.remove(x);

				break;
			}
		}

		try {
			return _getCommandProfile(argsList.toArray(new String[0]));
		}
		catch (MissingCommandException missingCommandException) {
			error(missingCommandException.getMessage());
			System.exit(0);
		}

		return null;
	}

	private ClassLoader _getClassLoader() {
		if (_extensionsClassLoaderSupplier == null) {
			_extensionsClassLoaderSupplier = new ExtensionsClassLoaderSupplier(getExtensionsPath());
		}

		return _extensionsClassLoaderSupplier.get();
	}

	private String _getCommandProfile(String[] args) throws MissingCommandException {
		final Collection<String> profileFlags = new HashSet<>();

		try {
			Field field = BaseArgs.class.getDeclaredField("_profileName");

			Parameter parameters = field.getAnnotation(Parameter.class);

			Collections.addAll(profileFlags, parameters.names());
		}
		catch (Exception exception) {
		}

		String profile = null;

		Collection<String> argsCollection = new ArrayList<>();

		for (String arg : args) {
			String[] argSplit = arg.split(" ");

			for (String argEach : argSplit) {
				argsCollection.add(argEach);
			}
		}

		String[] argsArray = argsCollection.toArray(new String[0]);

		for (int x = 0; x < argsArray.length; x++) {
			String arg = argsArray[x];

			if (profileFlags.contains(arg)) {
				if (((x + 1) == argsArray.length) || argsArray[x + 1].startsWith("-")) {
					throw new MissingCommandException("Error: The parameter for profile name is missing");
				}

				profile = argsArray[x + 1];

				break;
			}
		}

		return profile;
	}

	private String _getMessageFromPossibleValues(Map<String, String> optionsMap) {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : optionsMap.entrySet()) {
			sb.append(System.lineSeparator());

			sb.append(entry.getKey() + ": " + entry.getValue());
		}

		return sb.toString();
	}

	private String _getMissingParameterUnformatted(String missingParameter) {
		if (missingParameter.contains(" | ")) {
			missingParameter = missingParameter.split(" | ")[0];
		}

		if (missingParameter.startsWith("[")) {
			missingParameter = missingParameter.substring(1);
		}

		if (missingParameter.endsWith("]")) {
			missingParameter = missingParameter.substring(0, missingParameter.length() - 1);
		}

		return missingParameter;
	}

	private String _getParameterNames(List<String> parameterNamesList) {
		StringBuilder missingOptionSb = new StringBuilder();

		for (int x = 0; x < parameterNamesList.size(); x++) {
			String missingParameterArgument = parameterNamesList.get(x);

			if (x == 0) {
				missingOptionSb.append("[");
			}

			missingOptionSb.append(missingParameterArgument);

			if ((x + 1) <= (parameterNamesList.size() - 1)) {
				missingOptionSb.append(" | ");
			}
			else {
				missingOptionSb.append("]");
			}
		}

		return missingOptionSb.toString();
	}

	private Map<String, String> _getPossibleDefaultValuesMap(Field field, StringBuilder sb, String profileName) {
		Map<String, String> possibleValuesMap = null;

		ParameterPossibleValues possibleValuesAnnotation = field.getDeclaredAnnotation(ParameterPossibleValues.class);

		if (possibleValuesAnnotation != null) {
			Class<? extends Supplier<List<String>>> possibleValuesSupplier = possibleValuesAnnotation.value();

			if (possibleValuesSupplier != null) {
				if (Objects.equals(profileName, "maven")) {
					possibleValuesMap = _buildMavenPossibleValuesMap(possibleValuesSupplier);

					sb.append(_getMessageFromPossibleValues(possibleValuesMap));

					return possibleValuesMap;
				}

				possibleValuesMap = _buildPossibleValuesMap(possibleValuesSupplier);

				sb.append(_getMessageFromPossibleValues(possibleValuesMap));
			}
		}

		return possibleValuesMap;
	}

	private Map<String, String> _getPossibleMoreValuesMap(Field field, StringBuilder sb, String profileName) {
		Map<String, String> possibleValuesMap = null;

		ParameterPossibleValues possibleValuesAnnotation = field.getDeclaredAnnotation(ParameterPossibleValues.class);

		if (possibleValuesAnnotation != null) {
			Class<? extends Supplier<List<String>>> possibleValuesSupplier = possibleValuesAnnotation.more();

			if (possibleValuesSupplier != null) {
				if (Objects.equals(profileName, "maven")) {
					possibleValuesMap = _buildMavenPossibleValuesMap(possibleValuesSupplier);

					sb.append(_getMessageFromPossibleValues(possibleValuesMap));

					return possibleValuesMap;
				}

				possibleValuesMap = _buildPossibleValuesMap(possibleValuesSupplier);

				sb.append(_getMessageFromPossibleValues(possibleValuesMap));
			}
		}

		return possibleValuesMap;
	}

	private File _getSettingsBaseDir() {
		File baseDir = _args.getBase();

		File settingsBaseDir;

		WorkspaceProvider workspaceProvider = getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			settingsBaseDir = workspaceProvider.getWorkspaceDir(baseDir);
		}
		else {
			settingsBaseDir = _USER_HOME_DIR;
		}

		return settingsBaseDir;
	}

	private Path _getUpdateCheckPath() throws IOException {
		Path userBladePath = getUserBladePath();

		return userBladePath.resolve("updateCheck.properties");
	}

	private Optional<String> _getUpdateVersionIfAvailable(boolean snapshots) {
		UpdateArgs updateArgs = new UpdateArgs();

		updateArgs.setCheckOnly(true);

		UpdateCommand updateCommand = new UpdateCommand();

		updateCommand.setArgs(updateArgs);

		updateCommand.setBlade(this);

		StringPrintStream stdOut = StringPrintStream.newInstance();

		PrintStream currentStdOut = System.out;

		try {
			System.setOut(stdOut);

			_out = System.out;

			updateCommand.execute();
		}
		finally {
			System.setOut(currentStdOut);

			_out = System.out;
		}

		if (snapshots) {
			Optional<String> snapshotUpdateVersionOpt = updateCommand.getSnapshotUpdateVersion();

			if (!snapshotUpdateVersionOpt.isPresent()) {
				return Optional.empty();
			}

			String snapshotUpdateVersion = snapshotUpdateVersionOpt.get();

			snapshotUpdateVersion = snapshotUpdateVersion.substring(0, 14) + snapshotUpdateVersion.substring(15, 19);

			snapshotUpdateVersion = snapshotUpdateVersion.replace('-', '.');

			return Optional.of(snapshotUpdateVersion.trim());
		}

		return updateCommand.getReleaseUpdateVersion();
	}

	private Collection<WorkspaceProvider> _getWorkspaceProviders() throws Exception {
		if (_workspaceProviders == null) {
			_workspaceProviders = new ArrayList<>();

			ServiceLoader<WorkspaceProvider> serviceLoader = ServiceLoader.load(
				WorkspaceProvider.class, _getClassLoader());

			Iterator<WorkspaceProvider> workspaceProviderIterator = serviceLoader.iterator();

			while (workspaceProviderIterator.hasNext()) {
				try {
					WorkspaceProvider workspaceProvider = workspaceProviderIterator.next();

					_workspaceProviders.add(workspaceProvider);
				}
				catch (Throwable throwable) {
					Class<?> throwableClass = throwable.getClass();

					System.err.println(
						"Exception thrown while loading WorkspaceProvider." + System.lineSeparator() + "Exception: " +
							throwableClass.getName() + ": " + throwable.getMessage());

					Throwable cause = throwable.getCause();

					if (cause != null) {
						Class<?> throwableCauseClass = cause.getClass();

						System.err.print(throwableCauseClass.getName() + ": " + cause.getMessage());
					}
				}
			}

			return _workspaceProviders;
		}

		return _workspaceProviders;
	}

	private void _migrateBladeSettingsFile(File settingsFile) throws IOException {
		Path settingsPath = settingsFile.toPath();

		Path settingsParentPath = settingsPath.getParent();

		if (settingsParentPath.endsWith(".blade")) {
			Path settingsParentParentPath = settingsParentPath.getParent();

			Path newSettingsPath = settingsParentParentPath.resolve(_BLADE_PROPERTIES);

			Files.move(settingsPath, newSettingsPath);

			try (Stream<?> filesStream = Files.list(settingsParentPath)) {
				if (filesStream.count() == 0) {
					Files.delete(settingsParentPath);
				}
			}
		}
	}

	private void _printUpdateIfAvailable() throws IOException {
		Optional<String> releaseUpdateVersion = _getUpdateVersionIfAvailable(false);

		String currentVersion = VersionCommand.getBladeCLIVersion();

		boolean currentVersionIsSnapshot = currentVersion.contains("SNAPSHOT");

		currentVersion = currentVersion.replace("SNAPSHOT", "");

		if (!currentVersionIsSnapshot) {
			currentVersion = currentVersion.substring(0, 5);
		}

		if (currentVersionIsSnapshot) {
			Optional<String> snapshotUpdateVersion = _getUpdateVersionIfAvailable(true);

			if (releaseUpdateVersion.isPresent() && snapshotUpdateVersion.isPresent()) {
				out("Updates available to the installed version: " + currentVersion);
				out("-> (Snapshot) " + snapshotUpdateVersion + "\t Run `blade update` to install");
				out("-> (Release) " + releaseUpdateVersion + "\t\t\t Run `blade update -r` to install");
			}
			else if (snapshotUpdateVersion.isPresent()) {
				out("Update available " + currentVersion + " -> " + snapshotUpdateVersion.get());
				out("Run `blade update` to install");
			}
			else if (releaseUpdateVersion.isPresent()) {
				out("Update available " + currentVersion + " -> " + releaseUpdateVersion.get());
				out("Run `blade update -r` to install");
			}
		}
		else if (releaseUpdateVersion.isPresent()) {
			out("Update available " + currentVersion + " -> " + releaseUpdateVersion.get());
			out("Run `blade update` to install");
		}
	}

	private String _promptForMissingParameter(
		Object commandArgs, Optional<String> missingParameterOptional, Optional<String> missingParameterDescription,
		BufferedReader reader, String profileName) {

		String value = null;

		Class<?> commandArgsClass = commandArgs.getClass();

		for (Field field : commandArgsClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Parameter.class)) {
				Parameter parameterAnnotation = field.getDeclaredAnnotation(Parameter.class);

				String description = parameterAnnotation.description();

				String[] parameterAnnotationNames = parameterAnnotation.names();

				List<String> parameterNamesList = Arrays.asList(parameterAnnotationNames);

				StringBuilder sb = null;

				String missingParametersFormatted = null;

				boolean found = false;

				if (missingParameterOptional.isPresent() &&
					parameterNamesList.contains(missingParameterOptional.get())) {

					sb = new StringBuilder("The following option is required: ");

					missingParametersFormatted = _getParameterNames(parameterNamesList);

					sb.append(missingParametersFormatted);

					if (missingParameterDescription.isPresent()) {
						sb.append(" " + missingParameterDescription.get());
					}
					else if (!description.isEmpty()) {
						sb.append(" " + description);
					}

					found = true;
				}
				else if (!missingParameterOptional.isPresent() &&
						 ((parameterAnnotationNames == null) || (parameterAnnotationNames.length == 0))) {

					sb = new StringBuilder("The main parameter is required: ");

					if (parameterAnnotation.description() != null) {
						sb.append(" (" + parameterAnnotation.description() + ")");
					}

					missingParametersFormatted = "the main parameter";

					found = true;
				}

				if (found) {
					Map<String, String> optionsMap = _getPossibleDefaultValuesMap(field, sb, profileName);

					String message = sb.toString();

					value = _promptForValueWithOptions(
						field, sb, missingParametersFormatted, optionsMap, message, reader, out(), profileName);

					break;
				}
			}
		}

		return value;
	}

	private String _promptForValueWithOptions(
		Field field, StringBuilder sb, String missingParametersFormatted, Map<String, String> optionsMap,
		String message, BufferedReader reader, PrintStream printStream, String profileName) {

		String value = Prompter.promptString(message, reader, printStream);

		if ((optionsMap != null) && !optionsMap.isEmpty()) {
			while (!optionsMap.containsKey(value) && !optionsMap.containsValue(value) &&
				   !Objects.equals(value, "more")) {

				System.out.println("Please enter a valid value for " + missingParametersFormatted);

				value = Prompter.promptString("", reader, printStream);
			}

			if (optionsMap.containsKey(value)) {
				value = optionsMap.get(value);
			}
			else if (Objects.equals(value, "more")) {
				StringBuilder more = new StringBuilder();

				Map<String, String> moreOptionsMap = _getPossibleMoreValuesMap(field, more, profileName);

				value = _promptForValueWithOptions(
					field, sb, missingParametersFormatted, moreOptionsMap, more.toString(), reader, out(), profileName);
			}
		}

		return value;
	}

	private void _removeOutDatedTempDir() {
		List<Long> processIdList = ProcessesUtil.getAllProcessIds();

		String tmpdir = System.getProperty("java.io.tmpdir");

		File tmpFile = new File(tmpdir);

		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					Stream.of(
						tmpFile.listFiles()
					).filter(
						file -> {
							String fileName = file.getName();

							if (fileName.startsWith(Extensions.TEMP_EXTENSIONS_PREFIX) ||
								fileName.startsWith(Extensions.TEMP_TEMPLATES_PREFIX)) {

								String[] segments = fileName.split("-");

								String pid = segments[2];

								return !processIdList.contains(Long.parseLong(pid));
							}

							return false;
						}
					).forEach(
						file -> {
							try {
								FileUtil.deleteDirIfExists(file.toPath());
							}
							catch (Exception exception) {
							}
						}
					);
				}
				catch (Exception exception) {
				}
			}

		};

		thread.start();
	}

	private void _runCommand() throws Exception {
		BaseCommand<?> command = null;

		if (_commands.containsKey(_command)) {
			command = _commands.get(_command);
		}

		if (command != null) {
			_baseCommand = command;

			command.setArgs(_args);
			command.setBlade(this);

			Thread thread = Thread.currentThread();

			ClassLoader currentClassLoader = thread.getContextClassLoader();

			ClassLoader combinedClassLoader = new CombinedClassLoader(currentClassLoader, command.getClassLoader());

			try {
				thread.setContextClassLoader(combinedClassLoader);

				if (_args.getProfileName() == null) {
					_args.setProfileName("gradle");
				}

				command.execute();
			}
			catch (ParameterException parameterException) {
				throw parameterException;
			}
			catch (Throwable throwable) {
				throw throwable;
			}
			finally {
				if (command instanceof AutoCloseable) {
					AutoCloseable autoCloseable = (AutoCloseable)command;

					autoCloseable.close();
				}

				thread.setContextClassLoader(currentClassLoader);
			}
		}
		else {
			printUsage();
		}
	}

	private boolean _shouldCheckForUpdates() {
		try {
			if (_command.contains("update")) {
				return false;
			}

			BaseArgs baseArgs = getArgs();

			if (baseArgs.isQuiet()) {
				return false;
			}

			Path updateCheckPath = _getUpdateCheckPath();

			if (!Files.exists(updateCheckPath)) {
				return true;
			}

			Properties properties = new Properties();

			try (InputStream inputStream = Files.newInputStream(updateCheckPath)) {
				properties.load(inputStream);
			}

			Instant lastUpdateCheck = Instant.ofEpochMilli(
				Long.parseLong(properties.getProperty(_LAST_UPDATE_CHECK_KEY)));

			Instant now = Instant.now();

			Instant yesterday = now.minus(1, ChronoUnit.DAYS);

			if (yesterday.isAfter(lastUpdateCheck)) {
				return true;
			}
		}
		catch (Exception exception) {
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private <T extends BaseArgs> void _validateParameterDependency(T args) throws IllegalArgumentException {
		try {
			Class<? extends BaseArgs> argsClass = args.getClass();

			Field[] classFields = argsClass.getDeclaredFields();

			List<Pair<ParameterDepdendencyValidator, Field>> validatorPairs = new ArrayList<>();

			for (Field field : classFields) {
				ParameterDepdendencyValidator validator = field.getAnnotation(ParameterDepdendencyValidator.class);

				if (Objects.isNull(validator)) {
					continue;
				}

				validatorPairs.add(new Pair<>(validator, field));
			}

			Collections.sort(
				validatorPairs,
				new Comparator<Pair<ParameterDepdendencyValidator, Field>>() {

					@Override
					public int compare(
						Pair<ParameterDepdendencyValidator, Field> pair1,
						Pair<ParameterDepdendencyValidator, Field> pair2) {

						ParameterDepdendencyValidator firstValidator = pair1.first();

						ParameterDepdendencyValidator secondeValidator = pair2.first();

						return firstValidator.order() - secondeValidator.order();
					}

				});

			for (Pair<ParameterDepdendencyValidator, Field> validatorPair : validatorPairs) {
				ParameterDepdendencyValidator validator = validatorPair.first();

				Class<? extends Predicate<?>> predicateClass = validator.value();

				if (predicateClass != null) {
					ValidatorFunctionPredicate<T> validatorFunction =
						(ValidatorFunctionPredicate<T>)predicateClass.newInstance();

					if (!validatorFunction.test(args)) {
						Field field = validatorPair.second();

						List<String> possibleValues = validatorFunction.apply(args);

						String possibleValueString = StringUtils.join(possibleValues, "|");

						Parameter parameterAnnotation = field.getAnnotation(Parameter.class);

						if (!possibleValues.isEmpty()) {
							throw new IllegalArgumentException(
								"Parameter validataion failed for " + parameterAnnotation.names()[0] +
									", possible value are " + possibleValueString);
						}

						throw new IllegalArgumentException(
							"Parameter validataion failed for " + parameterAnnotation.names()[0]);
					}
				}
			}
		}
		catch (Exception exception) {
			Class<?> argsClass = args.getClass();

			throw new IllegalArgumentException(
				"Parameter's depdendency Validation failed for " + argsClass.getSimpleName(), exception);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends BaseArgs> void _validateParameters(T args) throws IllegalArgumentException {
		try {
			Class<? extends BaseArgs> argsClass = args.getClass();

			ParameterValidator[] validateParameters = argsClass.getAnnotationsByType(ParameterValidator.class);

			for (ParameterValidator parameterValidator : validateParameters) {
				Class<? extends Predicate<?>> predicateClass = parameterValidator.value();

				if (predicateClass != null) {
					Predicate<T> predicate = (Predicate<T>)predicateClass.newInstance();

					if (!predicate.test(args)) {
						throw new IllegalArgumentException();
					}
				}
			}
		}
		catch (Exception exception) {
			Class<?> argsClass = args.getClass();

			throw new IllegalArgumentException("Validation failed for " + argsClass.getSimpleName(), exception);
		}
	}

	private void _writeLastUpdateCheck() throws IOException {
		Path updateCheckPath = _getUpdateCheckPath();

		Properties properties = new Properties();

		Instant now = Instant.now();

		properties.put(_LAST_UPDATE_CHECK_KEY, String.valueOf(now.toEpochMilli()));

		try (OutputStream outputStream = Files.newOutputStream(updateCheckPath)) {
			properties.store(outputStream, null);
		}
	}

	private static final String _BLADE_PROPERTIES = ".blade.properties";

	private static final String _LAST_UPDATE_CHECK_KEY = "lastUpdateCheck";

	private static final String _MESSAGE_OPTION_IS_REQUIRED = "The following option is required: ";

	private static final String _MESSAGE_OPTIONS_ARE_REQUIRED = "The following options are required: ";

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private static final Pattern _parameterDescriptionPattern = Pattern.compile("(.*]) (.*)");
	private static final Formatter _tracer = new Formatter(System.out);

	private BaseArgs _args = new BaseArgs();
	private BaseCommand<?> _baseCommand;
	private String _command;
	private Map<String, BaseCommand<? extends BaseArgs>> _commands;
	private final PrintStream _error;
	private Extensions _extensions;
	private ExtensionsClassLoaderSupplier _extensionsClassLoaderSupplier;
	private final InputStream _in;
	private JCommander _jCommander;
	private PrintStream _out;
	private Collection<WorkspaceProvider> _workspaceProviders = null;

}