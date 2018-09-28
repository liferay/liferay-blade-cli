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
import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author David Truong
 */
public class SamplesCommand extends BaseCommand<SamplesArgs> {

	public SamplesCommand() {
	}

	@Override
	public void execute() throws Exception {
		SamplesArgs samplesArgs = getArgs();

		final String liferayVersion = samplesArgs.getLiferayVersion();

		_setBladeRepoName(liferayVersion);
		_setBladeRepoArchiveName(liferayVersion);
		_setBladeRepoUrl(liferayVersion);

		final String sampleName = samplesArgs.getSampleName();

		if (_downloadBladeRepoIfNeeded()) {
			_extractBladeRepo();
		}

		if (sampleName == null) {
			_listSamples();
		}
		else {
			_copySample(sampleName);
		}
	}

	@Override
	public Class<SamplesArgs> getArgsClass() {
		return SamplesArgs.class;
	}

	private void _addGradleWrapper(File dest) throws Exception {
		InputStream in = SamplesCommand.class.getResourceAsStream("/wrapper.zip");

		BladeUtil.copy(in, dest);

		new File(dest, "gradlew").setExecutable(true);
	}

	private void _copySample(String sampleName) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		SamplesArgs samplesArgs = getArgs();

		File workDir = samplesArgs.getDir();

		if (workDir == null) {
			workDir = new File(samplesArgs.getBase());
		}

		Path cachePath = bladeCLI.getCachePath();

		File bladeRepo = new File(cachePath.toFile(), _bladeRepoName);

		String buildType = samplesArgs.getBuild();

		File samples = new File(bladeRepo, buildType);

		SamplesVisitor visitor = new SamplesVisitor();

		for (File file : samples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && _topLevelFolders.contains(fileName)) {
				Files.walkFileTree(file.toPath(), visitor);
			}
		}

		for (Path path : visitor.getPaths()) {
			File file = path.toFile();

			String fileName = file.getName();

			if (Files.isDirectory(path) && fileName.equals(sampleName)) {
				File dest = new File(workDir, fileName);

				FileUtil.copyDir(path, dest.toPath());

				if (buildType.equals("gradle")) {
					_updateBuildGradle(dest);
				}

				if (!BladeUtil.hasGradleWrapper(dest)) {
					_addGradleWrapper(dest);
				}
			}
		}
	}

	private String _deindent(String s) {
		return s.replaceAll("(?m)^\t", "");
	}

	private boolean _downloadBladeRepoIfNeeded() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path cachePath = bladeCLI.getCachePath();

		File bladeRepoArchive = new File(cachePath.toFile(), _bladeRepoArchiveName);

		Date now = new Date();

		if (bladeRepoArchive.exists()) {
			long diff = now.getTime() - bladeRepoArchive.lastModified();

			boolean old = false;

			if (diff > _FILE_EXPIRATION_TIME) {
				old = true;
			}

			if (old || !BladeUtil.isZipValid(bladeRepoArchive)) {
				bladeRepoArchive.delete();
			}
		}

		if (!bladeRepoArchive.exists()) {
			BladeUtil.downloadLink(_bladeRepoUrl, bladeRepoArchive.toPath());

			return true;
		}

		return false;
	}

	private void _extractBladeRepo() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path cachePath = bladeCLI.getCachePath();

		File bladeRepoArchive = new File(cachePath.toFile(), _bladeRepoArchiveName);

		BladeUtil.unzip(bladeRepoArchive, cachePath.toFile(), null);
	}

	private void _listSamples() throws IOException {
		BladeCLI bladeCLI = getBladeCLI();
		SamplesArgs samplesArgs = getArgs();

		Path cachePath = bladeCLI.getCachePath();

		File bladeRepo = new File(cachePath.toFile(), _bladeRepoName);

		String buildType = samplesArgs.getBuild();

		File samples = new File(bladeRepo, buildType);

		Map<String, List<Path>> samplesMap = new HashMap<>();

		for (File file : samples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && _topLevelFolders.contains(fileName)) {
				SamplesVisitor visitor = new SamplesVisitor();

				Files.walkFileTree(file.toPath(), visitor);

				List<Path> samplesList = samplesMap.get(fileName);

				if (samplesList == null) {
					samplesList = new ArrayList<>();

					samplesMap.put(fileName, samplesList);
				}

				for (Path path : visitor.getPaths()) {
					samplesList.add(path.getFileName());
				}

				if (samplesMap.containsKey(fileName)) {
					Collections.sort(samplesList);
				}
			}
		}

		bladeCLI.out("Please provide the sample project name to create, e.g. \"blade samples jsp-portlet\"\n");
		bladeCLI.out("Currently available categories and samples:");

		Set<String> keySet = samplesMap.keySet();

		Stream<String> stream = keySet.stream();

		stream.sorted(
		).peek(
			category -> bladeCLI.out("\t " + category + ":")
		).map(
			samplesMap::get
		).flatMap(
			category -> category.stream()
		).forEach(
			sample -> bladeCLI.out("\t\t " + sample)
		);
	}

	private String _parseGradleScript(String script, String section, boolean contentsOnly) {
		int begin = script.indexOf(section + " {");

		int end = begin;

		int count = 0;

		if (contentsOnly) {
			begin += section.length() + 2;
		}

		while (true) {
			char c = script.charAt(end);

			if ((count != 0) && (c == '}')) {
				count--;
			}
			else if (c == '{') {
				count++;
			}

			if ((count == 0) && (c == '}')) {
				if (!contentsOnly) {
					end++;
				}

				break;
			}

			end++;
		}

		String newScript = script.substring(begin, end);

		if (contentsOnly) {
			return _deindent(newScript);
		}

		return newScript;
	}

	private String _removeGradleSection(String script, String section) {
		int begin = script.indexOf(section + " {");

		int end = begin;

		int count = 0;

		if (begin == -1) {
			return script;
		}

		while (true) {
			char c = script.charAt(end);

			if ((count != 0) && (c == '}')) {
				count--;
			}
			else if (c == '{') {
				count++;
			}

			end++;

			if ((count == 0) && (c == '}')) {
				break;
			}
		}

		return _removeGradleSection(script.substring(0, begin) + script.substring(end, script.length()), section);
	}

	private void _setBladeRepoArchiveName(String liferayVersion) {
		_bladeRepoArchiveName = _bladeRepoName + ".zip";
	}

	private void _setBladeRepoName(String liferayVersion) {
		_bladeRepoName = "liferay-blade-samples-" + liferayVersion;
	}

	private void _setBladeRepoUrl(String liferayVersion) {
		_bladeRepoUrl = "https://github.com/liferay/liferay-blade-samples/archive/" + liferayVersion + ".zip";
	}

	private void _updateBuildGradle(File dir) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Path cachePath = bladeCLI.getCachePath();

		File bladeRepo = new File(cachePath.toFile(), _bladeRepoName);

		File sampleGradleFile = new File(dir, "build.gradle");

		String script = BladeUtil.read(sampleGradleFile);

		if (!WorkspaceUtil.isWorkspace(dir)) {
			File parentBuildGradleFile = new File(bladeRepo, "gradle/build.gradle");

			String parentBuildScript = _parseGradleScript(BladeUtil.read(parentBuildGradleFile), "buildscript", false);

			String parentSubprojectsScript = _parseGradleScript(
				BladeUtil.read(parentBuildGradleFile), "subprojects", true);

			parentSubprojectsScript = _removeGradleSection(parentSubprojectsScript, "buildscript");

			System.out.println(parentSubprojectsScript);

			script = parentBuildScript + parentSubprojectsScript + script;
		}

		Files.write(sampleGradleFile.toPath(), script.getBytes());
	}

	private static final long _FILE_EXPIRATION_TIME = 604800000;

	private static final Collection<String> _topLevelFolders = Arrays.asList(
		"apps", "extensions", "overrides", "themes");

	private String _bladeRepoArchiveName;
	private String _bladeRepoName;
	private String _bladeRepoUrl;

}