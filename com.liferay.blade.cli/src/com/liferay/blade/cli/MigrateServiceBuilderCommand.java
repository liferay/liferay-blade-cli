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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.liferay.blade.cli.gradle.GradleExec;
import com.liferay.blade.cli.util.BndProperties;
import com.liferay.blade.cli.util.BndPropertiesValue;
import com.liferay.blade.cli.util.Constants;
import com.liferay.project.templates.ProjectTemplatesArgs;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;

/**
 * @author Terry Jia
 */
public class MigrateServiceBuilderCommand {

	public static final String DESCRIPTION = "Migrate service builder to new workspace Module project";

	public MigrateServiceBuilderCommand(blade blade, MigrateServiceBuilderOptions options) throws Exception {

		_blade = blade;
		_options = options;

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
		final List<String> args = _options._arguments();

		final String projectName = !args.isEmpty() ? args.get(0) : null;

		if (!Util.isWorkspace(_blade)) {
			_blade.error("Please execute this in a Liferay Workspace Project");

			return;
		}

		if (args.isEmpty()) {
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
			_blade.error("There is no service.xml file in project " + projectName);

			return;
		}

		String sbProjectName = !args.isEmpty() && args.size() >= 2 ? args.get(1) : null;

		if (sbProjectName == null) {
			sbProjectName = projectName + "-sb";
		}

		File sbProject = new File(_moduleDir, sbProjectName);

		if (sbProject.exists()) {
			_blade.error(
				"The service builder module project " + sbProjectName + " exist now, please choose another name");

			return;
		}

		CreateCommand createCommand = new CreateCommand(_blade);

		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		projectTemplatesArgs.setDestinationDir(_moduleDir);
		projectTemplatesArgs.setName(sbProject.getName());
		projectTemplatesArgs.setTemplate("service-builder");

		createCommand.execute(projectTemplatesArgs, true);

		File sbServiceProject = new File(sbProject, sbProjectName + "-service");

		// copy service.xml
		File emptyServiceXml = new File(sbServiceProject, ServiceBuilder.SERVICE_XML);

		IO.delete(emptyServiceXml);

		IO.copy(serviceFile, emptyServiceXml);

		GradleExec ge = new GradleExec(_blade);

		ge.executeGradleCommand("buildService", sbProject);

		ServiceBuilder serviceBuilderXml = new ServiceBuilder(serviceFile);

		String sbPackageName = serviceBuilderXml.getPackagePath();

		String packageName = sbPackageName.replaceAll("\\.", "/");

		File oldSBFolder = new File(project, Constants.DEFAULT_JAVA_SRC + packageName);

		File newSBFolder = new File(sbServiceProject, Constants.DEFAULT_JAVA_SRC + packageName);

		File oldServiceImplFolder = new File(oldSBFolder, ServiceBuilder.DEFAULT_SERVICE_IMPL);
		File newServiceImplFolder = new File(newSBFolder, ServiceBuilder.DEFAULT_SERVICE_IMPL);

		// copy local service impl class and service impl class
		if (oldServiceImplFolder.exists()) {
			Files.walkFileTree(oldServiceImplFolder.toPath(), new CopyDirVisitor(oldServiceImplFolder.toPath(),
				newServiceImplFolder.toPath(), StandardCopyOption.REPLACE_EXISTING));
		}

		System.out.println("Copid local-service impl class and service impl class.");

		File oldModelImplFolder = new File(oldSBFolder, ServiceBuilder.DEFAULT_MODEL_IMPL);
		File newModelImplFolder = new File(newSBFolder, ServiceBuilder.DEFAULT_MODEL_IMPL);

		// copy model impl class
		if (oldModelImplFolder.exists()) {
			Files.walkFileTree(oldModelImplFolder.toPath(), new CopyDirVisitor(oldModelImplFolder.toPath(),
				newModelImplFolder.toPath(), StandardCopyOption.REPLACE_EXISTING));
		}

		System.out.println("Copid model impl class.");

		File oldMetaInfFolder = new File(project, Constants.DEFAULT_JAVA_SRC + ServiceBuilder.META_INF);
		File newMetaInfFolder = new File(sbServiceProject, Constants.DEFAULT_RESOURCES_SRC + ServiceBuilder.META_INF);

		// copy portlet-model-hints.xml
		if (oldMetaInfFolder.exists()) {
			IO.copy(new File(oldMetaInfFolder, ServiceBuilder.PORTLET_MODEL_HINTS_XML),
				new File(newMetaInfFolder, ServiceBuilder.PORTLET_MODEL_HINTS_XML));
		}

		System.out.println("Copid portlet-model-hints.xml");

		File sbApiProject = new File(sbProject, sbProjectName + "-api");
		File oldApiFolder = new File(project, Constants.DEFAULT_WEBAPP_SRC + ServiceBuilder.API_62);

		File oldComparatorFolder = new File(oldApiFolder, packageName + "/" + ServiceBuilder.DEFAULT_COMPARATOR);
		File newComparatorFolder = new File(new File(sbApiProject, Constants.DEFAULT_JAVA_SRC),
			packageName + "/" + ServiceBuilder.DEFAULT_COMPARATOR);

		if (oldComparatorFolder.exists()) {
			newComparatorFolder.mkdirs();

			Files.walkFileTree(oldComparatorFolder.toPath(), new CopyDirVisitor(oldComparatorFolder.toPath(),
				newComparatorFolder.toPath(), StandardCopyOption.REPLACE_EXISTING));

			System.out.println("Copid comparator class");
		}

		// fix the bnd.bnd export package issue and see LPS-69637
		File bndFile = new File(sbApiProject, "bnd.bnd");

		if (bndFile.exists()) {
			BndProperties bndPro = new BndProperties();

			try {
				bndPro.load(new FileInputStream(bndFile));

				BndPropertiesValue e = (BndPropertiesValue) bndPro.get("Export-Package");

				String formatedValue = e.getFormatedValue();
				formatedValue = formatedValue + ",\\" + "\n\t" + sbPackageName + ".util.comparator";

				e.setFormatedValue(formatedValue);
				e.setOriginalValue("");

				bndPro.addValue("Export-Package", e);

				bndPro.store(new FileOutputStream(bndFile), null);
			}
			catch (IOException e) {
			}

			System.out.println("need to fix bnd.bnd issue and see LPS-69637.");
		}

		System.out.println("Migrate files done, then you should fix breaking changes and re-run build-service task.");
	}

	@Arguments(arg = { "[name]", "[service-builder-project-name]" })
	@Description(DESCRIPTION)
	public interface MigrateServiceBuilderOptions extends Options {
	}

	private class ServiceBuilder {
		public static final String DEFAULT_SERVICE_IMPL = "service/impl/";
		public static final String DEFAULT_MODEL_IMPL = "model/impl/";
		public static final String DEFAULT_COMPARATOR = "util/comparator";
		public static final String META_INF = "META-INF/";
		public static final String API_62 = "WEB-INF/service/";
		public static final String PORTLET_MODEL_HINTS_XML = "portlet-model-hints.xml";
		public static final String SERVICE_XML = "service.xml";

		File _serviceXml;
		Element _rootElement;

		public ServiceBuilder(File serviceXml) {
			_serviceXml = serviceXml;
			parse();
		}

		private void parse() {
			if ((_rootElement == null) && (_serviceXml != null) && (_serviceXml.exists())) {
				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(_serviceXml);

					_rootElement = doc.getDocumentElement();
				}
				catch (ParserConfigurationException e) {
				}
				catch (SAXException e) {
				}
				catch (IOException e) {
				}
			}
		}

		public String getPackagePath() {
			return _rootElement.getAttribute("package-path");
		}
	}

	private blade _blade;
	private final File _warsDir;
	private final File _moduleDir;
	private MigrateServiceBuilderOptions _options;

}