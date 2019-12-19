/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.project.templates.js.widget.internal;

import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;

/**
 * @author Christopher Bryan Boyd
 */
public class JSWidgetProjectTemplatesArgsExt implements ProjectTemplatesArgsExt {

	public String getModulesLocation() {
		return _modulesLocation;
	}

	@Override
	public String getTemplateName() {
		return "js-widget";
	}

	public String getWorkspaceLocation() {
		return _workspaceLocation;
	}

	public void setModulesLocation(String modulesLocation) {
		_modulesLocation = modulesLocation;
	}

	public void setWorkspaceLocation(String workspaceLocation) {
		_workspaceLocation = workspaceLocation;
	}

	private String _modulesLocation = null;
	private String _workspaceLocation = null;

}