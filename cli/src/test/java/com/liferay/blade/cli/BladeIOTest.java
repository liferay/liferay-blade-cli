/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

			return new BladeIOTest(outputStream, errorStream);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
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
		catch (Exception exception) {
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
				_error = Optional.ofNullable(exception.toString());
			}

			return true;
		}
	}

	private BladeIOTest(StringPrintStream out, StringPrintStream err) throws Exception {
		super(out, err, System.in);

		_outputStream = out;
		_errorStream = err;
	}

	private String _getErrorString(String error) {
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

	private Optional<String> _error = null;
	private StringPrintStream _errorStream;
	private Optional<String> _output = null;
	private StringPrintStream _outputStream;

}