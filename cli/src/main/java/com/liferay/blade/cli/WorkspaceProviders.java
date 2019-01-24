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

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

/**
 * @author Christopher Bryan Boyd
 */
public class WorkspaceProviders implements WorkspaceProvider {

	public WorkspaceProviders(ClassLoader classLoader) {
		_classLoader = classLoader;
	}

	@Override
	public File getWorkspaceDir(File dir) {
		File workspaceDir = null;

		try {
			Collection<WorkspaceProvider> providers = _getWorkspaceProviders();

			for (WorkspaceProvider provider : providers) {
				try {
					if (isWorkspace(dir)) {
						workspaceDir = provider.getWorkspaceDir(dir);

						if (workspaceDir != null) {
							break;
						}
					}
				}
				catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}
		catch (Throwable th) {
			throw new RuntimeException(th);
		}

		return workspaceDir;
	}

	@Override
	public boolean isWorkspace(File dir) {
		boolean workspace = false;

		try {
			Collection<WorkspaceProvider> providers = _getWorkspaceProviders();

			for (WorkspaceProvider provider : providers) {
				try {
					workspace = provider.isWorkspace(dir);

					if (workspace) {
						break;
					}
				}
				catch (Throwable th) {
					th.printStackTrace();
				}
			}
		}
		catch (Throwable th) {
			throw new RuntimeException(th);
		}

		return workspace;
	}

	private Collection<WorkspaceProvider> _getWorkspaceProviders() throws Exception {
		if (_workspaceProviders == null) {
			_workspaceProviders = new ArrayList<>();
			ServiceLoader<WorkspaceProvider> serviceLoader = ServiceLoader.load(WorkspaceProvider.class, _classLoader);

			for (WorkspaceProvider baseCommand : serviceLoader) {
				_workspaceProviders.add(baseCommand);
			}

			return _workspaceProviders;
		}

		return _workspaceProviders;
	}

	private ClassLoader _classLoader = null;
	private Collection<WorkspaceProvider> _workspaceProviders = null;

}