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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import aQute.lib.io.IO;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AgentCommandTest {

	@Before
	public void setUp() throws Exception {
		if (cacheDir.exists()) {
			IO.delete(cacheDir);
			assertFalse(cacheDir.exists());
		}
	}

	@Test
	public void testGetAgentJarDefaultURL() throws Exception {
		File agentJar = new AgentCommand(
			null, null).getAgentJar(cacheDir, null, null);

		assertNotNull(agentJar);
		assertTrue(agentJar.exists());
	}

	private final File cacheDir = IO.getFile("generated/cache");

}