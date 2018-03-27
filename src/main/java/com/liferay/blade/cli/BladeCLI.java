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

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeCLI implements Runnable {

	public static void main(String[] args) {
		new BladeCLI().run(args);
	}

	public BladeCLI() {
		this(System.out, System.err);
	}

	public BladeCLI(PrintStream out, PrintStream err) {
		_out = out;
		_err = err;
	}

	public void addErrors(String prefix, Collection<String> data) {
		err().println("Error: " + prefix);
		data.forEach(err()::println);
	}

	public void convert(ConvertCommandArgs args) throws Exception {
		new ConvertCommand(this, args).execute();
	}

	public void create(CreateCommandArgs args) throws Exception {
		new CreateCommand(this, args).execute();
	}

	public void deploy(DeployCommandArgs args) throws Exception {
		new DeployCommand(this, args).execute();
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

	public void gw(GradleCommandArgs args) throws Exception {
		new GradleCommand(this, args).execute();
	}

	public void help(HelpCommandArgs args) throws Exception {
		new HelpCommand(this, args).execute();
	}

	public void init(InitCommandArgs args) throws Exception {
		new InitCommand(this, args).execute();
	}

	public void install(InstallCommandArgs args) throws Exception {
		new InstallCommand(this, args).execute();
	}

	public void open(OpenCommandArgs args) throws Exception {
		new OpenCommand(this, args).execute();
	}

	public PrintStream out() {
		return _out;
	}

	public void out(String msg) {
		out().println(msg);
	}

	public void outputs(OutputsCommandArgs args) throws Exception {
		new OutputsCommand(this, args).execute();
	}

	public void printUsage() {
		StringBuilder usageString = new StringBuilder();

		_jcommander.usage(usageString);

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
		_jcommander.usage(command);
	}

	public void printUsage(String command, String message) {
		out(message);
		_jcommander.usage(command);
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
				switch (_command) {
					case "create":
						create((CreateCommandArgs)_commandArgs);

						break;

					case "convert":
						convert((ConvertCommandArgs)_commandArgs);

						break;

					case "deploy":
						deploy((DeployCommandArgs)_commandArgs);

						break;

					case "gw":
						gw((GradleCommandArgs)_commandArgs);

						break;

					case "help":
						help((HelpCommandArgs)_commandArgs);

						break;
					case "init":
						init((InitCommandArgs)_commandArgs);

						break;

					case "install":
						install((InstallCommandArgs)_commandArgs);

						break;

					case "open":
						open((OpenCommandArgs)_commandArgs);

						break;

					case "outputs":
						outputs((OutputsCommandArgs)_commandArgs);

						break;

					case "samples":
						samples((SamplesCommandArgs)_commandArgs);

						break;

					case "server start":
						serverStart((ServerStartCommandArgs)_commandArgs);

						break;

					case "server stop":
						serverStop((ServerStopCommandArgs)_commandArgs);

						break;

					case "sh":
						sh((ShellCommandArgs)_commandArgs);

						break;

					case "update":
						update((UpdateCommandArgs)_commandArgs);

						break;

					case "upgradeProps":
						upgradeProps((UpgradePropsArgs)_commandArgs);

						break;

					case "version":
						version((VersionCommandArgs)_commandArgs);

						break;
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

	public void run(String[] args) {
		System.setOut(out());

		System.setErr(err());

		List<String> flags = new ArrayList<>(Arrays.asList(args));

		_sort(flags);

		args = flags.toArray(new String[0]);

		List<Object> argsList = Arrays.asList(
			new CreateCommandArgs(), new ConvertCommandArgs(), new DeployCommandArgs(), new GradleCommandArgs(),
			new HelpCommandArgs(), new InitCommandArgs(), new InstallCommandArgs(), new OpenCommandArgs(),
			new OutputsCommandArgs(), new SamplesCommandArgs(), new ServerStartCommandArgs(),
			new ServerStopCommandArgs(), new ShellCommandArgs(), new UpdateCommandArgs(), new UpgradePropsArgs(),
			new VersionCommandArgs());

		Builder builder = JCommander.newBuilder();

		for (Object o : argsList) {
			builder.addCommand(o);
		}

		_jcommander = builder.build();

		if ((args.length == 1) && args[0].equals("--help")) {
			printUsage();
		}
		else {
			try {
				_jcommander.parse(args);

				String command = _jcommander.getParsedCommand();

				Map<String, JCommander> commands = _jcommander.getCommands();

				JCommander jcommander = commands.get(command);

				if (jcommander == null) {
					printUsage();
					return;
				}

				List<Object> objects = jcommander.getObjects();

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
				error(_jcommander.getParsedCommand() + ": " + pe.getMessage());
			}
		}
	}

	public void samples(SamplesCommandArgs args) throws Exception {
		new SamplesCommand(this, args).execute();
	}

	public void serverStart(ServerStartCommandArgs args) throws Exception {
		new ServerStartCommand(this, args).execute();
	}

	public void serverStop(ServerStopCommandArgs args) throws Exception {
		new ServerStopCommand(this, args).execute();
	}

	public void sh(ShellCommandArgs args) throws Exception {
		new ShellCommand(this, args).execute();
	}

	public void trace(String s, Object... args) {
		if (_commandArgs.isTrace() && (_tracer != null)) {
			_tracer.format("# " + s + "%n", args);
			_tracer.flush();
		}
	}

	public void update(UpdateCommandArgs args) throws Exception {
		new UpdateCommand(this, args).execute();
	}

	public void upgradeProps(UpgradePropsArgs args) throws Exception {
		new UpgradePropsCommand(this, args);
	}

	public void version(VersionCommandArgs args) throws Exception {
		new VersionCommand(this, args).execute();
	}

	private static void _sort(List<String> flags) {
		Collection<String> addLast = new ArrayList<>();

		for (int x = 0; x < flags.size(); x++) {
			String s = flags.get(x);

			if (s.equals("--base") || s.equals("--working-dir")) {
				addLast.add(flags.remove(x));
				addLast.add(flags.remove(x));
			}
			else if (s.equals("--trace") || s.equals("--help")) {
				addLast.add(flags.remove(x));
			}
			else if (s.equals("server")) {
				int next = x + 1;

				String serverCommand = s + " " + flags.get(next);

				flags.set(x, serverCommand);
				flags.remove(next);
			}
		}

		flags.addAll(addLast);
	}

	private static final Formatter _tracer = new Formatter(System.out);

	private String _command;
	private BaseArgs _commandArgs;
	private final PrintStream _err;
	private JCommander _jcommander;
	private final PrintStream _out;

}