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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Seiphon Wang
 */
public class ProcessesUtil {

	public static List<Long> getAllProcessIds() {
		List<Long> processIds = new ArrayList<>();

		try {
			String os = System.getProperty("os.name");

			if (os.startsWith("Win")) {
				ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe", "/fo", "csv", "/nh");

				Process process = processBuilder.start();

				try (Scanner scanner = new Scanner(process.getInputStream())) {
					if (scanner.hasNextLine()) {
						scanner.nextLine();
					}

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();

						String[] parts = line.split(",");

						String pid = parts[1].substring(1);

						processIds.add(Long.parseLong(pid.replaceFirst(".$", "")));
					}
				}
			}
			else {
				ProcessBuilder processBuilder = new ProcessBuilder("ps", "-e");

				Process process = processBuilder.start();

				try (Scanner scanner = new Scanner(process.getInputStream())) {
					if (scanner.hasNextLine()) {
						scanner.nextLine();
					}

					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();

						line = line.trim();

						processIds.add(Long.parseLong(line.substring(0, line.indexOf(" "))));
					}
				}
			}
		}
		catch (Exception exception) {
		}

		return processIds;
	}

	public static long getCurrentProcessId() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		String vmName = runtimeMXBean.getName();

		return Long.parseLong(vmName.substring(0, vmName.indexOf("@")));
	}

}