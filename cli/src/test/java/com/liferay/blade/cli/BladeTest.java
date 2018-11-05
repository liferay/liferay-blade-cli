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

import com.liferay.blade.cli.util.WorkspaceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Gregory Amerson
 */
public class BladeTest extends BladeCLI {

	public BladeTest() throws Exception {
		super(StringPrintStream.newInstance(), StringPrintStream.newInstance(), System.in);

		_userHomeDir = new File(System.getProperty("user.home"));
	}

	public BladeTest(boolean assertErrors) throws Exception {
		this();

		_assertErrors = assertErrors;
	}

	public BladeTest(File userHomeDir) throws Exception {
		this();

		_userHomeDir = userHomeDir;
	}

	public BladeTest(PrintStream ps) {
		this(ps, ps, null);
	}

	public BladeTest(PrintStream ps, InputStream in) {
		this(ps, ps, in);
	}

	public BladeTest(PrintStream outputStream, PrintStream errorStream) {
		this(outputStream, errorStream, System.in);
	}

	public BladeTest(PrintStream outputStream, PrintStream errorStream, InputStream in) {
		this(outputStream, errorStream, in, new File(System.getProperty("user.home")), true);
	}

	public BladeTest(PrintStream out, PrintStream err, InputStream in, File userHomeDir) {
		this(out, err, in, userHomeDir, true);
	}

	public BladeTest(PrintStream out, PrintStream err, InputStream in, File userHomeDir, boolean assertErrors) {
		super(out, err, in);

		_userHomeDir = userHomeDir;
		_assertErrors = assertErrors;
	}

	@Override
	public BladeSettings getBladeSettings() throws IOException {
		final File settingsFile;

		if (WorkspaceUtil.isWorkspace(this)) {
			File workspaceDir = WorkspaceUtil.getWorkspaceDir(this);

			settingsFile = new File(workspaceDir, ".blade/settings.properties");
		}
		else {
			settingsFile = new File(_userHomeDir, ".blade/settings.properties");
		}

		return new BladeSettings(settingsFile);
	}

	@Override
	public Path getCachePath() {
		Path userHomePath = _userHomeDir.toPath();

		Path cachePath = userHomePath.resolve(".blade/cache");

		try {
			Files.createDirectories(cachePath);
		}
		catch (IOException ioe) {
		}

		return cachePath;
	}

	@Override
	public Path getExtensionsPath() {
		Path userHomePath = _userHomeDir.toPath();

		Path extensionsPath = userHomePath.resolve(".blade/extensions");

		try {
			Files.createDirectories(extensionsPath);
		}
		catch (IOException ioe) {
		}

		return extensionsPath;
	}

	@Override
	public void run(String[] args) throws Exception {
		super.run(args);

		if (_assertErrors) {
			PrintStream err = err();

			if (err instanceof StringPrintStream) {
				StringPrintStream stringPrintStream = (StringPrintStream)err;

				String errors = stringPrintStream.get();

				errors = errors.replaceAll(
					"sh: warning: setlocale: LC_ALL: cannot change locale \\(en_US.UTF-8\\)", "");

				errors = errors.trim();

				if (!errors.isEmpty()) {
					throw new Exception("Errors not empty:\n" + errors);
				}
			}
		}
	}

	private boolean _assertErrors = true;
	private File _userHomeDir;

}