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

import java.io.File;
import java.io.PrintStream;

/**
 * @author Gregory Amerson
 */
public class BladeTest extends BladeCLI {

	public BladeTest() throws Exception {
		super(System.out, System.out);
	}

	public BladeTest(File base) throws Exception {
		this();

		_base = base;
	}

	public BladeTest(PrintStream ps) {
		this(ps, null);
	}

	public BladeTest(PrintStream ps, File base) {
		super(ps, ps);

		_base = base;
	}

	@Override
	public File getBase() {
		if (_base != null) {
			return _base;
		}
		else {
			return super.getBase();
		}
	}

	private File _base;

}