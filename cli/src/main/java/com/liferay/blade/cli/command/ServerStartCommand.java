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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Truong
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ServerStartCommand extends BaseCommand<ServerStartArgs> {

	public ServerStartCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File baseDir = new File(args.getBase());

		LocalServer localServer = newLocalServer(baseDir);

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

		ServerStartArgs serverStartArgs = getArgs();

		if (serverType.equals("tomcat")) {
			if (serverStartArgs.isDebug()) {
				commands.add("jpda");
				commands.add("start");
			}
			else {
				commands.add("start");
			}
		}
		else if (serverType.equals("jboss") || serverType.equals("wildfly")) {
			if (serverStartArgs.isDebug()) {
				commands.add("--debug");
			}

			Map<String, String> environment = processBuilder.environment();

			environment.put("LAUNCH_JBOSS_IN_BACKGROUND", "1");
		}

		Stream<String> stream = commands.stream();

		String shellCommand = stream.collect(Collectors.joining(" "));

		BladeUtil.setShell(processBuilder, shellCommand);

		Process process = processBuilder.start();

		BladeUtil.readProcessStream(process.getInputStream(), bladeCLI.out());
		BladeUtil.readProcessStream(process.getErrorStream(), bladeCLI.error());

		OutputStream outputStream = process.getOutputStream();

		outputStream.close();

		Optional<Path> log = localServer.getLogPath();

		if (serverType.equals("tomcat")) {
			process.waitFor();
		}
		else {
			bladeCLI.out(serverType + " started.");
		}

		if (serverStartArgs.isTail()) {
			if (log.isPresent()) {
				BladeUtil.tail(log.get(), bladeCLI.out());
			}
		}
		else {
			if (log.isPresent()) {
				Path logPath = log.get();

				bladeCLI.out("To view the log execute 'tail -f " + logPath.toString() + "'");
			}
		}
	}

	@Override
	public Class<ServerStartArgs> getArgsClass() {
		return ServerStartArgs.class;
	}

	protected LocalServer newLocalServer(File baseDir) {
		return new LocalServer(baseDir);
	}

}