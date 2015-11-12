package com.liferay.blade.cli.cmds;

import aQute.bnd.osgi.Jar;

import com.liferay.blade.cli.DeployOptions;
import com.liferay.blade.cli.blade;
import com.liferay.blade.cli.jmx.JMXBundleDeployer;
import com.liferay.blade.cli.util.FileWatcher;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Pattern;

public class DeployCommand {

	private final static Pattern BSN_GUESS = Pattern.compile("\\b\\d+(?:\\.\\d+)*\\b");

	final private blade lfr;
	final private DeployOptions options;
	private JMXBundleDeployer _bundleDeployer;

	public DeployCommand(blade lfr, DeployOptions options) throws Exception {
		this.lfr = lfr;
		this.options = options;

		List<String> args = options._arguments();

		if (args.size() == 0) {

			// Default command

			printHelp();
		}
		else {
			execute();
		}
	}

	private void addError(String prefix, String msg) {
		lfr.addErrors(prefix, Collections.singleton(msg));
	}

	private void deploy(JMXBundleDeployer bundleDeployer, String bsn,
			String bundleUrl) throws Exception {

		final long bundleId = bundleDeployer.deploy(bsn, bundleUrl);
		lfr.out().println("Installed or updated bundle " + bundleId);

		if (bundleId <= 0) {
			addError(
				"Deploy", "Unable to deploy bundle to framework " + bundleId);
		}
	}

	private void execute() throws Exception {
		int numOfFiles = options._arguments().size();

		for (int i = 0; i < numOfFiles; i++) {
			String bundlePath = options._arguments().get(i);

			File bundleFile = new File(bundlePath);

			if (!bundleFile.exists() && !bundleFile.isAbsolute()) {
				bundleFile = new File(lfr.getBase(), bundlePath);
			}

			if (bundleFile.exists()) {
				String bsn = null;

				try (Jar jar = new Jar(bundleFile)) {
					bsn = jar.getBsn();
				}

				if (bsn == null && bundleFile.getName().toLowerCase().endsWith(".war")) {
					bsn = guessBsnFromWar(bundleFile);
				}

				if (bsn == null) {
					addError("Deploy", "Unable to determine bsn for file " +
						bundleFile.getAbsolutePath());
				}

				final JMXBundleDeployer bundleDeployer = getBundleDeployer();

				String bundleUrl = getBundleUrl(bundleFile);

				if (bundleDeployer != null && bundleUrl != null) {
					deploy(bundleDeployer, bsn, bundleUrl);

					if (options.watch()) {
						watch(bundleDeployer, bsn, bundleFile);
					}
				}
			}
			else {
				addError("Deploy", "Unable to find specified bundle file " +
						bundleFile.getAbsolutePath());
			}
		}
	}

	private String guessBsnFromWar(File bundleFile) {
		return BSN_GUESS.matcher(bundleFile.getName()).replaceAll("")
				.replaceAll("\\.war$", "").replaceAll("-$", "");
	}

	private String getBundleUrl(File bundleFile) throws MalformedURLException {
		String bundleUrl = null;

		if (bundleFile.toPath().toString().toLowerCase().endsWith(".war")) {
			bundleUrl = "webbundle:"
					+ bundleFile.toURI().toURL().toExternalForm()
					+ "?Web-ContextPath=/" + guessBsnFromWar(bundleFile);
		}
		else {
			bundleUrl = bundleFile.toURI().toURL().toExternalForm();
		}

		return bundleUrl;
	}

	private JMXBundleDeployer getBundleDeployer() {
		if (_bundleDeployer == null) {
			int port = options.port();

			JMXBundleDeployer bundleDeployer = null;

			try {
				if (port > 0) {
					bundleDeployer = new JMXBundleDeployer(port);
				}
				else {
					bundleDeployer = new JMXBundleDeployer();
				}
			}
			catch (IllegalArgumentException e) {
				addError("Deploy", "Unable to connect to Liferay's OSGi framework");
			}

			_bundleDeployer = bundleDeployer;
		}

		return _bundleDeployer;
	}

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		options._command().help(f, this);
		lfr.out().println(f);
		f.close();
	}

	private void watch(final JMXBundleDeployer bundleDeployer, final String bsn,
			final File bundleFile) throws Exception {

		final boolean[] deploy = new boolean[1];

		new Thread() {
			@Override
			public void run() {
				synchronized (bundleFile) {
					while (true) {
						try {
							bundleFile.wait();
						} catch (InterruptedException e) {
						}

						while (deploy[0]) {
							deploy[0] = false;

							try {
								bundleFile.wait(300);
							} catch (InterruptedException e) {
							}
						}

						deploy[0] = false;

						try {
							String bundleUrl = getBundleUrl(bundleFile);

							long bundleId = bundleDeployer.deploy(
								bsn, bundleUrl);
							lfr.out().println("Installed or updated bundle " + bundleId);
						} catch (Exception e) {
						}
					}
				}
			}
		}.start();

		new FileWatcher(lfr.getBase().toPath(), bundleFile.getAbsoluteFile()
				.toPath(), true, new Runnable() {
			@Override
			public void run() {
				synchronized (bundleFile) {
					deploy[0] = true;
					bundleFile.notify();
				}
			}
		});
	}

}