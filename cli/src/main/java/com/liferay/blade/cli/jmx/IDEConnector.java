/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.jmx;

import java.io.File;

import java.net.MalformedURLException;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/**
 * @author Gregory Amerson
 */
public class IDEConnector extends JMXLocalConnector {

	public IDEConnector(Consumer<String> logger) throws MalformedURLException {
		super(_NAME + ":type=" + _TYPE + ",*", logger);

		_logger = logger;
	}

	public Object openDir(File dir) throws Exception {
		final ObjectName workspaceHelper = _getWorkspaceHelper(mBeanServerConnection);

		File absoluteDir = dir.getAbsoluteFile();

		String absolutePath = absoluteDir.getAbsolutePath();

		_logger.accept("Invoking WorkspaceHelper.openDir(" + absolutePath + ")");

		return mBeanServerConnection.invoke(
			workspaceHelper, "openDir", new Object[] {absolutePath}, new String[] {String.class.getName()});
	}

	private ObjectName _getWorkspaceHelper(MBeanServerConnection mBeanServerConnection) throws Exception {
		final ObjectName objectName = new ObjectName(_NAME + ":type=" + _TYPE + ",*");

		final Set<ObjectName> objectNames = mBeanServerConnection.queryNames(objectName, null);

		if ((objectNames != null) && !objectNames.isEmpty()) {
			Iterator<ObjectName> iterator = objectNames.iterator();

			return iterator.next();
		}

		return null;
	}

	private static final String _NAME = "com.liferay.ide.ui";

	private static final String _TYPE = "WorkspaceHelper";

	private Consumer<String> _logger;

}