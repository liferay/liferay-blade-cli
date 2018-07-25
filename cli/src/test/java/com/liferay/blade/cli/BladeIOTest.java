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

import java.util.Optional;
import java.util.Scanner;

/**
 * @author Christopher Bryan Boyd
 */
public class BladeIOTest extends BladeTest {

	public static BladeIOTest getBlade() {
		try {
			StringPrintStream outputStream = StringPrintStream.newInstance();

			StringPrintStream errorStream = StringPrintStream.newInstance();

			BladeIOTest test = new BladeIOTest(outputStream, errorStream);

			return test;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getError() {
		if (_error == null) {
			_error = Optional.ofNullable(_getErrorString(_errorStream.toString()));
		}

		return _error.orElse(null);
	}

	public String getOutput() {
		if (_output == null) {
			_output = Optional.ofNullable(_outputStream.toString());
		}

		return _output.orElse(null);
	}

	public boolean runBlade(String... args) {
		try {
			super.run(args);

			if (getError() != null) {
				return true;
			}

			return false;
		}
		catch (Exception e) {
			boolean errorMissing = false;

			if (_error == null) {
				errorMissing = true;
			}

			if (!errorMissing) {
				errorMissing = !_error.isPresent();
			}

			if (!errorMissing) {
				String errorMessage = _error.get();

				errorMessage = errorMessage.trim();

				errorMissing = errorMessage.length() == 0;
			}

			if (errorMissing) {
				_error = Optional.ofNullable(e.toString());
			}

			return true;
		}
	}

	private static String _getErrorString(String error) {
		StringBuilder sb = null;

		try (Scanner scanner = new Scanner(error)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.startsWith("SLF4J:")) {
					continue;
				}

				if (sb == null) {
					sb = new StringBuilder();

					sb.append("Encountered error at line: " + line + System.lineSeparator());
				}
				else {
					sb.append(line + System.lineSeparator());
				}
			}
		}

		if (sb == null) {
			return null;
		}

		return sb.toString();
	}

	private BladeIOTest(StringPrintStream out, StringPrintStream err) throws Exception {
		super(out, err);

		_outputStream = out;
		_errorStream = err;
	}

	private Optional<String> _error = null;
	private StringPrintStream _errorStream;
	private Optional<String> _output = null;
	private StringPrintStream _outputStream;

}