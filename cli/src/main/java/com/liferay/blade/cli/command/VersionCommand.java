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

import java.net.URL;

import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Christopher Boyd
 */
public class VersionCommand extends BaseCommand<VersionArgs> {

	public VersionCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Class<? extends BladeCLI> clazz = bladeCLI.getClass();

		ClassLoader cl = clazz.getClassLoader();

		Enumeration<URL> e = cl.getResources("META-INF/MANIFEST.MF");

		while (e.hasMoreElements()) {
			URL u = e.nextElement();

			Manifest m = new Manifest(u.openStream());

			Attributes mainAttributes = m.getMainAttributes();

			String bsn = mainAttributes.getValue(Constants.BUNDLE_SYMBOLICNAME);

			if ((bsn != null) && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = mainAttributes;

				bladeCLI.out(attrs.getValue(Constants.BUNDLE_VERSION));

				return;
			}
		}

		bladeCLI.error("Could not locate version");
	}

	@Override
	public Class<VersionArgs> getArgsClass() {
		return VersionArgs.class;
	}

}