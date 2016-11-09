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

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class InitCommand {

	private final static String _PLUGINS_SDK_7_ZIP = "com.liferay.portal.plugins.sdk-7.0-ga3-20160804222206210.zip";
	private final static String _PLUGINS_SDK_7_URL =
		"http://downloads.sourceforge.net/project/lportal/Liferay%20Portal/7.0.2%20GA3/" +
			_PLUGINS_SDK_7_ZIP;

	private final static String[] _SDK_6_GA5_FILES = {
		"app-servers.gradle", "build.gradle", "build-plugins.gradle",
		"build-themes.gradle", "sdk.gradle", "settings.gradle",
		"util.gradle", "versions.gradle" };

	public static final String DESCRIPTION =
		"Initializes a new Liferay workspace";

	public static final String WORKSPACE_VERSION = "1.0.40";

	public InitCommand(blade blade, InitOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final List<String> args = _options._arguments();

		String name = args.size() > 0 ? args.get(0) : null;

		File destDir = name != null ? new File(
			_blade.getBase(), name) : _blade.getBase();

		trace("Using destDir " + destDir);

		if (destDir.exists() && !destDir.isDirectory()) {
			addError(destDir.getAbsolutePath() + " is not a directory.");
			return;
		}

		if (destDir.exists()) {
			if (isPluginsSDK(destDir)) {
				if (!isPluginsSDK70(destDir)) {
					if (_options.upgrade()) {
						trace(
							"Found plugins-sdk 6.2, upgraded to 7.0, moving contents to new subdirectory " +
								"and initing workspace.");

						File sdk7zip = new File (_blade.getCacheDir(), _PLUGINS_SDK_7_ZIP);

						if (!sdk7zip.exists()) {
							FileUtils.copyURLToFile(new URL(_PLUGINS_SDK_7_URL), sdk7zip);
						}

						try {
							Util.unzip(sdk7zip, destDir, "com.liferay.portal.plugins.sdk-7.0/");
						}
						catch (Exception e) {
							addError("Opening zip file error, "
								+ "please delete zip file: " +
									sdk7zip.getPath());
							return;
						}

						for (String fileName : _SDK_6_GA5_FILES) {
							File file = new File(destDir, fileName);

							if (file.exists()) {
								file.delete();
							}
						}
					}
					else {
						addError("Unable to run blade init in plugins sdk 6.2, please add -u (--upgrade)"
							+ " if you want to upgrade to 7.0");
						return;
					}
				}

				trace("Found plugins-sdk, moving contents to new subdirectory " +
					"and initing workspace.");

				File pluginsSdkDir = new File(destDir, "plugins-sdk");

				moveContentsToDir(destDir, pluginsSdkDir);

				if (_options.upgrade()) {
					// go through all portlets hooks layout templates and themes and switch to wars with workspace
					movePluginsToWarsFolder(pluginsSdkDir, new File(destDir, "wars"));
				}
			}
			else if (destDir.list().length > 0) {
				if (_options.force()) {
					trace("Files found, initing anyways.");
				}
				else {
					addError(
						destDir.getAbsolutePath() +
						" contains files, please move them before continuing " +
							"or use -f (--force) option to init workspace " +
								"anyways.");
					return;
				}
			}
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		if (name == null) {
			name = destDir.getName();
		}

		destDir = destDir.getParentFile();

		projectTemplatesArgs.setDestinationDir(destDir);

		if (_options.force() || _options.upgrade()) {
			projectTemplatesArgs.setForce(true);
		}

		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setTemplate("workspace");

		new ProjectTemplates(projectTemplatesArgs);
	}

	private void movePluginsToWarsFolder(File pluginsSdkDir, File warsDir) throws Exception {
		warsDir.mkdirs();

		FileFilter containsDocrootFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && new File(pathname, "docroot").exists();
			}
		};

		Set<File> javaPlugins = new HashSet<>();

		Collections.addAll(javaPlugins, new File(pluginsSdkDir, "hooks").listFiles(containsDocrootFilter));
		Collections.addAll(javaPlugins, new File(pluginsSdkDir, "portlets").listFiles(containsDocrootFilter));
		Collections.addAll(javaPlugins, new File(pluginsSdkDir, "webs").listFiles(containsDocrootFilter));

		for (File javaPlugin : javaPlugins) {
			Files.move(javaPlugin.toPath(), warsDir.toPath().resolve(javaPlugin.getName()));

			File warDir = new File(warsDir, javaPlugin.getName());

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
	public interface InitOptions extends Options {

		@Description(
				"create anyway if there are files located at target folder")
		public boolean force();

		@Description("force to refresh workspace template")
		public boolean refresh();

		@Description("upgrade plugins-sdk from 6.2 to 7.0")
		public boolean upgrade();
	}

	private void addError(String msg) {
		_blade.addErrors("init", Collections.singleton(msg));
	}

	private boolean isPluginsSDK(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		List<String> names = Arrays.asList(dir.list());

		return names != null &&
			names.contains("portlets") &&
			names.contains("hooks") &&
			names.contains("layouttpl") &&
			names.contains("themes") &&
			names.contains("build.properties") &&
			names.contains("build.xml") &&
			names.contains("build-common.xml") &&
			names.contains("build-common-plugin.xml");
	}

	private boolean isPluginsSDK70(File dir) {
		if ((dir == null) || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		File buildProperties = new File(dir, "build.properties");
		Properties properties = new Properties();

		InputStream in = null;

		try {
			in = new FileInputStream(buildProperties);

			properties.load(in);

			String sdkVersionValue = (String) properties.get("lp.version");

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

	private void moveContentsToDir(File src, File dest)
		throws IOException {

		File[] filesToCopy = src.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".git");
			}
		});

		dest.mkdirs();

		for( File fileToCopy : filesToCopy) {
			IO.copy(fileToCopy, new File(dest, fileToCopy.getName()));
			IO.deleteWithException(fileToCopy);
		}
	}

	private void trace(String msg) {
		_blade.trace("%s: %s", "init", msg);
	}

	private final blade _blade;
	private final InitOptions _options;

}