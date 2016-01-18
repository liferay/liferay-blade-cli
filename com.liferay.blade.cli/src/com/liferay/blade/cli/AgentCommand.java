package com.liferay.blade.cli;

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Processor;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;
import aQute.remote.api.Agent;

import com.liferay.blade.cli.jmx.JMXBundleDeployer;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class AgentCommand {

	public AgentCommand(blade blade, AgentOptions options) throws Exception {
		_blade = blade;
	}

	public void _install(AgentInstallOptions options) throws Exception {
		if (ShellCommand.canConnect("localhost", Agent.DEFAULT_PORT)) {
			addWarning("Agent appears to be already installed and running.");
			return;
		}

		File cache = _blade.getCacheDir();

		cache.mkdirs();

		Processor reporter = new Processor();
		FixedIndexedRepo repo = new FixedIndexedRepo();
		Map<String, String> props = new HashMap<>();
		props.put("name", "index1");

		if (options.repo() != null) {
			props.put("locations", options.repo().toExternalForm());
		}
		else {
			props.put(
				"locations",
				"https://bndtools.ci.cloudbees.com/job/bnd.master/866/" +
					"artifact/dist/bundles/index.xml");
		}

		props.put(FixedIndexedRepo.PROP_CACHE, cache.getAbsolutePath());

		repo.setProperties(props);
		repo.setReporter(reporter);

		File[] files = null;

		if (options.version() != null) {
			files = repo.get(
				"biz.aQute.remote.agent", options.version().toString());
		}
		else {
			files = repo.get("biz.aQute.remote.agent", "latest");
		}

		if ((files == null) || (files.length == 0)) {
			addError(
				"Unable to find remote agent from remote repository. " +
				"Please ensure internet connectivity.");
			return;
		}

		File agentJar = files[0];

		if (options.liferayhome() != null) {
			File modulesDir = new File(options.liferayhome(), "osgi/modules");

			if (!modulesDir.exists()) {
				addError(
					"Could not find modules dir at " +
					modulesDir.getAbsolutePath());
				return;
			}

			IO.copy(agentJar, new File(modulesDir, agentJar.getName()));
		}
		else {
			JMXBundleDeployer deployer = null;

			if (options.port() != -1) {
				try {
					deployer = new JMXBundleDeployer(options.port());
				}
				catch (Exception e) {
					addError(
						"Unable to connect to Liferay using JMX.  Please " +
						"try again and specify the Liferay home folder using " +
						"the -l option.");
					return;
				}
			}
			else {
				try {
					deployer = new JMXBundleDeployer();
				}
				catch (Exception e) {
					addError(
						"Unable to connect to Liferay using JDK attach API, " +
						"please specify a port for JMX or specify Liferay " +
						"home folder -l option.");
					return;
				}
			}

			deployer.deploy(
				"biz.aQute.remote.agent",
				agentJar.toURI().toURL().toExternalForm());
		}

		_blade.out().println(
			"Installed remote agent sucessfully. Please wait 5-10 seconds " +
			"for remote agent to be autodeployed, then use the \"blade sh " +
			"<gogo-command>\". At any time, uninstall the remote agent by " +
			"using the \"blade agent uninstall\" command.");
	}

	public void _uninstall(AgentUninstallOptions options) throws Exception {
		if (options.liferayhome() == null) {
			addError("Must specify Liferay home location using -l option.");
			return;
		}

		File modulesDir = new File(options.liferayhome(), "osgi/modules");

		if (!modulesDir.exists()) {
			addError(
				"Could not find modules dir at " +
				modulesDir.getAbsolutePath());
			return;
		}

		File[] list = modulesDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains("biz.aQute.remote.agent");
			}

		});

		if ((list == null) || (list.length == 0)) {
			addError("Could not find remote agent bundle to uninstall.");
			return;
		}

		for (File agentJar : list) {
			if (!agentJar.delete()) {
				addError(
					"Could not delete remote agent jar " +
					agentJar.getAbsolutePath());
				return;
			}
		}

		_blade.out().println("Uninstalled remote agent sucessfully.");
	}

	public interface AgentInstallOptions extends AgentOptions {

		public File liferayhome();

		public URL repo();

		public Version version();

	}

	public interface AgentOptions extends Options {

		@Description("The jmx port to use to connect to Liferay 7")
		public int port();

	}

	public interface AgentUninstallOptions extends AgentOptions {

		public File liferayhome();

	}

	private void addError(String msg) {
		_blade.addErrors("agent", Collections.singleton(msg));
	}

	private void addWarning(String msg) {
		_blade.addWarnings("agent", Collections.singleton(msg));
	}

	private final blade _blade;

}