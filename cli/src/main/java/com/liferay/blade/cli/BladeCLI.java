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

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;

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
		this(System.out, System.err);
	}

	public BladeCLI(PrintStream out, PrintStream err) {
		AnsiConsole.systemInstall();

		_out = out;
		_err = err;
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

	public File getBase() {
		if (_commandArgs == null) {
			return new File(".");
		}

		return new File(_commandArgs.getBase());
	}

	public BaseArgs getBladeArgs() {
		return _commandArgs;
	}

	public Path getBundleDir() {
		String userHome = System.getProperty("user.home");

		return Paths.get(userHome, ".liferay", "bundles");
	}

	public File getCacheDir() {
		String userHome = System.getProperty("user.home");

		Path cacheDir = Paths.get(userHome, ".blade", "cache");

		return cacheDir.toFile();
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

			e.printStackTrace(err());
		}
	}

	public void run(String[] args) throws Exception {
		System.setOut(out());

		System.setErr(err());

		_commands = new Extensions().getCommands();

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

					return;
				}

				List<Object> objects = jCommander.getObjects();

				Object commandArgs = objects.get(0);

				_command = command;

				_commandArgs = (BaseArgs)commandArgs;

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
	}

	public void trace(String s, Object... args) {
		if (_commandArgs.isTrace() && (_tracer != null)) {
			_tracer.format("# " + s + "%n", args);
			_tracer.flush();
		}
	}

	private void _runCommand() throws Exception {
		BaseCommand<?> command = null;

		if (_commands.containsKey(_command)) {
			command = _commands.get(_command);
		}

		if (command != null) {
			command.setArgs(_commandArgs);
			command.setBlade(this);
			command.execute();
		}
		else {
			printUsage();
		}
	}

	private static final Formatter _tracer = new Formatter(System.out);

	private String _command;
	private BaseArgs _commandArgs;
	private Map<String, BaseCommand<? extends BaseArgs>> _commands;
	private final PrintStream _err;
	private JCommander _jCommander;
	private final PrintStream _out;

}