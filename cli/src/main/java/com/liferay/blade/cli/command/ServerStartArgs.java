/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Parameters(commandDescription = "Start server defined by your Liferay project", commandNames = "server start")
public class ServerStartArgs extends BaseArgs {

	@Override
	public CommandType getCommandType() {
		return CommandType.WORKSPACE_ONLY;
	}

	public int getDebugPort() {
		return _debugPort;
	}

	public boolean isDebug() {
		return _debug;
	}

	public boolean isSuspend() {
		return _suspend;
	}

	public boolean isTail() {
		return _tail;
	}

	public void setDebug(boolean debug) {
		_debug = debug;
	}

	@Parameter(description = "Start server in debug mode", names = {"-d", "--debug"})
	private boolean _debug;

	@Parameter(description = "Debug port number in debug mode", names = {"-p", "--port"})
	private int _debugPort = -1;

	@Parameter(
		description = "When in debug mode, suspend the started server until the debugger is connected",
		names = {"-s", "--suspend"}
	)
	private boolean _suspend = false;

	@Parameter(description = "Tail the started server", names = {"-t", "--tail"})
	private boolean _tail;

}