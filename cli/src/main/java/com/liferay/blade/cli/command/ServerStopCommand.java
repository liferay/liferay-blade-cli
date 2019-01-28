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
import com.liferay.blade.cli.util.BladeUtil;

import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

/**
 * @author David Truong
 * @author Gregory Amerson
 */
public class ServerStopCommand extends BaseCommand<ServerStopArgs> {

	public ServerStopCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		LocalServer localServer = newLocalServer(bladeCLI);

		Path liferayHomePath = localServer.getLiferayHomePath();

		if (Files.notExists(liferayHomePath) || BladeUtil.isDirEmpty(liferayHomePath)) {
			bladeCLI.error("Liferay home directory does not exist. Execute 'blade server init' to create it.");

			return;
		}

		String serverType = localServer.getServerType();

		if (!localServer.isSupported()) {
			bladeCLI.error(serverType + " not supported");

			return;
		}

		if (serverType.equals("jboss") || serverType.equals("wildfly")) {
			bladeCLI.error(serverType + " does not support stop command.");

			return;
		}

		ProcessBuilder processBuilder = localServer.newLocalServerProcess();

		List<String> commands = processBuilder.command();

		if (serverType.equals("tomcat")) {
			commands.add("stop");
			commands.add("60");
			commands.add("-force");
		}

		Process process = processBuilder.start();

		BladeUtil.readProcessStream(process.getInputStream(), bladeCLI.out());
		BladeUtil.readProcessStream(process.getErrorStream(), bladeCLI.error());

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		process.waitFor();
	}

	@Override
	public Class<ServerStopArgs> getArgsClass() {
		return ServerStopArgs.class;
	}

	protected LocalServer newLocalServer(BladeCLI bladeCLI) {
		return new LocalServer(bladeCLI);
	}

}