/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.command.BladeProfile;
import com.liferay.blade.cli.command.InitArgs;
import com.liferay.blade.cli.command.InitCommand;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductKeyInfo;
import com.liferay.blade.cli.util.ReleaseInfo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.nio.file.Files;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@BladeProfile("maven")
public class InitCommandMaven extends InitCommand {

	public static final String BUNDLE_URL_PROPERTY = "liferay.workspace.bundle.url";

	public static final String LIFERAY_BOM_VERSION = "liferay.bom.version";

	public void commandPostAction() throws Exception {
		InitArgs initArgs = getArgs();

		Map<String, ProductKeyInfo> workspaceProductTargetPlatformVersions =
			BladeUtil.getWorkspaceProductTargetPlatformVersions(false);

		ProductKeyInfo productKeyInfo = workspaceProductTargetPlatformVersions.get(initArgs.getLiferayVersion());

		if (Objects.nonNull(productKeyInfo)) {
			File initBaseDir = initArgs.getBase();

			if (initBaseDir.exists()) {
				File workspacePomFile = new File(initBaseDir, "pom.xml");

				if (Files.exists(workspacePomFile.toPath())) {
					MavenXpp3Reader xppReader = new MavenXpp3Reader();

					Model mavenModel = xppReader.read(new FileReader(workspacePomFile));

					Properties properties = mavenModel.getProperties();

					ReleaseInfo releaseInfo = BladeUtil.getReleaseInfo(productKeyInfo.getProductKey());

					properties.setProperty(BUNDLE_URL_PROPERTY, releaseInfo.getBundleUrl());

					properties.setProperty(LIFERAY_BOM_VERSION, releaseInfo.getTargetPlatformVersion());

					_updateMavenPom(mavenModel, workspacePomFile);
				}
			}
		}
	}

	@Override
	public void execute() throws Exception {
		InitArgs initArgs = getArgs();

		initArgs.setProfileName("maven");

		super.execute();
	}

	private void _updateMavenPom(Model model, File file) throws Exception {
		MavenXpp3Writer mavenWriter = new MavenXpp3Writer();

		FileWriter fileWriter = new FileWriter(file);

		mavenWriter.write(fileWriter, model);
	}

}