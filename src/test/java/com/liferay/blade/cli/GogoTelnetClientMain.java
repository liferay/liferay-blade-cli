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

import java.io.IOException;

/**
 * @author Gregory Amerson
 */
public class GogoTelnetClientMain {

	public static void main(String[] args) {
		try (GogoTelnetClient client = new GogoTelnetClient()) {
			System.out.println(client.send("help"));
			System.out.println(client.send("lb -s"));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}