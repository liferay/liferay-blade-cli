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

import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.BladeSettings;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Gregory Amerson
 * @author David Truong
 * @author Christopher Bryan Boyd
 * @author Charles Wu
 */
public class CreateCommand extends BaseCommand<CreateArgs> {

	public CreateCommand() {
	}

	public CreateCommand(BladeCLI bladeCLI) {
		super(bladeCLI, null);
	}

	@Override
	public void execute() throws Exception {
		CreateArgs createArgs = getArgs();

		String template = createArgs.getTemplate();

		if (Objects.equals(template, "portlet")) {
			template = "mvc-portlet";
		}

		BladeCLI bladeCLI = getBladeCLI();

		if (template.equals("service")) {
			if (createArgs.getService() == null) {
				StringBuilder sb = new StringBuilder();

				sb.append("\"-t service <FQCN>\" parameter missing.");
				sb.append(System.lineSeparator());
				sb.append("Usage: blade create -t service -s <FQCN> <project name>");
				sb.append(System.lineSeparator());

				bladeCLI.error(sb.toString());

				throw new ParameterException("The following option is required: [-s]");
			}
		}
		else if (template.equals("fragment")) {
			boolean hasHostBundleBSN = false;

			if (createArgs.getHostBundleBSN() != null) {
				hasHostBundleBSN = true;
			}

			boolean hasHostBundleVersion = false;

			if (createArgs.getHostBundleVersion() != null) {
				hasHostBundleVersion = true;
			}

			if (!hasHostBundleBSN || !hasHostBundleVersion) {
				if (!hasHostBundleBSN) {
					throw new ParameterException("The following option is required: [--host-bundle-bsn | -h]");
				}

				if (!hasHostBundleVersion) {
					throw new ParameterException("The following option is required: [--host-bundle-version | -H]");
				}
			}
		}
		else if (template.equals("modules-ext")) {
			if (Objects.equals("maven", createArgs.getProfileName())) {
				bladeCLI.error(
					"Modules Ext projects are not supported with Maven build. Please use Gradle build instead.");

				return;
			}

			boolean hasOriginalModuleName = false;

			if (createArgs.getOriginalModuleName() != null) {
				hasOriginalModuleName = true;
			}

			if (!hasOriginalModuleName) {
				StringBuilder sb = new StringBuilder();

				sb.append("modules-ext options missing:");
				sb.append(System.lineSeparator());
				sb.append("\"-m\", \"--original-module-name\") is required.");
				sb.append(System.lineSeparator());
				sb.append(
					"\"-M\", \"--original-module-version\") is required unless you have enabled target platform.");
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());

				bladeCLI.printUsage("create", sb.toString());

				throw new ParameterException("The following option is required: [--original-module-name]");
			}
		}

		String name = createArgs.getName();

		if (BladeUtil.isEmpty(name)) {
			_addError("Create", "SYNOPSIS\n\t create [options] <[name]>");

			return;
		}

		if (!_isExistingTemplate(template)) {
			_addError("Create", "The template " + template + " is not in the list");

			return;
		}

		File dir;

		File argsDir = createArgs.getDir();

		if (argsDir != null) {
			dir = new File(argsDir.getAbsolutePath());
		}
		else if (template.equals("war-core-ext") || template.startsWith("modules-ext")) {
			dir = _getDefaultExtDir();
		}
		else if (template.startsWith("war") || template.equals("theme") || template.equals("layout-template") ||
				 template.equals("spring-mvc-portlet")) {

			dir = _getDefaultWarsDir();
		}
		else {
			dir = _getDefaultModulesDir();
		}

		final File checkDir = new File(dir, name);

		if (!_checkDir(checkDir)) {
			_addError("Create", name + " is not empty or it is a file. Please clean or delete it then run again");

			return;
		}

		ProjectTemplatesArgs projectTemplatesArgs = getProjectTemplateArgs(createArgs, bladeCLI, template, name, dir);

		List<File> archetypesDirs = projectTemplatesArgs.getArchetypesDirs();

		Path customTemplatesPath = bladeCLI.getExtensionsPath();

		archetypesDirs.add(customTemplatesPath.toFile());

		execute(projectTemplatesArgs);

		Path path = dir.toPath();

		Path absolutePath = path.toAbsolutePath();

		absolutePath = absolutePath.normalize();

		bladeCLI.out("Successfully created project " + projectTemplatesArgs.getName() + " in " + absolutePath);
	}

	@Override
	public Class<CreateArgs> getArgsClass() {
		return CreateArgs.class;
	}

	public boolean isWorkspace(File file) {
		BladeCLI bladeCLI = getBladeCLI();

		if (bladeCLI.getWorkspaceProvider(file) != null) {
			return true;
		}

		return false;
	}

	protected void execute(ProjectTemplatesArgs projectTemplatesArgs) throws Exception {
		File dir = projectTemplatesArgs.getDestinationDir();
		String name = projectTemplatesArgs.getName();

		Map<String, String> properties = null;
		CreateArgs args = getArgs();

		if (args != null) {
			properties = getProjectTemplateArgsExtProperties(getArgs());
		}

		new ProjectTemplates(projectTemplatesArgs, properties);

		File gradlew = new File(dir, name + "/gradlew");

		if (gradlew.exists()) {
			gradlew.setExecutable(true);
		}
	}

	protected ProjectTemplatesArgs getProjectTemplateArgs(
			CreateArgs createArgs, BladeCLI bladeCLI, String template, String name, File dir)
		throws IOException {

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setGradle(true);
		projectTemplatesArgs.setMaven(false);

		projectTemplatesArgs.setClassName(createArgs.getClassname());

		projectTemplatesArgs.setDestinationDir(dir.getAbsoluteFile());

		File baseDir = new File(createArgs.getBase());

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider instanceof GradleWorkspaceProvider) {
			GradleWorkspaceProvider workspaceProviderGradle = (GradleWorkspaceProvider)workspaceProvider;

			projectTemplatesArgs.setDependencyManagementEnabled(
				workspaceProviderGradle.isDependencyManagementEnabled(baseDir));
		}

		projectTemplatesArgs.setLiferayVersion(_getLiferayVersion(bladeCLI, createArgs));

		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setPackageName(createArgs.getPackageName());

		projectTemplatesArgs.setTemplate(template);

		return projectTemplatesArgs;
	}

	protected Map<String, String> getProjectTemplateArgsExtProperties(CreateArgs createArgs) {
		Map<String, String> properties = new HashMap<>();

		properties.put("setContributorType", createArgs.getContributorType());
		properties.put("setDependencyInjector", createArgs.getDependencyInjector());
		properties.put("setFramework", createArgs.getFramework());
		properties.put("setFrameworkDependencies", createArgs.getFrameworkDependencies());
		properties.put("setHostBundleSymbolicName", createArgs.getHostBundleBSN());
		properties.put("setHostBundleVersion", createArgs.getHostBundleVersion());
		properties.put("setOriginalModuleName", createArgs.getOriginalModuleName());
		properties.put("setOriginalModuleVersion", createArgs.getOriginalModuleVersion());
		properties.put("setService", createArgs.getService());
		properties.put("setViewType", createArgs.getViewType());

		WorkspaceProvider workspaceProvider = getBladeCLI().getWorkspaceProvider(new File(createArgs.getBase()));

		try {
			if (workspaceProvider != null) {
				File workspaceLocation = workspaceProvider.getWorkspaceDir(getBladeCLI());

				if (workspaceLocation != null) {
					properties.put("setModulesLocation", _getDefaultModulesDir().toString());
					properties.put("setWorkspaceLocation", workspaceLocation.toString());
				}
			}
		}
		catch (Exception e) {
			getBladeCLI().error(e);
		}

		return properties;
	}

	protected Properties getWorkspaceProperties() {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File baseDir = new File(args.getBase());

		GradleWorkspaceProvider workspaceProviderGradle = (GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(
			baseDir);

		return workspaceProviderGradle.getGradleProperties(baseDir);
	}

	private static boolean _checkDir(File file) {
		if (file.exists()) {
			if (!file.isDirectory()) {
				return false;
			}

			File[] children = file.listFiles();

			if (BladeUtil.isNotEmpty(children)) {
				return false;
			}
		}

		return true;
	}

	private static boolean _containsDir(File currentDir, File parentDir) throws Exception {
		String currentPath = currentDir.getCanonicalPath();

		String parentPath = parentDir.getCanonicalPath();

		return currentPath.startsWith(parentPath);
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private File _getDefaultExtDir() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File base = new File(args.getBase());

		File baseDir = base.getCanonicalFile();

		if (!isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = getWorkspaceProperties();

		String extDirProperty = (String)properties.get(WorkspaceConstants.DEFAULT_EXT_DIR_PROPERTY);

		if (extDirProperty == null) {
			extDirProperty = WorkspaceConstants.DEFAULT_EXT_DIR;
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		File projectDir = workspaceProvider.getWorkspaceDir(baseDir);

		File extDir = new File(projectDir, extDirProperty);

		if (_containsDir(baseDir, extDir)) {
			return baseDir;
		}

		return extDir;
	}

	private File _getDefaultModulesDir() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File base = new File(args.getBase());

		File baseDir = base.getCanonicalFile();

		if (!isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = getWorkspaceProperties();

		String modulesDirValue = (String)properties.get(WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY);

		if (modulesDirValue == null) {
			modulesDirValue = WorkspaceConstants.DEFAULT_MODULES_DIR;
		}

		if (modulesDirValue.contains(",")) {
			bladeCLI.out(
				"WARNING: " + WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY + " has multiple values: " +
					modulesDirValue);
			modulesDirValue = modulesDirValue.substring(0, modulesDirValue.indexOf(","));

			bladeCLI.out("WARNING: using " + modulesDirValue);
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		File projectDir = workspaceProvider.getWorkspaceDir(baseDir);

		File modulesDir = new File(projectDir, modulesDirValue);

		if (_containsDir(baseDir, modulesDir)) {
			return baseDir;
		}

		return modulesDir;
	}

	private File _getDefaultWarsDir() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File base = new File(args.getBase());

		File baseDir = base.getCanonicalFile();

		if (!isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = getWorkspaceProperties();

		String warsDirValue = (String)properties.get(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);

		if (warsDirValue == null) {
			warsDirValue = WorkspaceConstants.DEFAULT_WARS_DIR;
		}

		if (warsDirValue.contains(",")) {
			warsDirValue = warsDirValue.split(",")[0];
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		File projectDir = workspaceProvider.getWorkspaceDir(baseDir);

		File warsDir = new File(projectDir, warsDirValue);

		if (_containsDir(baseDir, warsDir)) {
			return baseDir;
		}

		return warsDir;
	}

	private String _getLiferayVersion(BladeCLI bladeCLI, CreateArgs createArgs) throws IOException {
		String liferayVersion = createArgs.getLiferayVersion();

		if (liferayVersion == null) {
			BladeSettings bladeSettings = bladeCLI.getBladeSettings();

			liferayVersion = bladeSettings.getLiferayVersionDefault();
		}

		return liferayVersion;
	}

	private boolean _isExistingTemplate(String templateName) throws Exception {
		Collection<String> templateNames = BladeUtil.getTemplateNames(getBladeCLI());

		return templateNames.contains(templateName);
	}

}