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

import java.util.Collections;

/**
 * @author Gregory Amerson
 */
public class InstallCommand {

	public static final String DESCRIPTION = "Installs a bundle into Liferay module framework.";

	public InstallCommand(BladeCLI blade, InstallCommandArgs options) throws Exception {
		_blade = blade;
		_args = options;

		_host = options.getHost() != null ? options.getHost() : "localhost";
		_port = options.getPort() != 0 ? options.getPort() : 11311;
	}

	public void execute() throws Exception {
		if (!Util.canConnect(_host, _port)) {
			_addError("Unable to connect to gogo shell on " + _host + ":" + _port);
			return;
		}

		final String bundleFileName = _args.getBundleFileName();

		if (bundleFileName == null) {
			_addError("Must specify bundle file to install.");
			return;
		}

		final File bundleFile = new File(_blade.getBase(), bundleFileName);

		if (!bundleFile.exists()) {
			_addError(bundleFile + "doesn't exist.");
			return;
		}

		try (GogoTelnetClient client = new GogoTelnetClient(_host, _port)) {
			String response = client.send("install " + bundleFile.toURI());

			_blade.out(response);
		}
	}

	private void _addError(String msg) {
		_addError("install", msg);
	}

	private void _addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private final InstallCommandArgs _args;
	private final BladeCLI _blade;
	private final String _host;
	private final int _port;

}