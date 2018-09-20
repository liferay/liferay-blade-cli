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
import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.command.BaseArgs;
import com.liferay.blade.cli.command.BaseCommand;
import com.liferay.blade.cli.util.CombinerClassLoader;
import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

import org.fusesource.jansi.AnsiConsole;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeCLI implements Runnable {

	public static void main(String[] args) {
		BladeCLI bladeCLI = new BladeCLI();

		try {
			bladeCLI.run(args);
		}
		catch (Exception e) {
			bladeCLI.error("Unexpected error occured.");

			e.printStackTrace(bladeCLI._err);
		}
	}

	public BladeCLI() {
		this(System.out, System.err, System.in);
	}

	public BladeCLI(PrintStream out, PrintStream err, InputStream in) {
		AnsiConsole.systemInstall();

		_out = out;
		_err = err;
		_in = in;
	}

	public void addErrors(String prefix, Collection<String> data) {
		err().println("Error: " + prefix);

		data.forEach(err()::println);
	}

	public PrintStream err() {
		return _err;
	}

	public void err(String msg) {
		_err.println(msg);
	}

	public void error(String error) {
		err(error);
	}

	public void error(String string, String name, String message) {
		err(string + " [" + name + "]");
		err(message);
	}

	public void error(Throwable error) {
		err(error.getMessage());
		error.printStackTrace(err());
	}

	public BaseArgs getBladeArgs() {
		return _commandArgs;
	}

	public BladeSettings getBladeSettings() throws IOException {
		final File settingsFile;

		if (WorkspaceUtil.isWorkspace(this)) {
			File workspaceDir = WorkspaceUtil.getWorkspaceDir(this);

			settingsFile = new File(workspaceDir, ".blade/settings.properties");
		}
		else {
			settingsFile = new File(_USER_HOME_DIR, ".blade/settings.properties");
		}

		return new BladeSettings(settingsFile);
	}

	public Path getCachePath() throws IOException {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path cachePath = userHomePath.resolve(".blade/cache");

		if (!Files.exists(cachePath)) {
			Files.createDirectories(cachePath);
		}

		return cachePath;
	}

	public BaseCommand<?> getCommand() {
		return _baseCommand;
	}

	public Path getExtensionsPath() {
		try {
			Path userHomePath = _USER_HOME_DIR.toPath();

			Path dotBladePath = userHomePath.resolve(".blade");

			if (Files.notExists(dotBladePath)) {
				Files.createDirectories(dotBladePath);
			}
			else if (!Files.isDirectory(dotBladePath)) {
				throw new Exception(".blade is not a directory!");
			}

			Path extensions = dotBladePath.resolve("extensions");

			if (Files.notExists(extensions)) {
				Files.createDirectories(extensions);
			}
			else if (!Files.isDirectory(extensions)) {
				throw new Exception(".blade/extensions is not a directory!");
			}

			return extensions;
		}
		catch (Exception e) {
			e.printStackTrace();

			throw new RuntimeException(e);
		}
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

	@Override
	public void run() {
		try {
			if (_commandArgs.isHelp()) {
				if (Objects.isNull(_command) || (_command.length() == 0)) {
					printUsage();
				}
				else {
					printUsage(_command);
				}
			}
			else {
				if (_commandArgs != null) {
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
			error(e.getMessage());

			if (getBladeArgs().isTrace()) {
				e.printStackTrace(err());
			}
		}
	}

	public void run(String[] args) throws Exception {
		String basePath = _extractBasePath(args);

		File baseDir = new File(basePath).getAbsoluteFile();

		_commandArgs.setBase(baseDir);

		System.setOut(out());

		System.setErr(err());

		Extensions extensions = new Extensions(getBladeSettings(), getExtensionsPath());

		_commands = extensions.getCommands();

		args = Extensions.sortArgs(_commands, args);

		Builder builder = JCommander.newBuilder();

		for (Entry<String, BaseCommand<? extends BaseArgs>> e : _commands.entrySet()) {
			BaseCommand<? extends BaseArgs> value = e.getValue();

			builder.addCommand(e.getKey(), value.getArgs());
		}

		_jCommander = builder.build();

		if ((args.length == 1) && args[0].equals("--help")) {
			printUsage();
		}
		else {
			try {
				_jCommander.parse(args);

				String command = _jCommander.getParsedCommand();

				Map<String, JCommander> jCommands = _jCommander.getCommands();

				JCommander jCommander = jCommands.get(command);

				if (jCommander == null) {
					printUsage();

					extensions.close();

					return;
				}

				List<Object> objects = jCommander.getObjects();

				Object commandArgs = objects.get(0);

				_command = command;

				_commandArgs = (BaseArgs)commandArgs;

				_commandArgs.setBase(baseDir);

				run();
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

		extensions.close();
	}

	public void trace(String s, Object... args) {
		if (_commandArgs.isTrace() && (_tracer != null)) {
			_tracer.format("# " + s + "%n", args);
			_tracer.flush();
		}
	}

	private static String _extractBasePath(String[] args) {
		String defaultBasePath = ".";

		if (args.length > 2) {
			return IntStream.range(
				0, args.length - 1
			).filter(
				i -> args[i].equals("--base") && args.length > (i + 1)
			).mapToObj(
				i -> args[i + 1]
			).findFirst(
			).orElse(
				defaultBasePath
			);
		}

		return defaultBasePath;
	}

	private void _runCommand() throws Exception {
		BaseCommand<?> command = null;

		if (_commands.containsKey(_command)) {
			command = _commands.get(_command);
		}

		if (command != null) {
			_baseCommand = command;
			command.setArgs(_commandArgs);
			command.setBlade(this);

			Thread thread = Thread.currentThread();

			ClassLoader currentClassLoader = thread.getContextClassLoader();

			ClassLoader combinedClassLoader = CombinerClassLoader.newInstance(
				currentClassLoader, command.getClassLoader());

			try {
				thread.setContextClassLoader(combinedClassLoader);

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

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private static final Formatter _tracer = new Formatter(System.out);

	private BaseCommand<?> _baseCommand;
	private String _command;
	private BaseArgs _commandArgs = new BaseArgs();
	private Map<String, BaseCommand<? extends BaseArgs>> _commands;
	private final PrintStream _err;
	private final InputStream _in;
	private JCommander _jCommander;
	private final PrintStream _out;

}