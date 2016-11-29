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

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import aQute.lib.consoleapp.AbstractConsoleApp;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.blade.cli.CreateCommand.CreateOptions;
import com.liferay.blade.cli.DeployCommand.DeployOptions;
import com.liferay.blade.cli.GradleCommand.GradleOptions;
import com.liferay.blade.cli.InitCommand.InitOptions;
import com.liferay.blade.cli.InstallCommand.InstallOptions;
import com.liferay.blade.cli.MigrateServiceBuilderCommand.MigrateServiceBuilderOptions;
import com.liferay.blade.cli.MigrateThemeCommand.MigrateThemeOptions;
import com.liferay.blade.cli.MigrateWarCommand.MigrateWarOptions;
import com.liferay.blade.cli.OpenCommand.OpenOptions;
import com.liferay.blade.cli.SamplesCommand.SamplesOptions;
import com.liferay.blade.cli.ServerCommand.ServerOptions;
import com.liferay.blade.cli.ShellCommand.ShellOptions;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
@Component(properties = {"main.thread=true"}, provide = Runnable.class)
public class blade extends AbstractConsoleApp implements Runnable {

	public static void main(String[] args) throws Exception {
		new blade().run(args);
	}

	public blade() throws UnsupportedEncodingException {
	}

	public blade(Object target) throws UnsupportedEncodingException {
		super(target);
	}

	@Description(CreateCommand.DESCRIPTION)
	public void _create(CreateOptions options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	@Description(DeployCommand.DESCRIPTION)
	public void _deploy(DeployOptions options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	@Description(GradleCommand.DESCRIPTION)
	public void _gw(GradleOptions options) throws Exception {
		new GradleCommand(this, options).execute();
	}

	@Description("Get help on a specific command")
	public void _help(Options options) throws Exception {
		options._help();
	}

	@Description(InitCommand.DESCRIPTION)
	public void _init(InitOptions options) throws Exception {
		new InitCommand(this, options).execute();
	}

	@Description(InstallCommand.DESCRIPTION)
	public void _install(InstallOptions options) throws Exception {
		new InstallCommand(this, options).execute();
	}

	@Description(MigrateThemeCommand.DESCRIPTION)
	public void _migrateTheme(MigrateThemeOptions options) throws Exception {
		new MigrateThemeCommand(this, options).execute();
	}

	@Description(MigrateWarCommand.DESCRIPTION)
	public void _migrateWar(MigrateWarOptions options) throws Exception {
		new MigrateWarCommand(this, options).execute();
	}

	@Description(MigrateServiceBuilderCommand.DESCRIPTION)
	public void _migrateSB(MigrateServiceBuilderOptions options) throws Exception {
		new MigrateServiceBuilderCommand(this, options).execute();
	}

	@Description(OpenCommand.DESCRIPTION)
	public void _open(OpenOptions options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	public void _outputs(Options options) throws Exception {
		new OutputsCommand(this, options).execute();
	}

	@Description(SamplesCommand.DESCRIPTION)
	public void _samples(SamplesOptions options) throws Exception {
		new SamplesCommand(this, options).execute();
	}

	@Description(ServerCommand.DESCRIPTION)
	public void _server(ServerOptions options) throws Exception {
		ServerCommand serverCommand = new ServerCommand(this, options);
		String help = options._command().subCmd(options, serverCommand);

		if (help != null) {
			out.println(help);
		}
	}

	@Description(ShellCommand.DESCRIPTION)
	public void _sh(ShellOptions options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	@Description(UpdateCommand.DESCRIPTION)
	public void _update(Options options) throws Exception {
		new UpdateCommand(this, options).execute();
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

	@Reference(target = "(launcher.arguments=*)")
	public void args(Object object, Map<String, Object> map) {
		args = (String[])map.get("launcher.arguments");
	}

	public PrintStream err() {
		return err;
	}

	public File getCacheDir() {
		String userHome = System.getProperty("user.home");

		return IO.getFile(userHome + "/.blade/cache");
	}

	public PrintStream out() {
		return out;
	}

	@Override
	public void run() {
		try {
			new blade().run(args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void trace(String s, Object... args) {
		if (isTrace() && (tracer != null)) {
			tracer.format("# " + s + "%n", args);
			tracer.flush();
		}
	}

	private String[] args;
	private final Formatter tracer = new Formatter(System.out);

}