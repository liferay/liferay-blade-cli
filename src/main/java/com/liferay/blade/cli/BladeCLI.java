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

import aQute.lib.getopt.Options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class BladeCLI implements Runnable {

	public static void main(String[] args) {
		new BladeCLI().run(args);
	}

	public void addErrors(String prefix, Collection<String> data) {
		err().println("Error: " + prefix);
		data.forEach(err()::println);
	}

	public void convert(ConvertCommandArgs options) throws Exception {
		new ConvertCommand(this, options).execute();
	}

	public void create(CreateCommandArgs options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	public void deploy(DeployCommandArgs options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	public PrintStream err() {
		return _err;
	}

	public void error(String error) {
		err().println(error);
	}

	public void error(String string, String name, String message) {
		err().println(string + " [" + name + "]");
		err().println(message);
	}

	public File getBase() {
		return new File(_bladeArgs.getBase());
	}

	public BladeArgs getBladeArgs() {
		return _bladeArgs;
	}

	public Path getBundleDir() {
		String userHome = System.getProperty("user.home");

		return Paths.get(userHome, ".liferay", "bundles");
	}

	public File getCacheDir() {
		String userHome = System.getProperty("user.home");

		return Paths.get(userHome, ".blade", "cache").toFile();
	}

	public void gw(GradleCommandArgs options) throws Exception {
		new GradleCommand(this, options).execute();
	}

	public void help(Options options) throws Exception {
		options._help();
	}

	public void init(InitCommandArgs options) throws Exception {
		new InitCommand(this, options).execute();
	}

	public void install(InstallCommandArgs options) throws Exception {
		new InstallCommand(this, options).execute();
	}

	public void open(OpenCommandArgs options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	public PrintStream out() {
		return _out;
	}

	public void outputs(OutputsCommandArgs options) throws Exception {
		new OutputsCommand(this, options).execute();
	}

	@Override
	public void run() {
		try {
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
					upgradeProps((UpgradePropsOptions)_commandArgs);

					break;

				case "version":
					version((VersionCommandArgs)_commandArgs);

					break;
			}
		}
		catch (Exception e) {
			error(e.getMessage());
			e.printStackTrace(err());
		}
	}

	public void run(String[] args) {
		List<Object> argsList = Arrays.asList(
			new CreateCommandArgs(), new ConvertCommandArgs(), new DeployCommandArgs(), new GradleCommandArgs(),
			new InitCommandArgs(), new InstallCommandArgs(), new OpenCommandArgs(), new OutputsCommandArgs(),
			new SamplesCommandArgs(), new ServerStartCommandArgs(), new ServerStopCommandArgs(), new ShellCommandArgs(),
			new UpdateCommandArgs(), new UpgradePropsOptions(), new VersionCommandArgs());

		Builder builder = JCommander.newBuilder();

		for (Object o : argsList) {
			builder.addCommand(o);
		}

		JCommander commander = builder.addObject(_bladeArgs).build();

		commander.parse(args);

		String command = commander.getParsedCommand();

		Map<String, JCommander> commands = commander.getCommands();

		JCommander jcommander = commands.get(command);

		List<Object> objects = jcommander.getObjects();

		Object commandArgs = objects.get(0);

		_command = command;

		_commandArgs = commandArgs;

		run();
	}

	public void samples(SamplesCommandArgs options) throws Exception {
		new SamplesCommand(this, options).execute();
	}

	public void serverStart(ServerStartCommandArgs options) throws Exception {
		new ServerStartCommand(this, options).execute();
	}

	public void serverStop(ServerStopCommandArgs options) throws Exception {
		new ServerStopCommand(this, options).execute();
	}

	public void sh(ShellCommandArgs options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	public void trace(String s, Object... args) {
		if (_bladeArgs.isTrace() && (_tracer != null)) {
			_tracer.format("# " + s + "%n", args);
			_tracer.flush();
		}
	}

	public void update(UpdateCommandArgs options) throws Exception {
		new UpdateCommand(this, options).execute();
	}

	public void upgradeProps(UpgradePropsOptions options) throws Exception {
		new UpgradePropsCommand(this, options);
	}

	public void version(VersionCommandArgs options) throws Exception {
		new VersionCommand(this, options).execute();
	}

	private static final Formatter _tracer = new Formatter(System.out);

	private BladeArgs _bladeArgs = new BladeArgs();
	private String _command;
	private Object _commandArgs;
	private PrintStream _err = System.err;
	private PrintStream _out = System.out;

}