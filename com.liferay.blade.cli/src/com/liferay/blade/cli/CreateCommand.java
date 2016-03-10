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

import java.io.File;
import java.io.InputStream;

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

	public CreateCommand(blade blade, CreateOptions options) {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		List<String> args = _options._arguments();

		Template template = _options.template();

		if (template == null) {
			template = Template.mvcportlet;
		}

		File dir = _options.dir() != null ? _options.dir() : getDefaultDir();
		String name = args.remove(0);
		File workDir = Processor.getFile(dir, name);

		name = workDir.getName();

		final Pattern glob = Pattern.compile(
			"^standalone/" + template.name() + "/.*|\\...+/.*");

		final Map<String, String> subs = new HashMap<>();
		subs.put("templates/standalone/" + template.name() + "/", "");
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

		if (Template.service.equals(template)) {
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

		if (Template.servicewrapper.equals(template)) {
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

		if (Template.servicebuilder.equals(template)) {
			if (isEmpty(packageName)) {
				addError(
					"Create",
					"The servicebuilder template requires the name of the " +
						"root package within which to create service builder " +
						"classes must be specified.\nFor example: blade " +
						"create -t servicebuilder -p " +
						"com.liferay.docs.guestbook guestbook");
				return;
			}

			subs.put("_api_", packageName + ".api");
			subs.put("_svc_", packageName + ".svc");
			subs.put("_web_", packageName + ".web");
			subs.put("_portlet_", packageName + ".portlet");
			subs.put(
				"_portletpackage_",
				packageName.replaceAll("\\.", "/") + "/portlet");

			if (!classname.contains("Portlet")) {
				classname += "Portlet";
			}
		}
		else if (Template.activator.equals(template)) {
			if (!classname.contains("Activator")) {
				classname += "Activator";
			}
		}
		else if (Template.portlet.equals(template) ||
				 Template.mvcportlet.equals(template)) {

			if (!classname.contains("Portlet")) {
				classname += "Portlet";
			}
		}

		final String hostbundlebsn = _options.hostbundlebsn();

		final String hostbundleversion = _options.hostbundleversion();

		if (Template.fragment.equals(template)) {
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

		subs.put("_CLASSNAME_", classname);

		String unNormalizedPortletFqn =
			name.toLowerCase().replaceAll("-", ".") + "_" + classname;

		subs.put(
			"_portlet_fqn_",
			unNormalizedPortletFqn.replaceAll("\\.", "_"));

		InputStream in = getClass().getResourceAsStream("/templates.zip");

		copy("standalone", template.name(), workDir, in, glob, true, subs);

		if (Util.isWorkspace(dir)) {
			final Pattern buildGlob = Pattern.compile(
				"^workspace/" + template.name() + "/.*|\\...+/build.gradle");

			in = getClass().getResourceAsStream("/templates.zip");

			copy(
				"workspace", template.name(), workDir, in, buildGlob, true,
				subs);

			File settingsFile = new File(workDir, "settings.gradle");

			if (settingsFile.exists()) {
				settingsFile.delete();
			}
		}

		_blade.out().println(
			"Created the project " + name + " using the " + template +
				" template in " + workDir);
	}

	@Arguments(arg = {"name"})
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

		public String packagename();

		@Description(
			"If a new DS component needs to be created, provide the name of " +
				"the service to be implemented."
		)
		public String service();

		@Description(
			"The project template to use when creating the project. The " +
				"following templates are available: activator, fragment, " +
					"mvcportlet, portlet, service, servicebuilder, " +
						"servicewrapper"
		)
		public Template template();

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

	private boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}

		if (str.trim().isEmpty()) {
			return true;
		}

		return false;
	}

	private boolean isTextFile(File dest) {
		String name = dest.getName();

		return textExtensions.contains(
			name.substring(name.lastIndexOf("."), name.length()));
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

	private static final List<String> textExtensions = Arrays.asList(
		".bnd", ".java", ".project", ".xml", ".jsp", ".css", ".jspf", ".js",
		".properties", ".gradle", ".prefs");

	private final blade _blade;
	private final CreateOptions _options;

}