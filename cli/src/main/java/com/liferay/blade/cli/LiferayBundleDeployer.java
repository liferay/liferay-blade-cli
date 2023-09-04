/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli;

import com.liferay.blade.cli.gradle.LiferayBundleDeployerImpl;

import java.io.IOException;

import java.net.URI;

import java.util.Collection;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Christopher Bryan Boyd
 */
public interface LiferayBundleDeployer extends AutoCloseable {

	public static LiferayBundleDeployer newInstance(String host, int port) throws IOException {
		return new LiferayBundleDeployerImpl(host, port);
	}

	public BundleDTO getBundle(long id) throws Exception;

	public long getBundleId(Collection<BundleDTO> bundles, String name) throws Exception;

	public default long getBundleId(String name) throws Exception {
		return getBundleId(getBundles(), name);
	}

	public Collection<BundleDTO> getBundles() throws Exception;

	public long install(URI uri) throws Exception;

	public void refresh(long id) throws Exception;

	public default void reloadBundle(long id, URI uri) throws Exception {
		stop(id);

		update(id, uri);

		start(id);
	}

	public default void reloadFragment(long id, long hostId, URI uri) throws Exception {
		update(id, uri);

		refresh(hostId);
	}

	public void start(long id) throws Exception;

	public void stop(long id) throws Exception;

	public void uninstall(long id) throws Exception;

	public void update(long id, URI uri) throws Exception;

}