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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

/**
 * @author Gregory Amerson
 * @author Charles Wu
 * @author Simon Jiang
 */
@Parameters(
	commandDescription = "Creates a new Liferay module project from several available templates.",
	commandNames = "create"
)
public class CreateArgs extends BaseArgs {

	public String getBuild() {
		return _build;
	}

	public String getClassname() {
		return _classname;
	}

	public String getContributorType() {
		return _contributorType;
	}

	public File getDir() {
		return _dir;
	}

	public String getHostBundleBSN() {
		return _hostBundleBSN;
	}

	public String getHostBundleVersion() {
		return _hostBundleVersion;
	}

	public String getLiferayVersion() {
		return _liferayVersion;
	}

	public String getName() {
		return _name;
	}

	public String getOriginalModuleName() {
		return _originalModuleName;
	}

	public String getOriginalModuleVersion() {
		return _originalModuleVersion;
	}

	public String getPackageName() {
		return _packageName;
	}

	public String getService() {
		return _service;
	}

	public String getTemplate() {
		return _template;
	}

	public boolean isListTemplates() {
		return _listTemplates;
	}

	public void setBuild(String build) {
		_build = build;
	}

	public void setClassName(String className) {
		_classname = className;
	}

	public void setDir(File dir) {
		_dir = dir;
	}

	public void setHostBundleBSN(String hostBundleBSN) {
		_hostBundleBSN = hostBundleBSN;
	}

	public void setHostBundleVersion(String hostBundleVersion) {
		_hostBundleVersion = hostBundleVersion;
	}

	public void setLiferayVersion(String liferayVersion) {
		_liferayVersion = liferayVersion;
	}

	public void setListTemplates(boolean listTemplates) {
		_listTemplates = listTemplates;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPackageName(String packageName) {
		_packageName = packageName;
	}

	public void setService(String service) {
		_service = service;
	}

	public void setTemplate(String template) {
		_template = template;
	}

	@Parameter(
		description = "Specify the build type of the project. Available options are gradle, maven. (gradle is default)",
		names = {"-b", "--build"}
	)
	private String _build;

	@Parameter(
		description = "If a class is generated in the project, provide the name of the class to be generated. If not provided defaults to project name.",
		names = {"-c", "--classname"}
	)
	private String _classname;

	@Parameter(
		description = "Used to identify your module as a Theme Contributor. Also, used to add the Liferay-Theme-Contributor-Type and Web-ContextPath bundle headers.",
		names = {"-C", "--contributor-type"}
	)
	private String _contributorType;

	@Parameter(description = "The directory where to create the new project.", names = {"-d", "--dir"})
	private File _dir;

	@Parameter(
		description = "If a new jsp hook fragment needs to be created, provide the name of the host bundle symbolic name. Required for \"-t fragment\".",
		names = {"-h", "--host-bundle-bsn"}
	)
	private String _hostBundleBSN;

	@Parameter(
		description = "If a new jsp hook fragment needs to be created, provide the name of the host bundle version. Required for \"-t fragment\".",
		names = {"-H", "--host-bundle-version"}
	)
	private String _hostBundleVersion;

	@Parameter(
		description = "The version of Liferay to target when creating the project. Available options are 7.0, 7.1. (default 7.1).",
		names = {"-v", "--liferay-version"}
	)
	private String _liferayVersion;

	@Parameter(description = "Prints a list of available project templates", names = {"-l", "--list-templates"})
	private boolean _listTemplates;

	@Parameter(description = "The project name")
	private String _name;

	@Parameter(
		description = "Sets the name of the original module when creating a project with modules-ext template.",
		names = {"-m", "--original-module-name"}
	)
	private String _originalModuleName;

	@Parameter(
		description = "Sets the version of the original module when creating a project with modules-ext template.",
		names = {"-M", "--original-module-version"}
	)
	private String _originalModuleVersion;

	@Parameter(description = "The Java package to use when generating Java source.", names = {"-p", "--package-name"})
	private String _packageName;

	@Parameter(
		description = "If a new DS component needs to be created, provides the name of the service to be implemented.",
		names = {"-s", "--service"}
	)
	private String _service;

	@Parameter(
		description = "The project template to use when creating the project. To see the list of templates available use blade create <-l | --list-templates>",
		names = {"-t", "--template"}
	)
	private String _template;

}