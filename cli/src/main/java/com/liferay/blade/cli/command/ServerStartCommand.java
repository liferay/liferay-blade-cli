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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christopher Bryan Boyd
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

		ProcessBuilder processBuilder = localServer.newLocalServerProcess();

		List<String> commands = processBuilder.command();

		ServerStartArgs serverStartArgs = getArgs();

		Map<String, String> processBuilderEnvironment = processBuilder.environment();

		boolean tomcat = serverType.equals("tomcat");

		boolean wildfly = false;

		if (serverType.equals("jboss") || serverType.equals("wildfly")) {
			wildfly = true;
		}

		if (tomcat) {
			commands.add("start");
		}
		else if (wildfly) {
			processBuilderEnvironment.put("LAUNCH_JBOSS_IN_BACKGROUND", "1");
		}

		if (serverStartArgs.isDebug()) {
			String optsOriginal = null;

			if (tomcat) {
				optsOriginal = processBuilderEnvironment.getOrDefault("CATALINA_OPTS", "");
			}
			else if (wildfly) {
				optsOriginal = processBuilderEnvironment.getOrDefault("JAVA_OPTS", "");
			}

			if (optsOriginal != null) {
				StringBuilder opts = new StringBuilder(optsOriginal);

				if (opts.length() > 0) {
					opts.append(" ");
				}

				String debugPortString = _getDebugPortString(serverType);

				String suspendValue;

				if (serverStartArgs.isSuspend()) {
					suspendValue = "y";
				}
				else {
					suspendValue = "n";
				}

				if (tomcat) {
					opts.append(
						"-agentlib:jdwp=transport=dt_socket,address=" + debugPortString + ",server=y,suspend=" +
							suspendValue);

					processBuilderEnvironment.put("JAVA_OPTS", opts.toString());
				}
				else if (wildfly) {
					opts.append(
						"-Xrunjdwp:transport=dt_socket,address=" + debugPortString + ",server=y,suspend=" +
							suspendValue);

					processBuilderEnvironment.put("JAVA_OPTS", opts.toString());
				}
			}
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

		boolean quiet = serverStartArgs.isQuiet();

		if (serverType.equals("tomcat")) {
			process.waitFor();
		}
		else {
			if (log.isPresent()) {
				Path logPath = log.get();

				if (!Files.exists(logPath)) {
					Files.createFile(logPath);
				}
			}

			if (!quiet) {
				bladeCLI.out(serverType + " started.");
			}
		}

		if (serverStartArgs.isTail()) {
			if (log.isPresent()) {
				BladeUtil.tail(log.get(), bladeCLI.out());
			}
		}
		else {
			if (log.isPresent() && !quiet) {
				Path logPath = log.get();

				bladeCLI.out("To view the log execute 'tail -f " + logPath.toString() + "'");
			}
		}
	}

	@Override
	public Class<ServerStartArgs> getArgsClass() {
		return ServerStartArgs.class;
	}

	protected LocalServer newLocalServer(BladeCLI bladeCLI) {
		return new LocalServer(bladeCLI);
	}

	private String _getDebugPortString(String serverType) {
		ServerStartArgs serverStartArgs = getArgs();

		int debugPort = serverStartArgs.getDebugPort();

		if (debugPort == -1) {
			if (serverType.equals("tomcat")) {
				debugPort = 8000;
			}
			else if (serverType.equals("jboss") || serverType.equals("wildfly")) {
				debugPort = 8787;
			}
		}

		return String.valueOf(debugPort);
	}

}