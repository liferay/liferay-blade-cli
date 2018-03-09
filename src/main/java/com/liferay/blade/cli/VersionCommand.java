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

import java.net.URL;

import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Christopher Boyd
 */
public class VersionCommand {

	public VersionCommand(BladeCLI blade, VersionCommandArgs options) {
		_blade = blade;
	}

	public void execute() throws Exception {
		Class<? extends BladeCLI> clazz = _blade.getClass();

		ClassLoader cl = clazz.getClassLoader();

		Enumeration<URL> e = cl.getResources("META-INF/MANIFEST.MF");

		while (e.hasMoreElements()) {
			URL u = e.nextElement();

			Manifest m = new Manifest(u.openStream());

			Attributes mainAttributes = m.getMainAttributes();

			String bsn = mainAttributes.getValue(Constants.BUNDLE_SYMBOLICNAME);

			if ((bsn != null) && bsn.equals("com.liferay.blade.cli")) {
				Attributes attrs = mainAttributes;

				_blade.out(attrs.getValue(Constants.BUNDLE_VERSION));

				return;
			}
		}

		_blade.error("Could not locate version");
	}

	private BladeCLI _blade;

}