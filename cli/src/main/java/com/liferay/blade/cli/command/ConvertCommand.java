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

import aQute.lib.io.IO;

import com.liferay.blade.cli.BladeCLI;
import com.liferay.blade.cli.WorkspaceConstants;
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.blade.cli.util.CopyDirVisitor;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;

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
 */
public class ConvertCommand extends BaseCommand<ConvertArgs> {

	public ConvertCommand() {
	}

	@Override
	public void execute() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();
		ConvertArgs convertArgs = getArgs();

		File projectDir = BladeUtil.getWorkspaceDir(bladeCLI);

		Properties gradleProperties = BladeUtil.getGradleProperties(projectDir);

		String pluginsSdkDirPath = null;

		if (gradleProperties != null) {
			pluginsSdkDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);
		}

		if (pluginsSdkDirPath == null) {
			pluginsSdkDirPath = WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR;
		}

		File pluginsSdkDir = new File(projectDir, pluginsSdkDirPath);

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
				"Plugins SDK folder " + pluginsSdkDirPath + " doesn't exist.\nPlease edit gradle.properties and set " +
					WorkspaceConstants.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);

			return;
		}

		List<String> name = convertArgs.getName();

		final String pluginName = name.isEmpty() ? null : name.get(0);

		if (!BladeUtil.isWorkspace(bladeCLI)) {
			bladeCLI.error("Please execute this in a Liferay Workspace project");

			return;
		}

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

		if (convertArgs.isAll()) {
			Stream<File> serviceBuilderPluginStream = serviceBuilderPlugins.stream();

			serviceBuilderPluginStream.forEach(
				serviceBuilderPlugin -> _convertToServiceBuilderWarProject(warsDir, serviceBuilderPlugin));

			Stream<File> portletPluginStream = portletPlugins.stream();

			portletPluginStream.forEach(portalPlugin -> _convertToWarProject(warsDir, portalPlugin));

			Stream<File> hookPluginStream = hookPlugins.stream();

			hookPluginStream.forEach(hookPlugin -> _convertToWarProject(warsDir, hookPlugin));

			Stream<File> webPluginStream = webPlugins.stream();

			webPluginStream.forEach(webPlugin -> _convertToWarProject(warsDir, webPlugin));

			Stream<File> layoutPluginStream = layoutPlugins.stream();

			layoutPluginStream.forEach(layoutPlugin -> _convertToLayoutWarProject(warsDir, layoutPlugin));

			Stream<File> themes = themePlugins.stream();

			if (convertArgs.isThemeBuilder()) {
				themes.forEach(theme -> _convertToThemeBuilderWarProject(warsDir, theme));
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

			if (pluginDir == null) {
				bladeCLI.error("Plugin does not exist.");

				return;
			}

			Path pluginPath = pluginDir.toPath();

			if (pluginPath.startsWith(portletsDir.toPath())) {
				if (_isServiceBuilderPlugin(pluginDir)) {
					_convertToServiceBuilderWarProject(warsDir, pluginDir);
				}
				else {
					_convertToWarProject(warsDir, pluginDir);
				}
			}

			if (pluginPath.startsWith(hooksDir.toPath()) || pluginPath.startsWith(websDir.toPath())) {
				_convertToWarProject(warsDir, pluginDir);
			}
			else if (pluginPath.startsWith(layouttplDir.toPath())) {
				_convertToLayoutWarProject(warsDir, pluginDir);
			}
			else if (pluginPath.startsWith(themesDir.toPath())) {
				if (convertArgs.isThemeBuilder()) {
					_convertToThemeBuilderWarProject(warsDir, pluginDir);
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

	private static boolean _hasServiceXmlFile(File pathname) {
		return new File(pathname, "docroot/WEB-INF/service.xml").exists();
	}

	private static boolean _isServiceBuilderPlugin(File pluginDir) {
		return _hasServiceXmlFile(pluginDir);
	}

	private void _convertToLayoutWarProject(File warsDir, File layoutPluginDir) {
		try {
			warsDir.mkdirs();

			Path warsPath = warsDir.toPath();

			Files.move(layoutPluginDir.toPath(), warsPath.resolve(layoutPluginDir.getName()));

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
				Files.move(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
			}

			IO.delete(docroot);
			IO.delete(new File(warDir, "build.xml"));
			IO.delete(new File(warDir, ".classpath"));
			IO.delete(new File(warDir, ".project"));
			IO.delete(new File(warDir, ".settings"));
		}
		catch (Exception e) {
			getBladeCLI().error("Error upgrading project %s\n%s", layoutPluginDir.getName(), e.getMessage());
		}
	}

	private void _convertToServiceBuilderWarProject(File warsDir, File pluginDir) {
		ConvertArgs convertArgs = getArgs();

		try {
			_convertToWarProject(warsDir, pluginDir);

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
				convertArgs.isAll(), convertArgs.isList(), convertArgs.isThemeBuilder(), arguments);

			new ConvertServiceBuilderCommand(getBladeCLI(), convertServiceBuilderArgs).execute();
		}
		catch (Exception e) {
			getBladeCLI().error("Error upgrading project %s\n%s", pluginDir.getName(), e.getMessage());
		}
	}

	private void _convertToThemeBuilderWarProject(File warsDir, File themePlugin) {
		try {
			warsDir.mkdirs();

			CreateCommand createCommand = new CreateCommand(getBladeCLI());

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
						if (!"_diffs".equals(name) && !"WEB-INF".equals(name)) {
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
					Files.move(other.toPath(), backupPath.resolve(other.getName()));
				}
			}

			IO.delete(themePlugin);
		}
		catch (Exception e) {
			getBladeCLI().error("Error upgrading project %s\n%s", themePlugin.getName(), e.getMessage());
		}
	}

	private void _convertToThemeProject(File themePlugin) {
		try {
			new ConvertThemeCommand(getBladeCLI(), getArgs()).execute();
		}
		catch (Exception e) {
			getBladeCLI().error("Error upgrading project %s\n%s", themePlugin.getName(), e.getMessage());
		}
	}

	private void _convertToWarProject(File warsDir, File pluginDir) {
		try {
			warsDir.mkdirs();

			Path warsPath = warsDir.toPath();

			Files.move(pluginDir.toPath(), warsPath.resolve(pluginDir.getName()));

			File warDir = new File(warsDir, pluginDir.getName());

			File src = new File(warDir, "src/main/java");

			src.mkdirs();

			File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

			Path srcPath = src.toPath();

			if (docrootSrc.exists()) {
				for (File docrootSrcFile : docrootSrc.listFiles()) {
					Files.move(docrootSrcFile.toPath(), srcPath.resolve(docrootSrcFile.getName()));
				}

				docrootSrc.delete();
			}

			File webapp = new File(warDir, "src/main/webapp");

			webapp.mkdirs();

			File docroot = new File(warDir, "docroot");

			Path webappPath = webapp.toPath();

			for (File docrootFile : docroot.listFiles()) {
				Files.move(docrootFile.toPath(), webappPath.resolve(docrootFile.getName()));
			}

			IO.delete(docroot);
			IO.delete(new File(warDir, "build.xml"));
			IO.delete(new File(warDir, ".classpath"));
			IO.delete(new File(warDir, ".project"));
			IO.delete(new File(warDir, ".settings"));
			IO.delete(new File(warDir, "ivy.xml.MD5"));

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

			StringBuilder depsContent = new StringBuilder();

			depsContent.append("dependencies {\n");

			for (String dep : dependencies) {
				depsContent.append("\t" + dep + "\n");
			}

			depsContent.append("}");

			File gradleFile = new File(warDir, "build.gradle");

			String content = depsContent.toString();

			Files.write(gradleFile.toPath(), content.getBytes());
		}
		catch (Exception e) {
			getBladeCLI().error("Error upgrading project %s\n%s", pluginDir.getName(), e.getMessage());
		}
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
						pluginDir[0] = dir.toFile();

						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return pluginDir[0];
	}

}