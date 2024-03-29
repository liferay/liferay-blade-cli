/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.Extensions;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.StringUtil;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;
import com.liferay.project.templates.extensions.util.VersionUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gregory Amerson
 * @author David Truong
 * @author Christopher Bryan Boyd
 * @author Charles Wu
 * @author Seiphon Wang
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

		String name = createArgs.getName();

		if (!_isValidName(name)) {
			return;
		}

		if (!_isExistingTemplate(template)) {
			_addError("Create", "The template " + template + " is not in the list");

			return;
		}

		File dir;

		File argsDir = createArgs.getDir();

		if (Objects.equals(template, "client-extension") && Objects.isNull(argsDir)) {
			Path destinationPath = BladeUtil.getCurrentPath();

			argsDir = destinationPath.toFile();
		}

		File baseDir = createArgs.getBase();

		if (!_isWorkspaceDir(argsDir) && !_isWorkspaceDir(baseDir)) {
			_addError(
				"Create",
				"The indicated directory is not a Liferay workspace. Please specify the directory inside a workspace," +
					"or invoke blade from within a Liferay workspace project.");

			return;
		}

		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		String profileName = baseArgs.getProfileName();

		boolean defaultModulesDirSet = false;
		boolean legacyDefaultWarsDirSet = false;

		if (profileName.equals("gradle")) {
			Properties properties = getWorkspaceProperties();

			String defaultModulesDir = (String)properties.get(WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY);
			String legacyDefaultWarsDir = (String)properties.get(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);

			defaultModulesDirSet = (defaultModulesDir != null) && !defaultModulesDir.isEmpty();
			legacyDefaultWarsDirSet = (legacyDefaultWarsDir != null) && !legacyDefaultWarsDir.isEmpty();
		}

		if (argsDir != null) {
			dir = new File(argsDir.getAbsolutePath());
		}
		else if (template.equals("war-core-ext") || template.startsWith("modules-ext")) {
			dir = _getDefaultExtDir();
		}
		else if (template.equals("js-theme")) {
			dir = _getDefaultThemesDir();
		}
		else if (legacyDefaultWarsDirSet &&
				 (template.startsWith("war") || template.equals("theme") || template.equals("layout-template") ||
				  template.equals("spring-mvc-portlet"))) {

			dir = _getDefaultWarsDir();
		}
		else if (defaultModulesDirSet) {
			dir = _getDefaultModulesDir();
		}
		else {
			dir = createArgs.getBase();
		}

		if (dir == null) {
			dir = baseArgs.getBase();
		}

		dir = new File(dir.getCanonicalPath());

		final File checkDir = new File(dir, name);

		if (!_checkDir(checkDir)) {
			_addError("Create", name + " is not empty or it is a file. Please clean or delete it then run again");

			return;
		}

		ProjectTemplatesArgs projectTemplatesArgs = getProjectTemplateArgs(createArgs, bladeCLI, template, name, dir);

		File templateFile = ProjectTemplatesUtil.getTemplateFile(projectTemplatesArgs);

		if (templateFile == null) {
			_addError("Create", "Could not get templateFile for " + template);

			return;
		}

		String templateValidateStrig = _checkTemplateVersionRange(templateFile, projectTemplatesArgs);

		if (!StringUtil.isNullOrEmpty(templateValidateStrig)) {
			getBladeCLI().error(templateValidateStrig);

			return;
		}

		Thread thread = Thread.currentThread();

		ClassLoader oldContextClassLoader = thread.getContextClassLoader();

		Method m = null;

		try {
			URI uri = templateFile.toURI();

			thread.setContextClassLoader(new URLClassLoader(new URL[] {uri.toURL()}));

			m = ProjectTemplates.class.getDeclaredMethod("_getProjectTemplateArgsExt", String.class, File.class);

			m.setAccessible(true); //if security settings allow this

			Object o = m.invoke(null, projectTemplatesArgs.getTemplate(), templateFile); //use null if the method is static

			if (o != null) {
				ProjectTemplatesArgsExt projectTemplatesArgsExt = (ProjectTemplatesArgsExt)o;

				Class<? extends ProjectTemplatesArgsExt> argsClass = projectTemplatesArgsExt.getClass();

				for (Field field : argsClass.getDeclaredFields()) {
					if (field.isAnnotationPresent(Parameter.class)) {
						Parameter parameterAnnotation = field.getDeclaredAnnotation(Parameter.class);

						String[] parameterAnnotationNames = parameterAnnotation.names();

						if (parameterAnnotation.required()) {
							List<String> parameterNamesList = Arrays.asList(parameterAnnotationNames);

							for (Field createField : CreateArgs.class.getDeclaredFields()) {
								if (createField.isAnnotationPresent(Parameter.class)) {
									Parameter createParameterAnnotation = createField.getDeclaredAnnotation(
										Parameter.class);

									String[] createParameterAnnotationNames = createParameterAnnotation.names();

									List<String> createParameterNamesList = Arrays.asList(
										createParameterAnnotationNames);

									boolean found = false;

									for (String createParameterName : createParameterNamesList) {
										if (parameterNamesList.contains(createParameterName)) {
											found = true;

											break;
										}
									}

									if (found) {
										createField.setAccessible(true);

										Object value = createField.get(createArgs);

										if (value == null) {
											StringBuilder sb = new StringBuilder("The following option is required: [");

											for (int x = 0; x < createParameterNamesList.size(); x++) {
												String parameterName = createParameterNamesList.get(x);

												if (x > 0) {
													sb.append(" | ");
												}

												sb.append(parameterName);
											}

											sb.append("] ");

											sb.append(parameterAnnotation.description());

											throw new ParameterException(sb.toString());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		finally {
			if (m != null) {
				m.setAccessible(false);
			}

			thread.setContextClassLoader(oldContextClassLoader);
		}

		execute(projectTemplatesArgs);

		if (!createArgs.isQuiet()) {
			Path path = dir.toPath();

			Path absolutePath = path.toAbsolutePath();

			absolutePath = absolutePath.normalize();

			bladeCLI.out("Successfully created project " + projectTemplatesArgs.getName() + " in " + absolutePath);
		}
	}

	@Override
	public Class<CreateArgs> getArgsClass() {
		return CreateArgs.class;
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

		Path extensionsPath = bladeCLI.getExtensionsPath();

		Extensions extensions = bladeCLI.getExtensions();

		Path extensionsTemplatesPath = extensions.getTemplatesPath();

		List<File> archetypesDirs = new ArrayList<>();

		archetypesDirs.add(extensionsPath.toFile());
		archetypesDirs.add(extensionsTemplatesPath.toFile());

		projectTemplatesArgs.setArchetypesDirs(archetypesDirs);

		projectTemplatesArgs.setClassName(createArgs.getClassName());

		projectTemplatesArgs.setDestinationDir(dir.getAbsoluteFile());

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(dir);

		projectTemplatesArgs.setDependencyManagementEnabled(
			(workspaceProvider != null) ? workspaceProvider.isDependencyManagementEnabled(dir) : false);

		Optional<String> liferayVersion = _getLiferayVersion(workspaceProvider, createArgs);

		if (!liferayVersion.isPresent()) {
			throw new IOException("Cannot determine Liferay Version. Please enter a valid value for Liferay Version.");
		}

		projectTemplatesArgs.setLiferayVersion(liferayVersion.get());

		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setPackageName(createArgs.getPackageName());

		Optional<String> product = _getProduct(workspaceProvider, createArgs);

		projectTemplatesArgs.setLiferayProduct(product.orElse(createArgs.getLiferayProduct()));

		projectTemplatesArgs.setTemplate(template);

		return projectTemplatesArgs;
	}

	protected Map<String, String> getProjectTemplateArgsExtProperties(CreateArgs createArgs) {
		Map<String, String> properties = new HashMap<>();

		properties.put("setAddOns", createArgs.getAddOns());
		properties.put("setContributorType", createArgs.getContributorType());
		properties.put("setDependencyInjector", createArgs.getDependencyInjector());
		properties.put("setExtensionName", createArgs.getExtensionName());
		properties.put("setExtensionType", createArgs.getExtensionType());
		properties.put("setFramework", createArgs.getFramework());
		properties.put("setFrameworkDependencies", createArgs.getFrameworkDependencies());
		properties.put("setHostBundleSymbolicName", createArgs.getHostBundleBSN());
		properties.put("setHostBundleVersion", createArgs.getHostBundleVersion());
		properties.put("setJSFramework", createArgs.getJSFramework());
		properties.put("setOriginalModuleName", createArgs.getOriginalModuleName());
		properties.put("setService", createArgs.getService());
		properties.put("setViewType", createArgs.getViewType());

		BladeCLI bladeCLI = getBladeCLI();

		File dir = createArgs.getDir();

		if (dir == null) {
			dir = createArgs.getBase();
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(dir);

		try {
			if (workspaceProvider != null) {
				properties.put("setBatchModel", Boolean.toString(!createArgs.isJsInteractiveModel()));

				String product = workspaceProvider.getProduct(dir);
				String liferayVersion = workspaceProvider.getLiferayVersion(dir);

				properties.put("setPlatform", product + "-" + liferayVersion.substring(0, 3));

				properties.put("setProjectType", createArgs.getJsProjectType());
				properties.put("setTarget", createArgs.getJsProjectTarget());

				File workspaceLocation = workspaceProvider.getWorkspaceDir(bladeCLI);

				if (workspaceLocation != null) {
					properties.put("setModulesLocation", _getDefaultModulesDir().toString());
					properties.put("setWorkspaceLocation", workspaceLocation.toString());
				}
			}
		}
		catch (Exception exception) {
			bladeCLI.error(exception);
		}

		return properties;
	}

	protected Properties getWorkspaceProperties() {
		BladeCLI bladeCLI = getBladeCLI();

		CreateArgs args = getArgs();

		File argsDir = args.getDir();

		File baseDir = args.getBase();

		GradleWorkspaceProvider gradleWorkspaceProvider;

		File workspaceDir;

		if (bladeCLI.isWorkspaceDir(argsDir)) {
			gradleWorkspaceProvider = (GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(argsDir);

			workspaceDir = gradleWorkspaceProvider.getWorkspaceDir(argsDir);
		}
		else if (bladeCLI.isWorkspaceDir(baseDir)) {
			gradleWorkspaceProvider = (GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(baseDir);

			workspaceDir = gradleWorkspaceProvider.getWorkspaceDir(baseDir);
		}
		else {
			return null;
		}

		return gradleWorkspaceProvider.getGradleProperties(gradleWorkspaceProvider.getWorkspaceDir(workspaceDir));
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private boolean _checkDir(File file) {
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

	private String _checkTemplateVersionRange(File templateFile, ProjectTemplatesArgs projectTemplatesArgs) {
		String versionString = projectTemplatesArgs.getLiferayVersion();

		if (VersionUtil.isLiferayQuarterlyVersion(versionString)) {
			return "";
		}

		try (InputStream fileInputStream = Files.newInputStream(templateFile.toPath(), StandardOpenOption.READ);
			JarInputStream in = new JarInputStream(fileInputStream)) {

			Manifest manifest = in.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			String versionRangeValue = attributes.getValue("Liferay-Versions");

			VersionRange versionRange = new VersionRange(versionRangeValue);

			String liferayVersionString = String.format(
				"%s.%s", VersionUtil.getMajorVersion(versionString), VersionUtil.getMinorVersion(versionString));

			if (!versionRange.includes(Version.parseVersion(liferayVersionString))) {
				return String.format(
					"Error: The %s project can only be created in liferay version range: %s, current liferay version " +
						"is %s.",
					projectTemplatesArgs.getTemplate(), versionRange, liferayVersionString);
			}
		}
		catch (Exception exception) {
			return exception.getMessage();
		}

		return "";
	}

	private boolean _containsDir(File currentDir, File parentDir) throws Exception {
		File currentFile = currentDir.getCanonicalFile();

		Path currentPath = currentFile.toPath();

		String parentPath = parentDir.getCanonicalPath();

		return currentPath.startsWith(parentPath);
	}

	private File _getDefaultDir(String defaultDirProperty, String defaultDirValue) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getArgs();

		File base = args.getBase();

		File baseDir = base.getCanonicalFile();

		if (!_isWorkspaceDir(baseDir)) {
			return baseDir;
		}

		Properties properties = getWorkspaceProperties();

		String dirValue = (String)properties.get(defaultDirProperty);

		if (dirValue == null) {
			dirValue = defaultDirValue;
		}

		if (dirValue.contains(",")) {
			bladeCLI.out("WARNING: " + defaultDirProperty + " has multiple values: " + dirValue);
			dirValue = dirValue.substring(0, dirValue.indexOf(","));

			bladeCLI.out("WARNING: using " + dirValue);
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		File projectDir = workspaceProvider.getWorkspaceDir(baseDir);

		File dir = new File(projectDir, dirValue);

		if (_containsDir(baseDir, dir)) {
			return baseDir;
		}

		return dir;
	}

	private File _getDefaultExtDir() throws Exception {
		return _getDefaultDir(WorkspaceConstants.DEFAULT_EXT_DIR_PROPERTY, WorkspaceConstants.DEFAULT_EXT_DIR);
	}

	private File _getDefaultModulesDir() throws Exception {
		return _getDefaultDir(WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY, WorkspaceConstants.DEFAULT_MODULES_DIR);
	}

	private File _getDefaultThemesDir() throws Exception {
		return _getDefaultDir(WorkspaceConstants.DEFAULT_THEMES_DIR_PROPERTY, WorkspaceConstants.DEFAULT_THEMES_DIR);
	}

	private File _getDefaultWarsDir() throws Exception {
		return _getDefaultDir(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY, WorkspaceConstants.DEFAULT_WARS_DIR);
	}

	private Optional<String> _getLiferayVersion(WorkspaceProvider workspaceProvider, CreateArgs createArgs)
		throws IOException {

		if (workspaceProvider == null) {
			return Optional.ofNullable(
				createArgs.getLiferayVersion()
			).filter(
				BladeUtil::isNotEmpty
			);
		}

		File dir = createArgs.getDir();

		if (dir == null) {
			dir = createArgs.getBase();
		}

		String liferayVersion = createArgs.getLiferayVersion();

		if (liferayVersion == null) {
			liferayVersion = workspaceProvider.getLiferayVersion(dir);
		}

		return Optional.ofNullable(
			liferayVersion
		).filter(
			BladeUtil::isNotEmpty
		);
	}

	private Optional<String> _getProduct(WorkspaceProvider workspaceProvider, CreateArgs createArgs) {
		if (workspaceProvider == null) {
			return Optional.empty();
		}

		File dir = createArgs.getDir();

		if (dir == null) {
			dir = createArgs.getBase();
		}

		return Optional.ofNullable(workspaceProvider.getProduct(dir));
	}

	private boolean _isExistingTemplate(String templateName) throws Exception {
		Collection<String> templateNames = BladeUtil.getTemplateNames(getBladeCLI());

		return templateNames.contains(templateName);
	}

	private boolean _isValidName(String name) {
		if (BladeUtil.isEmpty(name)) {
			_addError("Create", "SYNOPSIS\n\t create [options] <[name]>");

			return false;
		}

		Matcher matcher = _inValidNamePattern.matcher(name);

		if (matcher.find()) {
			_addError(
				"Create",
				"SYNOPSIS\n\t invalid module name [" + name + "], do not use more than one hyphen for module name.");

			return false;
		}

		return true;
	}

	private boolean _isWorkspaceDir(File dir) {
		BladeCLI bladeCLI = getBladeCLI();

		return bladeCLI.isWorkspaceDir(dir);
	}

	private Pattern _inValidNamePattern = Pattern.compile("((-)\\2+)");

}