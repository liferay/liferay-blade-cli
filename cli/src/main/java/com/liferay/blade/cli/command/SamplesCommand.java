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
import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.FileUtil;

import java.io.File;
import java.io.IOException;

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
		BladeCLI bladeCLI = getBladeCLI();

		final SamplesArgs samplesArgs = getArgs();

		String liferayVersion = _getLiferayVersion(bladeCLI, samplesArgs);

		final String bladeRepoName = "liferay-blade-samples-" + liferayVersion;

		final String bladeRepoArchiveName = bladeRepoName + ".zip";

		final String bladeRepoUrl =
			"https://github.com/liferay/liferay-blade-samples/archive/" + liferayVersion + ".zip";

		final String sampleName = samplesArgs.getSampleName();

		if (_downloadBladeRepoIfNeeded(bladeRepoArchiveName, bladeRepoUrl)) {
			_extractBladeRepo(bladeRepoArchiveName);
		}

		if (sampleName == null) {
			_listSamples(bladeRepoName);
		}
		else {
			_copySample(sampleName, bladeRepoName);
		}
	}

	@Override
	public Class<SamplesArgs> getArgsClass() {
		return SamplesArgs.class;
	}

	private void _copySample(String sampleName, String bladeRepoName) throws Exception {
		SamplesArgs samplesArgs = getArgs();

		File workDir = samplesArgs.getDir();

		if (workDir == null) {
			workDir = samplesArgs.getBase();
		}

		Path cachePath = _getSamplesCachePath();

		File bladeRepo = new File(cachePath.toFile(), bladeRepoName);

		String buildType = samplesArgs.getProfileName();

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
					_updateBuildGradle(dest, bladeRepoName);
				}

				if (!BladeUtil.hasGradleWrapper(dest)) {
					BladeUtil.addGradleWrapper(dest);
				}
			}
		}
	}

	private String _deindent(String s) {
		return s.replaceAll("(?m)^\t", "");
	}

	private boolean _downloadBladeRepoIfNeeded(String bladeRepoArchiveName, String bladeRepoUrl) throws Exception {
		Path cachePath = _getSamplesCachePath();

		File bladeRepoArchive = new File(cachePath.toFile(), bladeRepoArchiveName);

		if (bladeRepoArchive.exists()) {
			Date now = new Date();

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
			BladeUtil.downloadLink(bladeRepoUrl, bladeRepoArchive.toPath());

			return true;
		}

		return false;
	}

	private void _extractBladeRepo(String bladeRepoArchiveName) throws Exception {
		Path samplesCachePath = _getSamplesCachePath();

		File bladeRepoArchive = new File(samplesCachePath.toFile(), bladeRepoArchiveName);

		FileUtil.unzip(bladeRepoArchive, samplesCachePath.toFile(), null);
	}

	private String _getLiferayVersion(BladeCLI bladeCLI, SamplesArgs samplesArgs) throws IOException {
		String liferayVersion = samplesArgs.getLiferayVersion();

		if (liferayVersion == null) {
			BladeSettings bladeSettings = bladeCLI.getBladeSettings();

			liferayVersion = bladeSettings.getLiferayVersionDefault();
		}

		return liferayVersion;
	}

	private Path _getSamplesCachePath() throws IOException {
		Path userHomePath = _USER_HOME_DIR.toPath();

		Path samplesCachePath = userHomePath.resolve(".blade/cache/samples");

		if (!Files.exists(samplesCachePath)) {
			Files.createDirectories(samplesCachePath);
		}

		return samplesCachePath;
	}

	private void _listSamples(String bladeRepoName) throws IOException {
		BladeCLI bladeCLI = getBladeCLI();
		SamplesArgs samplesArgs = getArgs();

		Path cachePath = _getSamplesCachePath();

		File bladeRepo = new File(cachePath.toFile(), bladeRepoName);

		String buildType = samplesArgs.getProfileName();

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

		if (begin == -1) {
			return script;
		}

		int end = begin;

		int count = 0;

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

	private void _updateBuildGradle(File dir, String bladeRepoName) throws Exception {
		Path cachePath = _getSamplesCachePath();

		File bladeRepo = new File(cachePath.toFile(), bladeRepoName);

		File sampleGradleFile = new File(dir, "build.gradle");

		String script = BladeUtil.read(sampleGradleFile);

		BladeCLI bladeCLI = getBladeCLI();

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(dir);

		if (workspaceProvider == null) {
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

	private static final File _USER_HOME_DIR = new File(System.getProperty("user.home"));

	private static final Collection<String> _topLevelFolders = Arrays.asList(
		"apps", "extensions", "overrides", "themes");

}