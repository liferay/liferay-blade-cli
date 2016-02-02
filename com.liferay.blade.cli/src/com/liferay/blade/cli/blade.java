package com.liferay.blade.cli;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import aQute.lib.consoleapp.AbstractConsoleApp;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.blade.cli.AgentCommand.AgentOptions;
import com.liferay.blade.cli.CreateCommand.CreateOptions;
import com.liferay.blade.cli.DeployCommand.DeployOptions;
import com.liferay.blade.cli.GradleCommand.GradleOptions;
import com.liferay.blade.cli.InitCommand.InitOptions;
import com.liferay.blade.cli.MigrateThemeCommand.MigrateThemeOptions;
import com.liferay.blade.cli.OpenCommand.OpenOptions;
import com.liferay.blade.cli.SamplesCommand.SamplesOptions;
import com.liferay.blade.cli.ServerCommand.ServerOptions;
import com.liferay.blade.cli.ShellCommand.ShellOptions;
import com.liferay.blade.cli.UpdateCommand.UpdateOptions;

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

	@Description("Install or uninstall remote agent in Liferay")
	public void _agent(AgentOptions options) throws Exception {
		AgentCommand agent = new AgentCommand(this, options);
		String help = options._command().subCmd(options, agent);

		if (help != null) {
			out.println(help);
		}
	}

	@Description(
		"Creates a new Liferay module project from several available " +
			"templates."
	)
	public void _create(CreateOptions options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	@Description("Deploys a bundle to the Liferay module framework.")
	public void _deploy(DeployOptions options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	@Description("Execute gradle command.  Will use gradle wrapper if detected")
	public void _gw(GradleOptions options) throws Exception {
		new GradleCommand(this, options).execute();
	}

	@Description("Get help on a specific command")
	public void _help(Options options) throws Exception {
		options._help();
	}

	@Description("Initializes a new Liferay 7 workspace")
	public void _init(InitOptions options) throws Exception {
		new InitCommand(this, options).execute();
	}

	@Description("Migrate a plugins sdk theme to new workspace theme project")
	public void _migrateTheme(MigrateThemeOptions options) throws Exception {
		new MigrateThemeCommand(this, options).execute();
	}

	@Description("Opens or imports a file or project in Liferay IDE.")
	public void _open(OpenOptions options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	@Description("Generate a sample project")
	public void _samples(SamplesOptions options) throws Exception {
		new SamplesCommand(this, options).execute();
	}

	@Description("Start or stop your server")
	public void _server(ServerOptions options) throws Exception {
		ServerCommand serverCommand = new ServerCommand(this, options);
		String help = options._command().subCmd(options, serverCommand);

		if (help != null) {
			out.println(help);
		}
	}

	@Description(
		"Connects to Liferay and executes gogo command and returns " +
			"output."
	)
	public void _sh(ShellOptions options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	@Description("Update blade to latest version")
	public void _update(UpdateOptions options) throws Exception {
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