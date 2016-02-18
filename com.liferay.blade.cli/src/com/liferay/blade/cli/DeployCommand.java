package com.liferay.blade.cli;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Jar;

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

import com.liferay.blade.cli.FileWatcher.Consumer;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 */
public class DeployCommand {

	public static final String DESCRIPTION =
		"Builds and deploys bundles to the Liferay module framework.";

	public DeployCommand(blade blade, DeployOptions options) throws Exception {
		_blade = blade;
		_options = options;
		_port = options.port() != 0 ? options.port() : Agent.DEFAULT_PORT;
	}

	public void deploy(GradleExec gradle, Set<File> outputFiles)
		throws Exception {

		final int retcode = gradle.executeGradleCommand("build -x check");

		if (retcode > 0) {
			addError("Gradle jar task failed.");
			return;
		}

		for (File outputFile : outputFiles) {
			String retval = installOrUpdate(outputFile);

			_blade.out().println(retval);
		}
	}

	public void deployWatch(
			final GradleExec gradleExec, final Set<File> outputFiles)
		throws Exception {

		deploy(gradleExec, outputFiles);

		new Thread() {

			public void run() {
				try {
					gradleExec.executeGradleCommand("build -x check -t");
				}
				catch (Exception e) {
				}
			}

		}.start();

		final Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				try {
					File modifiedFile = modified.toFile();

					if (outputFiles.contains(modifiedFile)) {
						_blade.out().println("installOrUpdate " + modifiedFile);

						final String retval = installOrUpdate(modifiedFile);

						_blade.out().println(retval);
					}
				}
				catch (Exception e) {
				}
			}

		};

		new FileWatcher(_blade.getBase().toPath(), true, consumer);
	}

	public void execute() throws Exception {
		if (!Util.canConnect("localhost", _port)) {
			addError(
				"deploy",
				"Unable to connect to remote agent on port " + _port + ". " +
					"To install the agent bundle run the command \"blade " +
						"agent install\".");
			return;
		}

		final GradleExec gradleExec = new GradleExec(_blade);

		final Set<File> outputFiles = GradleTooling.getOutputFiles(
			_blade.getCacheDir(), _blade.getBase());

		if (_options.watch()) {
			deployWatch(gradleExec, outputFiles);
		}
		else {
			deploy(gradleExec, outputFiles);
		}
	}

	@Description(DESCRIPTION)
	public interface DeployOptions extends Options {

		@Description("The port to use to connect to remote agent")
		public int port();

		@Description(
			"Watches the deployed file for changes and will automatically " +
				"redeploy"
		)
		public boolean watch();

	}

	public class DeploySupervisor
		extends AgentSupervisor<Supervisor, Agent> implements Supervisor {

		public DeploySupervisor(blade blade) {
			_blade = blade;
			_outLines = new ArrayList<>();
		}

		public void connect(String host, int port) throws Exception {
			super.connect(Agent.class, this, host, port);
		}

		@Override
		public void event(Event e) throws Exception {
		}

		public synchronized List<String> output() {
			List<String> retval = new ArrayList<>(_outLines);

			_outLines.clear();

			return retval;
		}

		@Override
		public boolean stderr(String out) throws Exception {
			_blade.err().print(out);
			return true;
		}

		@Override
		public synchronized boolean stdout(String out) throws Exception {
			_outLines.add(out.replaceAll("^>.*$", ""));
			return true;
		}

		private final blade _blade;
		private final List<String> _outLines;

	}

	private void addError(String msg) {
		_blade.addErrors("deploy", Collections.singleton(msg));
	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private String installOrUpdate(File outputFile) throws Exception {
		boolean isFragment = false;
		String fragmentHost = null;
		String bsn = null;

		try(Jar bundle = new Jar(outputFile)) {
			final Manifest manifest = bundle.getManifest();
			final Attributes mainAttributes = manifest.getMainAttributes();

			fragmentHost = mainAttributes.getValue("Fragment-Host");

			isFragment = fragmentHost != null;

			bsn = bundle.getBsn();
		}

		final DeploySupervisor supervisor = new DeploySupervisor(_blade);
		supervisor.connect("localhost", Agent.DEFAULT_PORT);

		final Agent agent = supervisor.getAgent();
		agent.redirect(-1);

		long existingId = -1;

		List<BundleDTO> bundles = agent.getBundles();

		for (BundleDTO bundle : bundles) {
			if (bundle.symbolicName.equals(bsn)) {
				existingId = bundle.id;
				break;
			}
		}

		String retval = null;

		String bundleURL = outputFile.toURI().toASCIIString();

		if (existingId > 0) {
			agent.stop(existingId);
			agent.updateFromURL(existingId, bundleURL);

			_blade.out().println("Updated bundle " + existingId);
		}
		else {
			_blade.out().println("install " + bundleURL);

			agent.stdin("install " + bundleURL);

			if (!isFragment) {
				bundles = agent.getBundles();

				for (BundleDTO bundle : bundles) {
					if (bundle.symbolicName.equals(bsn)) {
						existingId = bundle.id;
						break;
					}
				}
			}
		}

		agent.start(existingId);

		if (isFragment) {
			String hostBSN = new Parameters(
				fragmentHost).keySet().iterator().next();

			long hostId = -1;

			for (BundleDTO bundle : bundles) {
				if (bundle.symbolicName.equals(hostBSN)) {
					hostId = bundle.id;
					break;
				}
			}

			if (hostId > 0) {
				_blade.out().println("refreshing host " + hostId);

				agent.stdin("refresh " + hostId);
			}
		}

		String[] output = supervisor.output().toArray(new String[0]);

		retval =
			output != null && output.length > 0 ? Arrays.toString(output) : "";

		supervisor.close();

		return retval;
	}

	private final blade _blade;
	private final DeployOptions _options;
	private final int _port;

}