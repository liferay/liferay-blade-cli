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

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.cli.util.WorkspaceUtil;
import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.gogo.shell.client.GogoShellClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 */
public class WatchCommand extends BaseCommand<WatchArgs> {

	public WatchCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		WatchArgs watchArgs = getArgs();

		File base = new File(watchArgs.getBase());

		Path watchPath = Paths.get(base.getAbsolutePath());

		if (!Files.isDirectory(watchPath)) {
			bladeCLI.error("Error: base dir is not a directory: " + watchPath);

			return;
		}

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(watchPath);

		Map<String, Set<File>> projectOutputFiles = projectInfo.getProjectOutputFiles();

		_watch(watchPath, projectOutputFiles);
	}

	@Override
	public Class<WatchArgs> getArgsClass() {
		return WatchArgs.class;
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _deleteWatchOutputFiles(Map<String, Set<File>> projectOutputFiles) {
		BladeCLI bladeCLI = getBladeCLI();

		File workspaceDir = WorkspaceUtil.getWorkspaceDir(bladeCLI);

		Set<String> projectPaths = projectOutputFiles.keySet();

		for (String projectPath : projectPaths) {
			File projectDir = new File(workspaceDir, projectPath.replaceAll(":", File.separator));

			File watchOutputFile = new File(projectDir, "build/installedBundleId");

			if (watchOutputFile.exists()) {
				watchOutputFile.delete();
			}
		}
	}

	private void _uninstallBundles(Map<String, Set<File>> projectOutputFiles) {
		BladeCLI bladeCLI = getBladeCLI();

		File workspaceDir = WorkspaceUtil.getWorkspaceDir(bladeCLI);

		Set<String> projectPaths = projectOutputFiles.keySet();

		try (final GogoShellClient client = new GogoShellClient()) {
			for (String projectPath : projectPaths) {
				File projectDir = new File(workspaceDir, projectPath.replaceAll(":", File.separator));

				File watchOutputFile = new File(projectDir, "build/installedBundleId");

				if (watchOutputFile.exists()) {
					try {
						String installedBundleId = new String(Files.readAllBytes(watchOutputFile.toPath()));

						String response = client.send("uninstall " + installedBundleId);

						bladeCLI.out(response);
					}
					catch (IOException ioe) {
					}
				}
			}
		}
		catch (IOException ioe) {
		}
	}

	private void _watch(Path watchPath, Map<String, Set<File>> projectOutputFiles) throws InterruptedException {
		Thread watchThread = new Thread() {

			@Override
			public void run() {
				BladeCLI bladeCLI = getBladeCLI();

				try {
					final GradleExec gradleExec = new GradleExec(bladeCLI);

					Set<String> projectPaths = projectOutputFiles.keySet();

					Stream<String> stream = projectPaths.stream();

					String assembleTasks = stream.collect(Collectors.joining(":assemble "));

					String assembleTaskPath = assembleTasks + ":assemble";

					gradleExec.executeTask(assembleTaskPath, watchPath.toFile(), false);

					stream = projectPaths.stream();

					String watchTasks = stream.collect(Collectors.joining(":watch "));

					String watchTaskPath = watchTasks + ":watch --continuous --no-rebuild --stacktrace";

					gradleExec.executeTask(watchTaskPath, watchPath.toFile(), false);
				}
				catch (Exception e) {
					String message = e.getMessage();

					if (message == null) {
						message = "Gradle task failed.";
					}

					_addError("watch", message);

					PrintStream error = bladeCLI.error();

					e.printStackTrace(error);
				}
			}

		};

		Runtime runtime = Runtime.getRuntime();

		runtime.addShutdownHook(
			new Thread(
				() -> {
					_uninstallBundles(projectOutputFiles);

					_deleteWatchOutputFiles(projectOutputFiles);
				}));

		watchThread.start();

		watchThread.join();
	}

}