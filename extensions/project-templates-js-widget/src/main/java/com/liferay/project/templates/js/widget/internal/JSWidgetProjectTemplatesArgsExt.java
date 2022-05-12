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

package com.liferay.project.templates.js.widget.internal;

import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;

/**
 * @author Christopher Bryan Boyd
 * @author Simon Jiang
 */
public class JSWidgetProjectTemplatesArgsExt implements ProjectTemplatesArgsExt {

	public String getModulesLocation() {
		return _modulesLocation;
	}

	public String getPlatform() {
		return _platform;
	}

	public String getProjectType() {
		return _projectType;
	}

	public String getTarget() {
		return _target;
	}

	@Override
	public String getTemplateName() {
		return "js-widget";
	}

	public String getWorkspaceLocation() {
		return _workspaceLocation;
	}

	public boolean isBatchModel() {
		return _batchModel;
	}

	public void setBatchModel(String batchModel) {
		_batchModel = Boolean.parseBoolean(batchModel);
	}

	public void setModulesLocation(String modulesLocation) {
		_modulesLocation = modulesLocation;
	}

	public void setPlatform(String platform) {
		_platform = platform;
	}

	public void setProjectType(String projectType) {
		_projectType = projectType;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setWorkspaceLocation(String workspaceLocation) {
		_workspaceLocation = workspaceLocation;
	}

	private boolean _batchModel = false;
	private String _modulesLocation = null;
	private String _platform = null;
	private String _projectType = null;
	private String _target = null;
	private String _workspaceLocation = null;

}