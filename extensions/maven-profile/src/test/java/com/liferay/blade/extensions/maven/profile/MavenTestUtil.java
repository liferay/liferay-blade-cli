/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.TestUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class MavenTestUtil {

	public static void makeMavenWorkspace(File extensionsDir, File workspace, String version, String... args)
		throws Exception {

		File parentFile = workspace.getParentFile();

		List<String> completeArgs = new ArrayList<>();

		completeArgs.add("--base");
		completeArgs.add(parentFile.getPath());
		completeArgs.add("init");
		completeArgs.add("-P");
		completeArgs.add("maven");
		completeArgs.add(workspace.getName());
		completeArgs.add("-v");
		completeArgs.add(version);

		for (String arg : args) {
			completeArgs.add(arg);
		}

		TestUtil.runBlade(workspace, extensionsDir, completeArgs.toArray(new String[0]));
	}

	public static void verifyBuildOutput(String projectPath, String fileName) {
		File file = new File(projectPath, "/target/" + fileName);

		if (!file.exists()) {
			throw new RuntimeException("Maven file " + fileName + " doses not exist in project path " + projectPath);
		}
	}

}