/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Christopher Bryan Boyd
 */
public class ListProjectTemplatesCommand extends BaseCommand<ListProjectTemplatesArgs> {

	@Override
	public void execute() throws Exception {
		_printTemplates();
	}

	@Override
	public Class<ListProjectTemplatesArgs> getArgsClass() {
		return ListProjectTemplatesArgs.class;
	}

	private void _printTemplates() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Map<String, String> templates = BladeUtil.getTemplates(bladeCLI);

		List<String> templateNames = new ArrayList<>(BladeUtil.getTemplateNames(getBladeCLI()));

		Collections.sort(templateNames);

		Comparator<String> compareLength = Comparator.comparingInt(String::length);

		Stream<String> stream = templateNames.stream();

		String longestString = stream.max(
			compareLength
		).get();

		int padLength = longestString.length() + 2;

		for (String name : templateNames) {
			PrintStream out = bladeCLI.out();

			out.print(StringUtils.rightPad(name, padLength));

			bladeCLI.out(templates.get(name));
		}
	}

}