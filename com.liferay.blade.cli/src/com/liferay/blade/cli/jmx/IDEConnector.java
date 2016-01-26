package com.liferay.blade.cli.jmx;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author Gregory Amerson
 */
public class IDEConnector extends JMXLocalConnector {

	public IDEConnector() throws MalformedURLException {
		super(name + ":type=" + type + ",*");
	}

	public Object openDir(File dir) throws Exception {
		final ObjectName workspaceHelper = getWorkspaceHelper(
			mBeanServerConnection);

		return mBeanServerConnection.invoke(
			workspaceHelper, "openDir",
			new Object[] {dir.getAbsoluteFile().getAbsolutePath()},
			new String[] {String.class.getName()});
	}

	private static ObjectName getWorkspaceHelper(
			MBeanServerConnection mBeanServerConnection)
		throws IOException, MalformedObjectNameException {

		final ObjectName objectName = new ObjectName(
			name + ":type=" + type + ",*");
		final Set<ObjectName> objectNames = mBeanServerConnection.queryNames(
			objectName, null);

		if ((objectNames != null) && (objectNames.size() > 0)) {
			return objectNames.iterator().next();
		}

		return null;
	}

	private static final String name = "com.liferay.ide.ui";
	private static final String type = "WorkspaceHelper";

}