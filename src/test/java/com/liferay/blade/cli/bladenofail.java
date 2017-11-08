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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Gregory Amerson
 */
public class bladenofail extends blade {

	public bladenofail() throws UnsupportedEncodingException {
		this(System.out);
	}

	public bladenofail(PrintStream out) throws UnsupportedEncodingException {
		_out = out;
		_err = out;
	}

	@Override
	public boolean check(String... pattern) {
		return true;
	}

	@Override
	public PrintStream err() {
		return _err;
	}

	@Override
	public PrintStream out() {
		return _out;
	}

	private PrintStream _err;
	private PrintStream _out;

}