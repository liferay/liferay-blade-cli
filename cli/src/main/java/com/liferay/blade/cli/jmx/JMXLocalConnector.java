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
import java.util.function.Consumer;

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
	 * @param logger
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getLocalConnectorAddress(String objName, Consumer<String> logger) {
		Thread thread = Thread.currentThread();

		ClassLoader classLoader = thread.getContextClassLoader();

		ClassLoader toolsClassLoader = null;

		try {
			toolsClassLoader = _getToolsClassLoader(classLoader);

			if (toolsClassLoader != null) {
				thread.setContextClassLoader(toolsClassLoader);

				logger.accept("Trying to load VirtualMachine class...");

				Class<?> vmClass = toolsClassLoader.loadClass("com.sun.tools.attach.VirtualMachine");

				Method listMethod = vmClass.getMethod("list");

				List<Object> vmds = (List<Object>)listMethod.invoke(null);

				logger.accept("Found " + vmds.size() + " vms on this machine.");

				for (Object vmd : vmds) {
					String localConnectorAddress = _attach(toolsClassLoader, vmClass, vmd, objName, logger);

					if (localConnectorAddress != null) {
						logger.accept("Using localConnectorAddress=" + localConnectorAddress);

						return localConnectorAddress;
					}

					logger.accept("Could not find " + objName + " in this vm.");
				}
			}
		}
		catch (Exception exception) {
		}
		finally {
			thread.setContextClassLoader(classLoader);

			// try to get custom classLoader to unload native libs

			try {
				if (toolsClassLoader != null) {
					Field nl = ClassLoader.class.getDeclaredField("nativeLibraries");

					nl.setAccessible(true);

					Vector<?> nativeLibs = (Vector<?>)nl.get(toolsClassLoader);

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
			catch (Exception exception) {
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
		catch (Exception exception) {
			throw new IllegalArgumentException("Unable to get JMX connection", exception);
		}
	}

	public JMXLocalConnector(String objectName, Consumer<String> logger) throws MalformedURLException {
		this(new JMXServiceURL(getLocalConnectorAddress(objectName, logger)));
	}

	protected MBeanServerConnection mBeanServerConnection;

	private static String _attach(
		ClassLoader toolsClassLoader, Class<?> vmClass, Object vmd, String name, Consumer<String> logger) {

		try {
			Class<?> vmdClass = toolsClassLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");

			Method idMethod = vmdClass.getMethod("id");

			String id = (String)idMethod.invoke(vmd);

			logger.accept("Found vm id of " + id + ". Trying to attach...");

			Method attachMethod = vmClass.getMethod("attach", String.class);

			Object vm = attachMethod.invoke(null, id);

			logger.accept("Attached to vm = " + vm);

			try {
				Method getAgentProperties = vmClass.getMethod("getAgentProperties");

				Properties agentProperties = (Properties)getAgentProperties.invoke(vm);

				String localConnectorAddress = agentProperties.getProperty(
					"com.sun.management.jmxremote.localConnectorAddress");

				logger.accept("Trying to get localConnectorAddress=" + localConnectorAddress);

				if (localConnectorAddress == null) {
					logger.accept("localConnectorAdress is null, trying to load management-agent.jar.");

					File agentJar = _findJdkJar("management-agent.jar");

					if (agentJar != null) {
						logger.accept("Found agent jar = " + agentJar);

						Method loadAgent = vmClass.getMethod("loadAgent", String.class);

						logger.accept("Invoking loadAgent...");

						loadAgent.invoke(vm, agentJar.getCanonicalPath());

						logger.accept("Managemet agent loaded, trying to find localConnectorAddress");

						agentProperties = (Properties)getAgentProperties.invoke(vm);

						localConnectorAddress = agentProperties.getProperty(
							"com.sun.management.jmxremote.localConnectorAddress");
					}
					else {
						logger.accept("Could not find management-agent.jar at location " + agentJar);
					}
				}

				if (localConnectorAddress != null) {
					logger.accept("Found localConnectorAddress=" + localConnectorAddress);

					final JMXServiceURL jmxServiceURL = new JMXServiceURL(localConnectorAddress);

					final JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);

					logger.accept("Getting mbean server connection...");

					final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();

					if (mBeanServerConnection != null) {
						logger.accept("querying for objectname " + name);

						final ObjectName objectName = _getObjectName(name, mBeanServerConnection);

						logger.accept("found objectName = " + objectName);

						if (objectName != null) {
							return localConnectorAddress;
						}
					}
				}
			}
			catch (Exception exception) {
			}
			finally {
				Method detachMethod = vmClass.getMethod("detach");

				detachMethod.invoke(vm);
			}
		}
		catch (Exception exception) {
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
			URL toolsURL = null;

			try {
				URI toolsURI = toolsJar.toURI();

				toolsURL = toolsURI.toURL();
			}
			catch (MalformedURLException malformedURLException) {
				//
			}

			URL[] urls = {toolsURL};

			return new URLClassLoader(urls, parent);
		}

		throw new IOException("Could not find tools.jar in JDK at this location: " + toolsJar);
	}

}