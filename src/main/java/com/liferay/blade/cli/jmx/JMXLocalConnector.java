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

package com.liferay.blade.cli.jmx;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author Gregory Amerson
 */
public class JMXLocalConnector {

	/**
	 * Uses Oracle JDK's Attach API to try to search VMs on this machine looking
	 * for the osgi.core MBeans. This will stop searching for VMs once the
	 * MBeans are found. Beware if you have multiple JVMs with osgi.core MBeans
	 * published.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getLocalConnectorAddress(String objName) {
		Thread thread = Thread.currentThread();

		ClassLoader cl = thread.getContextClassLoader();

		ClassLoader toolsClassloader = null;

		try {
			toolsClassloader = _getToolsClassLoader(cl);

			if (toolsClassloader != null) {
				thread.setContextClassLoader(toolsClassloader);

				Class< ? > vmClass = toolsClassloader.loadClass("com.sun.tools.attach.VirtualMachine");

				Method listMethod = vmClass.getMethod("list");

				List<Object> vmds = (List<Object>)listMethod.invoke(null);

				for (Object vmd : vmds) {
					String localConnectorAddress = _attach(toolsClassloader, vmClass, vmd, objName);

					if (localConnectorAddress != null) {
						return localConnectorAddress;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			thread.setContextClassLoader(cl);

			// try to get custom classloader to unload native libs

			try {
				if (toolsClassloader != null) {
					Field nl = ClassLoader.class.getDeclaredField("nativeLibraries");

					nl.setAccessible(true);

					Vector< ? > nativeLibs = (Vector< ? >)nl.get(toolsClassloader);

					for (Object nativeLib : nativeLibs) {
						Class<?> clazz = nativeLib.getClass();

						Field nameField = clazz.getDeclaredField("name");

						nameField.setAccessible(true);

						String name = (String)nameField.get(nativeLib);

						File nativeLibFile = new File(name);

						String nativeLibFileName = nativeLibFile.getName();

						if (nativeLibFileName.contains("attach")) {
							Method f = clazz.getDeclaredMethod("finalize");

							f.setAccessible(true);
							f.invoke(nativeLib);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public JMXLocalConnector(int port) throws MalformedURLException {
		this(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi"));
	}

	public JMXLocalConnector(JMXServiceURL serviceUrl) {
		try {
			final JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);

			mBeanServerConnection = jmxConnector.getMBeanServerConnection();
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to get JMX connection", e);
		}
	}

	public JMXLocalConnector(String objectName) throws MalformedURLException {
		this(new JMXServiceURL(getLocalConnectorAddress(objectName)));
	}

	protected MBeanServerConnection mBeanServerConnection;

	private static String _attach(ClassLoader toolsClassloader, Class<?> vmClass, Object vmd, String name) {
		try {
			Class< ? > vmdClass = toolsClassloader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");

			Method idMethod = vmdClass.getMethod("id");

			String id = (String)idMethod.invoke(vmd);

			Method attachMethod = vmClass.getMethod("attach", String.class);

			Object vm = attachMethod.invoke(null, id);

			try {
				Method getAgentProperties = vmClass.getMethod("getAgentProperties");

				Properties agentProperties = (Properties)getAgentProperties.invoke(vm);

				String localConnectorAddress = agentProperties.getProperty(
					"com.sun.management.jmxremote.localConnectorAddress");

				if (localConnectorAddress == null) {
					File agentJar = _findJdkJar("management-agent.jar");

					if (agentJar != null) {
						Method loadAgent = vmClass.getMethod("loadAgent", String.class);

						loadAgent.invoke(vm, agentJar.getCanonicalPath());

						agentProperties = (Properties)getAgentProperties.invoke(vm);

						localConnectorAddress = agentProperties.getProperty(
							"com.sun.management.jmxremote.localConnectorAddress");
					}
				}

				if (localConnectorAddress != null) {
					final JMXServiceURL jmxServiceUrl = new JMXServiceURL(localConnectorAddress);

					final JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceUrl, null);

					final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

					if (mBeanServerConnection != null) {
						final ObjectName objectName = _getObjectName(name, mBeanServerConnection);

						if (objectName != null) {
							return localConnectorAddress;
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				Method detachMethod = vmClass.getMethod("detach");

				detachMethod.invoke(vm);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static File _findJdkJar(String jar) throws IOException {
		File retval = null;

		final String jarPath = File.separator + "lib" + File.separator + jar;
		final String javaHome = System.getProperty("java.home");

		File jarFile = new File(javaHome + jarPath);

		if (jarFile.exists()) {
			retval = jarFile;
		}
		else {
			jarFile = new File(javaHome + "/.." + jarPath);

			if (jarFile.exists()) {
				retval = jarFile.getCanonicalFile();
			}
		}

		return retval;
	}

	private static ObjectName _getObjectName(String objectNameValue, MBeanServerConnection mBeanServerConnection)
		throws IOException, MalformedObjectNameException {

		final ObjectName objectName = new ObjectName(objectNameValue);

		final Set<ObjectName> objectNames = mBeanServerConnection.queryNames(objectName, null);

		if ((objectNames != null) && !objectNames.isEmpty()) {
			Iterator<ObjectName> iterator = objectNames.iterator();

			return iterator.next();
		}

		return null;
	}

	private static ClassLoader _getToolsClassLoader(ClassLoader parent) throws IOException {
		File toolsJar = _findJdkJar("tools.jar");

		if ((toolsJar != null) && toolsJar.exists()) {
			URL toolsUrl = null;

			try {
				URI toolsURI = toolsJar.toURI();

				toolsUrl = toolsURI.toURL();
			}
			catch (MalformedURLException murle) {
				//
			}

			URL[] urls = {toolsUrl};

			return new URLClassLoader(urls, parent);
		}

		return null;
	}

}