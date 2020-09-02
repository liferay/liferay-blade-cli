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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
@SuppressWarnings("restriction")
public class WatchCommand extends BaseCommand<WatchArgs> {

	public WatchCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		WatchArgs watchArgs = getArgs();

		File base = watchArgs.getBase();

		Path watchPath = Paths.get(base.getCanonicalPath());

		if (!Files.isDirectory(watchPath)) {
			bladeCLI.error("Error: base dir is not a directory: " + watchPath);

			return;
		}

		List<String> ignorePaths = watchArgs.getIgnorePaths();

		Map<String, Path> projectPaths = _getProjectPaths(watchPath, watchArgs.getProjectPaths(), ignorePaths);

		if (!watchArgs.isQuiet()) {
			bladeCLI.out("Watching projects...");
		}

		projectPaths.keySet(
		).stream(
		).forEach(
			bladeCLI::out
		);

		_watch(watchPath, projectPaths, watchArgs.getFastPaths(), ignorePaths, !watchArgs.isSkipInit());
	}

	@Override
	public Class<WatchArgs> getArgsClass() {
		return WatchArgs.class;
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private void _addPathMatcher(List<PathMatcher> pathMatchers, FileSystem fileSystem, String pattern) {
		if (File.separatorChar == '\\') {
			pattern = pattern.replace("/", "\\\\");
		}

		pathMatchers.add(fileSystem.getPathMatcher("glob:" + pattern));
	}

	private String _getGradlePath(Path path, Path basePath) {
		String gradlePath = ":" + String.valueOf(basePath.relativize(path));

		return gradlePath.replaceAll(File.separator, ":");
	}

	private Path _getGradleProjectPath(Path basePath, Path path, Map<String, Path> projectPaths) {
		String gradlePath = _getGradlePath(path, basePath);

		for (Map.Entry<String, Path> entry : projectPaths.entrySet()) {
			String projectPath = entry.getKey();

			if (gradlePath.startsWith(projectPath)) {
				return projectPaths.get(projectPath);
			}
		}

		return basePath;
	}

	private List<PathMatcher> _getPathMatchers(Path baseDirPath, String... patterns) {
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

	private Map<String, Path> _getProjectPaths(
			final Path watchPath, List<String> projectPaths, List<String> ignorePaths)
		throws Exception {

		final Map<String, Path> foundProjectPaths = new HashMap<>();

		FileSystem fileSystem = FileSystems.getDefault();

		List<PathMatcher> ignorePathMatchers = ignorePaths.stream(
		).map(
			ignorePath -> fileSystem.getPathMatcher("glob:" + ignorePath)
		).collect(
			Collectors.toList()
		);

		Files.walkFileTree(
			watchPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					boolean shouldIgnorePath = ignorePathMatchers.stream(
					).anyMatch(
						pathMatcher -> pathMatcher.matches(path)
					);

					if (shouldIgnorePath) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					try (Stream<Path> files = Files.list(path)) {
						if (files.map(
								p -> p.getFileName()
							).filter(
								p -> !ignorePathMatchers.stream(
								).anyMatch(
									pathMatcher -> pathMatcher.matches(path.resolve(p))
								)
							).anyMatch(
								p -> projectPaths.stream(
								).anyMatch(
									pp -> Objects.equals(pp, p.toString())
								)
							)) {

							foundProjectPaths.put(_getGradlePath(path, watchPath), path);

							return FileVisitResult.SKIP_SUBTREE;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return foundProjectPaths;
	}

	private void _registerDirectory(WatchService watcher, Map<WatchKey, Path> keys, Path dir) throws IOException {
		WatchKey watchKey = dir.register(
			watcher,
			new WatchEvent.Kind[] {
				StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY
			},
			SensitivityWatchEventModifier.HIGH);

		keys.put(watchKey, dir);
	}

	private void _walkAndRegisterDirectories(
			final WatchService watchService, final Map<WatchKey, Path> watchKeys, final Path basePath,
			final List<PathMatcher> ignorePathMatchers)
		throws IOException {

		Files.walkFileTree(
			basePath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					for (PathMatcher pathMatcher : ignorePathMatchers) {
						if (pathMatcher.matches(path)) {
							return FileVisitResult.SKIP_SUBTREE;
						}
					}

					_registerDirectory(watchService, watchKeys, path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _watch(
			Path watchPath, Map<String, Path> projectPaths, List<String> fastPaths, List<String> ignorePaths,
			boolean deploy)
		throws InterruptedException {

		Thread watchThread = new Thread() {

			@Override
			public void run() {
				BladeCLI bladeCLI = getBladeCLI();

				BaseArgs baseArgs = bladeCLI.getArgs();

				try (final FileSystem fileSystem = FileSystems.getDefault();
					final WatchService watchService = fileSystem.newWatchService()) {

					final Map<WatchKey, Path> watchKeys = new HashMap<>();

					final List<PathMatcher> ignorePathMatchers = _getPathMatchers(
						watchPath, ignorePaths.toArray(new String[0]));

					final List<PathMatcher> fastPathMatchers = _getPathMatchers(
						watchPath, fastPaths.toArray(new String[0]));

					_walkAndRegisterDirectories(watchService, watchKeys, watchPath, ignorePathMatchers);

					final GradleExec gradleExec = new GradleExec(bladeCLI);

					if (deploy) {
						if (!baseArgs.isQuiet()) {
							bladeCLI.out("Deploying...  To skip initial deployment, use `blade watch -s`");
						}

						gradleExec.executeTask("deploy", false);
					}

					while (true) {
						WatchKey watchKey;

						try {
							watchKey = watchService.take();
						}
						catch (InterruptedException ie) {
							continue;
						}

						Path dir = watchKeys.get(watchKey);

						if (dir == null) {
							bladeCLI.error("WatchKey not recognized!!");

							continue;
						}

						for (WatchEvent<?> event : watchKey.pollEvents()) {
							WatchEvent.Kind<?> kind = event.kind();

							Path path = (Path)event.context();

							Path resolvedPath = dir.resolve(path);

							boolean ignoredPath = false;

							for (PathMatcher pathMatcher : ignorePathMatchers) {
								if (pathMatcher.matches(resolvedPath)) {
									ignoredPath = true;

									break;
								}
							}

							if (ignoredPath) {
								continue;
							}

							boolean directory = Files.isDirectory(resolvedPath);

							Path projectPath = _getGradleProjectPath(watchPath, resolvedPath, projectPaths);

							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
								if (directory) {
									try {
										_walkAndRegisterDirectories(
											watchService, watchKeys, resolvedPath, ignorePathMatchers);
									}
									catch (IOException ioe) {
										bladeCLI.error("Could not register directory:" + resolvedPath);
									}
								}

								if (!baseArgs.isQuiet()) {
									bladeCLI.out(resolvedPath + " has been created, deploying...");
								}

								gradleExec.executeTask("deploy", projectPath.toFile(), false);
							}
							else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								if (!baseArgs.isQuiet()) {
									bladeCLI.out(resolvedPath + " has been deleted, redeploying...");
								}

								gradleExec.executeTask("clean deploy", projectPath.toFile(), false);
							}
							else if (!directory) {
								boolean fastExtension = false;

								for (PathMatcher pathMatcher : fastPathMatchers) {
									if (pathMatcher.matches(resolvedPath)) {
										fastExtension = true;

										break;
									}
								}

								if (fastExtension) {
									if (!baseArgs.isQuiet()) {
										bladeCLI.out(resolvedPath + " has changed, fast deploying...");
									}

									gradleExec.executeTask("deployFast -a", projectPath.toFile(), false);
								}
								else {
									System.out.println(resolvedPath + " has changed, deploying...");

									gradleExec.executeTask("deploy -a", projectPath.toFile(), false);
								}
							}

							if (!baseArgs.isQuiet()) {
								bladeCLI.out("Watching files in " + watchPath + ". Press Crtl + C to stop.");
							}
						}

						boolean valid = watchKey.reset();

						if (!valid) {
							watchKeys.remove(watchKey);

							if (watchKeys.isEmpty()) {
								break;
							}
						}
					}
				}
				catch (Exception e) {
					_addError("watch", e.getMessage());

					PrintStream error = bladeCLI.error();

					e.printStackTrace(error);
				}
			}

		};

		watchThread.start();

		watchThread.join();
	}

}