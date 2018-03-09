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

import com.liferay.blade.cli.util.Constants;
import com.liferay.project.templates.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Terry Jia
 */
public class ConvertServiceBuilderCommand {

	public static final String DESCRIPTION = "Convert a service builder project to new Liferay Workspace projects";

	public ConvertServiceBuilderCommand(BladeCLI blade, ConvertCommandArgs options) throws Exception {
		_blade = blade;
		_args = options;

		File projectDir = Util.getWorkspaceDir(_blade);

		Properties gradleProperties = Util.getGradleProperties(projectDir);

		String warsDirPath = null;

		if (gradleProperties != null) {
			warsDirPath = gradleProperties.getProperty(Workspace.DEFAULT_WARS_DIR_PROPERTY);
		}

		if (warsDirPath == null) {
			warsDirPath = Workspace.DEFAULT_WARS_DIR;
		}

		_warsDir = new File(projectDir, warsDirPath);

		String moduleDirPath = null;

		if (gradleProperties != null) {
			moduleDirPath = gradleProperties.getProperty(Workspace.DEFAULT_MODULES_DIR_PROPERTY);
		}

		if (moduleDirPath == null) {
			moduleDirPath = Workspace.DEFAULT_MODULES_DIR;
		}

		_moduleDir = new File(projectDir, moduleDirPath);
	}

	public void execute() throws Exception {
		List<String> name = _args.getName();

		final String projectName = name.isEmpty() ? null : name.get(0);

		if (!Util.isWorkspace(_blade)) {
			_blade.error("Please execute command in a Liferay Workspace project");

			return;
		}

		if (projectName == null) {
			_blade.error("Please specify a plugin name");

			return;
		}

		File project = new File(_warsDir, projectName);

		if (!project.exists()) {
			_blade.error("The project " + projectName + " doesn't exist in " + _warsDir.getPath());

			return;
		}

		File serviceFile = new File(project, "src/main/webapp/WEB-INF/service.xml");

		if (!serviceFile.exists()) {
			_blade.error("There is no service.xml file in " + projectName);

			return;
		}

		List<String> args = name;

		String sbProjectName = !args.isEmpty() && args.size() >= 2 ? args.get(1) : null;

		if (sbProjectName == null) {
			if (projectName.endsWith("-portlet")) {
				sbProjectName = projectName.replaceAll("-portlet$", "");
			}
			else {
				sbProjectName = projectName;
			}
		}

		File sbProject = new File(_moduleDir, sbProjectName);

		if (sbProject.exists()) {
			_blade.error(
				"The service builder module project " + sbProjectName + " exist now, please choose another name");

			return;
		}

		ServiceBuilder oldServiceBuilderXml = new ServiceBuilder(serviceFile);

		CreateCommand createCommand = new CreateCommand(_blade);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setDestinationDir(_moduleDir);
		projectTemplatesArgs.setName(sbProject.getName());
		projectTemplatesArgs.setPackageName(oldServiceBuilderXml.getPackagePath());
		projectTemplatesArgs.setTemplate("service-builder");

		createCommand.execute(projectTemplatesArgs);

		File sbServiceProject = new File(sbProject, sbProjectName + "-service");

		File newServiceXml = new File(sbServiceProject, ServiceBuilder.SERVICE_XML);

		Files.move(serviceFile.toPath(), newServiceXml.toPath(), StandardCopyOption.REPLACE_EXISTING);

		ServiceBuilder serviceBuilderXml = new ServiceBuilder(newServiceXml);

		String sbPackageName = serviceBuilderXml.getPackagePath();

		String packageName = sbPackageName.replaceAll("\\.", "/");

		File oldSBFolder = new File(project, Constants.DEFAULT_JAVA_SRC + packageName);

		File newSBFolder = new File(sbServiceProject, Constants.DEFAULT_JAVA_SRC + packageName);

		File oldServiceImplFolder = new File(oldSBFolder, "service");
		File newServiceImplFolder = new File(newSBFolder, "service");

		if (oldServiceImplFolder.exists()) {
			newServiceImplFolder.mkdirs();

			Files.move(
				oldServiceImplFolder.toPath(), newServiceImplFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		File oldModelImplFolder = new File(oldSBFolder, "model");
		File newModelImplFolder = new File(newSBFolder, "model");

		if (oldModelImplFolder.exists()) {
			newModelImplFolder.mkdirs();

			Files.move(oldModelImplFolder.toPath(), newModelImplFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		File oldMetaInfFolder = new File(project, Constants.DEFAULT_JAVA_SRC + ServiceBuilder.META_INF);
		File newMetaInfFolder = new File(sbServiceProject, Constants.DEFAULT_RESOURCES_SRC + ServiceBuilder.META_INF);

		if (oldMetaInfFolder.exists()) {
			newMetaInfFolder.mkdirs();

			Files.move(
				new File(oldMetaInfFolder, ServiceBuilder.PORTLET_MODEL_HINTS_XML).toPath(),
				new File(newMetaInfFolder, ServiceBuilder.PORTLET_MODEL_HINTS_XML).toPath());
		}

		File oldSrcFolder = new File(project, Constants.DEFAULT_JAVA_SRC);
		File newResourcesSrcFolder = new File(sbServiceProject, Constants.DEFAULT_RESOURCES_SRC);

		if (oldSrcFolder.exists()) {
			newResourcesSrcFolder.mkdirs();

			Files.move(
				new File(oldSrcFolder, ServiceBuilder.SERVICE_PROPERTIES).toPath(),
				new File(newResourcesSrcFolder, ServiceBuilder.SERVICE_PROPERTIES).toPath());
		}

		File sbApiProject = new File(sbProject, sbProjectName + "-api");
		File oldApiFolder = new File(project, Constants.DEFAULT_WEBAPP_SRC + ServiceBuilder.API_62);

		if (oldApiFolder.exists()) {
			File newApiFolder = new File(sbApiProject, Constants.DEFAULT_JAVA_SRC);

			newApiFolder.mkdirs();

			Path newApiPath = newApiFolder.toPath();

			for (File oldApiFile : oldApiFolder.listFiles()) {
				Files.move(oldApiFile.toPath(), newApiPath.resolve(oldApiFile.getName()));
			}
		}

		oldApiFolder.delete();

		// go through all api folders and make sure to add a packageinfo file

		Path sbApiProjectPath = sbApiProject.toPath();

		Stream<Path> srcPaths = Files.walk(sbApiProjectPath.resolve(Constants.DEFAULT_JAVA_SRC));

		srcPaths.map(
			path -> path.toFile()
		).filter(
			file -> _isJavaFile(file) && _isInExportedApiFolder(file)
		).map(
			file -> {
				Path filePath = file.toPath();

				Path sibling = filePath.resolveSibling("packageinfo");

				return sibling.toFile();
			}
		).filter(
			file -> !file.exists()
		).forEach(
			file -> {
				try {
					Files.write(file.toPath(), new String("version 1.0.0").getBytes());
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		);

		srcPaths.close();

		// add dependency on -api to portlet project

		File gradleFile = new File(project, "build.gradle");

		String gradleContent = new String(Files.readAllBytes(gradleFile.toPath()));

		StringBuilder sb = new StringBuilder();

		sb.append("dependencies {\n");
		sb.append("\tcompileOnly project(\":modules:");
		sb.append(sbProject.getName());
		sb.append(":");
		sb.append(sbApiProject.getName());
		sb.append("\")\n");

		String updatedContent = gradleContent.replaceAll("dependencies \\{", sb.toString());

		Files.write(gradleFile.toPath(), updatedContent.getBytes());

		System.out.println("Migrating files done, then you should fix breaking changes and re-run build-service task.");
	}

	private static boolean _isInExportedApiFolder(File file) {
		File dir = file.getParentFile();

		String dirName = dir.getName();

		if (dirName.equals("exception") || dirName.equals("model") || dirName.equals("service") ||
			dirName.equals("persistence")) {

			return true;
		}

		return false;
	}

	private static boolean _isJavaFile(File file) {
		String name = file.getName();

		if (file.isFile() && name.endsWith(".java")) {
			return true;
		}

		return false;
	}

	private ConvertCommandArgs _args;
	private BladeCLI _blade;
	private final File _moduleDir;
	private final File _warsDir;

	private static class ServiceBuilder {

		public static final String API_62 = "WEB-INF/service/";

		public static final String META_INF = "META-INF/";

		public static final String PORTLET_MODEL_HINTS_XML = "portlet-model-hints.xml";

		public static final String SERVICE_PROPERTIES = "service.properties";

		public static final String SERVICE_XML = "service.xml";

		public ServiceBuilder(File serviceXml) throws Exception {
			_serviceXml = serviceXml;

			_parse();
		}

		public String getPackagePath() {
			return _rootElement.getAttribute("package-path");
		}

		private void _parse() throws Exception {
			if ((_rootElement == null) && (_serviceXml != null) && _serviceXml.exists()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				Document doc = dBuilder.parse(_serviceXml);

				_rootElement = doc.getDocumentElement();
			}
		}

		private Element _rootElement;
		private File _serviceXml;

	}

}