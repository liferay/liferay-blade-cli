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

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class InitCommand {

	private final static String _INSTALLER_PLUGINS_SDK_PATH = "com.liferay.portal.plugins.sdk-1.0.11-withdependencies";
	private final static String _INSTALLER_PLUGINS_SDK_ZIP = _INSTALLER_PLUGINS_SDK_PATH + ".zip";
	private final static String _PLUGINS_SDK_URL =
		"https://cdn.lfrs.sl/repository.liferay.com/nexus/content/groups/public/com/liferay/portal/com.liferay.portal.plugins.sdk/1.0.11/" +
			_INSTALLER_PLUGINS_SDK_ZIP;

	private final static String[] _SDK_6_GA5_FILES = {
		"app-servers.gradle", "build.gradle", "build-plugins.gradle",
		"build-themes.gradle", "sdk.gradle", "settings.gradle",
		"util.gradle", "versions.gradle" };

	public static final String DESCRIPTION =
		"Initializes a new Liferay workspace";

	public InitCommand(blade blade, InitOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		String name = args.size() > 0 ? args.get(0) : null;

		File destDir = name != null ? new File(
			_blade.getBase(), name) : _blade.getBase();

		trace("Using destDir " + destDir);

		if (destDir.exists() && !destDir.isDirectory()) {
			addError(destDir.getAbsolutePath() + " is not a directory.");
			return;
		}

		if (destDir.exists()) {
			if (isPluginsSDK(destDir)) {
				if (!isPluginsSDK70(destDir)) {
					if (_options.upgrade()) {
						trace(
							"Found plugins-sdk 6.2, upgraded to 7.0, moving contents to new subdirectory " +
								"and initing workspace.");

						File sdk7zip = new File (_blade.getBundleDir(), _INSTALLER_PLUGINS_SDK_ZIP);

						if (!sdk7zip.exists()) {
							trace("Downloading " + _PLUGINS_SDK_URL);
							FileUtils.copyURLToFile(new URL(_PLUGINS_SDK_URL), sdk7zip);
						}

						try {
							Util.unzip(sdk7zip, destDir, _INSTALLER_PLUGINS_SDK_PATH + "/");
						}
						catch (Exception e) {
							addError("Opening zip file error, "
								+ "please delete zip file: " +
									sdk7zip.getPath());
							return;
						}

						for (String fileName : _SDK_6_GA5_FILES) {
							File file = new File(destDir, fileName);

							if (file.exists()) {
								file.delete();
							}
						}
					}
					else {
						addError("Unable to run blade init in plugins sdk 6.2, please add -u (--upgrade)"
							+ " if you want to upgrade to 7.0");
						return;
					}
				}

				trace("Found plugins-sdk, moving contents to new subdirectory " +
					"and initing workspace.");

				File pluginsSdkDir = new File(destDir, "plugins-sdk");

				moveContentsToDir(destDir, pluginsSdkDir);
			}
			else if (destDir.list().length > 0) {
				if (_options.force()) {
					trace("Files found, initing anyways.");
				}
				else {
					addError(
						destDir.getAbsolutePath() +
						" contains files, please move them before continuing " +
							"or use -f (--force) option to init workspace " +
								"anyways.");
					return;
				}
			}
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		if (name == null) {
			name = destDir.getName();
		}

		File destParentDir = destDir.getParentFile();

		projectTemplatesArgs.setDestinationDir(destParentDir);

		if (_options.force() || _options.upgrade()) {
			projectTemplatesArgs.setForce(true);
		}

		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setTemplate("workspace");

		new ProjectTemplates(projectTemplatesArgs);
	}

	@Arguments(arg = "[name]")
	@Description(DESCRIPTION)
	public interface InitOptions extends Options {

		@Description(
				"create anyway if there are files located at target folder")
		public boolean force();

		@Description("force to refresh workspace template")
		public boolean refresh();

		@Description("upgrade plugins-sdk from 6.2 to 7.0")
		public boolean upgrade();
	}

	private void addError(String msg) {
		_blade.addErrors("init", Collections.singleton(msg));
	}

	private boolean isPluginsSDK(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		List<String> names = Arrays.asList(dir.list());

		return names != null &&
			names.contains("portlets") &&
			names.contains("hooks") &&
			names.contains("layouttpl") &&
			names.contains("themes") &&
			names.contains("build.properties") &&
			names.contains("build.xml") &&
			names.contains("build-common.xml") &&
			names.contains("build-common-plugin.xml");
	}

	private boolean isPluginsSDK70(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		File buildProperties = new File(dir, "build.properties");
		Properties properties = new Properties();

		InputStream in = null;

		try {
			in = new FileInputStream(buildProperties);

			properties.load(in);

			String sdkVersionValue = (String) properties.get("lp.version");

			if (sdkVersionValue.equals("7.0.0")) {
				return true;
			}
		}
		catch (Exception e) {
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
				}
			}
		}

		return false;
	}

	private void moveContentsToDir(File src, File dest)
		throws IOException {

		final String ignoreName = dest.getName();

		File[] filesToCopy = src.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".git") && !name.equals(ignoreName);
			}
		});

		dest.mkdirs();

		for (File fileToCopy : filesToCopy) {
			IO.copy(fileToCopy, new File(dest, fileToCopy.getName()));

			Files.walkFileTree(fileToCopy.toPath(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (Util.isWindows() && !dir.toFile().canWrite()) {
						Files.setAttribute(dir, "dos:readonly", false);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (Util.isWindows() && !file.toFile().canWrite()) {
						Files.setAttribute(file, "dos:readonly", false);
					}

					return FileVisitResult.CONTINUE;
				}
			});

			IO.deleteWithException(fileToCopy);
		}
	}

	private void trace(String msg) {
		_blade.trace("%s: %s", "init", msg);
	}

	private final blade _blade;
	private final InitOptions _options;

}