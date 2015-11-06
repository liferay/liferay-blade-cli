package com.liferay.blade.cli.jmx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

public class JMXLocalConnector {

	protected MBeanServerConnection mBeanServerConnection;

	public JMXLocalConnector() {
	}

	public JMXLocalConnector(String objectName, String type) {
		this(getLocalConnectorAddress(objectName, type));
	}

	public JMXLocalConnector(String serviceURL) {
		try {
			final JMXServiceURL jmxServiceUrl = new JMXServiceURL(serviceURL);
			final JMXConnector jmxConnector = JMXConnectorFactory.connect(
				jmxServiceUrl, null);

			mBeanServerConnection = jmxConnector.getMBeanServerConnection();
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"Unable to get JMX connection", e);
		}
	}

	static ObjectName getMBean(String objectNameVal, String type, MBeanServerConnection mBeanServerConnection)
			throws IOException, MalformedObjectNameException {

		final ObjectName objectName = new ObjectName(
			objectNameVal + ":type=" + type + ",*");
		final Set<ObjectName> objectNames = mBeanServerConnection.queryNames(
			objectName, null);

		if (objectNames != null && objectNames.size() > 0) {
			return objectNames.iterator().next();
		}

		return null;
	}

	/**
	 * Uses Oracle JDK's Attach API to try to search VMs on this machine looking
	 * for the osgi.core MBeans. This will stop searching for VMs once the
	 * MBeans are found. Beware if you have multiple JVMs with osgi.core MBeans
	 * published.
	 * @param type2
	 * @param objectName2
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static String getLocalConnectorAddress(String objectName, String type) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ClassLoader toolsClassloader = null;

		try {
			toolsClassloader = getToolsClassLoader(cl);

			if (toolsClassloader != null) {
				Thread.currentThread().setContextClassLoader(toolsClassloader);

				Class< ? > vmClass = toolsClassloader.loadClass(
					"com.sun.tools.attach.VirtualMachine");

				Method listMethod = vmClass.getMethod("list");
				List<Object> vmds = (List<Object>)listMethod.invoke(null);

				for (Object vmd : vmds) {
					try {
						Class< ? > vmdClass = toolsClassloader
								.loadClass(
									"com.sun.tools.attach.VirtualMachineDescriptor");
						Method idMethod = vmdClass.getMethod("id");
						String id = (String)idMethod.invoke(vmd);

						Method attachMethod = vmClass.getMethod(
							"attach", String.class);
						Object vm = attachMethod.invoke(null, id);

						try {
							Method getAgentPropertiesMethod = vmClass.getMethod(
								"getAgentProperties");
							Properties agentProperties =
								(Properties)getAgentPropertiesMethod.invoke(vm);

							String localConnectorAddress = agentProperties
									.getProperty(
										"com.sun.management.jmxremote.localConnectorAddress");

							if (localConnectorAddress == null) {
								File agentJar = findJdkJar(
									"management-agent.jar");

								if (agentJar != null) {
									Method loadAgent = vmClass.getMethod(
										"loadAgent", String.class);
									loadAgent.invoke(
										vm, agentJar.getCanonicalPath());

									agentProperties =
										(Properties)getAgentPropertiesMethod.invoke(vm);

									localConnectorAddress = agentProperties
											.getProperty(
												"com.sun.management.jmxremote.localConnectorAddress");
								}
							}

							if (localConnectorAddress != null) {
								final JMXServiceURL jmxServiceUrl =
									new JMXServiceURL(localConnectorAddress);
								final JMXConnector jmxConnector =
									JMXConnectorFactory.connect(
										jmxServiceUrl, null);

								final MBeanServerConnection mBeanServerConnection = jmxConnector
									.getMBeanServerConnection();

								if (mBeanServerConnection != null) {
									final ObjectName framework = getMBean(
										objectName, type,
										mBeanServerConnection);

									if (framework != null) {
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
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			Thread.currentThread().setContextClassLoader(cl);

			// try to get custom classloader to unload native libs

			try {
				if (toolsClassloader != null) {
					Field nl = ClassLoader.class.getDeclaredField(
						"nativeLibraries");
					nl.setAccessible(true);
					Vector< ? > nativeLibs = (Vector< ? >)nl.get(
						toolsClassloader);

					for (Object nativeLib : nativeLibs) {
						Field nameField =
							nativeLib.getClass().getDeclaredField("name");
						nameField.setAccessible(true);
						String name = (String)nameField.get(nativeLib);

						if (new File(name).getName().contains("attach")) {
							Method f =
								nativeLib.getClass().getDeclaredMethod("finalize");
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

	private static ClassLoader getToolsClassLoader(ClassLoader parent)
		throws IOException {
		File toolsJar = findJdkJar("tools.jar");

		if (toolsJar != null && toolsJar.exists()) {
			URL toolsUrl = null;

			try {
				toolsUrl = toolsJar.toURI().toURL();
			}
			catch (MalformedURLException e) {
				//
			}

			URL[] urls = new URL[] {toolsUrl};

			return new URLClassLoader(urls, parent);
		}

		return null;
	}

	static File findJdkJar(String jar) throws IOException {
		File retval = null;

		final String jarPath = File.separator + "lib" + File.separator + jar;
		final String javaHome = System.getProperty("java.home");
		File jarFile = new File(javaHome + jarPath);

		if (jarFile.exists()) {
			retval = jarFile;
		} else {
			jarFile = new File(javaHome + "/.." + jarPath);

			if (jarFile.exists()) {
				retval = jarFile.getCanonicalFile();
			}
		}

		return retval;
	}
}