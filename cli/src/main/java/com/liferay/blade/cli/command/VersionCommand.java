/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.util.BladeUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Constants;

/**
 * @author Christopher Boyd
 */
public class VersionCommand extends BaseCommand<VersionArgs> {

	public static String getBladeCLIVersion() throws IOException {
		Path path = BladeUtil.getBladeJarPath();

		if (Files.isDirectory(path)) {
			Path manifestPath = path.resolve("META-INF/MANIFEST.MF");

			try (InputStream inputStream = Files.newInputStream(manifestPath)) {
				Manifest manifest = new Manifest(inputStream);

				String version = _getVersionFromManifest(manifest);

				if (version != null) {
					return version;
				}
			}
		}
		else {
			ClassLoader classLoader = BladeCLI.class.getClassLoader();

			Enumeration<URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");

			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();

				try (InputStream inputStream = url.openStream()) {
					Manifest manifest = new Manifest(inputStream);

					String version = _getVersionFromManifest(manifest);

					if (version != null) {
						return version;
					}
				}
			}
		}

		return null;
	}

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
			bladeCLI.out("blade version " + bladeCLIVersion);
		}
	}

	@Override
	public Class<VersionArgs> getArgsClass() {
		return VersionArgs.class;
	}

	private static String _getVersionFromManifest(Manifest manifest) {
		Attributes attributes = manifest.getMainAttributes();

		String bundleSymbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);

		if (Objects.equals(bundleSymbolicName, "com.liferay.blade.cli")) {
			return attributes.getValue(Constants.BUNDLE_VERSION);
		}

		return null;
	}

}