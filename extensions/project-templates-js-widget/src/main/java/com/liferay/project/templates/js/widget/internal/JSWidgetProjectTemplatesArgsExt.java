package com.liferay.project.templates.js.widget.internal;

import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;

public class JSWidgetProjectTemplatesArgsExt implements ProjectTemplatesArgsExt {
	@Override
	public String getTemplateName() {
		return "js-widget";
	}
	public String getWorkspaceLocation() {
		return _workspaceLocation;
	}

	public String getModulesLocation() {
		return _modulesLocation;
	}

	public void setWorkspaceLocation(String workspaceLocation) {
		_workspaceLocation = workspaceLocation;
	}
	public void setModulesLocation(String modulesLocation) {
		_modulesLocation = modulesLocation;
	}
	private String _workspaceLocation = null;
	private String _modulesLocation = null;
}
