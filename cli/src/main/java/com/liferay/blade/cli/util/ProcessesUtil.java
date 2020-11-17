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

package com.liferay.blade.cli.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.tools.ant.util.ProcessUtil;

/**
 * @author Seiphon Wang
 */
public class ProcessesUtil {

	public static List<Long> getAllProcessIds() {
		List<Long> processIdList = new ArrayList<>();

		try {
			String os = System.getProperty("os.name");

			Process process = null;

			if (os.startsWith("Win")) {
				ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh");

				process = processBuilder.start();

				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(process.getInputStream());

				if (scanner.hasNextLine()) {
					scanner.nextLine();
				}

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();

					String[] parts = line.split(",");

					String pid = parts[1].substring(1);

					pid = pid.replaceFirst(".$", "");

					processIdList.add(Long.parseLong(pid));
				}
			}
			else {
				ProcessBuilder processBuilder = new ProcessBuilder("ps", "-e");

				process = processBuilder.start();

				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(process.getInputStream());

				if (scanner.hasNextLine()) {
					scanner.nextLine();
				}

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();

					line = line.trim();

					String pid = line.substring(0, line.indexOf(" "));

					processIdList.add(Long.parseLong(pid));
				}
			}
		}
		catch (Exception e) {
		}

		return processIdList;
	}

	public static long getAProcessId() {
		return Long.parseLong(ProcessUtil.getProcessId("0"));
	}

}