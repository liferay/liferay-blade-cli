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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Gregory Amerson
 */
@Parameters(commandNames = {"server start"}, commandDescription = "Start server defined by your Liferay project")
public class ServerStartCommandArgs {

	public boolean isBackground() {
		return background;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isTail() {
		return tail;
	}

	@Parameter(names = {"-b", "--background"}, description ="Start server in background")
	private boolean background;

	@Parameter(names = {"-d", "--debug"}, description ="Start server in debug mode")
	private boolean debug;

	@Parameter(names = {"-t", "--tail"}, description ="Tail a running server")
	private boolean tail;

}