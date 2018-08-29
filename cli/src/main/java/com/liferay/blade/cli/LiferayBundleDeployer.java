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