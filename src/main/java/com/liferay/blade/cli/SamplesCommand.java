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

package com.liferay.blade.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

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

import org.apache.commons.io.FileUtils;

/**
 * @author David Truong
 */
public class SamplesCommand {

	public SamplesCommand(BladeCLI blade, SamplesCommandArgs options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final String sampleName = _options.getSampleName();

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

	private void _addGradleWrapper(File dest) throws Exception {
		InputStream in = SamplesCommand.class.getResourceAsStream("/wrapper.zip");

		Util.copy(in, dest);

		new File(dest, "gradlew").setExecutable(true);
	}

	private void _copySample(String sampleName) throws Exception {
		File workDir = _options.getDir();

		if (workDir == null) {
			workDir = _blade.getBase();
		}

		File bladeRepo = new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File gradleSamples = new File(bladeRepo, "gradle");
		
		SamplesVisitor visitor = new SamplesVisitor();
		for (File file : gradleSamples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && _TOP_LEVEL_FOLDERS.contains(fileName)) {
				Files.walkFileTree(file.toPath(), visitor);
			}
		}
		
		for (Path path : visitor.getPaths()) {
			String fileName = path.getFileName().toString();
			if (Files.isDirectory(path) && fileName.equals(sampleName)) {
				File dest = new File(workDir, fileName);

				FileUtils.copyDirectory(path.toFile(), dest);
				
				_updateBuildGradle(dest);

				if (!Util.hasGradleWrapper(dest)) {
					_addGradleWrapper(dest);
				}
			}
		}
	}

	private String _deindent(String s) {
		return s.replaceAll("(?m)^\t", "");
	}

	private boolean _downloadBladeRepoIfNeeded() throws Exception {
		File bladeRepoArchive = new File(_blade.getCacheDir(), _BLADE_REPO_ARCHIVE_NAME);

		Date now = new Date();

		long diff = now.getTime() - bladeRepoArchive.lastModified();

		if (!bladeRepoArchive.exists() || (diff > _FILE_EXPIRATION_TIME)) {
			FileUtils.copyURLToFile(new URL(_BLADE_REPO_URL), bladeRepoArchive);

			return true;
		}

		return false;
	}

	private void _extractBladeRepo() throws Exception {
		File bladeRepoArchive = new File(_blade.getCacheDir(), _BLADE_REPO_ARCHIVE_NAME);

		Util.unzip(bladeRepoArchive, _blade.getCacheDir(), null);
	}

	private void _listSamples() throws IOException {
		File bladeRepo = new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File gradleSamples = new File(bladeRepo, "gradle");

		Map<String, List<String>> samplesMap = new HashMap<>();
		SamplesVisitor visitor = new SamplesVisitor();
		for (File file : gradleSamples.listFiles()) {
			String fileName = file.getName();

			if (file.isDirectory() && _TOP_LEVEL_FOLDERS.contains(fileName)) {
				Files.walkFileTree(file.toPath(), visitor);
				for (Path path : visitor.getPaths()) {
					if (!samplesMap.containsKey(fileName)) {
						samplesMap.put(fileName, new ArrayList<>());
					}
					samplesMap.get(fileName).add(path.getFileName().toString());
				}
				visitor.clear();
				if (samplesMap.containsKey(fileName)) {
					Collections.sort(samplesMap.get(fileName));
				}
			}
		}

		_blade.out().println("Please provide the sample project name to create, e.g. \"blade samples blade.rest\"\n");
		_blade.out().println("Currently available categories and samples:");
		
		for (String category : samplesMap.keySet()) {
			_blade.out().println("\t " + category + ":");
			for (String sample : samplesMap.get(category)) {
				_blade.out().println("\t\t " + sample);
			}
		}
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

	private void _updateBuildGradle(File dir) throws Exception {
		File bladeRepo = new File(_blade.getCacheDir(), _BLADE_REPO_NAME);

		File sampleGradleFile = new File(dir, "build.gradle");

		String script = Util.read(sampleGradleFile);

		if (!Util.isWorkspace(dir)) {
			File parentBuildGradleFile = new File(bladeRepo, "gradle/build.gradle");

			String parentBuildScript = _parseGradleScript(Util.read(parentBuildGradleFile), "buildscript", false);

			String parentSubprojectsScript = _parseGradleScript(Util.read(parentBuildGradleFile), "subprojects", true);

			parentSubprojectsScript = _removeGradleSection(parentSubprojectsScript, "buildscript");

			System.out.println(parentSubprojectsScript);

			script = parentBuildScript + parentSubprojectsScript + script;
		}

		Files.write(sampleGradleFile.toPath(), script.getBytes());
	}

	private static final String _BLADE_REPO_ARCHIVE_NAME = "liferay-blade-samples-master.zip";

	private static final String _BLADE_REPO_NAME = "liferay-blade-samples-master";

	private static final String _BLADE_REPO_URL = "https://github.com/liferay/liferay-blade-samples/archive/master.zip";

	private static final long _FILE_EXPIRATION_TIME = 604800000;
	
	private static final Collection<String> _TOP_LEVEL_FOLDERS = Arrays.asList("apps", "extensions", "overrides", "themes");

	private final BladeCLI _blade;
	private final SamplesCommandArgs _options;

}