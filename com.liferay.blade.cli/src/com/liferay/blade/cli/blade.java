package com.liferay.blade.cli;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import aQute.lib.consoleapp.AbstractConsoleApp;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.blade.cli.AgentCommand.AgentOptions;

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
 */
@Component(provide = Runnable.class, properties = { "main.thread=true"})
public class blade extends AbstractConsoleApp implements Runnable {

	private String[] args;
	private final Formatter tracer = new Formatter(System.out);

	public blade() throws UnsupportedEncodingException {
		super();
	}

	public blade(Object target) throws UnsupportedEncodingException {
		super(target);
	}

	public static void main(String[] args) throws Exception {
		new blade().run(args);
	}

	@Description("Creates a new Liferay module project from several available templates.")
	public void _create(CreateOptions options) throws Exception {
		new CreateCommand(this, options).execute();
	}

	@Description("Deploys a bundle to the Liferay module framework.")
	public void _deploy(DeployOptions options) throws Exception {
		new DeployCommand(this, options).execute();
	}

	@Description("Initializes a new Liferay 7 workspace")
	public void _init(InitOptions options) throws Exception {
		new InitCommand(this, options).execute();
	}

	@Description("Opens or imports a file or project in Liferay IDE.")
	public void _open(OpenOptions options) throws Exception {
		new OpenCommand(this, options).execute();
	}

	@Description("Connects to Liferay and executes gogo command and returns output.")
	public void _sh(ShellOptions options) throws Exception {
		new ShellCommand(this, options).execute();
	}

	@Description("Install or uninstall remote agent in Liferay")
	public void _agent(AgentOptions options) throws Exception {
		AgentCommand agent = new AgentCommand(this, options);
		String help = options._command().subCmd(options, agent);

		if (help != null) {
			out.println(help);
		}
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

			if (bsn != null && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = m.getMainAttributes();
				out.printf("%s\n", attrs.getValue(Constants.BUNDLE_VERSION));
				return;
			}
		}

		error("Could not locate version");
	}

	public PrintStream out() {
		return out;
	}

	public PrintStream err() {
		return err;
	}

	@Override
	public void run() {
		try {
			new blade().run(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Reference(target ="(launcher.arguments=*)")
	void args( Object object, Map<String, Object> map) {
		args = (String[])map.get("launcher.arguments");
	}

	public File getCacheDir() {
		String userHome = System.getProperty("user.home");

		return IO.getFile(userHome + "/.blade/cache");
	}

	@Override
	public void trace(String s, Object... args) {
		if (isTrace() && tracer != null) {
			tracer.format("# " + s + "%n", args);
			tracer.flush();
		}
	}

}