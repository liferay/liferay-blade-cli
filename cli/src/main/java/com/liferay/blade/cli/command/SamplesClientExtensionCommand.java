/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Simon Jiang
 */
public class SamplesClientExtensionCommand extends BaseCommand<SamplesClientExtensionArgs> {

	public SamplesClientExtensionCommand() {
	}

	@Override
	public void execute() throws Exception {
		final SamplesClientExtensionArgs samplesClientExtensionArgs = getArgs();

		final String sampleArchiveName = _clientExtensionSampleName + ".zip";

		final String clientExtensionSampleUrl =
			"https://repository.liferay.com/nexus/service/local/artifact/maven/content?r=" +
				"liferay-public-releases&g=com.liferay.workspace&a=com.liferay.sample.workspace&v=LATEST&p=zip";

		Path sampleArchivePath = BladeUtil.downloadFile(
			clientExtensionSampleUrl, _getSamplesCachePath(), sampleArchiveName);

		_extractSamplesClientExtension(sampleArchivePath);

		boolean listAllClientExtension = samplesClientExtensionArgs.isListAllCientExtensions();

		if (listAllClientExtension) {
			_listSamples(_clientExtensionSampleName);

			return;
		}

		final String sampleName = samplesClientExtensionArgs.getSampleName();

		if (sampleName != null) {
			_copySample(sampleName, _clientExtensionSampleName);
		}
		else {
			_copySample(null, _clientExtensionSampleName);
		}
	}

	@Override
	public Class<SamplesClientExtensionArgs> getArgsClass() {
		return SamplesClientExtensionArgs.class;
	}

	private void _copySample(String sampleName, String samplesClientExtensionRepoName) throws Exception {
		SamplesClientExtensionArgs samplesClientExtensionArgs = getArgs();

		File workDir = samplesClientExtensionArgs.getDir();

		if (workDir == null) {
			workDir = new File(samplesClientExtensionArgs.getBase(), "client-extensions");
		}

		if (!workDir.exists()) {
			workDir.mkdir();
		}

		Path cachePath = _getSamplesCachePath();

		File samplesClientExtensionRepo = new File(
			cachePath.toFile(), samplesClientExtensionRepoName + "/client-extensions");

		for (File file : samplesClientExtensionRepo.listFiles()) {
			String fileName = file.getName();

			if (Files.isDirectory(file.toPath()) && (Objects.isNull(sampleName) || fileName.equals(sampleName))) {
				File dest = new File(workDir, fileName);

				FileUtil.copyDir(file.toPath(), dest.toPath());
			}
		}
	}

	private void _extractSamplesClientExtension(Path samplesArchivePath) throws Exception {
		Path samplesCachePath = _getSamplesCachePath();

		File samplesExtractDir = new File(samplesCachePath.toFile(), _clientExtensionSampleName);

		if (samplesExtractDir.exists()) {
			samplesExtractDir.delete();
		}

		FileUtil.unzip(samplesArchivePath.toFile(), samplesExtractDir, null);
	}

	private Path _getSamplesCachePath() throws Exception {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path samplesCachePath = userHomePath.resolve(".blade/cache/samples");

		if (!Files.exists(samplesCachePath)) {
			Files.createDirectories(samplesCachePath);
		}

		return samplesCachePath;
	}

	private void _listSamples(String samplesRepoName) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path cachePath = _getSamplesCachePath();

		File samplesClientExtensionRepo = new File(cachePath.toFile(), samplesRepoName + "/client-extensions");

		Map<String, List<Path>> samplesMap = new HashMap<>();

		for (File file : samplesClientExtensionRepo.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory()) {
				Files.walkFileTree(file.toPath(), new SamplesVisitor());

				List<Path> samplesList = samplesMap.get(fileName);

				if (samplesList == null) {
					samplesList = new ArrayList<>();

					samplesMap.put(fileName, samplesList);
				}

				if (samplesMap.containsKey(fileName)) {
					Collections.sort(samplesList);
				}
			}
		}

		bladeCLI.out(
			"Specify client extension sample to download (\"blade samples client-extensions sample-global-css\"). " +
				"Otherwise (\"blade samples client-extensions\") will download all available client extension " +
					"samples.\n");
		bladeCLI.out("Currently available samples:");

		Set<String> keySet = samplesMap.keySet();

		Stream<String> stream = keySet.stream();

		stream.sorted(
		).peek(
			category -> bladeCLI.out("\t " + category)
		).map(
			samplesMap::get
		).flatMap(
			category -> category.stream()
		).forEach(
			sample -> bladeCLI.out("\t\t " + sample)
		);
	}

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private final String _clientExtensionSampleName = "client-extension-samples";

}