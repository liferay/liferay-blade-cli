/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import aQute.bnd.version.Version;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.ReleaseInfo;
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
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class InitCommand extends BaseCommand<InitArgs> {

	public InitCommand() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		InitArgs initArgs = getArgs();

		if (initArgs.isList()) {
			List<String> keys = BladeUtil.getWorkspaceProductKeys(!initArgs.isAll());

			keys.forEach(bladeCLI::out);

			return;
		}

		String name = initArgs.getName();

		File baseDir = initArgs.getBase();

		BaseArgs args = bladeCLI.getArgs();

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

		boolean mavenBuild = Objects.equals(profileName, "maven");

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

		String liferayVersion;
		String workspaceProductKey;

		Map<String, Object> releasesInfos = BladeUtil.getReleasesInfos(initArgs.isTrace(), bladeCLI.error());

		if (!mavenBuild) {
			workspaceProductKey = _getDefaultProductKey(initArgs);

			if (_legacyProductKeys.contains(workspaceProductKey)) {
				_addError(
					"This version of blade does not support " + workspaceProductKey + ". Please use blade 3.9.2 to " +
						"initialize a workspace with this version. https://bit.ly/3lVgTeH");

				return;
			}

			Object productInfoObject = releasesInfos.get(workspaceProductKey);

			if (productInfoObject == null) {
				_addError("Unable to get product info for selected version " + workspaceProductKey);

				return;
			}

			ReleaseInfo releaseInfo = BladeUtil.getReleaseInfo(workspaceProductKey);

			String targetPlatformVersion = releaseInfo.getTargetPlatformVersion();

			if (releaseInfo.getProductKey().isQuarterly()) {
				liferayVersion = "7.4";
			}
			else {
				Version normalTargetPlatformVersion = _makeCompatibleVersion(targetPlatformVersion);

				liferayVersion =
					normalTargetPlatformVersion.getMajor() + "." + normalTargetPlatformVersion.getMinor() + "." +
						normalTargetPlatformVersion.getMicro();
			}
		}
		else {
			workspaceProductKey = _setProductAndVersionForMaven(releasesInfos, initArgs);

			liferayVersion = initArgs.getLiferayVersion();
		}

		Object productInfoObject = releasesInfos.get(workspaceProductKey);

		if (productInfoObject == null) {
			_addError("Unable to get product info for selected version " + workspaceProductKey);

			return;
		}

		if (Objects.equals(initArgs.getLiferayProduct(), "commerce")) {
			initArgs.setLiferayProduct("dxp");
		}

		projectTemplatesArgs.setLiferayVersion(liferayVersion);

		projectTemplatesArgs.setMaven(mavenBuild);
		projectTemplatesArgs.setName(name);

		if (mavenBuild) {
			projectTemplatesArgs.setLiferayProduct(initArgs.getLiferayProduct());
		}

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

	private String _getDefaultProductKey(InitArgs initArgs) throws Exception {
		String liferayVersion = initArgs.getLiferayVersion();

		if (liferayVersion.startsWith("portal") || liferayVersion.startsWith("dxp") ||
			liferayVersion.startsWith("commerce")) {

			return initArgs.getLiferayVersion();
		}

		List<String> productInfoKeys = BladeUtil.getWorkspaceProductKeys(false);

		Optional<String> defaultVersion = productInfoKeys.stream(
		).filter(
			value -> value.startsWith(initArgs.getLiferayProduct() + "-" + initArgs.getLiferayVersion())
		).findFirst();

		if (!defaultVersion.isPresent()) {
			return initArgs.getLiferayVersion();
		}

		return defaultVersion.get();
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
		catch (Exception exception) {
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception exception) {
				}
			}
		}

		return false;
	}

	private Version _makeCompatibleVersion(String targetPlatformVersion) {
		int dash = targetPlatformVersion.indexOf("-");

		Version productTargetPlatformVersion = null;

		if (dash != -1) {
			productTargetPlatformVersion = Version.parseVersion(targetPlatformVersion.substring(0, dash));
		}
		else {
			productTargetPlatformVersion = Version.parseVersion(targetPlatformVersion);
		}

		return productTargetPlatformVersion;
	}

	private void _moveContentsToDirectory(File src, File dest) throws Exception {
		Path srcPath = src.toPath();

		Path source = srcPath.toAbsolutePath();

		Path destPath = dest.toPath();

		Path target = destPath.toAbsolutePath();

		Files.walkFileTree(
			source,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException ioException) throws IOException {
					File file = dir.toFile();

					String dirName = file.getName();

					if (!dirName.equals(src.getName())) {
						Files.delete(dir);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes basicFileAttributes)
					throws IOException {

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
				public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

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

	@SuppressWarnings("unchecked")
	private String _setProductAndVersionForMaven(Map<String, Object> productInfos, InitArgs initArgs) throws Exception {
		String possibleProductKey = _getDefaultProductKey(initArgs);

		if (possibleProductKey.startsWith("portal") || possibleProductKey.startsWith("dxp") ||
			possibleProductKey.startsWith("commerce")) {

			ReleaseInfo releaseInfo = BladeUtil.getReleaseInfo(possibleProductKey);

			initArgs.setLiferayVersion(releaseInfo.getTargetPlatformVersion());

			String[] productKeyValues = possibleProductKey.split("-");

			initArgs.setLiferayProduct(productKeyValues[0]);

			return possibleProductKey;
		}

		for (Map.Entry<String, Object> entryKey : productInfos.entrySet()) {
			ReleaseInfo releaseInfo = BladeUtil.getReleaseInfo(entryKey.getKey());

			if (Objects.equals(possibleProductKey, releaseInfo.getTargetPlatformVersion())) {
				possibleProductKey = entryKey.getKey();

				String[] productKeyValues = possibleProductKey.split("-");

				initArgs.setLiferayProduct(productKeyValues[0]);

				return possibleProductKey;
			}
		}

		return null;
	}

	private void _setWorkspacePluginVersion(Path path, String version) throws Exception {
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

	private static List<String> _legacyProductKeys = Arrays.asList(
		"portal-7.0-ga1", "portal-7.0-ga2", "portal-7.0-ga3", "portal-7.0-ga4", "portal-7.0-ga5", "portal-7.0-ga6");

}