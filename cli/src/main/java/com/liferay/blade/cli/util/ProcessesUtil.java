/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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