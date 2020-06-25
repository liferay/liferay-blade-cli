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
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.CopyDirVisitor;
import com.liferay.blade.cli.util.FileUtil;
import com.liferay.ide.gradle.core.model.GradleDependency;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
 */
public class ConvertCommand extends BaseCommand<ConvertArgs> implements FilesSupport {

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

		boolean isLegacyDefaultWarsDirSet = false;

		if ((legacyDefaultWarsDir != null) && !legacyDefaultWarsDir.isEmpty()) {
			isLegacyDefaultWarsDirSet = true;
		}

		if ((gradleProperties != null) && isLegacyDefaultWarsDirSet) {
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
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					File docroot = new File(pathname, "docroot");

					if (docroot.exists()) {
						return true;
					}
				}

				return false;
			}

		};

		final FileFilter serviceBuilderPluginsFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				boolean directory = pathname.isDirectory();
				File docroot = new File(pathname, "docroot");

				if (directory && docroot.exists() && _hasServiceXmlFile(pathname)) {
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
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			hookPlugins.forEach(
				hookPlugin -> {
					try {
						convertedPaths.addAll(
							_convertToWarProject(pluginsSdkDir, projectsDir, hookPlugin, null, removeSource));
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			webPlugins.forEach(
				webPlugin -> {
					try {
						convertedPaths.addAll(
							_convertToWarProject(pluginsSdkDir, projectsDir, webPlugin, null, removeSource));
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			layoutPlugins.forEach(layoutPlugin -> _convertToLayoutWarProject(projectsDir, layoutPlugin, removeSource));

			if (convertArgs.isThemeBuilder()) {
				themePlugins.forEach(
					theme -> convertedPaths.addAll(_convertToThemeBuilderWarProject(projectsDir, theme, removeSource)));
			}
			else {
				themePlugins.forEach(theme -> convertedPaths.addAll(_convertToThemeProject(theme)));
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
					convertedPaths.addAll(_convertToThemeProject(pluginDir));
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
				bladeCLI.out("The following projects were added to the Liferay workspace build:");

				convertedPaths.forEach(path -> bladeCLI.out("\t" + path));

				bladeCLI.out(
					"\nConversion is complete. Please use the upgrade tool to scan for breaking changes to continue.");
			}
		}
	}

	@Override
	public Class<ConvertArgs> getArgsClass() {
		return ConvertArgs.class;
	}

	private static String _getAttr(Node item, String attrName) {
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

	private static boolean _hasServiceXmlFile(File dir) {
		Path dirPath = dir.toPath();

		Path serviceXml = dirPath.resolve("docroot/WEB-INF/service.xml");

		return Files.exists(serviceXml);
	}

	private static boolean _isServiceBuilderPlugin(File pluginDir) {
		return _hasServiceXmlFile(pluginDir);
	}

	private void _assertTrue(String message, boolean value) {
		if (!value) {
			throw new AssertionError(message);
		}
	}

	private List<Path> _convertToLayoutWarProject(File warsDir, File layoutPluginDir, boolean removeSource) {
		try {
			warsDir.mkdirs();

			Path warsPath = warsDir.toPath();

			moveFile(layoutPluginDir.toPath(), warsPath.resolve(layoutPluginDir.getName()), removeSource);

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
				copyFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
			}

			Path warPath = warDir.toPath();

			FileUtil.deleteDir(docroot.toPath());
			Files.deleteIfExists(warPath.resolve("build.xml"));
			Files.deleteIfExists(warPath.resolve(".classpath"));
			Files.deleteIfExists(warPath.resolve(".project"));
			FileUtil.deleteDirIfExists(warPath.resolve(".settings"));

			return Collections.singletonList(warDir.toPath());
		}
		catch (Exception e) {
			BladeCLI bladeCLI = getBladeCLI();

			bladeCLI.error("Error upgrading project " + layoutPluginDir.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}

		return Collections.emptyList();
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

			ConvertArgs convertServiceBuilderArgs = new ConvertArgs(
				convertArgs.isAll(), convertArgs.isList(), convertArgs.isThemeBuilder(), convertArgs.isRemoveSource(),
				convertArgs.getSource(), arguments);

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
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + pluginDir.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}

		return convertedPaths;
	}

	private List<Path> _convertToThemeBuilderWarProject(File warsDir, File themePlugin, boolean removeSource) {
		BladeCLI bladeCLI = getBladeCLI();

		try {
			warsDir.mkdirs();

			CreateCommand createCommand = new CreateCommand(bladeCLI);

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
						if (!Objects.equals("_diffs", name) && !Objects.equals("WEB-INF", name)) {
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
					moveFile(other.toPath(), backupPath.resolve(other.getName()), removeSource);
				}
			}

			if (removeSource) {
				FileUtil.deleteDir(themePlugin.toPath());
			}

			return Collections.singletonList(newThemeDir.toPath());
		}
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}

		return Collections.emptyList();
	}

	private List<Path> _convertToThemeProject(File themePlugin) {
		BladeCLI bladeCLI = getBladeCLI();

		try {
			ConvertThemeCommand convertThemeCommand = new ConvertThemeCommand(bladeCLI, getArgs());

			convertThemeCommand.execute();

			return convertThemeCommand.getConvertedPaths();
		}
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
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

		copyFile(pluginDir.toPath(), warPath);

		File warDir = warPath.toFile();

		File src = new File(warDir, "src/main/java");

		src.mkdirs();

		File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

		Path srcPath = src.toPath();

		if (docrootSrc.exists()) {
			for (File docrootSrcFile : docrootSrc.listFiles()) {
				moveFile(docrootSrcFile.toPath(), srcPath.resolve(docrootSrcFile.getName()));
			}
		}

		File webapp = new File(warDir, "src/main/webapp");

		webapp.mkdirs();

		File docroot = new File(warDir, "docroot");

		Path webappPath = webapp.toPath();

		for (File docrootFile : docroot.listFiles()) {
			moveFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
		}

		FileUtil.deleteDir(docroot.toPath());
		Files.deleteIfExists(warPath.resolve("build.xml"));
		Files.deleteIfExists(warPath.resolve(".classpath"));
		Files.deleteIfExists(warPath.resolve(".project"));
		FileUtil.deleteDirIfExists(warPath.resolve(".settings"));
		Files.deleteIfExists(warPath.resolve("ivy.xml.MD5"));

		_deleteServiceBuilderFiles(warPath);

		FileUtil.deleteDirIfExists(webappPath.resolve("WEB-INF/classes"));

		_initBuildGradle(warPath);

		List<GradleDependency> convertedDependencies = new ArrayList<>();

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

					if ((name != null) && (org != null) && (rev != null)) {
						GAV gav = new GAV(org, name, rev);

						convertedDependencies.add(new GradleDependency(gav.toCompileDependency()));
					}
				}
			}

			ivyFile.delete();
		}

		List<GAV> warDependencies = _convertWarDependencies(pluginsSdkDir, warDir);

		warDependencies.stream(
		).map(
			gav -> new GradleDependency(gav.toCompileDependency())
		).forEach(
			convertedDependencies::add
		);

		if (apiProjectDir != null) {
			StringBuilder sb = new StringBuilder("compileOnly project(\":modules:");

			sb.append(projectParentPath.getFileName());
			sb.append(":");
			sb.append(apiProjectDir.getName());
			sb.append("\")");

			convertedDependencies.add(new GradleDependency(sb.toString()));
		}

		Path buildGradlePath = warPath.resolve("build.gradle");

		String existingContent = new String(Files.readAllBytes(buildGradlePath));

		StringBuilder dependenciesBlock = new StringBuilder();

		convertedDependencies.forEach(dep -> dependenciesBlock.append("\t" + dep.toString()));

		dependenciesBlock.append(System.lineSeparator());
		dependenciesBlock.append("}");

		Matcher matcher = _dependenciesBlockPattern.matcher(existingContent);

		matcher.find();

		String newContent = matcher.group(1) + dependenciesBlock.toString() + matcher.group(2);

		Files.write(buildGradlePath, newContent.getBytes());

		convertedPaths.add(warPath);

		if (removeSource) {
			FileUtil.deleteDir(pluginDir.toPath());
		}

		return convertedPaths;
	}

	private List<GAV> _convertWarDependencies(File pluginsSdkDir, File warDir)
		throws FileNotFoundException, IOException {

		List<GAV> convertedDependencies = new ArrayList<>();

		File liferayPluginPackageFile = new File(warDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		if (liferayPluginPackageFile.exists()) {
			try (InputStream fileInputStream = new FileInputStream(liferayPluginPackageFile)) {
				Properties liferayPluginPackageProperties = _loadProperties(fileInputStream);

				String portalJarsValue = liferayPluginPackageProperties.getProperty("portal-dependency-jars");

				if (portalJarsValue != null) {
					List<String> missingDependencyJars = new ArrayList<>();

					List<String> portalDependencyJars = Arrays.asList(portalJarsValue.split(","));

					try (InputStream inputStream = ConvertCommand.class.getResourceAsStream(
							"/portal-dependency-jars-62.properties")) {

						Properties properties = _loadProperties(inputStream);

						for (String portalDependencyJar : portalDependencyJars) {
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

							convertedDependencies.add(new GAV(coordinates[0], coordinates[1], coordinates[2]));
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

						Optional.ofNullable(
							portalDirValue
						).map(
							File::new
						).filter(
							File::exists
						).ifPresent(
							portalDir -> {
								Stream<String> stream = missingDependencyJars.stream();

								stream.map(
									jarName -> new File(portalDirValue, "WEB-INF/lib/" + jarName)
								).filter(
									File::exists
								).map(
									portalJar -> {
										try (JarFile jarFile = new JarFile(portalJar)) {
											Enumeration<JarEntry> jarEntries = jarFile.entries();

											while (jarEntries.hasMoreElements()) {
												JarEntry jarEntry = jarEntries.nextElement();

												String name = jarEntry.getName();

												if (name.startsWith("META-INF/maven") &&
													name.endsWith("pom.properties")) {

													Properties properties = _loadProperties(
														jarFile.getInputStream(jarEntry));

													return new GAV(
														properties.get("groupId"), properties.get("artifactId"),
														properties.get("version"));
												}
											}
										}
										catch (IOException e) {
										}

										return new GAV(portalJar.getName());
									}
								).forEach(
									gav -> {
										if (gav.isUnknown()) {
											_warn(
												MessageFormat.format(
													"Found dependency {0} but unable to determine its artifactId. " +
														"Please resolve manually.",
													gav.getJarName()));
										}

										convertedDependencies.add(gav);
									}
								);
							}
						);
					}
				}
			}
		}

		return convertedDependencies;
	}

	private void _deleteServiceBuilderFiles(Path warPath) throws IOException {
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
					Path name = dir.getName(dir.getNameCount() - 1);

					String nameValue = name.toString();

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

	private void _initBuildGradle(Path warPath) throws Exception {
		Path initPath = Files.createTempDirectory("ws");

		BladeCLI bladeCLI = new BladeCLI();

		BaseArgs baseArgs = bladeCLI.getArgs();

		baseArgs.setProfileName("gradle");
		baseArgs.setQuiet(true);

		InitArgs initArgs = new InitArgs();

		initArgs.setBase(initPath.toFile());
		initArgs.setLiferayVersion("7.3");

		InitCommand initCommand = new InitCommand();

		initCommand.setArgs(initArgs);
		initCommand.setBlade(bladeCLI);

		initCommand.execute();

		Path modulesPath = initPath.resolve("modules");

		CreateArgs createArgs = new CreateArgs();

		createArgs.setBase(modulesPath.toFile());
		createArgs.setTemplate("war-mvc-portlet");
		createArgs.setName("war-portlet");

		CreateCommand createCommand = new CreateCommand();

		createCommand.setArgs(createArgs);
		createCommand.setBlade(bladeCLI);

		createCommand.execute();

		Path tempBuildGradle = modulesPath.resolve("war-portlet/build.gradle");

		copyFile(tempBuildGradle, warPath.resolve("build.gradle"));

		FileUtil.deleteDir(initPath);
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

	private Properties _loadProperties(InputStream inputStream) throws IOException {
		Properties properties = new Properties();

		properties.load(inputStream);

		inputStream.close();

		return properties;
	}

	private void _warn(String message) {
		BladeCLI bladeCLI = getBladeCLI();

		bladeCLI.out("WARNING: " + message);
	}

	private static final Pattern _dependenciesBlockPattern = Pattern.compile(
		"(.*^dependencies \\{.*)\\}(.*^war \\{.*)", Pattern.MULTILINE | Pattern.DOTALL);

	private static class GAV {

		public GAV(Object groupId, Object artifactId, Object version) {
			_groupId = Optional.ofNullable(groupId);
			_artifactId = Optional.ofNullable(artifactId);
			_version = Optional.ofNullable(version);
		}

		public GAV(String jarName) {
			_groupId = Optional.empty();
			_artifactId = Optional.empty();
			_version = Optional.empty();
			_jarName = jarName;
		}

		public Object getJarName() {
			return _jarName;
		}

		public boolean isUnknown() {
			if (!_groupId.isPresent() || !_artifactId.isPresent() || !_version.isPresent()) {
				return true;
			}

			return false;
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
				"<unkonwn>"
			);
		}

		private Optional<Object> _artifactId;
		private Optional<Object> _groupId;
		private String _jarName;
		private Optional<Object> _version;

	}

}