package com.liferay.blade.cli;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author David Truong
 */
public class SamplesCommand {
	private static final String _BLADE_REPO_URL = "https://github.com/rotty3000/blade/archive/master.zip";

	private static final String _BLADE_REPO_ARCHIVE_NAME = "blade-master.zip";

	private static final String _BLADE_REPO_NAME = "blade-master";

	private static final long _FILE_EXPIRATION_TIME = 604800000;

	public SamplesCommand(blade blade, SamplesOptions options)
		throws Exception {

		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String sampleName = args.size() > 0 ? args.get(0) : null;

		if (downloadBladeRepoIfNeeded()) {
			extractBladeRepo();
		}

		if (sampleName == null) {
			listSamples();
		}
		else {
			copySample(sampleName);
		}
	}

	private void copySample(String sampleName) throws Exception {
		File workDir = _options.dir();

		if (workDir == null) {
			workDir = _blade.getBase();
		}

		File bladeRepo =
			new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File liferayGradleSamples = new File(bladeRepo, "liferay-gradle");

		for (File file : liferayGradleSamples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && fileName.equals(sampleName)) {
				File dest = new File(workDir, fileName);

				FileUtils.copyDirectory(file, dest);
			}
		}
	}

	private boolean downloadBladeRepoIfNeeded() throws Exception {
		File bladeRepoArchive =
			new File(_blade.getCacheDir(), _BLADE_REPO_ARCHIVE_NAME);

		Date now = new Date();

		long diff = now.getTime() - bladeRepoArchive.lastModified();

		if (!bladeRepoArchive.exists() || (diff > _FILE_EXPIRATION_TIME)) {
			FileUtils.copyURLToFile(new URL(_BLADE_REPO_URL), bladeRepoArchive);

			return true;
		}

		return false;
	}

	private void extractBladeRepo() throws Exception {
		File bladeRepoArchive =
			new File(_blade.getCacheDir(), _BLADE_REPO_ARCHIVE_NAME);

		Util.unzip( bladeRepoArchive, _blade.getCacheDir(), null);
	}

	private void listSamples() {
		File bladeRepo =
			new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File liferayGradleSamples = new File(bladeRepo, "liferay-gradle");

		List<String> samples = new ArrayList<>();

		for (File file : liferayGradleSamples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && fileName.startsWith("blade.")) {
				samples.add(fileName);
			}
		}

		_blade.out().println(
			"Please provide the sample project name to create, " +
			"e.g. \"blade samples blade.rest\"\n");
		_blade.out().println("Currently available samples:");
		_blade.out().println(
			WordUtils.wrap(StringUtils.join(samples, ", "), 80));
	}

	final private blade _blade;

	final private SamplesOptions _options;
}
