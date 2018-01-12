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

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class blade implements Runnable {

	public void _convert(ConvertCommandArgs options) throws Exception {
		new ConvertCommand(this, options).execute();
	}

	public void _create(CreateCommandArgs options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	public void _deploy(DeployCommandArgs options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	public void _gw(GradleCommandArgs options) throws Exception {
		new GradleCommand(this, options).execute();
	}

	public void _help(Options options) throws Exception {
		options._help();
	}

	public void _init(InitCommandArgs options) throws Exception {
		new InitCommand(this, options).execute();
	}

	public void _install(InstallCommandArgs options) throws Exception {
		new InstallCommand(this, options).execute();
	}

	public void _open(OpenCommandArgs options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	public void _outputs(OutputsCommandArgs options) throws Exception {
		new OutputsCommand(this, options).execute();
	}

	public void _samples(SamplesCommandArgs options) throws Exception {
		new SamplesCommand(this, options).execute();
	}

	public void _serverStart(ServerStartCommandArgs options) throws Exception {
		new ServerStartCommand(this, options).execute();
	}

	public void _serverStop(ServerStopCommandArgs options) throws Exception {
		new ServerStopCommand(this, options).execute();
	}

	public void _sh(ShellCommandArgs options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	public void _update(UpdateCommandArgs options) throws Exception {
		new UpdateCommand(this, options).execute();
	}

	public void _upgradeProps(UpgradePropsOptions options) throws Exception {
		new UpgradePropsCommand(this, options);
	}

	@Description("Show version information about blade")
	public void _version(Options options) throws IOException {
		Enumeration<URL> e = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");

		while (e.hasMoreElements()) {
			URL u = e.nextElement();

			Manifest m = new Manifest(u.openStream());

			String bsn = m.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);

			if ((bsn != null) && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = m.getMainAttributes();

				out.printf("%s\n", attrs.getValue(Constants.BUNDLE_VERSION));
				return;
			}
		}

		error("Could not locate version");
	}

	public void addErrors(String prefix, Collection<String> data) {
		err().println("Error: " + prefix);
		data.forEach(err()::println);
	}

	public PrintStream err() {
		return err;
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

	public PrintStream out() {
		return out;
	}

	@Override
	public void run() {
		try {
			switch (_command) {
			case "create":
				_create((CreateCommandArgs) _commandArgs);

				break;

			case "convert":
				_convert((ConvertCommandArgs) _commandArgs);

				break;

			case "deploy":
				_deploy((DeployCommandArgs) _commandArgs);

				break;

			case "gw":
				_gw((GradleCommandArgs) _commandArgs);

				break;

			case "help":

				break;

			case "init":
				_init((InitCommandArgs) _commandArgs);

				break;

			case "install":
				_install((InstallCommandArgs) _commandArgs);

				break;

			case "open":
				_open((OpenCommandArgs) _commandArgs);

				break;

			case "outputs":
				_outputs((OutputsCommandArgs) _commandArgs);

				break;

			case "samples":
				_samples((SamplesCommandArgs) _commandArgs);

				break;

			case "server start":
				_serverStart((ServerStartCommandArgs) _commandArgs);

				break;

			case "server stop":
				_serverStop((ServerStopCommandArgs) _commandArgs);

				break;

			case "sh":
				_sh((ShellCommandArgs) _commandArgs);

				break;

			case "update":
				_update((UpdateCommandArgs) _commandArgs);

				break;

			case "upgradeProps":
				_upgradeProps((UpgradePropsOptions) _commandArgs);

				break;

			case "version":

				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run(String[] args) {
		List<Object> argsList = Arrays.asList(
				new CreateCommandArgs(), new ConvertCommandArgs(), new DeployCommandArgs(), new GradleCommandArgs(),
				new InitCommandArgs(), new InstallCommandArgs(), new OpenCommandArgs(), new OutputsCommandArgs(),
				new SamplesCommandArgs(), new ServerStartCommandArgs(), new ServerStopCommandArgs(),
				new ShellCommandArgs(), new UpdateCommandArgs(), new UpgradePropsOptions());

		Builder builder = JCommander.newBuilder();

		for (Object o : argsList) {
			builder.addCommand(o);
		}

		JCommander commander = builder.addObject(_bladeArgs).build();

		commander.parse(args);

		String command = commander.getParsedCommand();
		Object commandArgs = commander.getCommands().get(command).getObjects().get(0);

		_command = command;
		_commandArgs = commandArgs;

		run();
	}

	public void trace(String s, Object... args) {
		if (_bladeArgs.isTrace() && (tracer != null)) {
			tracer.format("# " + s + "%n", args);
			tracer.flush();
		}
	}

	private BladeArgs _bladeArgs = new BladeArgs();
	private String _command;
	private Object _commandArgs;
	private PrintStream err = System.err;
	private PrintStream out = System.out;
	private final Formatter tracer = new Formatter(System.out);

}