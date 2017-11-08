/**
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.liferay.blade.cli;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Example to watch a directory (or tree) for changes to files.
 *
 * @author n/a
 */
public class FileWatcher {

	@SuppressWarnings("unchecked")
	public static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>)event;
	}

	public FileWatcher(Path baseDir, boolean recursive, Consumer<Path> consumer)
		throws IOException {

		this(baseDir, null, recursive, consumer);
	}

	/**
	 * Creates a WatchService and registers the given directory
	 * @param runnable
	 */
	public FileWatcher(
			Path baseDir, Path fileToWatch, boolean recursive,
			Consumer<Path> consumer)
		throws IOException {

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<>();
		this.recursive = recursive;

		System.out.format("Scanning %s\n", baseDir);

		if (recursive) {
			registerAll(baseDir);
		}
		else {
			register(baseDir);
		}

		// enable trace after initial registration

		this.trace = true;

		processEvents(fileToWatch, consumer);
	}

	/**
	 * Process all events for keys queued to the watcher
	 * @param fileToWatch
	 * @param runnable
	 */
	public void processEvents(Path fileToWatch, Consumer<Path> consumer) {
		while (true) {

			// wait for key to be signalled

			WatchKey key;

			try {
				key = watcher.take();
			}
			catch (InterruptedException ie) {
				return;
			}

			Path dir = keys.get(key);

			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			final Set<Path> reportModified = new HashSet<>();

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled

				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry

				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				if ((child.equals(fileToWatch) || (fileToWatch == null)) &&
					((kind == ENTRY_CREATE) || (kind == ENTRY_MODIFY))) {

					reportModified.add(child);
				}

				// if directory is created, and watching recursively, then
				// register it and its sub-directories

				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					}
					catch (IOException ioe) {

						// ignore to keep sample readbale

					}
				}
			}

			if (reportModified.size() > 0) {
				for (Path modified : reportModified) {
					try {
						consumer.consume(modified);
					}
					catch (Throwable t) {
						//ignore
					}
				}
			}

			// reset key and remove from set if directory no longer accessible

			boolean valid = key.reset();

			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible

				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	public interface Consumer<E> {

		public void consume(E reference);

	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		Modifier modifier = null;

		try {
			Class<?> c = Class.forName(
				"com.sun.nio.file.SensitivityWatchEventModifier");
			Field f = c.getField("HIGH");
			modifier = (Modifier)f.get(c);
		}
		catch (Exception e) {
		}

		WatchKey key;

		if (modifier != null) {
			key = dir.register(
				watcher, new WatchEvent.Kind[] {ENTRY_CREATE, ENTRY_MODIFY},
				modifier);
		}
		else {
			key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
		}

		if (trace) {
			Path prev = keys.get(key);

			if (prev == null) {
			}
			else {
				if (!dir.equals(prev)) {
				}
			}
		}

		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {

		// register directory and sub-directories

		Files.walkFileTree(
			start,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dir, BasicFileAttributes attrs)
					throws IOException {

					register(dir);
					return FileVisitResult.CONTINUE;
				}

			});
	}

	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;
	private final WatchService watcher;

}