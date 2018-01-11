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
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.liferay.blade.cli.ConvertCommand.ConvertOptions;
import com.liferay.blade.cli.CreateCommand.CreateOptions;
import com.liferay.blade.cli.DeployCommand.DeployOptions;
import com.liferay.blade.cli.GradleCommand.GradleOptions;
import com.liferay.blade.cli.InitCommand.InitOptions;
import com.liferay.blade.cli.InstallCommand.InstallOptions;
import com.liferay.blade.cli.OpenCommand.OpenOptions;
import com.liferay.blade.cli.OutputsCommand.OutputsOptions;
import com.liferay.blade.cli.SamplesCommand.SamplesOptions;
import com.liferay.blade.cli.ServerStartCommand.ServerStartOptions;
import com.liferay.blade.cli.ServerStopCommand.ServerStopOptions;
import com.liferay.blade.cli.ShellCommand.ShellOptions;
import com.liferay.blade.cli.UpdateCommand.UpdateOptions;
import com.liferay.blade.cli.UpgradePropsCommand.UpgradePropsOptions;
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

	public void run(String[] args) {
		
		List<Object> argsList = Arrays.asList(
				new CreateOptions(),
				new ConvertOptions(),
				new DeployOptions(), 
				new GradleOptions(),
				new InitOptions(),
				new InstallOptions(),
				new OpenOptions(),
				new OutputsOptions(),
				new SamplesOptions(),
				new ServerStartOptions(),
				new ServerStopOptions(),
				new ShellOptions(),
				new UpdateOptions(),
				new UpgradePropsOptions());
		Builder builder=
				JCommander.newBuilder();
		 	for (Object o : argsList) {
		 		builder.addCommand(o);
		 	}
		 JCommander commander =	builder
		  .addObject(_bladeArgs)
		  .build();
		 commander
		  .parse(args);
		 	
		 String command = commander.getParsedCommand();
		 Object commandArgs = commander.getCommands().get(command).getObjects().get(0);
		 
		_command = command;
		_commandArgs = commandArgs;
		
		run();
	}
	
	public void _create(CreateOptions options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	public void _deploy(DeployOptions options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	public void _gw(GradleOptions options) throws Exception {
		new GradleCommand(this, options).execute();
	}
	
	public void error(String error) {
		err().println(error);
	}
	
	public void addErrors(String prefix, Collection<String> data) {
		err().println("Error: " + prefix);
		data.forEach(err()::println);
	}
	
	public void error(String string, String name, String message) {
		err().println(string + " [" + name + "]");
		err().println(message);
		
	}
	
	public File getBase() {
		return new File(_bladeArgs.getBase());
	}
	
	public void _help(Options options) throws Exception {
		options._help();
	}

	public void _init(InitOptions options) throws Exception {
		new InitCommand(this, options).execute();
	}

	public void _install(InstallOptions options) throws Exception {
		new InstallCommand(this, options).execute();
	}

	public void _open(OpenOptions options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	public BladeArgs getBladeArgs() {
		return _bladeArgs;
	}
	public void _outputs(OutputsOptions options) throws Exception {
		new OutputsCommand(this, options).execute();
	}

	public void _samples(SamplesOptions options) throws Exception {
		new SamplesCommand(this, options).execute();
	}

	public void _serverStart(ServerStartOptions options) throws Exception {
		new ServerStartCommand(this, options).execute();
	}
	
	public void _serverStop(ServerStopOptions options) throws Exception {
		new ServerStopCommand(this, options).execute();	
	}

	public void _sh(ShellOptions options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	public void _update(UpdateOptions options) throws Exception {
		new UpdateCommand(this, options).execute();
	}

	public void _upgradeProps(UpgradePropsOptions options) throws Exception {
		new UpgradePropsCommand(this, options);
	}

	public void _convert(ConvertOptions options) throws Exception {
		new ConvertCommand(this, options).execute();
	}

	@Description("Show version information about blade")
	public void _version(Options options) throws IOException {
		Enumeration<URL> e =
			getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");

		while (e.hasMoreElements()) {
			URL u = e.nextElement();
			Manifest m = new Manifest(u.openStream());
			String bsn =
				m.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);

			if ((bsn != null) && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = m.getMainAttributes();
				out.printf("%s\n", attrs.getValue(Constants.BUNDLE_VERSION));
				return;
			}
		}

		error("Could not locate version");
	}

	public PrintStream err() {
		return err;
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
			case "create": {
				_create((CreateOptions) _commandArgs);
			}
				break;
			case "convert": {
				_convert((ConvertOptions) _commandArgs);
			}
				break;
			case "deploy": {
				_deploy((DeployOptions) _commandArgs);
			}
				break;
			case "gw": {
				_gw((GradleOptions) _commandArgs);
			}
				break;
			default:
			case "help": {
				// TODO: Print help here?
			}
				break;
			case "init": {
				_init((InitOptions) _commandArgs);
			}
				break;
			case "install": {
				_install((InstallOptions) _commandArgs);
			}
			case "open": {
				_open((OpenOptions) _commandArgs);
			}
				break;
			case "outputs": {
				_outputs((OutputsOptions) _commandArgs);
			}
				break;
			case "samples": {
				_samples((SamplesOptions) _commandArgs);
			}
				break;
			case "server start": {
				_serverStart((ServerStartOptions) _commandArgs);

			}
				break;
			case "server stop": {
				_serverStop((ServerStopOptions) _commandArgs);
			}
				break;
			case "sh": {
				_sh((ShellOptions) _commandArgs);
			}
				break;
			case "update": {
				_update((UpdateOptions) _commandArgs);
			}
				break;
			case "upgradeProps": {
				_upgradeProps((UpgradePropsOptions) _commandArgs);
			}
				break;
			case "version": {
				// TODO: What is this supposed to do?
			}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void trace(String s, Object... args) {
		if (_bladeArgs.isTrace() && (tracer != null)) {
			tracer.format("# " + s + "%n", args);
			tracer.flush();
		}

	}

	@Parameters(commandDescription = "Options valid for all commands. Must be given before sub command")
	private static class BladeArgs {
		
		public boolean isTrace() {
			return trace;
		}

		public String getBase() {
			return base;
		}

		public String getFailok() {
			return failok;
		}

		@Parameter(
			names = {"-b", "--base"},
			description ="Specify a new base directory (default working directory).")
		private String base = ".";

		@Parameter(
			names = {"-f", "--failok"},
			description ="Do not return error status for error that match this given regular expression.")
		private String failok;
			
		@Parameter(
			names = {"-t", "--trace"},
			description ="Print exception stack traces when they occur.")
		private boolean trace;

		
	}
	
	private String _command;
	private BladeArgs _bladeArgs = new BladeArgs();
	private Object _commandArgs;
	private final Formatter tracer = new Formatter(System.out);
	private PrintStream out = System.out;
	private PrintStream err = System.err;
}