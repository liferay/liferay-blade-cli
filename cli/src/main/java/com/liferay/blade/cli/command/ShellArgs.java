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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandDescription = "Connects to Liferay and executes gogo command and returns output.", commandNames = "sh"
)
public class ShellArgs extends BaseArgs {

	public List<String> getArgs() {
		return _args;
	}

	public String getHost() {
		return _host;
	}

	public int getPort() {
		return _port;
	}

	@Parameter
	private List<String> _args = new ArrayList<>();

	@Parameter(description = "The host to use to connect to gogo shell", names = {"-h", "--host"})
	private String _host;

	@Parameter(description = "The port to use to connect to gogo shell", names = {"-p", "--port"})
	private int _port;

}