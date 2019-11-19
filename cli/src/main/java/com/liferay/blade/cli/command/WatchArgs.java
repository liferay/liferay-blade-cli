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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Watch for changes to workspace projects and automatically build, deploy, or update them into running Liferay instance.",
	commandNames = "watch"
)
public class WatchArgs extends BaseArgs {

	public List<String> getFastPaths() {
		return Stream.concat(
			_defaultFastPaths.stream(), _fastPaths.stream()
		).collect(
			Collectors.toList()
		);
	}

	public List<String> getIgnorePaths() {
		return Stream.concat(
			_defaultIgnorePaths.stream(), _ignorePaths.stream()
		).collect(
			Collectors.toList()
		);
	}

	public List<String> getProjectPaths() {
		return Stream.concat(
			_defaultProjectPaths.stream(), _projectPaths.stream()
		).collect(
			Collectors.toList()
		);
	}

	public boolean isSkipInit() {
		return _skipInit;
	}

	public void setFastPaths(List<String> fastPaths) {
		_fastPaths.addAll(fastPaths);
	}

	public void setIgnorePaths(List<String> ignorePaths) {
		_ignorePaths.addAll(ignorePaths);
	}

	public void setProjectPaths(List<String> projectPaths) {
		_projectPaths.addAll(projectPaths);
	}

	public void setSkipInit(boolean skipInit) {
		_skipInit = skipInit;
	}

	private static final List<String> _defaultFastPaths = Arrays.asList(
		"**/*.css", "**/*.js", "**/*.jsp", "**/*.map", "**/*.scss");
	private static final List<String> _defaultIgnorePaths = Arrays.asList(
		".gradle", ".idea", ".settings", "**/.sass-cache", "**/bin", "**/build", "**/classes", "**/dist",
		"**/liferay-theme.json", "**/node_modules", "**/liferay-npm-bundler-report.html", "**/target", "bundles",
		"gradle");
	private static final List<String> _defaultProjectPaths = Arrays.asList("src");

	@Parameter(description = "File paths that will use deployFast instead of deploy.", names = {"-f", "--fast-paths"})
	private List<String> _fastPaths = new ArrayList<>();

	@Parameter(description = "Ignored watch paths.", names = {"-i", "--ignore-paths"})
	private List<String> _ignorePaths = new ArrayList<>();

	@Parameter(description = "File paths to use to identify a project path.", names = {"-p", "--project-paths"})
	private List<String> _projectPaths = new ArrayList<>();

	@Parameter(description = "Skip initial deploy", names = {"-s", "--skip-init"})
	private boolean _skipInit = false;

}