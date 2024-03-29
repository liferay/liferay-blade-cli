/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.command;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.CopyDirVisitor;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.blade.cli.util.ListUtil;
import com.liferay.blade.cli.util.ReleaseUtil;
import com.liferay.blade.cli.util.StringUtil;
import com.liferay.blade.gradle.model.GradleDependency;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.VersionUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.LoadProperties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ConvertCommand extends BaseCommand<ConvertArgs> {

	public ConvertCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		ConvertArgs convertArgs = getArgs();

		File baseDir = convertArgs.getBase();

		GradleWorkspaceProvider workspaceProviderGradle = (GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(
			baseDir);

		File projectDir = workspaceProviderGradle.getWorkspaceDir(bladeCLI);

		Properties gradleProperties = workspaceProviderGradle.getGradleProperties(projectDir);

		final File pluginsSdkDir = _getPluginsSdkDir(convertArgs, projectDir, gradleProperties);

		_assertTrue("pluginsSdkDir is null: %s", pluginsSdkDir != null);
		_assertTrue(String.format("pluginsSdkDir does not exist: %s", pluginsSdkDir), pluginsSdkDir.exists());
		_assertTrue(
			String.format("pluginsSdkDir is not a valid Plugins SDK dir: %s", pluginsSdkDir),
			_isValidSDKDir(pluginsSdkDir));

		File hooksDir = new File(pluginsSdkDir, "hooks");
		File layouttplDir = new File(pluginsSdkDir, "layouttpl");
		File portletsDir = new File(pluginsSdkDir, "portlets");
		File themesDir = new File(pluginsSdkDir, "themes");
		File websDir = new File(pluginsSdkDir, "webs");

		String projectsDirPath;

		String legacyDefaultWarsDir = (String)gradleProperties.get(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);

		boolean legacyDefaultWarsDirSet = false;

		if ((legacyDefaultWarsDir != null) && !legacyDefaultWarsDir.isEmpty()) {
			legacyDefaultWarsDirSet = true;
		}

		if ((gradleProperties != null) && legacyDefaultWarsDirSet) {
			projectsDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);
		}
		else {
			projectsDirPath = "modules";
		}

		File projectsDir = new File(projectDir, projectsDirPath);

		projectsDir.mkdir();

		if (!pluginsSdkDir.exists()) {
			bladeCLI.error(
				"Plugins SDK folder " + pluginsSdkDir.getAbsolutePath() +
					" does not exist.\nPlease edit gradle.properties and set " +
						WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);

			return;
		}

		List<String> name = convertArgs.getName();

		final String pluginName = name.isEmpty() ? null : name.get(0);

		if ((pluginName == null) && !convertArgs.isAll() && !convertArgs.isList()) {
			bladeCLI.error(
				"Please specify a plugin name, list the projects with [-l] or specify all using option [-a]");

			return;
		}

		final FileFilter containsDocrootFilter = new FileFilter() {

			@Override
			public boolean accept(File pathName) {
				if (pathName.isDirectory()) {
					File docroot = new File(pathName, "docroot");

					if (docroot.exists()) {
						return true;
					}
				}

				return false;
			}

		};

		final FileFilter serviceBuilderPluginsFilter = new FileFilter() {

			@Override
			public boolean accept(File pathName) {
				boolean directory = pathName.isDirectory();
				File docroot = new File(pathName, "docroot");

				if (directory && docroot.exists() && _hasServiceXmlFile(pathName)) {
					return true;
				}

				return false;
			}

		};

		File[] portletList = portletsDir.listFiles(containsDocrootFilter);
		File[] hookFiles = hooksDir.listFiles(containsDocrootFilter);
		File[] layoutFiles = layouttplDir.listFiles(containsDocrootFilter);
		File[] serviceBuilderList = portletsDir.listFiles(serviceBuilderPluginsFilter);
		File[] themeFiles = themesDir.listFiles(containsDocrootFilter);
		File[] webFiles = websDir.listFiles(containsDocrootFilter);

		if (serviceBuilderList == null) {
			serviceBuilderList = new File[0];
		}

		List<File> serviceBuilderPlugins = Arrays.asList(serviceBuilderList);

		List<File> portlets = Arrays.asList((portletList != null) ? portletList : new File[0]);

		List<File> portletPlugins = portlets.stream(
		).filter(
			portletPlugin -> !serviceBuilderPlugins.contains(portletPlugin)
		).collect(
			Collectors.toList()
		);

		List<File> hookPlugins = Arrays.asList((hookFiles != null) ? hookFiles : new File[0]);
		List<File> layoutPlugins = Arrays.asList((layoutFiles != null) ? layoutFiles : new File[0]);
		List<File> webPlugins = Arrays.asList((webFiles != null) ? webFiles : new File[0]);
		List<File> themePlugins = Arrays.asList((themeFiles != null) ? themeFiles : new File[0]);

		boolean removeSource = convertArgs.isRemoveSource();

		final List<Path> convertedPaths = new ArrayList<>();

		if (convertArgs.isAll()) {
			serviceBuilderPlugins.forEach(
				serviceBuilderPlugin -> _convertToServiceBuilderWarProject(
					pluginsSdkDir, projectsDir, serviceBuilderPlugin, removeSource));

			portletPlugins.forEach(
				portalPlugin -> {
					try {
						convertedPaths.addAll(
							_convertToWarProject(pluginsSdkDir, projectsDir, portalPlugin, null, removeSource));
					}
					catch (Exception exception) {
						exception.printStackTrace(bladeCLI.error());
					}
				});

			hookPlugins.forEach(
				hookPlugin -> {
					try {
						convertedPaths.addAll(
							_convertToWarProject(pluginsSdkDir, projectsDir, hookPlugin, null, removeSource));
					}
					catch (Exception exception) {
						exception.printStackTrace(bladeCLI.error());
					}
				});

			webPlugins.forEach(
				webPlugin -> {
					try {
						convertedPaths.addAll(
							_convertToWarProject(pluginsSdkDir, projectsDir, webPlugin, null, removeSource));
					}
					catch (Exception exception) {
						exception.printStackTrace(bladeCLI.error());
					}
				});

			layoutPlugins.forEach(layoutPlugin -> _convertToLayoutWarProject(projectsDir, layoutPlugin, removeSource));

			if (convertArgs.isThemeBuilder()) {
				themePlugins.forEach(
					theme -> convertedPaths.addAll(_convertToThemeBuilderWarProject(projectsDir, theme, removeSource)));
			}
			else {
				themePlugins.forEach(theme -> convertedPaths.addAll(_convertToThemeProject(theme, convertArgs)));
			}
		}
		else if (convertArgs.isList()) {
			if (!convertArgs.isQuiet()) {
				bladeCLI.out("The following is a list of projects available to convert:\n");
			}

			List<File> plugins = new ArrayList<>();

			plugins.addAll(serviceBuilderPlugins);
			plugins.addAll(portletPlugins);
			plugins.addAll(hookPlugins);
			plugins.addAll(webPlugins);
			plugins.addAll(layoutPlugins);
			plugins.addAll(themePlugins);

			plugins.forEach(plugin -> bladeCLI.out(plugin.getName()));
		}
		else {
			File pluginDir = _findPluginDir(pluginsSdkDir, pluginName);

			_assertTrue("pluginDir is null", pluginDir != null);
			_assertTrue("pluginDir does not exists", pluginDir.exists());

			Path pluginPath = pluginDir.toPath();

			if (pluginPath.startsWith(portletsDir.toPath())) {
				if (_isServiceBuilderPlugin(pluginDir)) {
					convertedPaths.addAll(
						_convertToServiceBuilderWarProject(pluginsSdkDir, projectsDir, pluginDir, removeSource));
				}
				else {
					convertedPaths.addAll(
						_convertToWarProject(pluginsSdkDir, projectsDir, pluginDir, null, removeSource));
				}
			}

			if (pluginPath.startsWith(hooksDir.toPath()) || pluginPath.startsWith(websDir.toPath())) {
				convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, projectsDir, pluginDir, null, removeSource));
			}
			else if (pluginPath.startsWith(layouttplDir.toPath())) {
				convertedPaths.addAll(_convertToLayoutWarProject(projectsDir, pluginDir, removeSource));
			}
			else if (pluginPath.startsWith(themesDir.toPath())) {
				if (convertArgs.isThemeBuilder()) {
					convertedPaths.addAll(_convertToThemeBuilderWarProject(projectsDir, pluginDir, removeSource));
				}
				else {
					convertedPaths.addAll(_convertToThemeProject(pluginDir, convertArgs));
				}
			}

			if (convertArgs.isQuiet()) {
				convertedPaths.stream(
				).map(
					Path::toString
				).forEach(
					bladeCLI::out
				);
			}
			else {
				if (!convertArgs.isQuiet()) {
					bladeCLI.out("The following projects were added to the Liferay workspace build:");
				}

				convertedPaths.forEach(path -> bladeCLI.out("\t" + path));

				if (!convertArgs.isQuiet()) {
					bladeCLI.out(
						"\nConversion is complete. Use the upgrade tool to scan for breaking changes to continue.");
				}
			}
		}
	}

	@Override
	public Class<ConvertArgs> getArgsClass() {
		return ConvertArgs.class;
	}

	private void _assertTrue(String message, boolean value) {
		if (!value) {
			throw new AssertionError(message);
		}
	}

	private List<GAV> _convertPortalDependencyJarProperty(File pluginsSdkDir, File warDir) throws Exception {
		List<GAV> convertedDependencies = new ArrayList<>();

		File liferayPluginPackageFile = new File(warDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		if (liferayPluginPackageFile.exists()) {
			try (InputStream fileInputStream = new FileInputStream(liferayPluginPackageFile)) {
				Properties liferayPluginPackageProperties = _loadProperties(fileInputStream);

				String portalJarsValue = liferayPluginPackageProperties.getProperty("portal-dependency-jars");

				List<String> portalDependencyJars = new ArrayList<>(Arrays.asList(_PORTLET_PLUGIN_API_DEPENDENCIES));

				if (Objects.nonNull(portalJarsValue)) {
					Collections.addAll(portalDependencyJars, portalJarsValue.split(","));
				}

				List<String> missingDependencyJars = new ArrayList<>();

				try (InputStream inputStream = ConvertCommand.class.getResourceAsStream(
						"/portal-dependency-jars-62.properties")) {

					Properties properties = _loadProperties(inputStream);

					Map<String, GAV> migratedDependencies = _getMigratedDependecies();

					for (String portalDependencyJar : portalDependencyJars) {
						GAV gav = migratedDependencies.get(portalDependencyJar);

						if (gav == null) {
							String newDependency = properties.getProperty(portalDependencyJar);

							if ((newDependency == null) || newDependency.isEmpty()) {
								missingDependencyJars.add(portalDependencyJar);

								continue;
							}

							String[] coordinates = newDependency.split(":");

							if (coordinates.length != 3) {
								missingDependencyJars.add(portalDependencyJar);

								continue;
							}

							gav = new GAV(coordinates[0], coordinates[1], coordinates[2]);
						}

						if (!gav.isRemove()) {
							convertedDependencies.add(gav);
						}
					}
				}

				if (!missingDependencyJars.isEmpty()) {
					LoadProperties loadProperties = new LoadProperties();

					Project project = new Project();

					project.setProperty("sdk.dir", pluginsSdkDir.getCanonicalPath());

					loadProperties.setProject(project);

					loadProperties.setSrcFile(new File(pluginsSdkDir, "build.properties"));
					loadProperties.execute();

					String portalDirValue = project.getProperty(
						"app.server." + project.getProperty("app.server.type") + ".portal.dir");

					if (FileUtil.exists(portalDirValue)) {
						Stream<String> stream = missingDependencyJars.stream();

						stream.map(
							jarName -> new File(portalDirValue, "WEB-INF/lib/" + jarName)
						).filter(
							File::exists
						).map(
							portalJar -> _getGAVFromJarFile(portalJar)
						).forEach(
							gav -> {
								if (gav.isUnknown()) {
									_warn(
										MessageFormat.format(
											"Found dependency {0} but unable to determine its artifactId. Please " +
												"resolve manually.",
											gav.getJarName()));
								}

								convertedDependencies.add(gav);
							}
						);
					}
					else {
						Stream<String> stream = missingDependencyJars.stream();

						stream.map(
							jarName -> new GAV(jarName)
						).forEach(
							gav -> {
								if (gav.isUnknown()) {
									_warn(
										MessageFormat.format(
											"Found dependency {0} but unable to determine its artifactId. Please " +
												"resolve manually.",
											gav.getJarName()));
								}

								convertedDependencies.add(gav);
							}
						);
					}
				}
			}
		}

		return convertedDependencies;
	}

	private List<Path> _convertToLayoutWarProject(File warsDir, File layoutPluginDir, boolean removeSource) {
		try {
			warsDir.mkdirs();

			Path warsPath = warsDir.toPath();

			FileUtil.moveFile(layoutPluginDir.toPath(), warsPath.resolve(layoutPluginDir.getName()), removeSource);

			File warDir = new File(warsDir, layoutPluginDir.getName());

			File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

			if (docrootSrc.exists()) {
				throw new IllegalStateException(
					"layouttpl project " + layoutPluginDir.getName() + " contains java src at " +
						docrootSrc.getAbsolutePath() + ". Please remove it before continuing.");
			}

			File webapp = new File(warDir, "src/main/webapp");

			webapp.mkdirs();

			File docroot = new File(warDir, "docroot");

			Path webappPath = webapp.toPath();

			for (File docrootFile : docroot.listFiles()) {
				FileUtil.copyFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
			}

			Path warPath = warDir.toPath();

			FileUtil.deleteDir(docroot.toPath());
			Files.deleteIfExists(warPath.resolve("build.xml"));
			Files.deleteIfExists(warPath.resolve(".classpath"));
			Files.deleteIfExists(warPath.resolve(".project"));
			FileUtil.deleteDirIfExists(warPath.resolve(".settings"));

			return Collections.singletonList(warDir.toPath());
		}
		catch (Exception exception) {
			BladeCLI bladeCLI = getBladeCLI();

			bladeCLI.error("Error upgrading project " + layoutPluginDir.getName() + "\n");

			exception.printStackTrace(bladeCLI.error());
		}

		return Collections.emptyList();
	}

	{
		_portalClasspathDependenciesMap.put(
			"util-bridges.jar", "compileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.bridges\"");
		_portalClasspathDependenciesMap.put(
			"util-java.jar", "compileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.java\"");
		_portalClasspathDependenciesMap.put(
			"util-taglib.jar", "compileOnly group: \"com.liferay.portal\", name: \"com.liferay.util.taglib\"");
	}

	private List<Path> _convertToServiceBuilderWarProject(
		File pluginsSdkDir, File projectsDir, File pluginDir, boolean removeSource) {

		ConvertArgs convertArgs = getArgs();

		BladeCLI bladeCLI = getBladeCLI();

		List<Path> convertedPaths = new ArrayList<>();

		try {
			List<String> arguments;

			if (convertArgs.isAll()) {
				arguments = new ArrayList<>();

				String pluginName = pluginDir.getName();

				arguments.add(pluginName);
			}
			else {
				arguments = convertArgs.getName();
			}

			WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(convertArgs.getBase());

			ConvertArgs convertServiceBuilderArgs = new ConvertArgs(
				convertArgs.isAll(), convertArgs.isList(), convertArgs.isThemeBuilder(), convertArgs.isRemoveSource(),
				convertArgs.getSource(), arguments, workspaceProvider.getProduct(convertArgs.getBase()));

			convertServiceBuilderArgs.setBase(convertArgs.getBase());

			ConvertServiceBuilderCommand command = new ConvertServiceBuilderCommand(
				bladeCLI, convertServiceBuilderArgs);

			command.execute();

			List<Path> projectPaths = command.getConvertedPaths();

			if (!projectPaths.isEmpty()) {
				convertedPaths.addAll(projectPaths);

				Path apiPath = projectPaths.get(0);

				List<Path> warPaths = _convertToWarProject(
					pluginsSdkDir, projectsDir, pluginDir, apiPath.toFile(), removeSource);

				convertedPaths.addAll(warPaths);
			}
		}
		catch (Exception exception) {
			bladeCLI.error("Error upgrading project " + pluginDir.getName() + "\n");

			exception.printStackTrace(bladeCLI.error());
		}

		return convertedPaths;
	}

	private List<Path> _convertToThemeBuilderWarProject(File warsDir, File themePlugin, boolean removeSource) {
		BladeCLI bladeCLI = getBladeCLI();

		try {
			warsDir.mkdirs();

			CreateArgs createArgs = new CreateArgs();

			createArgs.setQuiet(true);

			CreateCommand createCommand = new CreateCommand(bladeCLI);

			createCommand.setArgs(createArgs);

			ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

			projectTemplatesArgs.setDestinationDir(warsDir);
			projectTemplatesArgs.setName(themePlugin.getName());
			projectTemplatesArgs.setTemplate("theme");

			createCommand.execute(projectTemplatesArgs);

			File docroot = new File(themePlugin, "docroot");

			File diffsDir = new File(docroot, "_diffs");

			if (!diffsDir.exists()) {
				throw new IllegalStateException(
					"theme " + themePlugin.getName() +
						" does not contain a docroot/_diffs folder.  Please correct it and try again.");
			}

			// only copy _diffs and WEB-INF

			File newThemeDir = new File(warsDir, themePlugin.getName());

			File webapp = new File(newThemeDir, "src/main/webapp");

			Files.walkFileTree(
				diffsDir.toPath(),
				new CopyDirVisitor(diffsDir.toPath(), webapp.toPath(), StandardCopyOption.REPLACE_EXISTING));

			File webinfDir = new File(docroot, "WEB-INF");

			File newWebinfDir = new File(webapp, "WEB-INF");

			Files.walkFileTree(
				webinfDir.toPath(),
				new CopyDirVisitor(webinfDir.toPath(), newWebinfDir.toPath(), StandardCopyOption.REPLACE_EXISTING));

			File[] others = docroot.listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						if (!Objects.equals(name, "_diffs") && !Objects.equals(name, "WEB-INF")) {
							return true;
						}

						return false;
					}

				});

			if ((others != null) && (others.length > 0)) {
				File backup = new File(newThemeDir, "docroot_backup");

				backup.mkdirs();

				Path backupPath = backup.toPath();

				for (File other : others) {
					FileUtil.moveFile(other.toPath(), backupPath.resolve(other.getName()), removeSource);
				}
			}

			if (removeSource) {
				FileUtil.deleteDir(themePlugin.toPath());
			}

			return Collections.singletonList(newThemeDir.toPath());
		}
		catch (Exception exception) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			exception.printStackTrace(bladeCLI.error());
		}

		return Collections.emptyList();
	}

	private List<Path> _convertToThemeProject(File themePlugin, ConvertArgs args) {
		BladeCLI bladeCLI = getBladeCLI();

		try {
			ConvertThemeCommand convertThemeCommand = new ConvertThemeCommand(bladeCLI, args);

			convertThemeCommand.execute();

			return convertThemeCommand.getConvertedPaths();
		}
		catch (Exception exception) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			exception.printStackTrace(bladeCLI.error());
		}

		return Collections.emptyList();
	}

	private List<Path> _convertToWarProject(
			File pluginsSdkDir, File projectsDir, File pluginDir, File apiProjectDir, boolean removeSource)
		throws Exception {

		List<Path> convertedPaths = new ArrayList<>();

		Path projectParentPath = projectsDir.toPath();

		File projectParentDir = new File(projectsDir, _getProjectParentName(pluginDir));

		if (!Objects.equals(projectParentDir.getName(), pluginDir.getName())) {
			projectParentDir.mkdirs();

			projectParentPath = projectParentDir.toPath();
		}

		Path warPath = projectParentPath.resolve(pluginDir.getName());

		_createWarPortlet(projectParentPath, pluginDir.getName());

		FileUtil.copyFile(pluginDir.toPath(), warPath);

		File warDir = warPath.toFile();

		File src = new File(warDir, "src/main/java");

		src.mkdirs();

		File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

		Path srcPath = src.toPath();

		if (docrootSrc.exists()) {
			for (File docrootSrcFile : docrootSrc.listFiles()) {
				FileUtil.moveFile(docrootSrcFile.toPath(), srcPath.resolve(docrootSrcFile.getName()));
			}
		}

		File webapp = new File(warDir, "src/main/webapp");

		webapp.mkdirs();

		File docroot = new File(warDir, "docroot");

		Path webappPath = webapp.toPath();

		for (File docrootFile : docroot.listFiles()) {
			FileUtil.moveFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
		}

		FileUtil.deleteDir(docroot.toPath());
		Files.deleteIfExists(warPath.resolve("build.xml"));
		Files.deleteIfExists(warPath.resolve(".classpath"));
		Files.deleteIfExists(warPath.resolve(".project"));
		FileUtil.deleteDirIfExists(warPath.resolve(".settings"));
		Files.deleteIfExists(warPath.resolve("ivy.xml.MD5"));

		_deleteServiceBuilderFiles(warPath);

		FileUtil.deleteDirIfExists(webappPath.resolve("WEB-INF/classes"));

		List<GAV> convertedGavs = new CopyOnWriteArrayList<>();

		File ivyFile = new File(warDir, "ivy.xml");

		if (ivyFile.exists()) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(ivyFile);

			Element documentElement = doc.getDocumentElement();

			documentElement.normalize();

			NodeList depElements = documentElement.getElementsByTagName("dependency");

			if ((depElements != null) && (depElements.getLength() > 0)) {
				for (int i = 0; i < depElements.getLength(); i++) {
					Node depElement = depElements.item(i);

					String name = _getAttr(depElement, "name");
					String org = _getAttr(depElement, "org");
					String rev = _getAttr(depElement, "rev");

					Map<String, GAV> migratedDependencies = _getMigratedDependecies();

					Set<String> migratedKeys = migratedDependencies.keySet();

					boolean removedGav = false;

					if ((name != null) &&
						migratedKeys.stream(
						).filter(
							key -> name.equals(key.replaceAll("\\.jar$", ""))
						).map(
							key -> migratedDependencies.get(key)
						).filter(
							GAV::isRemove
						).findFirst(
						).isPresent()) {

						removedGav = true;
					}

					if ((name != null) && (org != null) && (rev != null) && !removedGav) {
						GAV gav = new GAV(org, name, rev);

						convertedGavs.add(gav);
					}
				}
			}

			ivyFile.delete();
		}

		convertedGavs.addAll(_convertPortalDependencyJarProperty(pluginsSdkDir, warDir));

		List<GradleDependency> convertedGradleDependencies = convertedGavs.stream(
		).map(
			gav -> {
				if (gav.isUnknown() && ListUtil.contains(_portalClasspathDependenciesMap.keySet(), gav.getJarName())) {
					return new GradleDependency(_portalClasspathDependenciesMap.get(gav.getJarName()));
				}

				return new GradleDependency(gav.toCompileDependency());
			}
		).collect(
			Collectors.toList()
		);

		_convertWebInfLibNames(warDir, convertedGradleDependencies);

		if (apiProjectDir != null) {
			StringBuilder sb = new StringBuilder("compileOnly project(\":modules:");

			sb.append(projectParentPath.getFileName());
			sb.append(":");
			sb.append(apiProjectDir.getName());
			sb.append("\")");

			convertedGradleDependencies.add(new GradleDependency(sb.toString()));
		}

		List<String> releaseApiDependencies = _getReleaseApirtifactIds();

		Path buildGradlePath = warPath.resolve("build.gradle");

		String existingContent = new String(Files.readAllBytes(buildGradlePath));

		StringBuilder dependenciesBlock = new StringBuilder();

		convertedGradleDependencies.forEach(
			dep -> {
				if (!releaseApiDependencies.contains(dep.getGroup() + ":" + dep.getName())) {
					dependenciesBlock.append("\t" + dep.toString() + System.lineSeparator());
				}
			});

		dependenciesBlock.append(System.lineSeparator());
		dependenciesBlock.append("}");

		Matcher matcher = _dependenciesBlockPattern.matcher(existingContent);

		matcher.find();

		String newContent = matcher.group(1) + dependenciesBlock.toString();

		Files.write(buildGradlePath, newContent.getBytes());

		convertedPaths.add(warPath);

		if (removeSource) {
			FileUtil.deleteDir(pluginDir.toPath());
		}

		return convertedPaths;
	}

	private void _convertWebInfLibNames(File warDir, List<GradleDependency> convertDependencies) {
		File webInfLibDir = new File(warDir, "src/main/webapp/WEB-INF/lib");

		if (!webInfLibDir.exists()) {
			return;
		}

		Map<String, GAV> migratedDependencies = _getMigratedDependecies();

		Set<String> jarNames = migratedDependencies.keySet();

		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		Optional.ofNullable(
			bladeCLI.getWorkspaceProvider(baseArgs.getBase())
		).map(
			wp -> wp.getWorkspaceDir(baseArgs.getBase())
		).map(
			workspaceDir -> new File(workspaceDir, "libs")
		).ifPresent(
			libsFolder -> {
				for (File libFile : webInfLibDir.listFiles((dir, name) -> name.endsWith(".jar"))) {
					try {
						GAV gav = migratedDependencies.get(libFile.getName());

						if (gav == null) {
							gav = _getGAVFromJarFile(libFile);
						}

						if (gav.isRemove()) {
							FileUtils.deleteQuietly(libFile);
						}
						else if (gav.isUnknown()) {
							if (!jarNames.contains(libFile.getName())) {
								String noExtensionName = FilenameUtils.removeExtension(libFile.getName());

								boolean foundDependency = convertDependencies.stream(
								).filter(
									dependency -> StringUtils.contains(dependency.getSingleLine(), noExtensionName)
								).findAny(
								).isPresent();

								if (!foundDependency) {
									StringBuilder sb = new StringBuilder("compile rootProject.files(\"libs/");

									sb.append(libFile.getName());
									sb.append("\")");

									convertDependencies.add(new GradleDependency(sb.toString()));
								}
							}

							FileUtils.moveFileToDirectory(libFile, libsFolder, true);
						}
						else {
							convertDependencies.add(new GradleDependency(gav.toCompileDependency()));

							FileUtils.deleteQuietly(libFile);
						}
					}
					catch (Exception exception) {
						bladeCLI.error(exception.getMessage());
					}
				}
			}
		);
	}

	private void _createWarPortlet(Path warPortletDirPath, String warPortleName) throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		CreateArgs createArgs = new CreateArgs();

		createArgs.setQuiet(true);
		createArgs.setBase(baseArgs.getBase());

		CreateCommand createCommand = new CreateCommand(getBladeCLI());

		createCommand.setArgs(createArgs);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setDestinationDir(warPortletDirPath.toFile());
		projectTemplatesArgs.setName(warPortleName);
		projectTemplatesArgs.setTemplate("war-mvc-portlet");
		projectTemplatesArgs.setForce(true);

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseArgs.getBase());

		projectTemplatesArgs.setLiferayProduct(workspaceProvider.getProduct(createArgs.getBase()));

		createCommand.execute(projectTemplatesArgs);
	}

	private void _deleteServiceBuilderFiles(Path warPath) throws Exception {
		Path metaInfPath = warPath.resolve("src/main/java/META-INF");

		Files.deleteIfExists(metaInfPath.resolve("base-spring.xml"));
		Files.deleteIfExists(metaInfPath.resolve("cluster-spring.xml"));
		Files.deleteIfExists(metaInfPath.resolve("hibernate-spring.xml"));
		Files.deleteIfExists(metaInfPath.resolve("infrastructure-spring.xml"));
		Files.deleteIfExists(metaInfPath.resolve("portlet-hbm.xml"));
		//Files.deleteIfExists(metaInfPath.resolve("portlet-model-hints.xml"));
		Files.deleteIfExists(metaInfPath.resolve("portlet-orm.xml"));
		Files.deleteIfExists(metaInfPath.resolve("portlet-spring.xml"));
		Files.deleteIfExists(metaInfPath.resolve("shard-data-source-spring.xml"));

		Path webInfPath = warPath.resolve("src/main/webapp/WEB-INF");

		Files.deleteIfExists(webInfPath.resolve("service.xml"));
	}

	private File _findPluginDir(File pluginsSdkDir, final String pluginName) throws Exception {
		final File[] pluginDir = new File[1];

		Files.walkFileTree(
			pluginsSdkDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					String nameValue = String.valueOf(dir.getName(dir.getNameCount() - 1));

					if (nameValue.equals(pluginName)) {
						Path parent = dir.getParent();

						if ((parent != null) && Files.exists(parent)) {
							parent = parent.getParent();
						}

						if ((parent != null) && Files.exists(parent) && _isValidSDKDir(parent.toFile())) {
							pluginDir[0] = dir.toFile();

							return FileVisitResult.TERMINATE;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return pluginDir[0];
	}

	private String _getAttr(Node item, String attrName) {
		if (item != null) {
			NamedNodeMap attrs = item.getAttributes();

			if (attrs != null) {
				Node attr = attrs.getNamedItem(attrName);

				if (attr != null) {
					return attr.getNodeValue();
				}
			}
		}

		return null;
	}

	private GAV _getGAVFromJarFile(File dependencyJarFile) {
		try (JarFile jarFile = new JarFile(dependencyJarFile)) {
			Enumeration<JarEntry> jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();

				String name = jarEntry.getName();

				if (name.startsWith("META-INF/maven") && name.endsWith("pom.properties")) {
					Properties properties = _loadProperties(jarFile.getInputStream(jarEntry));

					return new GAV(properties.get("groupId"), properties.get("artifactId"), properties.get("version"));
				}
			}
		}
		catch (IOException ioException) {
		}

		return new GAV(dependencyJarFile.getName());
	}

	private Map<String, GAV> _getMigratedDependecies() {
		ConvertArgs convertArgs = getArgs();

		String liferayVersion = convertArgs.getLiferayVersion();

		if (Validator.isNull(liferayVersion)) {
			BladeCLI bladeCLI = getBladeCLI();

			BaseArgs baseArgs = bladeCLI.getArgs();

			File baseDir = baseArgs.getBase();

			liferayVersion = Optional.ofNullable(
				bladeCLI.getWorkspaceProvider(baseDir)
			).map(
				wp -> wp.getLiferayVersion(baseDir)
			).map(
				version -> new String(
					String.valueOf(VersionUtil.getMajorVersion(version)) + "." +
						String.valueOf(VersionUtil.getMinorVersion(version)))
			).orElse(
				"0.0"
			);
		}

		switch (liferayVersion) {
			case "7.1":
				return _migratedDependencies71;
			case "7.2":
				return _migratedDependencies72;
			case "7.3":
				return _migratedDependencies73;
			case "7.4":
				return _migratedDependencies74;
		}

		return Collections.emptyMap();
	}

	private File _getPluginsSdkDir(ConvertArgs convertArgs, File projectDir, Properties gradleProperties) {
		File pluginsSdkDir = convertArgs.getSource();

		if (pluginsSdkDir == null) {
			if (gradleProperties != null) {
				String pluginsSdkDirValue = gradleProperties.getProperty(
					WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);

				if (pluginsSdkDirValue != null) {
					pluginsSdkDir = new File(projectDir, pluginsSdkDirValue);
				}
			}

			if (pluginsSdkDir == null) {
				pluginsSdkDir = new File(projectDir, WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR);
			}
		}

		return pluginsSdkDir;
	}

	private String _getProjectParentName(File pluginDir) {
		String parentProjectName = pluginDir.getName();

		if (parentProjectName.endsWith("-portlet")) {
			parentProjectName = parentProjectName.replaceAll("-portlet$", "");
		}

		return parentProjectName;
	}

	private List<String> _getReleaseApirtifactIds() {
		try {
			BladeCLI bladeCLI = getBladeCLI();

			BaseArgs baseArgs = bladeCLI.getArgs();

			Optional<String> productKeyOpt = Optional.ofNullable(
				(GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(baseArgs.getBase())
			).filter(
				Objects::nonNull
			).map(
				provider -> provider.getGradleProperties(baseArgs.getBase())
			).filter(
				Objects::nonNull
			).map(
				properties -> properties.getProperty(WorkspaceConstants.DEFAULT_WORKSPACE_PRODUCT_PROPERTY, null)
			).filter(
				Objects::nonNull
			);

			if (!productKeyOpt.isPresent()) {
				return Collections.emptyList();
			}

			String productKey = productKeyOpt.get();

			String targetPlatformVersion = ReleaseUtil.withReleaseEntry(
				productKey, ReleaseUtil.ReleaseEntry::getTargetPlatformVersion);

			if (targetPlatformVersion == null) {
				return Collections.emptyList();
			}

			String simplifiedVersion = BladeUtil.simplifyTargetPlatformVersion(targetPlatformVersion);

			String[] versionParts = simplifiedVersion.split("\\.");

			if (productKey.startsWith("dxp")) {
				simplifiedVersion = versionParts[0] + "." + versionParts[1] + "." + versionParts[2] + ".x";
			}
			else if (productKey.startsWith("portal")) {
				simplifiedVersion = versionParts[0] + "." + versionParts[1] + ".x";
			}

			Class<?> clazz = ConvertCommand.class;

			try (InputStream inputStream = clazz.getResourceAsStream(
					"/release-api/" + simplifiedVersion + "-versions.txt");
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

				String dependency = null;

				List<String> allArtifactIds = new ArrayList<>();

				while ((dependency = bufferedReader.readLine()) != null) {
					allArtifactIds.add(dependency);
				}

				return allArtifactIds;
			}
			catch (Exception exception) {
			}
		}
		catch (Exception exception) {
		}

		return Collections.emptyList();
	}

	private boolean _hasServiceXmlFile(File dir) {
		Path dirPath = dir.toPath();

		Path serviceXml = dirPath.resolve("docroot/WEB-INF/service.xml");

		return Files.exists(serviceXml);
	}

	private boolean _isServiceBuilderPlugin(File pluginDir) {
		return _hasServiceXmlFile(pluginDir);
	}

	private boolean _isValidSDKDir(File pluginsSdkDir) {
		File buildProperties = new File(pluginsSdkDir, "build.properties");
		File portletsBuildXml = new File(pluginsSdkDir, "portlets/build.xml");
		File hooksBuildXml = new File(pluginsSdkDir, "hooks/build.xml");

		if (buildProperties.exists() && portletsBuildXml.exists() && hooksBuildXml.exists()) {
			return true;
		}

		return false;
	}

	private final void _loadMigratedDependencies(String resource, Map<String, GAV> migratedDependencies) {
		try (InputStream inputStream = ConvertCommand.class.getResourceAsStream(resource)) {
			Properties properties = new Properties();

			properties.load(inputStream);

			Set<Map.Entry<Object, Object>> entries = properties.entrySet();

			entries.forEach(
				entry -> {
					String key = (String)entry.getKey();
					String value = (String)entry.getValue();

					GAV gav = null;

					if (Objects.equals(value, "__remove__")) {
						gav = new GAV(key);

						gav.setRemove(true);
					}
					else {
						String[] coords = StringUtil.split(value, ":");

						gav = new GAV(coords[0], coords[1], coords[2]);
					}

					migratedDependencies.put(key, gav);
				});
		}
		catch (IOException ioException) {
		}
	}

	private Properties _loadProperties(InputStream inputStream) throws IOException {
		Properties properties = new Properties();

		properties.load(inputStream);

		inputStream.close();

		return properties;
	}

	private void _warn(String message) {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		if (!baseArgs.isQuiet()) {
			bladeCLI.out("WARNING: " + message);
		}
	}

	private static final String[] _PORTLET_PLUGIN_API_DEPENDENCIES = {
		"commons-logging.jar", "log4j.jar", "util-bridges.jar", "util-java.jar", "util-taglib.jar"
	};

	private static final Pattern _dependenciesBlockPattern = Pattern.compile(
		"(.*^dependencies \\{.*)\\}", Pattern.MULTILINE | Pattern.DOTALL);
	private static final Map<String, GAV> _migratedDependencies71 = new HashMap<>();
	private static final Map<String, GAV> _migratedDependencies72 = new HashMap<>();
	private static final Map<String, GAV> _migratedDependencies73 = new HashMap<>();
	private static final Map<String, GAV> _migratedDependencies74 = new HashMap<>();
	{
		_loadMigratedDependencies("/migrated-dependencies-7.1.properties", _migratedDependencies71);
		_loadMigratedDependencies("/migrated-dependencies-7.2.properties", _migratedDependencies72);
		_loadMigratedDependencies("/migrated-dependencies-7.3.properties", _migratedDependencies73);
		_loadMigratedDependencies("/migrated-dependencies-7.4.properties", _migratedDependencies74);
	}

	private static final Map<String, String> _portalClasspathDependenciesMap = new HashMap<>();

	private static class GAV {

		public GAV(Object groupId, Object artifactId, Object version) {
			_groupId = Optional.ofNullable(groupId);
			_artifactId = Optional.ofNullable(artifactId);
			_version = Optional.ofNullable(version);
		}

		public GAV(String jarName) {
			_jarName = jarName;

			_groupId = Optional.empty();
			_artifactId = Optional.empty();
			_version = Optional.empty();
		}

		public Object getJarName() {
			return _jarName;
		}

		public boolean isRemove() {
			return _remove;
		}

		public boolean isUnknown() {
			if (isRemove()) {
				return false;
			}

			if (!_groupId.isPresent() || !_artifactId.isPresent() || !_version.isPresent()) {
				return true;
			}

			return false;
		}

		public void setRemove(boolean remove) {
			_remove = remove;
		}

		public String toCompileDependency() {
			if (isUnknown()) {
				return MessageFormat.format("// Unknown dependency: {0}", getJarName());
			}

			return MessageFormat.format(
				"compile group: \"{0}\", name: \"{1}\", version: \"{2}\"", _getGroupId(), _getArtifactId(),
				_getVersion());
		}

		private String _getArtifactId() {
			return _map(_artifactId);
		}

		private String _getGroupId() {
			return _map(_groupId);
		}

		private String _getVersion() {
			return _map(_version);
		}

		private String _map(Optional<Object> object) {
			return object.map(
				String.class::cast
			).orElse(
				"<unknown>"
			);
		}

		private Optional<Object> _artifactId;
		private Optional<Object> _groupId;
		private String _jarName;
		private boolean _remove = false;
		private Optional<Object> _version;

	}

}