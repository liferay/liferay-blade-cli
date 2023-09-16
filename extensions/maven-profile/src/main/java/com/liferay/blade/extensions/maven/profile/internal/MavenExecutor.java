/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.extensions.maven.profile.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Gregory Amerson
 */
public interface MavenExecutor {

	public default void execute(String projectPath, String[] args) {
		execute(projectPath, args, false);
	}

	public default void execute(String projectPath, String[] args, boolean printOutput) {
		Objects.requireNonNull(args, "Args must be specified");

		if (!(args.length > 0)) {
			throw new RuntimeException("Args must be specified");
		}

		File projectFile = new File(projectPath);

		try {
			projectPath = projectFile.getCanonicalPath();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		String os = System.getProperty("os.name");

		boolean windows = false;

		os = os.toLowerCase();

		if (os.startsWith("win")) {
			windows = true;
		}

		AtomicBoolean buildSuccess = new AtomicBoolean(false);

		int exitValue = 1;

		StringBuilder stringBuilder = new StringBuilder();

		for (String arg : args) {
			stringBuilder.append(arg + " ");
		}

		StringBuilder output = new StringBuilder();

		String command = null;

		try {
			Runtime runtime = Runtime.getRuntime();

			command = (windows ? "cmd.exe /c .\\mvnw.cmd" : "./mvnw") + " " + stringBuilder.toString();

			Process process = runtime.exec(command, null, new File(projectPath));

			try (BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader processError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

				CountDownLatch latch = new CountDownLatch(2);

				CompletableFuture.runAsync(
					() -> {
						String line = null;

						try {
							while ((line = processOutput.readLine()) != null) {
								output.append(line);
								output.append(System.lineSeparator());

								if (line.contains("BUILD SUCCESS")) {
									buildSuccess.set(true);
								}

								if (printOutput) {
									System.out.println(line);
								}
							}
						}
						catch (Exception exception) {
							exception.printStackTrace(System.err);
						}
						finally {
							latch.countDown();
						}
					});

				CompletableFuture.runAsync(
					() -> {
						String line = null;

						try {
							while ((line = processError.readLine()) != null) {
								output.append(line);
								output.append(System.lineSeparator());

								if (printOutput) {
									System.err.println(line);
								}
							}
						}
						catch (Exception exception) {
							exception.printStackTrace(System.err);
						}
						finally {
							latch.countDown();
						}
					});

				latch.await();

				exitValue = process.waitFor();
			}
		}
		catch (Exception exception) {
			StringBuilder sb = new StringBuilder();

			sb.append("Project path: " + projectPath + "\n");
			sb.append("maven command failed: " + command);
			sb.append(exception.getMessage());

			throw new RuntimeException(sb.toString(), exception);
		}

		boolean exitValueCorrect = false;

		if (exitValue == 0) {
			exitValueCorrect = true;
		}

		if (!exitValueCorrect) {
			throw new RuntimeException("Maven exec failed.\n " + output.toString());
		}

		if (!buildSuccess.get()) {
			throw new RuntimeException("Maven exec failed.\n " + output.toString());
		}
	}

}