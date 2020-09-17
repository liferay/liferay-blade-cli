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
import com.liferay.blade.cli.gradle.GradleWorkspaceProvider;
import com.liferay.blade.cli.util.Constants;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Terry Jia
 */
public class ConvertServiceBuilderCommand implements FilesSupport {

	public static final String DESCRIPTION = "Convert a service builder project to new Liferay Workspace projects";

	public ConvertServiceBuilderCommand(BladeCLI bladeCLI, ConvertArgs convertArgs) throws Exception {
		_bladeCLI = bladeCLI;

		_convertArgs = convertArgs;

		File baseDir = _convertArgs.getBase();

		GradleWorkspaceProvider gradleWorkspaceProvider = (GradleWorkspaceProvider)_bladeCLI.getWorkspaceProvider(
			baseDir);

		File projectDir = gradleWorkspaceProvider.getWorkspaceDir(_bladeCLI);

		Properties gradleProperties = gradleWorkspaceProvider.getGradleProperties(projectDir);

		String modulesDirPath = null;

		if (gradleProperties != null) {
			modulesDirPath = gradleProperties.getProperty(WorkspaceConstants.DEFAULT_MODULES_DIR_PROPERTY);
		}

		if (modulesDirPath == null) {
			modulesDirPath = WorkspaceConstants.DEFAULT_MODULES_DIR;
		}

		_modulesDir = new File(projectDir, modulesDirPath);
	}

	public void execute() throws Exception {
		File baseDir = _convertArgs.getBase();

		GradleWorkspaceProvider workspaceProvider = (GradleWorkspaceProvider)_bladeCLI.getWorkspaceProvider(baseDir);

		if (workspaceProvider == null) {
			_bladeCLI.error("Please execute command in a Liferay Workspace project");

			return;
		}

		List<String> name = _convertArgs.getName();

		final String projectName = name.isEmpty() ? null : name.get(0);

		if (projectName == null) {
			_bladeCLI.error("Please specify a plugin name");

			return;
		}

		File pluginsSDKDir = _getPluginsSdkDir(_convertArgs, baseDir, workspaceProvider.getGradleProperties(baseDir));

		Path pluginsSDKPath = pluginsSDKDir.toPath();

		Path originalProjectPath = pluginsSDKPath.resolve("portlets/" + projectName);

		if (Files.notExists(originalProjectPath)) {
			_bladeCLI.error("The project " + projectName + " does not exist in " + originalProjectPath);

			return;
		}

		Path serviceXmlPath = originalProjectPath.resolve("docroot/WEB-INF/service.xml");

		if (Files.notExists(serviceXmlPath)) {
			_bladeCLI.error("There is no service.xml file in " + projectName);

			return;
		}

		List<String> args = name;

		String sbProjectName = null;

		if (args.contains("true") || args.contains("false")) {
			sbProjectName = !args.isEmpty() && (args.size() >= 3) ? args.get(1) : null;
		}
		else {
			sbProjectName = !args.isEmpty() && (args.size() >= 2) ? args.get(1) : null;
		}

		if (sbProjectName == null) {
			if (projectName.endsWith("-portlet")) {
				sbProjectName = projectName.replaceAll("-portlet$", "");
			}
			else {
				sbProjectName = projectName;
			}
		}

		Path modulesPath = _modulesDir.toPath();

		Path sbProjectPath = modulesPath.resolve(sbProjectName);

		if (Files.exists(sbProjectPath)) {
			_bladeCLI.error(
				"The service builder module project " + sbProjectName + " already exists. Please choose another name");

			return;
		}

		ServiceBuilder oldServiceBuilderXml = new ServiceBuilder(serviceXmlPath.toFile());

		CreateCommand createCommand = new CreateCommand(_bladeCLI);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		Path sbProjectFileName = sbProjectPath.getFileName();

		projectTemplatesArgs.setDestinationDir(_modulesDir);
		projectTemplatesArgs.setName(sbProjectFileName.toString());
		projectTemplatesArgs.setPackageName(oldServiceBuilderXml.getPackagePath());
		projectTemplatesArgs.setTemplate("service-builder");
		projectTemplatesArgs.setProduct(_convertArgs.getProduct());

		createCommand.execute(projectTemplatesArgs);

		Path sbServiceProjectPath = sbProjectPath.resolve(sbProjectName + "-service");

		Path newServiceXmlPath = sbServiceProjectPath.resolve(ServiceBuilder.SERVICE_XML);

		copyFile(serviceXmlPath, newServiceXmlPath);

		ServiceBuilder serviceBuilderXml = new ServiceBuilder(newServiceXmlPath.toFile());

		String sbPackageName = serviceBuilderXml.getPackagePath();

		String packageName = sbPackageName.replaceAll("\\.", "/");

		Path oldSBFolder = originalProjectPath.resolve(Constants.DEFAULT_PLUGINS_SDK_PORTLET_SRC + packageName);

		Path newSBFolder = sbServiceProjectPath.resolve(Constants.DEFAULT_JAVA_SRC + packageName);

		Path oldServiceImplFolder = oldSBFolder.resolve("service");
		Path newServiceImplFolder = newSBFolder.resolve("service");

		if (Files.exists(oldServiceImplFolder)) {
			Files.createDirectories(newServiceImplFolder);

			copyFile(oldServiceImplFolder, newServiceImplFolder);
		}

		Path oldModelImplFolder = oldSBFolder.resolve("model");
		Path newModelImplFolder = newSBFolder.resolve("model");

		if (Files.exists(oldModelImplFolder)) {
			Files.createDirectories(newModelImplFolder);

			copyFile(oldModelImplFolder, newModelImplFolder);
		}

		Path oldMetaInfFolder = originalProjectPath.resolve(
			Constants.DEFAULT_PLUGINS_SDK_PORTLET_SRC + ServiceBuilder.META_INF);
		Path newMetaInfFolder = sbServiceProjectPath.resolve(Constants.DEFAULT_RESOURCES_SRC + ServiceBuilder.META_INF);

		if (Files.exists(oldMetaInfFolder)) {
			Files.createDirectories(newMetaInfFolder);

			copyFile(
				oldMetaInfFolder.resolve(ServiceBuilder.PORTLET_MODEL_HINTS_XML),
				newMetaInfFolder.resolve(ServiceBuilder.PORTLET_MODEL_HINTS_XML));
		}

		Path oldSrcFolder = originalProjectPath.resolve(Constants.DEFAULT_PLUGINS_SDK_PORTLET_SRC);
		Path newResourcesSrcFolder = sbServiceProjectPath.resolve(Constants.DEFAULT_RESOURCES_SRC);

		if (Files.exists(oldSrcFolder)) {
			Files.createDirectories(newResourcesSrcFolder);

			copyFile(
				oldSrcFolder.resolve(ServiceBuilder.SERVICE_PROPERTIES),
				newResourcesSrcFolder.resolve(ServiceBuilder.SERVICE_PROPERTIES));
		}

		Path sbApiProjectPath = sbProjectPath.resolve(sbProjectName + "-api");
		Path oldApiFolder = originalProjectPath.resolve(ServiceBuilder.API_62);

		if (Files.exists(oldApiFolder)) {
			Path newApiPath = sbApiProjectPath.resolve(Constants.DEFAULT_JAVA_SRC);

			Files.createDirectories(newApiPath);

			try (Stream<Path> files = Files.list(oldApiFolder)) {
				files.forEach(
					oldApiFile -> {
						try {
							copyFile(oldApiFile, newApiPath.resolve(oldApiFile.getFileName()));
						}
						catch (IOException ioe) {
							ioe.printStackTrace(_bladeCLI.error());
						}
					});
			}
		}

		//Files.delete(oldApiFolder);

		_convertedPaths.add(sbApiProjectPath);
		_convertedPaths.add(sbServiceProjectPath);

		// go through all api folders and make sure to add a packageinfo file

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
					Files.write(file.toPath(), "version 1.0.0".getBytes());
				}
				catch (IOException ioe) {
					ioe.printStackTrace(_bladeCLI.error());
				}
			}
		);

		srcPaths.close();

		// add dependency on -api to portlet project

		Path gradlePath = sbServiceProjectPath.resolve("build.gradle");

		String gradleContent = new String(Files.readAllBytes(gradlePath));

		StringBuilder sb = new StringBuilder();

		sb.append("dependencies {\n");
		sb.append("\tcompileOnly project(\":modules:");
		sb.append(sbProjectPath.getFileName());
		sb.append(":");
		sb.append(sbApiProjectPath.getFileName());
		sb.append("\")");
		sb.append(System.lineSeparator());

		String updatedContent = gradleContent.replaceAll("dependencies \\{", sb.toString());

		Files.write(gradlePath, updatedContent.getBytes());
	}

	public List<Path> getConvertedPaths() {
		return _convertedPaths;
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

	private File _getPluginsSdkDir(ConvertArgs convertArgs, File projectDir, Properties gradleProperties) {
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

		return pluginsSdkDir;
	}

	private BladeCLI _bladeCLI;
	private ConvertArgs _convertArgs;
	private final List<Path> _convertedPaths = new ArrayList<>();
	private final File _modulesDir;

	private static class ServiceBuilder {

		public static final String API_62 = "docroot/WEB-INF/service/";

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
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

				documentBuilder.setEntityResolver(
					new EntityResolver() {

						@Override
						public InputSource resolveEntity(String publicId, String systemId) {
							return new InputSource(new StringReader(""));
						}

					});

				Document doc = documentBuilder.parse(_serviceXml);

				_rootElement = doc.getDocumentElement();
			}
		}

		private Element _rootElement;
		private File _serviceXml;

	}

}