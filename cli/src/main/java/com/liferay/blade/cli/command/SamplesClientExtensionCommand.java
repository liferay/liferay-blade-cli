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

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

		final String clientExtensionSampleArchiveName = _clientExtensionSampleName + ".zip";

		final String clientExtensionSampleUrl =
			"https://repository.liferay.com/nexus/service/local/artifact/maven/content?r=" +
				"liferay-public-releases&g=com.liferay.workspace&a=com.sample.workspace&v=LATEST&p=zip";

		if (_downloadClientExtensionSampleIfNeeded(clientExtensionSampleArchiveName, clientExtensionSampleUrl)) {
			_extractSamplesClientExtensionRepo(clientExtensionSampleArchiveName);
		}

		boolean listAllCientExtension = samplesClientExtensionArgs.isListAllCientExtensions();

		if (listAllCientExtension) {
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
			workDir = samplesClientExtensionArgs.getBase();
		}

		File clientExtensionDir = new File(workDir, "client-extensions");

		if (!FileUtil.exists(clientExtensionDir.getAbsolutePath())) {
			clientExtensionDir.mkdir();
		}

		Path cachePath = _getSamplesCachePath();

		File samplesClientExtensionRepo = new File(
			cachePath.toFile(), samplesClientExtensionRepoName + "/client-extensions");

		for (File file : samplesClientExtensionRepo.listFiles()) {
			String fileName = file.getName();

			if (Files.isDirectory(file.toPath()) && (Objects.isNull(sampleName) || fileName.equals(sampleName))) {
				File dest = new File(clientExtensionDir, fileName);

				FileUtil.copyDir(file.toPath(), dest.toPath());
			}
		}
	}

	private boolean _downloadClientExtensionSampleIfNeeded(
			String clientExtensionSampleArchiveName, String clientExtensionSampleUrl)
		throws Exception {

		Path cachePath = _getSamplesCachePath();

		File clientExtensionSampleArchive = new File(cachePath.toFile(), clientExtensionSampleArchiveName);

		if (clientExtensionSampleArchive.exists()) {
			Date now = new Date();

			long diff = now.getTime() - clientExtensionSampleArchive.lastModified();

			boolean old = false;

			if (diff > _FILE_EXPIRATION_TIME) {
				old = true;
			}

			if (old || !BladeUtil.isZipValid(clientExtensionSampleArchive)) {
				clientExtensionSampleArchive.delete();
			}
		}

		if (!clientExtensionSampleArchive.exists()) {
			BladeUtil.downloadLink(clientExtensionSampleUrl, cachePath.toFile(), clientExtensionSampleArchive.toPath());

			return true;
		}

		return false;
	}

	private void _extractSamplesClientExtensionRepo(String samplesClientExtensionArchiveName) throws Exception {
		Path samplesCachePath = _getSamplesCachePath();

		File samplesClientExtensionArchive = new File(samplesCachePath.toFile(), samplesClientExtensionArchiveName);

		FileUtil.unzip(
			samplesClientExtensionArchive, new File(samplesCachePath.toFile(), _clientExtensionSampleName), null);
	}

	private Path _getSamplesCachePath() throws Exception {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path samplesCachePath = userHomePath.resolve(".blade/cache/samples");

		if (!Files.exists(samplesCachePath)) {
			Files.createDirectories(samplesCachePath);
		}

		return samplesCachePath;
	}

	private void _listSamples(String samplesClientExtensionRepoName) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path cachePath = _getSamplesCachePath();

		File samplesClientExtensionRepo = new File(
			cachePath.toFile(), samplesClientExtensionRepoName + "/client-extensions");

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
			"Specify client extension sample to download (\"blade samples client-extensions " +
				"sample-custom-element-1\"). Otherwise (\"blade samples client-extensions\") will download all " +
					"available client extension samples.\n");
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

	private static final long _FILE_EXPIRATION_TIME = 604800000;

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private final String _clientExtensionSampleName = "client-extension-samples";

}