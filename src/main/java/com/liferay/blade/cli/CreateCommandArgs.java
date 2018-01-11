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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

/**
 * @author Gregory Amerson
 */
@Parameters(
	commandNames = {"create"},
	commandDescription = "Creates a new Liferay module project from several available templates."
)
public class CreateCommandArgs {

	public String getBuild() {
		return build;
	}

	public String getClassname() {
		return classname;
	}

	public String getContributorType() {
		return contributorType;
	}

	public File getDir() {
		return dir;
	}

	public String getHostbundlebsn() {
		return hostbundlebsn;
	}

	public String getHostbundleversion() {
		return hostbundleversion;
	}

	public String getName() {
		return name;
	}

	public String getPackagename() {
		return packagename;
	}

	public String getService() {
		return service;
	}

	public String getTemplate() {
		return template;
	}

	public boolean isListTemplates() {
		return listTemplates;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Parameter(
		names = {"-b", "--build"},
		description = "Specify the build type of the project. Available options are gradle, maven. (gradle is default)"
	)
	private String build;

	@Parameter(
		names = {"-c", "--classname"},
		description = "If a class is generated in the project, provide the name of the class to be generated. If not provided defaults to project name."
	)
	private String classname;

	@Parameter(
		names = {"-C", "--contributor-type"},
		description = "Used to identify your module as a Theme Contributor. Also, used to add the Liferay-Theme-Contributor-Type and Web-ContextPath bundle headers."
	)
	private String contributorType;

	@Parameter(names = {"-d", "--dir"}, description ="The directory where to create the new project.")
	private File dir;

	@Parameter(
		names = {"-h", "--host-bundle-bsn"},
		description = "If a new jsp hook fragment needs to be created, provide the name of the host bundle symbolic name."
	)
	private String hostbundlebsn;

	@Parameter(
		names = {"-H", "--host-bundle-version"},
		description = "If a new jsp hook fragment needs to be created, provide the name of the host bundle version."
	)
	private String hostbundleversion;

	@Parameter(names = {"-l", "--list-templates"}, description ="Prints a list of available project templates")
	private boolean listTemplates;

	@Parameter(description ="The project name")
	private String name;

	@Parameter(names = {"-p", "--package-name"})
	private String packagename;

	@Parameter(
		names = {"-s", "--service"},
		description = "If a new DS component needs to be created, provides the name of the service to be implemented."
	)
	private String service;

	@Parameter(
		names = {"-t", "--template"},
		description = "The project template to use when creating the project. To see the list of templates available use blade create <-l | --list-templates>"
	)
	private String template;

}