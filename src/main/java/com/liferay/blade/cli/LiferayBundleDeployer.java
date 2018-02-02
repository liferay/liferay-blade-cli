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

import java.net.URI;
import java.util.Collection;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Christopher Bryan Boyd
 */
public interface LiferayBundleDeployer extends AutoCloseable {

	public static LiferayBundleDeployer newInstance(String host, int port) {
		return new LiferayBundleDeployerImpl(host, port);
	}

	Collection<BundleDTO> getBundles() throws Exception;

	default long getBundleId(String name) throws Exception {
		return getBundleId(getBundles(), name);
	}

	long getBundleId(Collection<BundleDTO> bundles, String name) throws Exception;

	void update(long id, URI uri) throws Exception;

	void refresh(long id) throws Exception;

	void stop(long id) throws Exception;

	void start(long id) throws Exception;

	long install(URI uri) throws Exception;

	default void reloadFragment(long id, long hostId, URI uri) throws Exception {
		update(id, uri);

		refresh(hostId);
	}

	default void reloadBundle(long id, URI uri) throws Exception {
		stop(id);

		update(id, uri);

		start(id);
	}
}