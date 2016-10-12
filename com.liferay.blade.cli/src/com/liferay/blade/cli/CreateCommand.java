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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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
		if (_options.listtemplates()) {
			listTemplates();
			return;
		}

		List<String> args = _options._arguments();

		String template = _options.template();

		if (template == null) {
			template = "mvc-portlet";
		}
		else if (!isExistingTemplate(template)) {
				addError(
					"Create", "the template "+template+" is not in the list");
				return;
		}

		String name = args.remove(0);

		final File dir =
			_options.dir() != null ? _options.dir() : getDefaultDir();

		final File checkDir = new File(dir, name);

		if(!checkDir(checkDir)) {
			addError(
				"Create", name + " is not empty or it is a file." +
				" Please clean or delete it then run again");
			return;
		}

		final boolean isWorkspace = Util.isWorkspace(dir);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setClassName(_options.classname());
		projectTemplatesArgs.setDestinationDir(dir);
		projectTemplatesArgs.setHostBundleSymbolicName(_options.hostbundlebsn());
		projectTemplatesArgs.setHostBundleVersion(_options.hostbundleversion());
		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setPackageName(_options.packagename());
		projectTemplatesArgs.setService(_options.service());
		projectTemplatesArgs.setTemplate(template);

		new ProjectTemplates(projectTemplatesArgs);

		if (isWorkspace) {
			File settingsFile = new File(dir, "settings.gradle");

			if (settingsFile.exists()) {
				settingsFile.delete();
			}

			IO.delete(new File(dir, "gradlew"));
			IO.delete(new File(dir, "gradlew.bat"));
			IO.delete(new File(dir, "gradle"));
		}
		else {
			File gradlew = new File(dir, "gradlew");

			if (gradlew.exists()) {
				gradlew.setExecutable(true);
			}
		}

		_blade.out().println(
			"Created the project " + name + " using the " + template +
				" template in " + dir);
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

	private List<String> getTemplates() throws Exception {
		List<String> templateNames = new ArrayList<>();

		for (String templateName : getTemplateNames()) {
			templateNames.add(templateName);
		}

		return templateNames;
	}

	private String[] getTemplateNames() throws Exception {
		return ProjectTemplates.getTemplates();
	}

	private boolean isExistingTemplate(String templateName) throws Exception {
		List<String> templates = getTemplates();

		for (String template : templates) {
			if (templateName.equals(template)) {
				return true;
			}
		}

		return false;
	}

	private void listTemplates() throws Exception {
		String[] templateNames = getTemplateNames();

		for (String name : templateNames) {
			_blade.out().println(name);
		}
	}

	private final blade _blade;
	private final CreateOptions _options;

}