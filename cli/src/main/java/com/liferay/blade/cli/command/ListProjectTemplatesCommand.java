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