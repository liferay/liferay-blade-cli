package com.liferay.blade.cli;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by dtruong on 1/13/16.
 */
public class SampleCommand {
	private static final String _BLADE_REPO_URL = "https://github.com/rotty3000/blade/archive/master.zip";

	private static final String _BLADE_REPO_ARCHIVE_NAME = "blade-master.zip";

	private static final String _BLADE_REPO_NAME = "blade-master";

	private static final long _FILE_EXPIRATION_TIME = 604800000;

	public SampleCommand(blade blade, SampleOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String sampleName = args.size() > 0 ? args.get(0) : null;

		if (_downloadBladeRepo()) {
			_extractBladeRepo();
		}

		if (sampleName == null) {
			_listSamples();
		}
		else {
			_copySample(sampleName);
		}
	}

	private void _copySample(String sampleName) throws Exception {
		File bladeRepo =
			new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File liferayGradleSamples = new File(bladeRepo, "liferay-gradle");

		for (File file : liferayGradleSamples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && fileName.equals(sampleName)) {
				File dest = new File(System.getProperty("user.dir"), fileName);

				FileUtils.copyDirectory(file, dest);
			}
		}
	}

	private boolean _downloadBladeRepo() throws Exception {
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

	private void _extractBladeRepo() throws Exception {
		File bladeRepoArchive =
			new File(_blade.getCacheDir(), _BLADE_REPO_ARCHIVE_NAME);

		byte[] buffer = new byte[1024];

		try (ZipInputStream zis =
				 new ZipInputStream(new FileInputStream(bladeRepoArchive))) {
			ZipEntry ze = zis.getNextEntry();

			while( ze != null) {
				String fileName = ze.getName();

				File newFile = new File(_blade.getCacheDir() + File.separator + fileName);

				if (ze.isDirectory()) {
					newFile.mkdir();
				}
				else {
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;

					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
				}

				ze = zis.getNextEntry();
			}
		}
	}

	private void _listSamples() {
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

		_blade.out().println("Please provide the sample name you would like (blade samples blade.rest)");
		_blade.out().println(StringUtils.join(samples, ","));
	}

	final private blade _blade;

	final private SampleOptions _options;
}
