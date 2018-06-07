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

package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author David Truong
 */
public class GradleExecTest {

	@Test
	public void testGradleWrapper() throws Exception {
		File file = temporaryFolder.getRoot();

		String[] args = {"create", "-t", "api", "foo"};

		new BladeTest(file).run(args);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PrintStream ps = new PrintStream(baos);

		BladeCLI blade = new BladeTest(ps, new File(file, "foo"));

		GradleExec gradleExec = new GradleExec(blade);

		int errorCode = gradleExec.executeGradleCommand("tasks");

		Assert.assertEquals(0, errorCode);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

}