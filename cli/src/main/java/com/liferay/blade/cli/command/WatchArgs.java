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

import java.util.Arrays;
import java.util.List;

/**
 * @author David Truong
 */
@Parameters(
	commandDescription = "Watch for changes to workspace projects and automatically build, deploy, or update them into running Liferay instance.",
	commandNames = "watch"
)
public class WatchArgs extends BaseArgs {
	public List<String> getIgnores() {
		return _ignores;
	}

	public void setIgnores(List<String> ignores) {
		_ignores.addAll(ignores);
	}

	@Parameter(description = "Skip initial deploy", names = {"--no-init"})
	private boolean _init = true;

	public boolean isInit() {
		return _init;
	}

	public void setInit(boolean init) {
		_init = init;
	}

	@Parameter(description = "Ignored watch paths.", names = {"--ignores"})
	private List<String> _ignores = Arrays.asList(
		".gradle", ".idea", ".settings", "**/.sass-cache", "**/build", "**/dist", "**/liferay-theme.json", "**/node_modules",
		"**/liferay-npm-bundler-report.html", "**/target", "bundles", "gradle");

	@Parameter(description = "File extensions that will use deployFast instead of deploy.", names = {"--fast-extensions"})
	private List<String> _fastExtensions = Arrays.asList(
		"**/*.css", "**/*.js", "**/*.jsp", "**/*.map", "**/*.scss");

	public List<String> getFastExtensions() {
		return _fastExtensions;
	}

	public void setFastExtensions(List<String> fastExtensions) {
		_fastExtensions.addAll(fastExtensions);
	}
}