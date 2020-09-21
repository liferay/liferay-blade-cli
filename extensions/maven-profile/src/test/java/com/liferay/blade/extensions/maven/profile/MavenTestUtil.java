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

package com.liferay.blade.extensions.maven.profile;

import com.liferay.blade.cli.TestUtil;

import java.io.File;

/**
 * @author Gregory Amerson
 */
public class MavenTestUtil {

	public static void makeMavenWorkspace(File extensionsDir, File workspace, String version) throws Exception {
		File parentFile = workspace.getParentFile();

		String[] args = {"--base", parentFile.getPath(), "init", "-P", "maven", workspace.getName(), "-v", version};

		TestUtil.runBlade(workspace, extensionsDir, args);
	}

	public static void verifyBuildOutput(String projectPath, String fileName) {
		File file = new File(projectPath, "/target/" + fileName);

		if (!file.exists()) {
			throw new RuntimeException("Maven file " + fileName + " doses not exist in project path " + projectPath);
		}
	}

}