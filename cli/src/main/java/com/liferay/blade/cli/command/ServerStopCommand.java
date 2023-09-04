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