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

import com.liferay.blade.cli.TestUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class ServerCommandsTest {

	@Test
	public void testServerStartCommandExists() throws Exception {
		Assert.assertTrue(_commandExists("server", "start"));
		Assert.assertTrue(_commandExists("server start"));
		Assert.assertFalse(_commandExists("server", "startx"));
		Assert.assertFalse(_commandExists("server startx"));
		Assert.assertFalse(_commandExists("serverx", "start"));
		Assert.assertFalse(_commandExists("serverx start"));
	}

	@Test
	public void testServerStopCommandExists() throws Exception {
		Assert.assertTrue(_commandExists("server", "stop"));
		Assert.assertTrue(_commandExists("server stop"));
		Assert.assertFalse(_commandExists("server", "stopx"));
		Assert.assertFalse(_commandExists("server stopx"));
		Assert.assertFalse(_commandExists("serverx", "stopx"));
		Assert.assertFalse(_commandExists("serverx stop"));
	}

	private static boolean _commandExists(String... args) {
		try {
			TestUtil.runBlade(args);
		}
		catch (Throwable throwable) {
			String message = throwable.getMessage();

			if (Objects.nonNull(message) && !message.contains("No such command")) {
				return true;
			}

			return false;
		}

		return false;
	}

}