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

import java.io.IOException;

import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Christopher Bryan Boyd
 */
public class PathChangeWatcher implements AutoCloseable, Supplier<Boolean> {

	public PathChangeWatcher(Path path) throws ExecutionException, InterruptedException {
		try {
			_watchService = FileSystems.getDefault().newWatchService();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		Path pathToWatch = _getPathToWatch(path);

		_pathFileName = path.getFileName();

		try {
			_watchKey = pathToWatch.register(
				_watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
				StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws Exception {
		if (!_closed) {
			_closed = true;

			if (_watchKey.isValid()) {
				_watchKey.cancel();
			}

			try {
				_watchService.close();
			}
			catch (IOException ioe) {
			}
		}
	}

	@Override
	public Boolean get() {
		if (_changed) {
			return _changed;
		}
		else if (_closed) {
			return false;
		}
		else {
			return _getPathChanged();
		}
	}

	public boolean isClosed() {
		return _closed;
	}

	private boolean _getPathChanged() {
		try {
			_processWatchKeys();

			return _changed;
		}
		catch (ClosedWatchServiceException cwse) {
			_safeClose();
		}
		catch (Exception e) {
			_safeClose();

			throw new RuntimeException(e);
		}

		return false;
	}

	private Path _getPathToWatch(Path path) {
		if (!Files.isDirectory(path)) {
			return path.getParent();
		}
		else {
			return path;
		}
	}

	private void _processWatchKeyPollEvents(WatchKey key) throws Exception {
		for (WatchEvent<?> event : key.pollEvents()) {
			final Path changedPath = (Path)event.context();

			if (_pathFileName.equals(changedPath.getFileName())) {
				_changed = true;
				_safeClose();

				break;
			}
		}
	}

	private void _processWatchKeys() throws ClosedWatchServiceException {
		WatchKey key = null;

		try {
			while (!_closed && !_changed && (key = _watchService.poll(100, TimeUnit.MILLISECONDS)) != null) {
				_processWatchKeyPollEvents(key);

				if (!_closed) {
					key.reset();
				}
			}
		}
		catch (ClosedWatchServiceException cwse) {
			throw cwse;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void _safeClose() {
		if (!_closed) {
			try {
				close();
			}
			catch (Exception e) {
			}
		}
	}

	private boolean _changed = false;
	private boolean _closed = false;
	private Path _pathFileName;
	private WatchKey _watchKey;
	private WatchService _watchService;

}