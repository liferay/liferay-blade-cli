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

import java.io.IOException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * This class will try to connect to a remote OSGi framework using JMX and will
 * deploy a bundle for you, by deploy, that means install the bundle if it
 * doesn't existing in the remote runtime or update the bundle if it already
 * exists. For the actual JMX connection it will use a port if you tell it to,
 * or if not, it will try to use the JDK's attach API and search for the OSGi
 * framework JMX beans. For the JDK attach API, beware, assumptions about the
 * Oracle JDK directory layout have been made.
 *
 * @author Gregory Amerson
 */
public class JMXBundleDeployer extends JMXLocalConnector {

	public JMXBundleDeployer(Consumer<String> logger) throws MalformedURLException {
		super(_NAME + ":type=" + _TYPE + ",*", logger);
	}

	public JMXBundleDeployer(int port) throws MalformedURLException {
		super(port);
	}

	/**
	 * Gets the current list of installed bsns, compares it to the bsn provided.
	 * If bsn doesn't exist, then install it. If it does exist then update it.
	 *
	 * @param bsn
	 *            Bundle-SymbolicName of bundle you are wanting to deploy
	 * @param bundle
	 *            the bundle
	 * @return the id of the updated or installed bundle
	 * @throws Exception
	 */
	public long deploy(String bsn, String bundleUrl) throws Exception {
		final ObjectName framework = _getFramework(mBeanServerConnection);

		long bundleId = -1;

		for (BundleDTO osgiBundle : listBundles()) {
			if (osgiBundle.symbolicName.equals(bsn)) {
				bundleId = osgiBundle.id;

				break;
			}
		}

		if (bundleId > -1) {
			mBeanServerConnection.invoke(framework, "stopBundle", new Object[] {bundleId}, new String[] {"long"});

			Object[] params = {bundleId, bundleUrl};

			mBeanServerConnection.invoke(
				framework, "updateBundleFromURL", params, new String[] {"long", String.class.getName()});

			mBeanServerConnection.invoke(framework, "refreshBundle", new Object[] {bundleId}, new String[] {"long"});
		}
		else {
			Object[] params = {bundleUrl, bundleUrl};

			String[] signature = {String.class.getName(), String.class.getName()};

			Object installed = mBeanServerConnection.invoke(framework, "installBundleFromURL", params, signature);

			bundleId = Long.parseLong(installed.toString());
		}

		mBeanServerConnection.invoke(framework, "startBundle", new Object[] {bundleId}, new String[] {"long"});

		return bundleId;
	}

	/**
	 * Calls osgi.core bundleState MBean listBundles operation
	 *
	 * @return array of bundles in framework
	 */
	public BundleDTO[] listBundles() {
		final List<BundleDTO> retval = new ArrayList<>();

		try {
			final ObjectName bundleState = _getBundleState();

			final Object[] params = {new String[] {"Identifier", "SymbolicName", "State", "Version"}};

			final String[] signature = {String[].class.getName()};

			final TabularData data = (TabularData)mBeanServerConnection.invoke(
				bundleState, "listBundles", params, signature);

			for (Object value : data.values()) {
				final CompositeData cd = (CompositeData)value;

				try {
					retval.add(_newFromData(cd));
				}
				catch (Exception exception) {
				}
			}
		}
		catch (Exception exception) {
		}

		return retval.toArray(new BundleDTO[0]);
	}

	/**
	 * Calls through directly to the OSGi frameworks MBean uninstallBundle
	 * operation
	 *
	 * @param id
	 *            id of bundle to uninstall
	 * @throws Exception
	 */
	public void uninstall(long id) throws Exception {
		final ObjectName framework = _getFramework(mBeanServerConnection);

		Object[] objects = {id};

		String[] params = {"long"};

		mBeanServerConnection.invoke(framework, "uninstallBundle", objects, params);
	}

	/**
	 * Uninstall a bundle by passing in its Bundle-SymbolicName. If bundle
	 * doesn't exist, this is a NOP.
	 *
	 * @param bsn
	 *            bundle symbolic name
	 * @throws Exception
	 */
	public void uninstall(String bsn) throws Exception {
		for (BundleDTO osgiBundle : listBundles()) {
			if (osgiBundle.symbolicName.equals(bsn)) {
				uninstall(osgiBundle.id);

				return;
			}
		}

		throw new IllegalStateException("Unable to uninstall " + bsn);
	}

	private ObjectName _getBundleState() throws IOException, MalformedObjectNameException {
		ObjectName objectName = new ObjectName(_NAME + ":type=bundleState,*");

		Set<ObjectName> queryNames = mBeanServerConnection.queryNames(objectName, null);

		Iterator<ObjectName> iterator = queryNames.iterator();

		return iterator.next();
	}

	private ObjectName _getFramework(MBeanServerConnection mBeanServerConnection) throws Exception {
		final ObjectName objectName = new ObjectName(_NAME + ":type=" + _TYPE + ",*");

		final Set<ObjectName> objectNames = mBeanServerConnection.queryNames(objectName, null);

		if ((objectNames != null) && !objectNames.isEmpty()) {
			Iterator<ObjectName> iterator = objectNames.iterator();

			return iterator.next();
		}

		return null;
	}

	private BundleDTO _newFromData(CompositeData cd) {
		final BundleDTO dto = new BundleDTO();

		Object identifier = cd.get("Identifier");

		dto.id = Long.parseLong(identifier.toString());

		Object symbolicName = cd.get("SymbolicName");

		dto.symbolicName = symbolicName.toString();

		Object state = cd.get("State");

		if (Objects.equals("UNINSTALLED", state)) {
			dto.state = Bundle.UNINSTALLED;
		}
		else if (Objects.equals("INSTALLED", state)) {
			dto.state = Bundle.INSTALLED;
		}
		else if (Objects.equals("RESOLVED", state)) {
			dto.state = Bundle.RESOLVED;
		}
		else if (Objects.equals("STARTING", state)) {
			dto.state = Bundle.STARTING;
		}
		else if (Objects.equals("STOPPING", state)) {
			dto.state = Bundle.STOPPING;
		}
		else if (Objects.equals("ACTIVE", state)) {
			dto.state = Bundle.ACTIVE;
		}

		Object version = cd.get("Version");

		dto.version = version.toString();

		return dto;
	}

	private static final String _NAME = "osgi.core";

	private static final String _TYPE = "framework";

}