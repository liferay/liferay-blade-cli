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

import java.io.File;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;
import java.util.Optional;

/**
 * @author David Truong
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ServerRunCommand extends BaseCommand<ServerRunArgs> {

	public ServerRunCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File baseDir = new File(args.getBase());

		LocalServer localServer = getLocalServer(baseDir);

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

		ProcessBuilder processBuilder = localServer.newLocalServerProcess();

		List<String> commands = processBuilder.command();

		ServerRunArgs serverRunArgs = getArgs();

		if (serverType.equals("tomcat")) {
			if (serverRunArgs.isDebug()) {
				commands.add("jpda");
				commands.add("run");
			}
			else {
				commands.add("run");
			}
		}
		else if (serverType.equals("jboss") || serverType.equals("wildfly")) {
			if (serverRunArgs.isDebug()) {
				commands.add("--debug");
			}
		}

		Process process = processBuilder.start();

		BladeUtil.readProcessStream(process.getInputStream(), bladeCLI.out());
		BladeUtil.readProcessStream(process.getErrorStream(), bladeCLI.error());

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		Optional<Path> log = localServer.getLogPath();

		process.waitFor();

		if (log.isPresent()) {
			BladeUtil.tail(log.get(), bladeCLI.out());
		}
	}

	@Override
	public Class<ServerRunArgs> getArgsClass() {
		return ServerRunArgs.class;
	}

	protected LocalServer getLocalServer(File baseDir) {
		return new LocalServer(baseDir);
	}

}