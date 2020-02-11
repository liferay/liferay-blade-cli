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
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

		File baseDir = new File(convertArgs.getBase());

		GradleWorkspaceProvider workspaceProviderGradle = (GradleWorkspaceProvider)bladeCLI.getWorkspaceProvider(
			baseDir);

		File projectDir = workspaceProviderGradle.getWorkspaceDir(bladeCLI);

		Properties gradleProperties = workspaceProviderGradle.getGradleProperties(projectDir);

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

		if (gradleProperties != null) {
			warsDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);
		}

		if (warsDirPath == null) {
			warsDirPath = WorkspaceConstants.DEFAULT_WARS_DIR;
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
				if (pathname.isDirectory() && new File(pathname, "docroot").exists()) {
					return true;
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

		if (convertArgs.isAll()) {
			Stream<File> serviceBuilderPluginStream = serviceBuilderPlugins.stream();

			serviceBuilderPluginStream.forEach(
				serviceBuilderPlugin -> _convertToServiceBuilderWarProject(
					warsDir, serviceBuilderPlugin, removeSource));

			Stream<File> portletPluginStream = portletPlugins.stream();

			portletPluginStream.forEach(
				portalPlugin -> {
					try {
						_convertToWarProject(warsDir, portalPlugin, removeSource);
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> hookPluginStream = hookPlugins.stream();

			hookPluginStream.forEach(
				hookPlugin -> {
					try {
						_convertToWarProject(warsDir, hookPlugin, removeSource);
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> webPluginStream = webPlugins.stream();

			webPluginStream.forEach(
				webPlugin -> {
					try {
						_convertToWarProject(warsDir, webPlugin, removeSource);
					}
					catch (Exception e) {
						e.printStackTrace(bladeCLI.error());
					}
				});

			Stream<File> layoutPluginStream = layoutPlugins.stream();

			layoutPluginStream.forEach(layoutPlugin -> _convertToLayoutWarProject(warsDir, layoutPlugin, removeSource));

			Stream<File> themes = themePlugins.stream();

			if (convertArgs.isThemeBuilder()) {
				themes.forEach(theme -> _convertToThemeBuilderWarProject(warsDir, theme, removeSource));
			}
			else {
				themes.forEach(this::_convertToThemeProject);
			}
		}
		else if (convertArgs.isList()) {
			bladeCLI.out("The following is a list of projects available to convert:\n");

			List<File> plugins = new ArrayList<>();

			plugins.addAll(serviceBuilderPlugins);
			plugins.addAll(portletPlugins);
			plugins.addAll(hookPlugins);
			plugins.addAll(webPlugins);
			plugins.addAll(layoutPlugins);
			plugins.addAll(themePlugins);

			plugins.forEach(plugin -> bladeCLI.out("\t" + plugin.getName()));
		}
		else {
			File pluginDir = _findPluginDir(pluginsSdkDir, pluginName);

			_assertTrue("pluginDir is null", pluginDir != null);
			_assertTrue("pluginDir does not exists", pluginDir.exists());

			Path pluginPath = pluginDir.toPath();

			if (pluginPath.startsWith(portletsDir.toPath())) {
				if (_isServiceBuilderPlugin(pluginDir)) {
					_convertToServiceBuilderWarProject(warsDir, pluginDir, removeSource);
				}
				else {
					_convertToWarProject(warsDir, pluginDir, removeSource);
				}
			}

			if (pluginPath.startsWith(hooksDir.toPath()) || pluginPath.startsWith(websDir.toPath())) {
				_convertToWarProject(warsDir, pluginDir, removeSource);
			}
			else if (pluginPath.startsWith(layouttplDir.toPath())) {
				_convertToLayoutWarProject(warsDir, pluginDir, removeSource);
			}
			else if (pluginPath.startsWith(themesDir.toPath())) {
				if (convertArgs.isThemeBuilder()) {
					_convertToThemeBuilderWarProject(warsDir, pluginDir, removeSource);
				}
				else {
					_convertToThemeProject(pluginDir);
				}
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

	private void _convertToLayoutWarProject(File warsDir, File layoutPluginDir, boolean removeSource) {
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
		}
		catch (Exception e) {
			BladeCLI bladeCLI = getBladeCLI();

			bladeCLI.error("Error upgrading project " + layoutPluginDir.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}
	}

	private void _convertToServiceBuilderWarProject(File warsDir, File pluginDir, boolean removeSource) {
		ConvertArgs convertArgs = getArgs();

		BladeCLI bladeCLI = getBladeCLI();

		try {
			_convertToWarProject(warsDir, pluginDir, removeSource);

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

			convertServiceBuilderArgs.setBase(new File(convertArgs.getBase()));

			ConvertServiceBuilderCommand command = new ConvertServiceBuilderCommand(
				bladeCLI, convertServiceBuilderArgs);

			command.execute();
		}
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + pluginDir.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}
	}

	private void _convertToThemeBuilderWarProject(File warsDir, File themePlugin, boolean removeSource) {
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
		}
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}
	}

	private void _convertToThemeProject(File themePlugin) {
		BladeCLI bladeCLI = getBladeCLI();

		try {
			ConvertThemeCommand convertThemeCommand = new ConvertThemeCommand(bladeCLI, getArgs());

			convertThemeCommand.execute();
		}
		catch (Exception e) {
			bladeCLI.error("Error upgrading project " + themePlugin.getName() + "\n");

			e.printStackTrace(bladeCLI.error());
		}
	}

	private void _convertToWarProject(File warsDir, File pluginDir, boolean removeSource) throws Exception {
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
								"compile group: ''{0}'', name: ''{1}'', version: ''{2}''", org, name, rev));
					}
				}
			}

			ivyFile.delete();
		}

		File liferayPluginPackageFile = new File(warDir, "src/main/webapp/WEB-INF/liferay-plugin-package.properties");

		if (liferayPluginPackageFile.exists()) {
			try (InputStream fileInputStream = new FileInputStream(liferayPluginPackageFile)) {
				Properties liferayPluginPackageProperties = new Properties();

				liferayPluginPackageProperties.load(fileInputStream);

				String portalJarsValue = liferayPluginPackageProperties.getProperty("portal-dependency-jars");

				if (portalJarsValue != null) {
					String[] portalJars = portalJarsValue.split(",");

					try (InputStream inputStream = ConvertCommand.class.getResourceAsStream(
							"/portal-dependency-jars-62.properties")) {

						Properties properties = new Properties();

						properties.load(inputStream);

						for (String portalJar : portalJars) {
							String newDependency = properties.getProperty(portalJar);

							if ((newDependency == null) || newDependency.isEmpty()) {
								continue;
							}

							String[] s = newDependency.split(",");

							if (s.length != 3) {
								continue;
							}

							dependencies.add(
								MessageFormat.format(
									"compile group: ''{0}'', name: ''{1}'', version: ''{2}''", s[0], s[1], s[2]));
						}
					}
					catch (Exception e) {
						getBladeCLI().error(
							"Convert failed on portal jars of liferay-plugin-package.properties. \n",
							pluginDir.getName(), e.getMessage());
					}
				}
			}
		}

		StringBuilder depsBlock = new StringBuilder();

		depsBlock.append("dependencies {" + System.lineSeparator());

		for (String dependency : dependencies) {
			depsBlock.append("\t" + dependency + System.lineSeparator());
		}

		depsBlock.append("}");

		File gradleFile = new File(warDir, "build.gradle");

		String content = depsBlock.toString();

		Files.write(gradleFile.toPath(), content.getBytes());
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

	private boolean _isValidSDKDir(File pluginsSdkDir) {
		File buildProperties = new File(pluginsSdkDir, "build.properties");
		File portletsBuildXml = new File(pluginsSdkDir, "portlets/build.xml");
		File hooksBuildXml = new File(pluginsSdkDir, "hooks/build.xml");

		if (buildProperties.exists() && portletsBuildXml.exists() && hooksBuildXml.exists()) {
			return true;
		}

		return false;
	}

}