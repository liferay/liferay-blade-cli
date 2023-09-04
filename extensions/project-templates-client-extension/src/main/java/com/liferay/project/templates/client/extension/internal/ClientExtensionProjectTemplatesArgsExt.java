/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.client.extension.internal;

import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProjectTemplatesArgsExt implements ProjectTemplatesArgsExt {

	public String getExtensionName() {
		return _extensionName;
	}

	public String getExtensionType() {
		return _extensionType;
	}

	@Override
	public String getTemplateName() {
		return "client-extension";
	}

	public void setExtensionName(String extensionName) {
		_extensionName = extensionName;
	}

	public void setExtensionType(String extensionType) {
		_extensionType = extensionType;
	}

	private String _extensionName = null;
	private String _extensionType = null;

}