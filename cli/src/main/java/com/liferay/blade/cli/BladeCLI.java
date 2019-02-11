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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.UpdateCommand;
import com.liferay.blade.cli.command.VersionCommand;
import com.liferay.blade.cli.util.CombinedClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.fusesource.jansi.AnsiConsole;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeCLI {

	public static Map<String, BaseCommand<? extends BaseArgs>> getCommandMapByClassLoader(
			String profileName, ClassLoader classLoader)
		throws IllegalAccessException, InstantiationException {

		Collection<BaseCommand<?>> allCommands = _getCommandsByClassLoader(classLoader);

		Map<String, BaseCommand<?>> commandMap = new HashMap<>();

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
		catch (Exception e) {
			bladeCLI.error("Unexpected error occured.");

			e.printStackTrace(bladeCLI._error);
		}
	}

	public BladeCLI() {
		this(System.out, System.err, System.in);
	}

	public BladeCLI(PrintStream out, PrintStream err, InputStream in) {
		AnsiConsole.systemInstall();

		_out = out;
		_error = err;
		_in = in;
	}

	public void addErrors(String prefix, Collection<String> data) {
		error().println("Error: " + prefix);

		data.forEach(error()::println);
	}

	public PrintStream error() {
		return _error;
	}

	public void error(String msg) {
		_error.println(msg);
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
		File settingsFile = null;

		BaseArgs baseArgs = getArgs();

		File baseDir = new File(baseArgs.getBase());

		WorkspaceProvider workspaceProvider = getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			File workspaceDir = workspaceProvider.getWorkspaceDir(baseDir);

			settingsFile = new File(workspaceDir, ".blade/settings.properties");
		}
		else {
			settingsFile = new File(_USER_HOME_DIR, ".blade/settings.properties");
		}

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
		Path userBladePath = _getUserBladePath();

		Path extensions = userBladePath.resolve("extensions");

		try {
			if (Files.notExists(extensions)) {
				Files.createDirectories(extensions);
			}
			else if (!Files.isDirectory(extensions)) {
				throw new IOException(".blade/extensions is not a directory!");
			}
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		return extensions;
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
				catch (Throwable th) {
					throw new RuntimeException("_getWorkspaceProvider error", th);
				}
			}
		}
		catch (Throwable th) {
			throw new RuntimeException("_getWorkspaceProvider error", th);
		}

		return null;
	}

	public InputStream in() {
		return _in;
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

				printUpdateIfAvailable();
			}
			catch (IOException ioe) {
				error(ioe);
			}
		}
	}

	public boolean printUpdateIfAvailable() throws IOException {
		boolean available;

		String bladeCLIVersion = VersionCommand.getBladeCLIVersion();

		boolean fromSnapshots = false;

		if (bladeCLIVersion == null) {
			throw new IOException("Could not determine blade version");
		}

		fromSnapshots = bladeCLIVersion.contains("SNAPSHOT");

		String updateVersion = "";

		try {
			updateVersion = UpdateCommand.getUpdateVersion(fromSnapshots);

			available = UpdateCommand.shouldUpdate(bladeCLIVersion, updateVersion);

			if (available) {
				out(System.lineSeparator() + "blade version " + bladeCLIVersion + System.lineSeparator());
				out(
					"Run \'blade update" + (fromSnapshots ? " --snapshots" : "") + "\' to update to " +
						(fromSnapshots ? "the latest snapshot " : " ") + "version " + updateVersion +
							System.lineSeparator());
			}
			else {
				if (fromSnapshots) {
					if (!UpdateCommand.equal(bladeCLIVersion, updateVersion)) {
						out(
							String.format(
								"blade version %s is newer than latest snapshot %s; skipping update.\n",
								bladeCLIVersion, updateVersion));
					}
				}
			}
		}
		catch (IOException ioe) {
			available = false;
		}

		return available;
	}

	public void printUsage() {
		StringBuilder usageString = new StringBuilder();

		_jCommander.usage(usageString);

		try (Scanner scanner = new Scanner(usageString.toString())) {
			StringBuilder simplifiedUsageString = new StringBuilder();

			while (scanner.hasNextLine()) {
				String oneLine = scanner.nextLine();

				if (!oneLine.startsWith("          ") && !oneLine.contains("Options:")) {
					simplifiedUsageString.append(oneLine + System.lineSeparator());
				}
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
			Extensions extensions = getExtensions();

			String basePath = _extractBasePath(args);

			String profileName = _extractProfileName(args);

			File baseDir = new File(basePath).getAbsoluteFile();

			_args.setBase(baseDir);

			System.setOut(out());

			System.setErr(error());

			BladeSettings bladeSettings = getBladeSettings();

			if (profileName != null) {
				bladeSettings.setProfileName(profileName);
			}

			bladeSettings.migrateWorkspaceIfNecessary(this);

			_commands = extensions.getCommands(bladeSettings.getProfileName());

			args = Extensions.sortArgs(_commands, args);

			_jCommander = _buildJCommanderWithCommandMap(_commands);

			if ((args.length == 1) && args[0].equals("--help")) {
				printUsage();
			}
			else {
				try {
					_jCommander.parse(args);

					String command = _jCommander.getParsedCommand();

					Map<String, JCommander> jCommands = _jCommander.getCommands();

					JCommander jCommander = jCommands.get(command);

					if (jCommander != null) {
						List<Object> objects = jCommander.getObjects();

						Object commandArgs = objects.get(0);

						_command = command;

						_args = (BaseArgs)commandArgs;

						_args.setProfileName(profileName);

						_args.setBase(baseDir);

						runCommand();

						postRunCommand();
					}
					else {
						printUsage();
					}
				}
				catch (MissingCommandException mce) {
					error("Error");

					StringBuilder stringBuilder = new StringBuilder("0. No such command");

					for (String arg : args) {
						stringBuilder.append(" " + arg);
					}

					error(stringBuilder.toString());

					printUsage();
				}
				catch (ParameterException pe) {
					error(_jCommander.getParsedCommand() + ": " + pe.getMessage());
				}
			}
		}
		finally {
			if (_extensionsClassLoaderSupplier != null) {
				_extensionsClassLoaderSupplier.close();
			}
		}
	}

	public void runCommand() {
		try {
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
		catch (ParameterException pe) {
			throw pe;
		}
		catch (Exception e) {
			Class<?> exceptionClass = e.getClass();

			String exceptionClassName = exceptionClass.getName();

			error("error: " + exceptionClassName + " :: " + e.getMessage() + System.lineSeparator());

			if (_args.isTrace()) {
				e.printStackTrace(error());
			}
			else {
				error("\tat " + e.getStackTrace()[0] + System.lineSeparator());
				error("For more information run `blade " + _command + " --trace");
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

		String[] commandNames = _getCommandNames(baseCommand);

		map.putIfAbsent(commandNames[0], baseCommand);
	}

	private static JCommander _buildJCommanderWithCommandMap(Map<String, BaseCommand<? extends BaseArgs>> commandMap) {
		Builder builder = JCommander.newBuilder();

		for (Entry<String, BaseCommand<? extends BaseArgs>> entry : commandMap.entrySet()) {
			BaseCommand<? extends BaseArgs> value = entry.getValue();

			try {
				builder.addCommand(entry.getKey(), value.getArgs());
			}
			catch (ParameterException pe) {
				System.err.println(pe.getMessage());
			}
		}

		return builder.build();
	}

	private static String _extractBasePath(String[] args) {
		String defaultBasePath = ".";

		if (args.length > 2) {
			return IntStream.range(
				0, args.length - 1
			).filter(
				i -> args[i].equals("--base") && (args.length > (i + 1))
			).mapToObj(
				i -> args[i + 1]
			).findFirst(
			).orElse(
				defaultBasePath
			);
		}

		return defaultBasePath;
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

	private static String _getCommandProfile(String[] args) throws MissingCommandException {
		final Collection<String> profileFlags = new HashSet<>();

		try {
			Field field = BaseArgs.class.getDeclaredField("_profileName");

			Parameter parameters = field.getAnnotation(Parameter.class);

			Collections.addAll(profileFlags, parameters.names());
		}
		catch (Exception e) {
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
				profile = argsArray[x + 1];

				break;
			}
		}

		return profile;
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
			catch (Throwable e) {
				Class<?> throwableClass = e.getClass();

				System.err.println(
					"Exception thrown while loading extension." + System.lineSeparator() + "Exception: " +
						throwableClass.getName() + ": " + e.getMessage() + System.lineSeparator());

				Throwable cause = e.getCause();

				if (cause != null) {
					Class<?> throwableCauseClass = cause.getClass();

					System.err.print(
						throwableCauseClass.getName() + ": " + cause.getMessage() + System.lineSeparator());
				}
			}
		}

		return allCommands;
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
		catch (MissingCommandException mce) {
			error(mce);
		}

		return null;
	}

	private ClassLoader _getClassLoader() {
		if (_extensionsClassLoaderSupplier == null) {
			_extensionsClassLoaderSupplier = new ExtensionsClassLoaderSupplier(getExtensionsPath());
		}

		return _extensionsClassLoaderSupplier.get();
	}

	private Path _getUpdateCheckPath() throws IOException {
		Path userBladePath = _getUserBladePath();

		return userBladePath.resolve("updateCheck.properties");
	}

	private Path _getUserBladePath() {
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
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		return userBladePath;
	}

	private Collection<WorkspaceProvider> _getWorkspaceProviders() throws Exception {
		if (_workspaceProviders == null) {
			_workspaceProviders = new ArrayList<>();

			ClassLoader classLoader = _getClassLoader();

			ServiceLoader<WorkspaceProvider> serviceLoader = ServiceLoader.load(WorkspaceProvider.class, classLoader);

			Iterator<WorkspaceProvider> workspaceProviderIterator = serviceLoader.iterator();

			while (workspaceProviderIterator.hasNext()) {
				try {
					WorkspaceProvider workspaceProvider = workspaceProviderIterator.next();

					_workspaceProviders.add(workspaceProvider);
				}
				catch (Throwable e) {
					Class<?> throwableClass = e.getClass();

					System.err.println(
						"Exception thrown while loading WorkspaceProvider." + System.lineSeparator() + "Exception: " +
							throwableClass.getName() + ": " + e.getMessage());

					Throwable cause = e.getCause();

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
			catch (Throwable th) {
				throw th;
			}
			finally {
				if (command instanceof AutoCloseable) {
					((AutoCloseable)command).close();
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
		catch (Exception ioe) {
		}

		return false;
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

	private static final String _LAST_UPDATE_CHECK_KEY = "lastUpdateCheck";

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

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
	private final PrintStream _out;
	private Collection<WorkspaceProvider> _workspaceProviders = null;

}