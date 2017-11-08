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

import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

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

	CreateCommand(blade blade) {
		_blade = blade;
		_options = null;
	}

	public void execute() throws Exception {
		if (_options.listtemplates()) {
			printTemplates();
			return;
		}

		List<String> args = _options._arguments();

		String name = args.size() > 0 ? args.get(0) : null;

		if (name == null) {
			addError("Create", "SYNOPSIS\n\t create [options] <[name]>");
			return;
		}

		String template = _options.template();

		if (template == null) {
			template = "mvc-portlet";
		}
		else if (!isExistingTemplate(template)) {
				addError(
					"Create", "the template "+template+" is not in the list");
				return;
		}

		File dir;

		if(_options.dir() != null) {
			dir = new File(_options.dir().getAbsolutePath());
		}
		else if (template.equals("theme") || template.equals("layout-template")
				|| template.equals("spring-mvc-portlet")) {
			dir = getDefaultWarsDir();
		}
		else {
			dir = getDefaultModulesDir();
		}

		final File checkDir = new File(dir, name);

		if(!checkDir(checkDir)) {
			addError(
				"Create", name + " is not empty or it is a file." +
				" Please clean or delete it then run again");
			return;
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setClassName(_options.classname());
		projectTemplatesArgs.setContributorType(_options.contributorType());
		projectTemplatesArgs.setDestinationDir(dir);
		projectTemplatesArgs.setHostBundleSymbolicName(_options.hostbundlebsn());
		projectTemplatesArgs.setHostBundleVersion(_options.hostbundleversion());
		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setPackageName(_options.packagename());
		projectTemplatesArgs.setService(_options.service());
		projectTemplatesArgs.setTemplate(template);

		boolean mavenBuild = "maven".equals(_options.build());

		projectTemplatesArgs.setGradle(!mavenBuild);
		projectTemplatesArgs.setMaven(mavenBuild);

		execute(projectTemplatesArgs);

		_blade.out().println(
			"Successfully created project " + projectTemplatesArgs.getName() + 
				" in " + dir.getAbsolutePath());
	}

	void execute(ProjectTemplatesArgs projectTemplatesArgs) throws Exception {
		File dir = projectTemplatesArgs.getDestinationDir();
		String name = projectTemplatesArgs.getName();

		new ProjectTemplates(projectTemplatesArgs);

		File gradlew = new File(dir, name+"/gradlew");

		if(gradlew.exists()) {
			gradlew.setExecutable(true);
		}
	}

	@Arguments(arg = {"[name]"})
	@Description(DESCRIPTION)
	public interface CreateOptions extends Options {

		@Description(
			"Specify the build type of the project. " +
				"Available options are gradle, maven. (gradle is default)")
	    public String build();

		@Description(
			"If a class is generated in the project, provide the name of the " +
				"class to be generated. If not provided defaults to Project " +
					"name."
		)
		public String classname();

		@Description(
			"Used to identify your module as a Theme Contributor. Also, used " +
			"to add the Liferay-Theme-Contributor-Type and Web-ContextPath " +
			"bundle headers.")
		public String contributorType();

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

	private File getDefaultModulesDir() throws Exception {
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

	private File getDefaultWarsDir() throws Exception {
		File baseDir = _blade.getBase();

		if (!Util.isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = Util.getGradleProperties(baseDir);

		String warsDirValue = (String)properties.get(
			Workspace.DEFAULT_WARS_DIR_PROPERTY);

		if (warsDirValue == null) {
			warsDirValue = Workspace.DEFAULT_WARS_DIR;
		}

		if(warsDirValue.contains(",")) {
			warsDirValue = warsDirValue.split(",")[0];
		}

		File projectDir = Util.getWorkspaceDir(_blade);

		File warsDir = new File(projectDir, warsDirValue);

		return containsDir(baseDir, warsDir) ? baseDir : warsDir;
	}

	private String[] getTemplateNames() throws Exception {
		Map<String, String> templates = ProjectTemplates.getTemplates();

		return templates.keySet().toArray(new String[0]);
	}

	private boolean isExistingTemplate(String templateName) throws Exception {
		String[] templates = getTemplateNames();

		for (String template : templates) {
			if (templateName.equals(template)) {
				return true;
			}
		}

		return false;
	}

	private void printTemplates() throws Exception {
		Map<String,String> templates = ProjectTemplates.getTemplates();

		List<String> templateNames = new ArrayList<>(templates.keySet());

		Collections.sort(templateNames);

		Comparator<String> compareLength =
			Comparator.comparingInt(String::length);

		String longestString = templateNames.stream().max(compareLength).get();

		int padLength = longestString.length() + 2;

		for (String name : templateNames) {
			_blade.out().print(StringUtils.rightPad(name, padLength));

			_blade.out().println(templates.get(name));
		}
	}

	private final blade _blade;
	private final CreateOptions _options;

}