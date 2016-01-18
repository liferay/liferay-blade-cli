package com.liferay.blade.cli;

import java.io.File;

import javax.management.ObjectName;

/**
 * @author Gregory Amerson
 */
public class IDEConnector extends JMXLocalConnector {

	public IDEConnector() {
		super(name, type);
	}

	public Object openDir(File dir) throws Exception {
		final ObjectName workspaceHelper = getMBean(
			name, type, mBeanServerConnection);

		return mBeanServerConnection.invoke(workspaceHelper, "openDir",
				new Object[] { dir.getAbsoluteFile().getAbsolutePath() },
				new String[] { String.class.getName() });
	}

	private static final String name = "com.liferay.ide.ui";
	private static final String type = "WorkspaceHelper";

}