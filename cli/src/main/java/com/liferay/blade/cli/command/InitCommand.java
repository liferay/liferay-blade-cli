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

import aQute.bnd.version.Version;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ProductInfo;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class InitCommand extends BaseCommand<InitArgs> {

	public InitCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		InitArgs initArgs = getArgs();

		String name = initArgs.getName();

		File baseDir = initArgs.getBase();

		if (baseDir == null) {
			baseDir = args.getBase();
		}

		Path correctPath = baseDir.toPath();

		if (Objects.nonNull(name)) {
			correctPath = correctPath.resolve(name);
		}

		correctPath = correctPath.normalize();

		File destDir = correctPath.toFile();

		File temp = null;

		boolean pluginsSDK = _isPluginsSDK(destDir);

		_trace("Using destDir " + destDir);

		if (destDir.exists() && !destDir.isDirectory()) {
			_addError(destDir.getAbsolutePath() + " is not a directory.");

			return;
		}

		String profileName = initArgs.getProfileName();

		boolean mavenBuild = Objects.equals("maven", profileName);

		if (destDir.exists()) {
			if (pluginsSDK) {
				if (!_isPluginsSDK70(destDir)) {
					if (initArgs.isUpgrade()) {
						if (mavenBuild) {
							_addError("Upgrading Plugins SDK in Liferay Maven Workspace not supported.");

							return;
						}

						_trace(
							"Found plugins-sdk 6.2, upgraded to 7.0, moving contents to new subdirectory and initing " +
								"workspace.");

						for (String fileName : _SDK_6_GA5_FILES) {
							File file = new File(destDir, fileName);

							if (file.exists()) {
								file.delete();
							}
						}
					}
					else {
						_addError(
							"Unable to run blade init in Plugins SDK 6.2, please add -u (--upgrade) if you want to " +
								"upgrade to 7.0");

						return;
					}
				}

				_trace("Found Plugins SDK, moving contents to new subdirectory and initing workspace.");

				Path tempDir = Files.createTempDirectory("orignal-sdk");

				temp = tempDir.toFile();

				_moveContentsToDirectory(destDir, temp);
			}
			else {
				String[] files = destDir.list();

				if ((files != null) && (files.length > 0)) {
					if (initArgs.isForce()) {
						_trace("Files found, continuing init.");
					}
					else {
						_addError(
							destDir.getAbsolutePath() +
								" contains files, please move them before continuing or use -f (--force) option to " +
									"init workspace.");

						return;
					}
				}
			}
		}
		else {
			WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

			if (workspaceProvider != null) {
				_addError("blade does not support initializing a workspace inside of another workspace.");

				return;
			}
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		List<File> archetypesDirs = projectTemplatesArgs.getArchetypesDirs();

		Path customTemplatesPath = bladeCLI.getExtensionsPath();

		archetypesDirs.add(customTemplatesPath.toFile());

		if ((name == null) || Objects.equals(name, ".")) {
			name = destDir.getName();
		}

		File destParentDir = destDir.getParentFile();

		projectTemplatesArgs.setDestinationDir(destParentDir);

		if (initArgs.isForce() || initArgs.isUpgrade()) {
			projectTemplatesArgs.setForce(true);
		}

		projectTemplatesArgs.setGradle(!mavenBuild);

		switch (initArgs.getLiferayVersion()) {
			case "7.0":
				initArgs.setLiferayVersion("portal-7.0-ga7");

				break;
			case "7.1":
				initArgs.setLiferayVersion("portal-7.1-ga4");

				break;
			case "7.2":
				initArgs.setLiferayVersion("portal-7.2-ga2");

				break;
			case "7.3":
				initArgs.setLiferayVersion("portal-7.3-ga2");

				break;
		}

		String workspaceProductKey = initArgs.getLiferayVersion();

		Map<String, ProductInfo> productInfos = BladeUtil.getProductInfos();

		ProductInfo productInfo = productInfos.get(workspaceProductKey);

		if (productInfo == null) {
			_addError("Unable to get product info for selected version " + workspaceProductKey);

			return;
		}

		Version targetPlatformVersion = new Version(productInfo.getTargetPlatformVersion());

		initArgs.setLiferayVersion(
			new String(targetPlatformVersion.getMajor() + "." + targetPlatformVersion.getMinor()));

		projectTemplatesArgs.setLiferayVersion(initArgs.getLiferayVersion());

		projectTemplatesArgs.setMaven(mavenBuild);
		projectTemplatesArgs.setName(name);

		String template = "workspace";

		Map<String, String> initTemplates = BladeUtil.getInitTemplates(bladeCLI);

		if (profileName != null) {
			Set<String> templateNames = initTemplates.keySet();
			String customInitTemplateName = "workspace-" + profileName;

			if (templateNames.contains(customInitTemplateName)) {
				template = customInitTemplateName;
			}
		}

		projectTemplatesArgs.setTemplate(template);

		new ProjectTemplates(projectTemplatesArgs);

		if (mavenBuild) {
			FileUtil.deleteFiles(destDir.toPath(), "gradle.properties", "gradle-local.properties");
		}
		else {
			BladeUtil.writePropertyValue(
				new File(destDir, "gradle.properties"), "liferay.workspace.product", workspaceProductKey);

			_setWorkspacePluginVersion(destDir.toPath(), "2.5.3");
		}

		if (pluginsSDK) {
			if (initArgs.isUpgrade() && !mavenBuild) {
				GradleExec gradleExec = new GradleExec(bladeCLI);

				gradleExec.executeTask("upgradePluginsSDK");
			}

			File gitFile = new File(temp, ".git");

			if (gitFile.exists()) {
				File destGitFile = new File(destDir, ".git");

				_moveContentsToDirectory(gitFile, destGitFile);

				FileUtil.deleteDir(gitFile.toPath());
			}

			File pluginsSdkDir = new File(destDir, "plugins-sdk");

			_moveContentsToDirectory(temp, pluginsSdkDir);

			FileUtil.deleteDir(temp.toPath());
		}

		args.setBase(destDir);

		BladeSettings settings = bladeCLI.getBladeSettings();

		if (profileName == null) {
			profileName = "gradle";
		}

		settings.setProfileName(profileName);

		settings.save();
	}

	@Override
	public Class<InitArgs> getArgsClass() {
		return InitArgs.class;
	}

	private void _addError(String msg) {
		getBladeCLI().addErrors("init", Collections.singleton(msg));
	}

	private boolean _isPluginsSDK(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		List<String> names = Arrays.asList(dir.list());

		if ((names != null) && names.contains("portlets") && names.contains("hooks") && names.contains("layouttpl") &&
			names.contains("themes") && names.contains("build.properties") && names.contains("build.xml") &&
			names.contains("build-common.xml") && names.contains("build-common-plugin.xml")) {

			return true;
		}

		return false;
	}

	private boolean _isPluginsSDK70(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		File buildProperties = new File(dir, "build.properties");
		Properties properties = new Properties();

		InputStream in = null;

		try {
			in = new FileInputStream(buildProperties);

			properties.load(in);

			String sdkVersionValue = (String)properties.get("lp.version");

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

	private void _moveContentsToDirectory(File src, File dest) throws IOException {
		Path srcPath = src.toPath();

		Path source = srcPath.toAbsolutePath();

		Path destPath = dest.toPath();

		Path target = destPath.toAbsolutePath();

		Files.walkFileTree(
			source,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					File file = dir.toFile();

					String dirName = file.getName();

					if (!dirName.equals(src.getName())) {
						Files.delete(dir);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path targetDir = target.resolve(source.relativize(dir));

					if (!Files.exists(targetDir)) {
						Files.createDirectory(targetDir);
					}

					File file = dir.toFile();

					if (BladeUtil.isWindows() && !file.canWrite()) {
						Files.setAttribute(dir, "dos:readonly", false);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					Path targetFile = target.resolve(source.relativize(path));

					if (!Files.exists(targetFile)) {
						Files.copy(path, targetFile);
					}

					File file = path.toFile();

					if (BladeUtil.isWindows() && !file.canWrite()) {
						Files.setAttribute(path, "dos:readonly", false);
					}

					Files.delete(path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _setWorkspacePluginVersion(Path path, String version) throws IOException {
		Path settingsPath = path.resolve("settings.gradle");

		String content = new String(Files.readAllBytes(settingsPath));

		String updated = content.replaceFirst("2\\.5\\.[0-9]", version);

		Files.write(settingsPath, updated.getBytes());
	}

	private void _trace(String msg) {
		getBladeCLI().trace("%s: %s", "init", msg);
	}

	private static final String[] _SDK_6_GA5_FILES = {
		"app-servers.gradle", "build.gradle", "build-plugins.gradle", "build-themes.gradle", "sdk.gradle",
		"settings.gradle", "util.gradle", "versions.gradle"
	};

}