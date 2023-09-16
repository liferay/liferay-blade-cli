/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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