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

/**
 * @author Christopher Bryan Boyd
 */
public abstract class BaseCommand<T extends BaseArgs> {

	public BaseCommand() {
	}

	public BaseCommand(BladeCLI blade, T args) {
		_args = args;
		_blade = blade;
	}

	public abstract void execute() throws Exception;

	public T getArgs() {
		return _args;
	}

	public abstract Class<T> getArgsClass();

	public BladeCLI getBlade() {
		return _blade;
	}

	public void setArgs(BaseArgs _commandArgs) {
		this._args = getArgsClass().cast(_commandArgs);
	}

	public void setBlade(BladeCLI _blade) {
		this._blade = _blade;
	}

	protected T _args;
	protected BladeCLI _blade;

}