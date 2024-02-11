/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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

	public void commandPostAction() throws Exception {
	}

	public abstract void execute() throws Exception;

	public T getArgs() {
		return _args;
	}

	public abstract Class<T> getArgsClass();

	public BladeCLI getBladeCLI() {
		return _blade;
	}

	public ClassLoader getClassLoader() {
		return _classLoader;
	}

	public void setArgs(BaseArgs commandArgs) {
		_args = getArgsClass().cast(commandArgs);
	}

	public void setBlade(BladeCLI blade) {
		_blade = blade;
	}

	public void setClassLoader(ClassLoader classLoader) {
		_classLoader = classLoader;
	}

	private T _args;
	private BladeCLI _blade;
	private ClassLoader _classLoader;

}