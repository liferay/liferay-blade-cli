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
import com.liferay.blade.cli.util.BladeUtil;
import com.liferay.project.templates.ProjectTemplates;
import com.liferay.project.templates.ProjectTemplatesArgs;
import com.liferay.project.templates.internal.util.FileUtil;

import java.io.File;
import java.io.PrintStream;

import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gregory Amerson
 * @author David Truong
 * @author Christopher Boyd
 */
public class CreateCommand extends BaseCommand<CreateArgs> {

	public CreateCommand() {
	}

	public CreateCommand(BladeCLI bladeCLI) {
		super(bladeCLI, null);
	}

	@Override
	public void execute() throws Exception {
		CreateArgs createArgs = getArgs();
		BladeCLI bladeCLI = getBladeCLI();

		if (createArgs.isListTemplates()) {
			_printTemplates();

			return;
		}

		String template = createArgs.getTemplate();

		if (template == null) {
			bladeCLI.err("The following option is required: [-t | --template]\n\n");
			bladeCLI.err("Availble project templates:\n\n");

			_printTemplates();

			return;
		}
		else if (template.equals("service")) {
			if (createArgs.getService() == null) {
				StringBuilder sb = new StringBuilder();

				sb.append("\"-t service <FQCN>\" parameter missing.");
				sb.append(System.lineSeparator());
				sb.append("Usage: blade create -t service -s <FQCN> <project name>");
				sb.append(System.lineSeparator());

				bladeCLI.err(sb.toString());

				return;
			}
		}
		else if (template.equals("fragment")) {
			boolean hasHostBundleBSN = false;

			if (createArgs.getHostBundleBSN() != null) {
				hasHostBundleBSN = true;
			}

			boolean hasHostBundleVersion = false;

			if (createArgs.getHostBundleVersion() != null) {
				hasHostBundleVersion = true;
			}

			if (!hasHostBundleBSN || !hasHostBundleVersion) {
				StringBuilder sb = new StringBuilder("\"-t fragment\" options missing:" + System.lineSeparator());

				if (!hasHostBundleBSN) {
					sb.append("Host Bundle BSN (\"-h\", \"--host-bundle-bsn\") is required.");
				}

				if (!hasHostBundleVersion) {
					sb.append("Host Bundle Version (\"-H\", \"--host-bundle-version\") is required.");
				}

				bladeCLI.printUsage("create", sb.toString());

				return;
			}
		}

		String name = createArgs.getName();

		if (BladeUtil.isEmpty(name)) {
			_addError("Create", "SYNOPSIS\n\t create [options] <[name]>");

			return;
		}

		if (!_isExistingTemplate(template)) {
			_addError("Create", "The template " + template + " is not in the list");

			return;
		}

		File dir;

		File argsDir = createArgs.getDir();

		if (argsDir != null) {
			dir = new File(argsDir.getAbsolutePath());
		}
		else if (template.startsWith("war") || template.equals("theme") || template.equals("layout-template") ||
				 template.equals("spring-mvc-portlet")) {

			dir = _getDefaultWarsDir();
		}
		else {
			dir = _getDefaultModulesDir();
		}

		final File checkDir = new File(dir, name);

		if (!_checkDir(checkDir)) {
			_addError("Create", name + " is not empty or it is a file. Please clean or delete it then run again");

			return;
		}

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setClassName(createArgs.getClassname());
		projectTemplatesArgs.setContributorType(createArgs.getContributorType());
		projectTemplatesArgs.setDestinationDir(dir.getAbsoluteFile());
		projectTemplatesArgs.setDependencyManagementEnabled(BladeUtil.dependencyManagerEnable(dir));
		projectTemplatesArgs.setHostBundleSymbolicName(createArgs.getHostBundleBSN());
		projectTemplatesArgs.setHostBundleVersion(createArgs.getHostBundleVersion());
		projectTemplatesArgs.setLiferayVersion(createArgs.getLiferayVersion());
		projectTemplatesArgs.setName(name);
		projectTemplatesArgs.setPackageName(createArgs.getPackageName());
		projectTemplatesArgs.setService(createArgs.getService());
		projectTemplatesArgs.setTemplate(template);

		List<File> archetypesDirs = projectTemplatesArgs.getArchetypesDirs();

		Path customTemplatesPath = bladeCLI.getExtensionsPath();

		archetypesDirs.add(FileUtil.getJarFile(ProjectTemplates.class));
		archetypesDirs.add(customTemplatesPath.toFile());

		boolean mavenBuild = "maven".equals(createArgs.getBuild());

		projectTemplatesArgs.setGradle(!mavenBuild);
		projectTemplatesArgs.setMaven(mavenBuild);

		execute(projectTemplatesArgs);

		bladeCLI.out("Successfully created project " + projectTemplatesArgs.getName() + " in " + dir.getAbsolutePath());
	}

	@Override
	public Class<CreateArgs> getArgsClass() {
		return CreateArgs.class;
	}

	protected void execute(ProjectTemplatesArgs projectTemplatesArgs) throws Exception {
		File dir = projectTemplatesArgs.getDestinationDir();
		String name = projectTemplatesArgs.getName();

		new ProjectTemplates(projectTemplatesArgs);

		File gradlew = new File(dir, name + "/gradlew");

		if (gradlew.exists()) {
			gradlew.setExecutable(true);
		}
	}

	private static boolean _checkDir(File file) {
		if (file.exists()) {
			if (!file.isDirectory()) {
				return false;
			}
			else {
				File[] children = file.listFiles();

				if (BladeUtil.isNotEmpty(children)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean _containsDir(File currentDir, File parentDir) throws Exception {
		String currentPath = currentDir.getCanonicalPath();

		String parentPath = parentDir.getCanonicalPath();

		return currentPath.startsWith(parentPath);
	}

	private void _addError(String prefix, String msg) {
		getBladeCLI().addErrors(prefix, Collections.singleton(msg));
	}

	private File _getDefaultModulesDir() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getBladeArgs();

		File base = new File(args.getBase());

		File baseDir = base.getCanonicalFile();

		if (!BladeUtil.isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = BladeUtil.getGradleProperties(baseDir);

		String modulesDirValue = (String)properties.get(WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY);

		if (modulesDirValue == null) {
			modulesDirValue = WorkspaceConstants.DEFAULT_MODULES_DIR;
		}

		File projectDir = BladeUtil.getWorkspaceDir(bladeCLI);

		File modulesDir = new File(projectDir, modulesDirValue);

		if (_containsDir(baseDir, modulesDir)) {
			return baseDir;
		}

		return modulesDir;
	}

	private File _getDefaultWarsDir() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		BaseArgs args = bladeCLI.getBladeArgs();

		File base = new File(args.getBase());

		File baseDir = base.getCanonicalFile();

		if (!BladeUtil.isWorkspace(baseDir)) {
			return baseDir;
		}

		Properties properties = BladeUtil.getGradleProperties(baseDir);

		String warsDirValue = (String)properties.get(WorkspaceConstants.DEFAULT_WARS_DIR_PROPERTY);

		if (warsDirValue == null) {
			warsDirValue = WorkspaceConstants.DEFAULT_WARS_DIR;
		}

		if (warsDirValue.contains(",")) {
			warsDirValue = warsDirValue.split(",")[0];
		}

		File projectDir = BladeUtil.getWorkspaceDir(bladeCLI);

		File warsDir = new File(projectDir, warsDirValue);

		if (_containsDir(baseDir, warsDir)) {
			return baseDir;
		}

		return warsDir;
	}

	private boolean _isExistingTemplate(String templateName) throws Exception {
		Collection<String> templateNames = BladeUtil.getTemplateNames(getBladeCLI());

		return templateNames.contains(templateName);
	}

	private void _printTemplates() throws Exception {
		BladeCLI bladeCLI = getBladeCLI();

		Map<String, String> templates = BladeUtil.getTemplates(bladeCLI);

		List<String> templateNames = new ArrayList<>(BladeUtil.getTemplateNames(getBladeCLI()));

		Collections.sort(templateNames);

		Comparator<String> compareLength = Comparator.comparingInt(String::length);

		Stream<String> stream = templateNames.stream();

		String longestString = stream.max(
			compareLength
		).get();

		int padLength = longestString.length() + 2;

		for (String name : templateNames) {
			PrintStream out = bladeCLI.out();

			out.print(StringUtils.rightPad(name, padLength));

			bladeCLI.out(templates.get(name));
		}
	}

}