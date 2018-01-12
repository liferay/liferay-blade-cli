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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandNames = {"sh"}, commandDescription = "Connects to Liferay and executes gogo command and returns output."
)
public class ShellCommandArgs {

	public List<String> getArgs() {
		return args;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Parameter
	private List<String> args = new ArrayList<>();

	@Parameter(names = {"-h", "--host"}, description ="The host to use to connect to gogo shell")
	private String host;

	@Parameter(names = {"-p", "--port"}, description ="The port to use to connect to gogo shell")
	private int port;

}