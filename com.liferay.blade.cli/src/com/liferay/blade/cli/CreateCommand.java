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

import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class CreateCommand {

	public static final String DESCRIPTION =
		"Creates a new Liferay module project from several available " +
			"templates.";

	public static final String TEMPLATES_VERSION = "1+";

	public CreateCommand(blade blade, CreateOptions options) {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		if (_options.listtemplates()) {
			listTemplates();
			return;
		}

		List<String> args = _options._arguments();

		String template = _options.template();

		if (template == null) {
			template = "mvcportlet";
		}
		else if (!isExistingTemplate(template)) {
				addError(
					"Create", "the template "+template+" is not in the list");
				return;
		}

		String name = args.remove(0);
		final File dir =
			_options.dir() != null ? _options.dir() : getDefaultDir();
		final File workDir = Processor.getFile(dir, name);

		if(!checkDir(workDir)) {
			addError(
				"Create", name+" is not empty or it is a file." +
				" Please clean or delete it then run again");
			return;
		}

		final boolean isWorkspace = Util.isWorkspace(dir);

		name = workDir.getName();

		final Pattern glob = Pattern.compile(
			"^standalone/" + template + "/.*|\\...+/.*");

		final Map<String, String> subs = new HashMap<>();
		subs.put("templates/standalone/" + template + "/", "");
		subs.put("_project_path_", workDir.getAbsolutePath());
		subs.put("_name_", getPackageName(name));
		subs.put("_NAME_", name);

		final String packageName = _options.packagename();

		if (isEmpty(packageName)) {
			subs.put(
				"_package_path_", getPackageName(name).replaceAll("\\.", "/"));
			subs.put("_package_", getPackageName(name));
		}
		else {
			subs.put("_package_path_", packageName.replaceAll("\\.", "/"));
			subs.put("_package_", packageName);
		}

		String classname = _options.classname();

		if (isEmpty(classname)) {
			classname = getClassName(name);
		}

		String service = _options.service();

		if ("service".equals(template)) {
			if (isEmpty(service)) {
				addError(
					"Create",
					"The service template requires the fully qualified name " +
						"of service must be specified after the service " +
						"argument.\nFor example: blade create -t service -s " +
						"com.liferay.portal.kernel.events.LifecycleAction " +
						"customPreAction");
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		if ("servicewrapper".equals(template)) {
			if (isEmpty(service)) {
				addError(
					"Create",
					"The servicewrapper template requires the fully qualified" +
						" name of service must be specified after the service" +
						" argument.\nFor example: blade create -t " +
						"servicewrapper -s " +
						"com.liferay.portal.service.UserLocalServiceWrapper " +
						"customServiceWrapper");
				return;
			}

			subs.put("_SERVICE_FULL_", service);
			subs.put(
				"_SERVICE_SHORT_",
				service.substring(service.lastIndexOf('.') + 1));
		}

		if ("servicebuilder".equals(template)) {
			if (isEmpty(packageName)) {
				addError(
					"Create",
					"The servicebuilder template requires the name of the " +
						"root package within which to create service builder " +
						"classes must be specified.\nFor example: blade " +
						"create -t servicebuilder -p " +
						"com.liferay.guestbook guestbook");
				return;
			}

			if (name.indexOf(".") > -1) {
				subs.put("_api_", packageName + ".api");
				subs.put("_service_", packageName + ".svc");
				subs.put("_web_", packageName + ".web");
			}
			else {
				subs.put("_api_", name + "-api");
				subs.put("_service_", name + "-service");
				subs.put("_web_", name + "-web");
			}

			if (isWorkspace) {
				final Path workspacePath = Util.getWorkspaceDir(
					dir).getAbsoluteFile().toPath();

				final Path dirPath = dir.getAbsoluteFile().toPath();

				final String relativePath = workspacePath.relativize(
					dirPath).toString();

				final String apiPath =
					":" + relativePath.replaceAll("\\\\", "/").replaceAll("\\/", ":") + ":" + name;

				subs.put("_api_path_", apiPath);
			}
			else {
				subs.put("_api_path_", "");
			}

			subs.put("_portlet_", packageName + ".portlet");
			subs.put(
				"_portletpackage_",
				packageName.replaceAll("\\.", "/") + "/portlet");
		}
		else if ("activator".equals(template)) {
			if (!classname.contains("Activator")) {
				classname += "Activator";
			}
		}

		if ("portlet".equals(template) || "mvcportlet".equals(template)) {
			if (classname.endsWith("Portlet")) {
				classname = classname.replaceAll("Portlet$", "");
			}
		}

		final String hostbundlebsn = _options.hostbundlebsn();

		final String hostbundleversion = _options.hostbundleversion();

		if ("fragment".equals(template)) {
			if (isEmpty(hostbundlebsn) || isEmpty(hostbundleversion)) {
				addError(
					"Create",
					"The fragment template requires the bundle symbolic name " +
						"of the hostbundle and version must be specified.\n" +
						"For example: blade create -t fragment -h " +
						"com.liferay.login.web -H 1.0.0 name");
				return;
			}

			subs.put("_HOST_BUNDLE_BSN_", hostbundlebsn);
			subs.put("_HOST_BUNDLE_VERSION_", hostbundleversion);
		}

		subs.put("_CLASS_", classname);

		String unNormalizedPortletFqn =
			name.toLowerCase().replaceAll("-", ".") + "_" + classname;

		subs.put(
			"_portlet_fqn_",
			unNormalizedPortletFqn.replaceAll("\\.", "_"));

		File moduleTemplatesZip = getGradleTemplatesZip();

		InputStream in = new FileInputStream(moduleTemplatesZip);

		copy("standalone", template, workDir, in, glob, true, subs);

		in.close();

		if (isWorkspace) {
			final Pattern buildGlob = Pattern.compile(
				"^workspace/" + template + "/.*|\\...+/.*");

			in = new FileInputStream(moduleTemplatesZip);

			copy("workspace", template, workDir, in, buildGlob, true, subs);

			in.close();

			File settingsFile = new File(workDir, "settings.gradle");

			if (settingsFile.exists()) {
				settingsFile.delete();
			}
		}

		_blade.out().println(
			"Created the project " + name + " using the " + template +
				" template in " + workDir);
	}

	@Arguments(arg = {"[name]"})
	@Description(DESCRIPTION)
	public interface CreateOptions extends Options {

		@Description(
			"If a class is generated in the project, provide the name of the " +
				"class to be generated. If not provided defaults to Project " +
					"name."
		)
		public String classname();

		@Description("The directory where to create the new project.")
		public File dir();

		@Description(
			"If a new jsp hook fragment needs to be created, provide the name" +
				" of the host bundle symbolic name."
		)
		public String hostbundlebsn();

		@Description(
			"If a new jsp hook fragment needs to be created, provide the name" +
				" of the host bundle version."
		)
		public String hostbundleversion();

		@Description("Prints a list of available project templates")
		public boolean listtemplates();

		public String packagename();

		@Description(
			"If a new DS component needs to be created, provide the name of " +
				"the service to be implemented."
		)
		public String service();

		@Description(
			"The project template to use when creating the project. To " +
				"see the list of templates available use blade create <-l | " +
					"--listtemplates>"
		)
		public String template();
	}

	File getGradleTemplatesZip() throws Exception {
		trace(
			"Connecting to repository to find version " + TEMPLATES_VERSION +
				" gradle templates.");

		File zipFile = GradleTooling.findLatestAvailableArtifact(
			"group: 'com.liferay', " +
				"name: 'com.liferay.gradle.templates', " + "version: '" +
					TEMPLATES_VERSION + "', classifier: " +
						"'sources', ext: 'jar'");

		trace("Found gradle templates " + zipFile);

		return zipFile;
	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private boolean containsDir(File currentDir, File parentDir)
		throws Exception {

		String currentPath = currentDir.getCanonicalPath();

		String parentPath = parentDir.getCanonicalPath();

		return currentPath.startsWith(parentPath);
	}

	private boolean checkDir(File file) {
		if(file.exists()) {
			if(!file.isDirectory()) {
				return false;
			}
			else {
				File[] children = file.listFiles();

				if(children != null && children.length > 0) {
					return false;
				}
			}
		}

		return true;
	}

	private void copy(
			String type, String template, File workspaceDir, InputStream in,
			Pattern glob, boolean overwrite, Map<String, String> subs)
		throws Exception {

		try (Jar jar = new Jar("dot", in)) {
			for (Entry<String, Resource> e : jar.getResources().entrySet()) {
				String path = e.getKey();

				if (glob != null && !glob.matcher(path).matches())continue;

				Resource r = e.getValue();

				for (String key : subs.keySet()) {
					path = path.replaceAll(key, subs.get(key));
				}

				path = path.replaceAll(type + "/" + template + "/", "");

				File dest = Processor.getFile(workspaceDir, path);

				if (overwrite || (dest.lastModified() < r.lastModified()) ||
					(r.lastModified() <= 0)) {

					File dp = dest.getParentFile();

					if (!dp.exists() && !dp.mkdirs()) {
						throw new Exception("Could not create directory " + dp);
					}

					IO.copy(r.openInputStream(), dest);

					if (isTextFile(dest)) {
						process(dest, subs);
					}
				}
			}
		}
	}

	private String getClassName(String name) {
		name = WordUtils.capitalizeFully(name, ' ', '.', '-');
		name = name.replaceAll("[- .]", "");

		return name;
	}

	private File getDefaultDir() throws Exception {
		File baseDir = _blade.getBase();

		if (!Util.isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = Util.getGradleProperties(baseDir);

		String modulesDirValue = (String)properties.get(
			Workspace.DEFAULT_MODULES_DIR_PROPERTY);

		if (modulesDirValue == null) {
			modulesDirValue = Workspace.DEFAULT_MODULES_DIR;
		}

		File projectDir = Util.getWorkspaceDir(_blade);

		File modulesDir = new File(projectDir, modulesDirValue);

		return containsDir(baseDir, modulesDir) ? baseDir : modulesDir;
	}

	private String getPackageName(String name) {
		name = name.replaceAll("[- .]", ".");
		name = name.toLowerCase();

		return name;
	}

	private List<String> getTemplates() throws Exception {
		List<String> templateNames = new ArrayList<>();
		File templatesZip = getGradleTemplatesZip();

		try (Jar jar = new Jar(templatesZip)) {
			Map<String, Map<String, Resource>> directories =
				jar.getDirectories();

			for (String key : directories.keySet()) {
				Path path = Paths.get(key);

				if (path.getNameCount() == 2 && path.startsWith("standalone")) {
					templateNames.add(path.getName(1).toString());
				}
			}
		}

		return templateNames;
	}

	private boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}

		if (str.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	private boolean isExistingTemplate(String templateName) throws Exception
	{
		List<String> templates = getTemplates();

		for (String template : templates) {
			if (templateName.equals(template)) {
				return true;
			}
		}

		return false;
	}

	private boolean isTextFile(File dest) {
		String name = dest.getName();

		return textExtensions.contains(
			name.substring(name.lastIndexOf("."), name.length()));
	}

	private void listTemplates() throws Exception {
		List<String> templateNames = getTemplates();

		for (String name : templateNames) {
			_blade.out().println(name);
		}
	}

	private void process(File dest, Map<String, String> subs) throws Exception {
		String content = new String(IO.read(dest));
		String newContent = content;

		for (String sub : subs.keySet()) {
			newContent = newContent.replaceAll(sub, subs.get(sub));
		}

		if (!content.equals(newContent)) {
			IO.write(newContent.getBytes(), dest);
		}
	}

	private void trace(String msg) {
		_blade.trace("%s: %s", "create", msg);
	}

	private static final List<String> textExtensions = Arrays.asList(
		".bnd", ".java", ".project", ".xml", ".jsp", ".css", ".jspf", ".js",
		".properties", ".gradle", ".prefs");

	private final blade _blade;
	private final CreateOptions _options;

}