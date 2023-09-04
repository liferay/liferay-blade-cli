/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command.validator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Simon Jiang
 */
public interface ValidatorFunctionPredicate<T> extends Function<T, List<String>>, Predicate<T> {
}