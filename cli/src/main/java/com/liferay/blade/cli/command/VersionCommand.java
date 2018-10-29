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

import java.io.IOException;
import java.io.InputStream;

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

	public VersionCommand(BladeCLI bladeCLI) {
		setBlade(bladeCLI);
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		String bladeCLIVersion = getBladeCLIVersion();

		if (bladeCLIVersion == null) {
			bladeCLI.error("Could not determine version.");
		}
		else {
			bladeCLI.out(bladeCLIVersion);
		}
	}

	@Override
	public Class<VersionArgs> getArgsClass() {
		return VersionArgs.class;
	}

	public String getBladeCLIVersion() throws IOException {
		ClassLoader classLoader = BladeCLI.class.getClassLoader();

		Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");

		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();

			try (InputStream inputStream = url.openStream()) {
				Manifest manifest = new Manifest(inputStream);

				Attributes attributes = manifest.getMainAttributes();

				String bundleSymbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);

				if ("com.liferay.blade.cli".equals(bundleSymbolicName)) {
					return attributes.getValue(Constants.BUNDLE_VERSION);
				}
			}
		}

		return null;
	}

}