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

/**
 * @author Christopher Bryan Boyd
 */
public abstract class BaseCommand<T extends BaseArgs> {

	public BaseCommand() {
	}

	public BaseCommand(BladeCLI blade, T args) {
		_blade = blade;
		_args = args;
	}

	public abstract void execute() throws Exception;

	public T getArgs() {
		return _args;
	}

	public abstract Class<T> getArgsClass();

	public BladeCLI getBladeCLI() {
		return _blade;
	}

	public void setArgs(BaseArgs commandArgs) {
		_args = getArgsClass().cast(commandArgs);
	}

	public void setBlade(BladeCLI blade) {
		_blade = blade;
	}

	private T _args;
	private BladeCLI _blade;

}