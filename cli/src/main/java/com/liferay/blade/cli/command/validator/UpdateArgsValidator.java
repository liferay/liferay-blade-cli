/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import com.liferay.blade.cli.command.UpdateArgs;

import java.util.function.Predicate;

/**
 * @author Christopher Bryan Boyd
 */
public class UpdateArgsValidator implements Predicate<UpdateArgs> {

	@Override
	public boolean test(UpdateArgs updateArgs) {
		if (updateArgs.isRelease() && updateArgs.isSnapshots()) {
			System.err.println(
				"Can only either specify snapshot (-s, --snapshot) or release (-r, --release), not both.");

			return false;
		}

		return true;
	}

}