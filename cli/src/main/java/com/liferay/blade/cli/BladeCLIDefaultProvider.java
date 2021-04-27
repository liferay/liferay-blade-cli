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

import com.beust.jcommander.IDefaultProvider;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Gregory Amerson
 */
public class BladeCLIDefaultProvider implements IDefaultProvider {

	public BladeCLIDefaultProvider(String[] args) {
		_args = args;
	}

	@Override
	public String getDefaultValueFor(String optionName) {
		if ((Objects.equals("-v", optionName) || Objects.equals("--version", optionName)) && (_args.length > 0) &&
			Objects.equals("init", _args[0]) &&
			Arrays.stream(
				_args
			).filter(
				arg -> Objects.equals("-l", arg) || Objects.equals("--list", arg)
			).findAny(
			).isPresent()) {

			return "7.4";
		}

		return null;
	}

	private String[] _args;

}