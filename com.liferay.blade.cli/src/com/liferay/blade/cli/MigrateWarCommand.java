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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.liferay.project.templates.ProjectTemplatesArgs;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

/**
 * @author Gregory Amerson
 */
public class MigrateWarCommand {

	public static final String DESCRIPTION =
		"Migrate a plugins sdk plugin to a Liferay workspace war project";

	public MigrateWarCommand(blade blade, MigrateWarOptions options)
		throws Exception {

		_blade = blade;
		_options = options;

		File projectDir = Util.getWorkspaceDir(_blade);

		Properties gradleProperties = Util.getGradleProperties(projectDir);

		String pluginsSdkDirPath = null;

		if (gradleProperties != null) {
			pluginsSdkDirPath = gradleProperties.getProperty(
				Workspace.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);
		}

		if (pluginsSdkDirPath == null) {
			pluginsSdkDirPath = Workspace.DEFAULT_PLUGINS_SDK_DIR;
		}

		_pluginsSdkDir = new File(projectDir, pluginsSdkDirPath);
		_hooksDir = new File(_pluginsSdkDir, "hooks");
		_layouttplDir = new File(_pluginsSdkDir, "layouttpl");
		_portletsDir = new File(_pluginsSdkDir, "portlets");
		_websDir = new File(_pluginsSdkDir, "webs");
		_themesDir = new File(_pluginsSdkDir, "themes");

		String warsDirPath = null;

		if (gradleProperties != null) {
			warsDirPath = gradleProperties.getProperty(
				Workspace.DEFAULT_WARS_DIR_PROPERTY);
		}

		if (warsDirPath == null) {
			warsDirPath = Workspace.DEFAULT_WARS_DIR;
		}

		_warsDir = new File(projectDir, warsDirPath);

		if (!_pluginsSdkDir.exists()) {
			_blade.error("Plugins SDK folder " + pluginsSdkDirPath + " doesn't exist.\n" +
					"Please edit gradle.properties and set " + Workspace.DEFAULT_PLUGINS_SDK_DIR_PROPERTY);

			return;
		}
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		final String pluginName = args.size() > 0 ? args.get(0) : null;

		if (!Util.isWorkspace(_blade)) {
			_blade.error("Please execute this in a Liferay Workspace project");

			return;
		}

		if (args.size() == 0 && !_options.all()) {
			_blade.error("Please specify a plugin name or specify all using option [-a]");

			return;
		}

		final FileFilter containsDocrootFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && new File(pathname, "docroot").exists();
			}
		};

		if (_options.all()) {
			Set<File> javaPlugins = new HashSet<>();

			Collections.addAll(javaPlugins, _hooksDir.listFiles(containsDocrootFilter));
			Collections.addAll(javaPlugins, _portletsDir.listFiles(containsDocrootFilter));
			Collections.addAll(javaPlugins, _websDir.listFiles(containsDocrootFilter));

			for (File javaPlugin : javaPlugins) {
				migrateJavaPlugin(javaPlugin);
			}

			Set<File> layoutPlugins = new HashSet<>();

			Collections.addAll(layoutPlugins, _layouttplDir.listFiles(containsDocrootFilter));

			for (File layoutPlugin : layoutPlugins) {
				migrateLayoutPlugin(layoutPlugin);
			}

			Set<File> themePlugins = new HashSet<>();

			Collections.addAll(themePlugins, _themesDir.listFiles(containsDocrootFilter));

			for (File themePlugin : themePlugins) {
				migrateThemePlugin(themePlugin);
			}
		}
		else {
			File pluginDir = findPluginDir(pluginName);

			if (pluginDir == null) {
				_blade.error("Plugin does not exist.");

				return;
			}

			Path pluginPath = pluginDir.toPath();

			if (pluginPath.startsWith(_hooksDir.toPath()) ||
				pluginPath.startsWith(_portletsDir.toPath()) ||
				pluginPath.startsWith(_websDir.toPath())) {

				migrateJavaPlugin(pluginDir);
			}
			else if(pluginPath.startsWith(_layouttplDir.toPath())) {
				migrateLayoutPlugin(pluginDir);
			}
			else if(pluginPath.startsWith(_themesDir.toPath())) {
				migrateThemePlugin(pluginDir);
			}
		}
	}

	private File findPluginDir(final String pluginName) throws Exception {
		final File[] pluginDir = new File[1];

		Files.walkFileTree(_pluginsSdkDir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.getName(dir.getNameCount() - 1).toString().equals(pluginName)) {
					pluginDir[0] = dir.toFile();

					return FileVisitResult.TERMINATE;
				}

				return FileVisitResult.CONTINUE;
			}
		});

		return pluginDir[0];
	}

	private void migrateThemePlugin(File themePlugin) throws Exception {
		_warsDir.mkdirs();

		CreateCommand createCommand = new CreateCommand(_blade);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setDestinationDir(_warsDir);
		projectTemplatesArgs.setName(themePlugin.getName());
		projectTemplatesArgs.setTemplate("theme");

		createCommand.execute(projectTemplatesArgs, true);

		File docroot = new File(themePlugin, "docroot");

		File diffsDir = new File(docroot, "_diffs");

		if (!diffsDir.exists()) {
			throw new IllegalStateException(
				"theme " + themePlugin.getName() + " does not contain a docroot/_diffs folder.  "
						+ "Please correct it and try again.");
		}

		// only copy _diffs and WEB-INF

		File newThemeDir = new File(_warsDir, themePlugin.getName());

		File webapp = new File(newThemeDir, "src/main/webapp");

		Files.walkFileTree(diffsDir.toPath(), new CopyDirVisitor(diffsDir.toPath(), webapp.toPath(), StandardCopyOption.REPLACE_EXISTING));

		File webinfDir = new File(docroot, "WEB-INF");

		File newWebinfDir = new File(webapp, "WEB-INF");

		Files.walkFileTree(webinfDir.toPath(), new CopyDirVisitor(webinfDir.toPath(), newWebinfDir.toPath(), StandardCopyOption.REPLACE_EXISTING));

		File[] others = docroot.listFiles( new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !"_diffs".equals(name) && !"WEB-INF".equals(name);
			}
		});

		if (others != null && others.length > 0) {
			File backup = new File(newThemeDir, "docroot_backup");

			backup.mkdirs();

			for (File other : others) {
				Files.move(other.toPath(), backup.toPath().resolve(other.getName()));
			}
		}

		IO.delete(themePlugin);
	}

	private void migrateLayoutPlugin(File layoutPlugin) throws Exception {
		_warsDir.mkdirs();

		Files.move(layoutPlugin.toPath(), _warsDir.toPath().resolve(layoutPlugin.getName()));

		File warDir = new File(_warsDir, layoutPlugin.getName());

		File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

		if (docrootSrc.exists()) {
			throw new IllegalStateException(
				"layouttpl project " + layoutPlugin.getName() + " contains java src at " +
						docrootSrc.getAbsolutePath() + ". Please remove it before continuing.");
		}

		File webapp = new File(warDir, "src/main/webapp");

		webapp.mkdirs();

		File docroot = new File(warDir, "docroot");

		for(File docrootFile : docroot.listFiles()) {
			Files.move(docrootFile.toPath(), webapp.toPath().resolve(docrootFile.getName()));
		}

		IO.delete(docroot);
		IO.delete(new File(warDir, "build.xml"));
		IO.delete(new File(warDir, ".classpath"));
		IO.delete(new File(warDir, ".project"));
		IO.delete(new File(warDir, ".settings"));
	}

	private void migrateJavaPlugin(File javaPlugin) throws Exception {
		_warsDir.mkdirs();

		Files.move(javaPlugin.toPath(), _warsDir.toPath().resolve(javaPlugin.getName()));

		File warDir = new File(_warsDir, javaPlugin.getName());

		File src = new File(warDir, "src/main/java");

		src.mkdirs();

		File docrootSrc = new File(warDir, "docroot/WEB-INF/src");

		if (docrootSrc.exists()) {
			for(File docrootSrcFile : docrootSrc.listFiles()) {
				Files.move(docrootSrcFile.toPath(), src.toPath().resolve(docrootSrcFile.getName()));
			}

			docrootSrc.delete();
		}

		File webapp = new File(warDir, "src/main/webapp");

		webapp.mkdirs();

		File docroot = new File(warDir, "docroot");

		for(File docrootFile : docroot.listFiles()) {
			Files.move(docrootFile.toPath(), webapp.toPath().resolve(docrootFile.getName()));
		}

		IO.delete(docroot);
		IO.delete(new File(warDir, "build.xml"));
		IO.delete(new File(warDir, ".classpath"));
		IO.delete(new File(warDir, ".project"));
		IO.delete(new File(warDir, ".settings"));
		IO.delete(new File(warDir, "ivy.xml.MD5"));

		List<String> dependencies = new ArrayList<>();
		dependencies.add("compileOnly group: \"com.liferay.portal\", name: \"com.liferay.portal.kernel\", version: \"2.0.0\"");
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

			if (depElements != null && depElements.getLength() > 0) {
				for (int i = 0; i < depElements.getLength(); i++) {
					Node depElement = depElements.item(i);

					String name = getAttr(depElement, "name");
					String org = getAttr(depElement, "org");
					String rev = getAttr(depElement, "rev");

					if (name != null && org != null && rev != null) {
						dependencies.add(MessageFormat.format("compile group: ''{0}'', name: ''{1}'', version: ''{2}''", org, name, rev));
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

		IO.write(depsContent.toString().getBytes(), gradleFile);
	}

	private String getAttr(Node item, String attrName) {
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

	@Arguments(arg = "[name]")
	@Description(DESCRIPTION)
	public interface MigrateWarOptions extends Options {

		@Description("Migrate all plugins")
		public boolean all();
	}

	private final blade _blade;
	private final MigrateWarOptions _options;
	private final File _hooksDir;
	private final File _layouttplDir;
	private final File _pluginsSdkDir;
	private final File _portletsDir;
	private final File _themesDir;
	private final File _warsDir;
	private final File _websDir;

}