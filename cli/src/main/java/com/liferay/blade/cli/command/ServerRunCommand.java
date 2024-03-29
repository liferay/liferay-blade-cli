/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

/**
 * @author Christopher Bryan Boyd
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

		ServerRunArgs serverRunArgs = getArgs();

		Map<String, String> processBuilderEnvironment = processBuilder.environment();

		boolean tomcat = serverType.equals("tomcat");

		boolean wildfly = false;

		if (serverType.equals("jboss") || serverType.equals("wildfly")) {
			wildfly = true;
		}

		if (tomcat) {
			commands.add("run");
		}

		if (serverRunArgs.isDebug()) {
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

				if (serverRunArgs.isSuspend()) {
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

	protected LocalServer newLocalServer(BladeCLI bladeCLI) {
		return new LocalServer(bladeCLI);
	}

	private String _getDebugPortString(String serverType) {
		ServerRunArgs serverRunArgs = getArgs();

		int debugPort = serverRunArgs.getDebugPort();

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