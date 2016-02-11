package com.liferay.blade.cli;

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

	public DeployCommand(blade blade, DeployOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	private void addError(String msg) {
		_blade.addErrors("deploy", Collections.singleton(msg));
	}

	public String deploy(GradleExec gradle, Set<File> outputFiles) throws Exception {
		final int retcode = gradle.executeGradleCommand("jar");

		if (retcode > 0) {
			addError("Gradle jar task failed.");
			return null;
		}

		return installOrUpdate(outputFiles.iterator().next());
	}

	public void deployWatch(
		final GradleExec gradleExec, final Set<File> outputFiles)
			throws Exception {

		if (outputFiles.size() > 0) {
			for (File outputFile : outputFiles) {
				final String retval = installOrUpdate(outputFile);

				_blade.out().println(retval);
			}
		}

		new Thread() {
			public void run() {
				try {
					gradleExec.executeGradleCommand("jar -t");
				}
				catch (Exception e) {
				}
			}
		}.start();

		final Consumer<Path> consumer = new Consumer<Path>(){
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

		new FileWatcher(_blade.getBase().toPath(), false, consumer);
	}

	public void execute() throws Exception {
		final GradleExec gradleExec = new GradleExec(_blade);

		final Set<File> outputFiles =
			GradleTooling.getOutputFiles(_blade.getCacheDir(), _blade.getBase());

		if (_options.watch()) {
			deployWatch(gradleExec, outputFiles);
		}
		else {
			deploy(gradleExec, outputFiles);
		}
	}

	private String installOrUpdate(File outputFile) throws Exception {
		boolean isFragment = false;
		String bsn = null;

		try(Jar bundle = new Jar(outputFile)) {
			final Manifest manifest = bundle.getManifest();
			final Attributes mainAttributes = manifest.getMainAttributes();

			isFragment =
				mainAttributes.getValue("Fragment-Host") != null;

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

		retval = Arrays.toString(supervisor.output().toArray(new String[0]));

		supervisor.close();

		return retval;
	}

	private final blade _blade;
	private final DeployOptions _options;

	public interface DeployOptions extends Options {
		@Description("Watches the deployed file for changes and will " +
				"automatically redeploy")
		boolean watch();
	}

	public class DeploySupervisor
		extends AgentSupervisor<Supervisor, Agent>implements Supervisor {

		private final blade _blade;

		private final List<String> _outLines;

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
			_outLines.add(out);
			return true;
		}

	}
}