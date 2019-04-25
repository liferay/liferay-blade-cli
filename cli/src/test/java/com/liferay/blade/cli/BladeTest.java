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

import com.liferay.blade.cli.command.BaseArgs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Scanner;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 */
public class BladeTest extends BladeCLI {

	public static BladeTestBuilder builder() {
		return new BladeTestBuilder();
	}

	public BladeSettings getBladeSettings() throws IOException {
		File settingsBaseDir = _getSettingsBaseDir();

		File settingsFile = new File(settingsBaseDir, ".blade/settings.properties");

		if (settingsFile.exists()) {
			String name = settingsFile.getName();

			if ("settings.properties".equals(name)) {
				_migrateBladeSettingsFile(settingsFile);
			}
		}

		settingsFile = new File(settingsBaseDir, _BLADE_PROPERTIES);

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

				
				errors = errors.trim();
				
				StringBuilder sb = new StringBuilder();

				try (Scanner scanner = new Scanner(errors)) {
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();

						if ((line != null) && (line.length() > 0)) {
							if (line.startsWith("SLF4J:")) {
								continue;
							}

							if (line.startsWith("Picked up JAVA_TOOL_OPTIONS")) {
								continue;
							}
							

							if (line.contains("LC_ALL: cannot change locale")) {
								continue;
							}
							
							sb.append(line);
							

							if (scanner.hasNextLine()) {
								sb.append(System.lineSeparator());
							}
						}
					}
				}
				
				errors = sb.toString();

				errors = errors.replaceAll("^\\/bin\\/$", "");

				if (!errors.isEmpty()) {
					throw new Exception("\nErrors not empty:\n" + errors);
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

	private File _getSettingsBaseDir() {
		BaseArgs args = getArgs();

		File baseDir = new File(args.getBase());

		File settingsBaseDir;

		WorkspaceProvider workspaceProvider = getWorkspaceProvider(baseDir);

		if (workspaceProvider != null) {
			settingsBaseDir = workspaceProvider.getWorkspaceDir(baseDir);
		}
		else {
			settingsBaseDir = _settingsDir.toFile();
		}

		return settingsBaseDir;
	}

	private void _migrateBladeSettingsFile(File settingsFile) throws IOException {
		Path settingsPath = settingsFile.toPath();

		Path settingsParentPath = settingsPath.getParent();

		if (settingsParentPath.endsWith(".blade")) {
			Path settingsParentParentPath = settingsParentPath.getParent();

			Path newSettingsPath = settingsParentParentPath.resolve(_BLADE_PROPERTIES);

			Files.move(settingsPath, newSettingsPath);

			try (Stream<?> filesStream = Files.list(settingsParentPath)) {
				if (filesStream.count() == 0) {
					Files.delete(settingsParentPath);
				}
			}
		}
	}

	private static final String _BLADE_PROPERTIES = ".blade.properties";

	private boolean _assertErrors = true;
	private Path _extensionsDir;
	private Path _settingsDir;

}