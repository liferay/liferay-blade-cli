/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
		if ((Objects.equals(optionName, "-v") || Objects.equals(optionName, "--version")) && (_args.length > 0) &&
			Objects.equals(_args[0], "init") &&
			Arrays.stream(
				_args
			).filter(
				arg -> Objects.equals(arg, "-l") || Objects.equals(arg, "--list")
			).findAny(
			).isPresent()) {

			return "7.4";
		}

		return null;
	}

	private String[] _args;

}