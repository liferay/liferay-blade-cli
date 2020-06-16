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
import com.liferay.blade.cli.WorkspaceProvider;
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.CopyDirVisitor;
import com.liferay.blade.cli.util.FileUtil;
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

		String warsDirPath = null;

		String legacyDefaultWarsDir = (String)gradleProperties.get(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);

		boolean isLegacyDefaultWarsDirSet = false;

		if ((legacyDefaultWarsDir != null) && !legacyDefaultWarsDir.isEmpty()) {
			isLegacyDefaultWarsDirSet = true;
		}

		if ((gradleProperties != null) && isLegacyDefaultWarsDirSet) {
			warsDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WARS_DIR);
		}
		else {
			warsDirPath = "modules";
		}

		File warsDir = new File(projectDir, warsDirPath);

		if (!pluginsSdkDir.exists()) {
			bladeCLI.error(
				"Plugins SDK folder " + pluginsSdkDir.getAbsolutePath() +
					" does not exist.\nPlease edit gradle.properties and set " +
						WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);

			return;
		}

		WorkspaceProvider workspaceProvider = bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider == null) {
			bladeCLI.error("Please execute this in a Liferay Workspace project");

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

		Stream<File> portletStream = portlets.stream();

		List<File> portletPlugins = portletStream.filter(
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
			Stream<File> serviceBuilderPluginStream = serviceBuilderPlugins.stream();

			serviceBuilderPluginStream.forEach(
				serviceBuilderPlugin -> _convertToServiceBuilderWarProject(
					pluginsSdkDir, warsDir, serviceBuilderPlugin, removeSource));

			Stream<File> portletPluginStream = portletPlugins.stream();

			portletPluginStream.forEach(
				portalPlugin -> {
					try {
						convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, warsDir, portalPlugin, removeSource));
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> hookPluginStream = hookPlugins.stream();

			hookPluginStream.forEach(
				hookPlugin -> {
					try {
						convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, warsDir, hookPlugin, removeSource));
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> webPluginStream = webPlugins.stream();

			webPluginStream.forEach(
				webPlugin -> {
					try {
						convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, warsDir, webPlugin, removeSource));
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> layoutPluginStream = layoutPlugins.stream();

			layoutPluginStream.forEach(layoutPlugin -> _convertToLayoutWarProject(warsDir, layoutPlugin, removeSource));

			Stream<File> themes = themePlugins.stream();

			if (convertArgs.isThemeBuilder()) {
				themes.forEach(
					theme -> convertedPaths.addAll(_convertToThemeBuilderWarProject(warsDir, theme, removeSource)));
			}
			else {
				themes.forEach(theme -> convertedPaths.addAll(_convertToThemeProject(theme)));
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
						_convertToServiceBuilderWarProject(pluginsSdkDir, warsDir, pluginDir, removeSource));
				}
				else {
					convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, warsDir, pluginDir, removeSource));
				}
			}

			if (pluginPath.startsWith(hooksDir.toPath()) || pluginPath.startsWith(websDir.toPath())) {
				convertedPaths.addAll(_convertToWarProject(pluginsSdkDir, warsDir, pluginDir, removeSource));
			}
			else if (pluginPath.startsWith(layouttplDir.toPath())) {
				convertedPaths.addAll(_convertToLayoutWarProject(warsDir, pluginDir, removeSource));
			}
			else if (pluginPath.startsWith(themesDir.toPath())) {
				if (convertArgs.isThemeBuilder()) {
					convertedPaths.addAll(_convertToThemeBuilderWarProject(warsDir, pluginDir, removeSource));
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
				moveFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
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
		File pluginsSdkDir, File warsDir, File pluginDir, boolean removeSource) {

		ConvertArgs convertArgs = getArgs();

		BladeCLI bladeCLI = getBladeCLI();

		List<Path> convertedPaths = new ArrayList<>();

		try {
			List<Path> warPaths = _convertToWarProject(pluginsSdkDir, warsDir, pluginDir, removeSource);

			convertedPaths.addAll(warPaths);

			List<String> arguments;

			if (convertArgs.isAll()) {
				arguments = new ArrayList<>();

				String pluginName = pluginDir.getName();

				arguments.add(pluginName);

				if (pluginName.endsWith("-portlet")) {
					arguments.add(pluginName.replaceAll("-portlet$", ""));
				}
			}
			else {
				arguments = convertArgs.getName();
			}

			ConvertArgs convertServiceBuilderArgs = new ConvertArgs(
				convertArgs.isAll(), convertArgs.isList(), convertArgs.isThemeBuilder(), convertArgs.isRemoveSource(),
				arguments);

			convertServiceBuilderArgs.setBase(convertArgs.getBase());

			ConvertServiceBuilderCommand command = new ConvertServiceBuilderCommand(
				bladeCLI, convertServiceBuilderArgs);

			command.execute();

			convertedPaths.addAll(command.getConvertedPaths());
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

	private List<Path> _convertToWarProject(File pluginsSdkDir, File warsDir, File pluginDir, boolean removeSource)
		throws Exception {

		List<Path> convertedPaths = new ArrayList<>();

		warsDir.mkdirs();

		Path warsPath = warsDir.toPath();

		moveFile(pluginDir.toPath(), warsPath.resolve(pluginDir.getName()), removeSource);

		File warDir = new File(warsDir, pluginDir.getName());

		File src = new File(warDir, "src/main/java");

		src.mkdirs();

		File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

		Path srcPath = src.toPath();

		if (docrootSrc.exists()) {
			for (File docrootSrcFile : docrootSrc.listFiles()) {
				moveFile(docrootSrcFile.toPath(), srcPath.resolve(docrootSrcFile.getName()));
			}

			docrootSrc.delete();
		}

		File webapp = new File(warDir, "src/main/webapp");

		webapp.mkdirs();

		File docroot = new File(warDir, "docroot");

		Path webappPath = webapp.toPath();

		for (File docrootFile : docroot.listFiles()) {
			moveFile(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
		}

		Path warPath = warDir.toPath();

		FileUtil.deleteDir(docroot.toPath());
		Files.deleteIfExists(warPath.resolve("build.xml"));
		Files.deleteIfExists(warPath.resolve(".classpath"));
		Files.deleteIfExists(warPath.resolve(".project"));
		FileUtil.deleteDirIfExists(warPath.resolve(".settings"));
		Files.deleteIfExists(warPath.resolve("ivy.xml.MD5"));

		List<String> dependencies = new ArrayList<>();

		dependencies.add(
			"compileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"");
		dependencies.add("compileOnly group: \"javax.portlet\", name: \"portlet-api\", version: \"2.0\"");
		dependencies.add("compileOnly group: \"javax.servlet\", name: \"javax.servlet-api\", version: \"3.0.1\"");

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
						dependencies.add(
							MessageFormat.format(
								"compile group: \"{0}\", name: \"{1}\", version: \"{2}\"", org, name, rev));
					}
				}
			}

			ivyFile.delete();
		}

		List<GAV> convertedDependencies = _convertWarDependencies(pluginsSdkDir, warDir);

		Stream<GAV> stream = convertedDependencies.stream();

		stream.map(
			gav -> gav.toCompileDependency()
		).forEach(
			dependencies::add
		);

		StringBuilder depsBlock = new StringBuilder();

		depsBlock.append("dependencies {" + System.lineSeparator());

		for (String dependency : dependencies) {
			depsBlock.append("\t" + dependency + System.lineSeparator());
		}

		depsBlock.append("}");

		File gradleFile = new File(warDir, "build.gradle");

		String content = depsBlock.toString();

		Files.write(gradleFile.toPath(), content.getBytes());

		convertedPaths.add(warDir.toPath());

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