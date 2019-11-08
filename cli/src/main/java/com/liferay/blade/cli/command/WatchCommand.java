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
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.gradle.GradleTooling;
import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.gogo.shell.client.GogoShellClient;
import com.sun.nio.file.SensitivityWatchEventModifier;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class WatchCommand extends BaseCommand<WatchArgs> {

	public WatchCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		WatchArgs watchArgs = getArgs();

		File base = new File(watchArgs.getBase());

		Path watchPath = Paths.get(base.getCanonicalPath());

		if (!Files.isDirectory(watchPath)) {
			bladeCLI.error("Error: base dir is not a directory: " + watchPath);

			return;
		}

		ProjectInfo projectInfo = GradleTooling.loadProjectInfo(watchPath);

		_watch(watchArgs, watchPath, projectInfo.getProjectOutputFiles());
	}

	@Override
	public Class<WatchArgs> getArgsClass() {
		return WatchArgs.class;
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _watch(
			WatchArgs watchArgs, Path watchPath, Map<String, Set<File>> projectOutputFiles)
		throws InterruptedException {

		Thread watchThread = new Thread() {

			@Override
			public void run() {
				BladeCLI bladeCLI = getBladeCLI();

				try {
					FileSystem fileSystem = FileSystems.getDefault();

					final WatchService watcher = fileSystem.newWatchService();

					final Map<WatchKey, Path> keys = new HashMap<>();;

					List<String> ignores = watchArgs.getIgnores();

					final List<PathMatcher> ignorePathMatchers =
						_getPathMatchers(
							watchPath, ignores.toArray(new String[0]));

					List<String> fastExtensions = watchArgs.getFastExtensions();

					final List<PathMatcher> fastExtensionMatchers =
						_getPathMatchers(
							watchPath, fastExtensions.toArray(new String[0]));

					System.out.println("Watching " + watchPath);

					_walkAndRegisterDirectories(
						watcher, keys, watchPath, ignorePathMatchers);

					final GradleExec gradleExec = new GradleExec(bladeCLI);

					gradleExec.executeTask("deploy", false);

					Set<String> projectPaths = projectOutputFiles.keySet();

					while (true) {
						WatchKey key;

						try {
							key = watcher.take();
						}
						catch (InterruptedException ie) {
							return;
						}

						Path dir = keys.get(key);

						if (dir == null) {
							System.err.println("WatchKey not recognized!!");

							continue;
						}

						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent.Kind kind = event.kind();

							Path path = ((WatchEvent<Path>)event).context();

							Path resolvedPath = dir.resolve(path);

							Path projectPath = _getGradleProjectPath(watchPath, resolvedPath, projectPaths);

							boolean directory = Files.isDirectory(resolvedPath);

							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								try {
									if (directory) {
										_walkAndRegisterDirectories(watcher, keys, resolvedPath, ignorePathMatchers);
									}

									System.out.println(resolvedPath + " has been created");

									gradleExec.executeTask("deploy", projectPath.toFile(), false);
								}
								catch (IOException ioe) {
									System.err.println(
										"Could not register directory:" + resolvedPath);
								}
							}
							else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								System.out.println(resolvedPath + " has been deleted");

								gradleExec.executeTask("clean deploy", projectPath.toFile(), false);
							}
							else if (!directory) {
								boolean ignoredPath = false;

								for (PathMatcher pathMatcher : ignorePathMatchers) {
									if (pathMatcher.matches(resolvedPath)) {
										ignoredPath = true;
										break;
									}
								}

								if (!ignoredPath) {
									boolean fastExtension = false;
									for (PathMatcher pathMatcher : fastExtensionMatchers) {
										if (pathMatcher.matches(resolvedPath)) {
											fastExtension = true;
											break;
										}
									}

									System.out.println(resolvedPath + " has caused a new deployment");

									if (fastExtension) {
										gradleExec.executeTask("deployFast -a", projectPath.toFile(), false);
									}
									else {
										gradleExec.executeTask("deploy -a", projectPath.toFile(), false);
									}
								}
							}
						}

						boolean valid = key.reset();

						if (!valid) {
							keys.remove(key);

							if (keys.isEmpty()) {
								break;
							}
						}
					}
				}
				catch (Exception e) {
					String message = e.getMessage();

					_addError("watch", message);

					PrintStream error = bladeCLI.error();

					e.printStackTrace(error);
				}
			}

		};

		watchThread.start();

		watchThread.join();
	}

	private void _walkAndRegisterDirectories(
			final WatchService watcher, final Map<WatchKey, Path> keys, final Path basePath, final List<PathMatcher> ignorePathMatchers)
		throws IOException {

		Files.walkFileTree(
			basePath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					for (PathMatcher pathMatcher : ignorePathMatchers) {
						if (pathMatcher.matches(path)) {
							return FileVisitResult.SKIP_SUBTREE;
						}
					}

					_registerDirectory(watcher, keys, path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private Path _getGradleProjectPath(Path basePath, Path path, Set<String> projectPaths) {
		Path relativePath = basePath.relativize(path);

		String gradlePath = ":" + relativePath.toString();

		gradlePath = gradlePath.replaceAll(File.separator, ":");

		for (String projectPath : projectPaths) {
			if (gradlePath.startsWith(projectPath)) {
				return Paths.get(basePath.toString(), projectPath.replaceAll(":", File.separator));
			}
		}

		return basePath;
	}

	private void _registerDirectory(WatchService watcher, Map<WatchKey, Path> keys, Path dir) throws IOException {
		WatchKey key = dir.register(
			watcher,
			new WatchEvent.Kind[] {
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY
			},
			SensitivityWatchEventModifier.HIGH);

		keys.put(key, dir);
	}

	private void _addPathMatcher(
		List<PathMatcher> pathMatchers, FileSystem fileSystem, String pattern) {

		if (File.separatorChar == '\\') {
			pattern = pattern.replace("/", "\\\\");
		}

		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + pattern);

		pathMatchers.add(pathMatcher);
	}

	private List<PathMatcher> _getPathMatchers(
		Path baseDirPath, String... patterns) {

		FileSystem fileSystem = FileSystems.getDefault();

		List<PathMatcher> pathMatchers = new ArrayList<>(patterns.length);

		String patternPrefix = baseDirPath.toAbsolutePath() + File.separator;

		if (File.separatorChar != '/') {
			patternPrefix = patternPrefix.replace(File.separatorChar, '/');
		}

		for (String pattern : patterns) {
			if (pattern.startsWith("**/")) {
				String absolutePattern = patternPrefix + pattern.substring(3);

				_addPathMatcher(pathMatchers, fileSystem, absolutePattern);
			}

			String absolutePattern = patternPrefix + pattern;

			_addPathMatcher(pathMatchers, fileSystem, absolutePattern);
		}

		return pathMatchers;
	}

}