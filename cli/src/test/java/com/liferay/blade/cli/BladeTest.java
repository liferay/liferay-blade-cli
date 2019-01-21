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
import java.nio.file.Paths;

/**
 * @author Gregory Amerson
 */
public class BladeTest extends BladeCLI {

	public static BladeTestBuilder builder() {
		return new BladeTestBuilder();
	}

	@Override
	public BladeSettings getBladeSettings() throws IOException {
		final File settingsFile;

		if (WorkspaceUtil.isWorkspace(this)) {
			File workspaceDir = WorkspaceUtil.getWorkspaceDir(this);

			settingsFile = new File(workspaceDir, ".blade/settings.properties");
		}
		else {
			Path settingsPath = _settingsDir.resolve("settings.properties");

			settingsFile = settingsPath.toFile();
		}

		return new BladeSettings(settingsFile);
	}

	@Override
	public Path getExtensionsPath() {
		try {
			Files.createDirectories(_extensionsDir);
		}
		catch (IOException ioe) {
		}

		return _extensionsDir;
	}

	@Override
	public void postRunCommand() {
	}

	@Override
	public void run(String[] args) throws Exception {
		super.run(args);

		if (_assertErrors) {
			PrintStream error = error();

			if (error instanceof StringPrintStream) {
				StringPrintStream stringPrintStream = (StringPrintStream)error;

				String errors = stringPrintStream.get();

				errors = errors.replaceAll(
					"sh: warning: setlocale: LC_ALL: cannot change locale \\(en_US.UTF-8\\)", "");

				errors = errors.trim();

				errors = errors.replaceAll("^\\/bin\\/$", "");

				if (!errors.isEmpty()) {
					throw new Exception("Errors not empty:\n" + errors);
				}
			}
		}
	}

	public static class BladeTestBuilder {

		public BladeTest build() {
			if (_extensionsDir == null) {
				_extensionsDir = _userHomePath.resolve(".blade/extensions");
			}

			if (_settingsDir == null) {
				_settingsDir = _userHomePath.resolve(".blade");
			}

			if (_stdIn == null) {
				_stdIn = System.in;
			}

			if (_stdOut == null) {
				_stdOut = StringPrintStream.newInstance();
			}

			if (_stdError == null) {
				_stdError = StringPrintStream.newInstance();
			}

			BladeTest bladeTest = new BladeTest(_stdOut, _stdError, _stdIn);

			bladeTest._assertErrors = _assertErrors;
			bladeTest._extensionsDir = _extensionsDir;
			bladeTest._settingsDir = _settingsDir;

			return bladeTest;
		}

		public void setAssertErrors(boolean assertErrors) {
			_assertErrors = assertErrors;
		}

		public void setExtensionsDir(Path extensionsDir) {
			_extensionsDir = extensionsDir;
		}

		public void setSettingsDir(Path settingsDir) {
			_settingsDir = settingsDir;
		}

		public void setStdError(PrintStream printStream) {
			_stdError = printStream;
		}

		public void setStdIn(InputStream inputStream) {
			_stdIn = inputStream;
		}

		public void setStdOut(PrintStream printStream) {
			_stdOut = printStream;
		}

		private boolean _assertErrors = true;
		private Path _extensionsDir = null;
		private Path _settingsDir = null;
		private PrintStream _stdError = null;
		private InputStream _stdIn = null;
		private PrintStream _stdOut = null;
		private Path _userHomePath = Paths.get(System.getProperty("user.home"));

	}

	protected BladeTest(PrintStream out, PrintStream err, InputStream in) {
		super(out, err, in);
	}

	private boolean _assertErrors = true;
	private Path _extensionsDir;
	private Path _settingsDir;

}