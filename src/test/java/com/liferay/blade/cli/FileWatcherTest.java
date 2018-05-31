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

import aQute.lib.io.IO;

import com.liferay.blade.cli.FileWatcher.Consumer;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Greg Amerson
 */
public class FileWatcherTest {

	@After
	public void cleanUp() throws Exception {
		if (_testdir.exists()) {
			IO.delete(_testdir);

			Assert.assertFalse(_testdir.exists());
		}
	}

	@Before
	public void setUp() throws Exception {
		_testdir.mkdirs();

		Assert.assertTrue(_testdir.exists());
		Assert.assertTrue(_testfile.createNewFile());
	}

	@Ignore
	@Test
	public void testFileWatcherMultipleFiles() throws Exception {
		IO.write("foobar".getBytes(), _testfile);

		final Map<Path, Boolean> changed = new HashMap<>();

		changed.put(_testfile.toPath(), false);
		changed.put(_testsecondfile.toPath(), false);

		final CountDownLatch latch = new CountDownLatch(2);

		final Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				changed.put(modified, true);
				latch.countDown();
			}

		};

		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					new FileWatcher(_testdir.toPath(), false, consumer);
				}
				catch (IOException ioe) {
				}
			}

		};

		t.setDaemon(true);
		t.start();

		// let the file watcher get all registered before we touch the file

		Thread.sleep(2000);

		IO.write("touch".getBytes(), _testfile);
		IO.write("second file content".getBytes(), _testsecondfile);

		latch.await();

		for (Map.Entry<Path, Boolean> entry : changed.entrySet()) {
			Assert.assertTrue(entry.getValue());
		}
	}

	@Ignore
	@Test
	public void testFileWatcherSingleFile() throws Exception {
		final boolean[] changed = new boolean[1];
		final CountDownLatch latch = new CountDownLatch(1);

		final Consumer<Path> consumer = new Consumer<Path>() {

			@Override
			public void consume(Path modified) {
				changed[0] = true;
				latch.countDown();
			}

		};

		Assert.assertFalse(changed[0]);

		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					new FileWatcher(_testdir.toPath(), _testfile.toPath(), false, consumer);
				}
				catch (IOException ioe) {
				}
			}

		};

		t.setDaemon(true);
		t.start();

		// let the file watcher get all registered before we touch the file

		Thread.sleep(1000);

		IO.write("touch".getBytes(), _testfile);

		latch.await();

		Assert.assertTrue(changed[0]);
	}

	private static final File _testdir = IO.getFile("build/watch");
	private static final File _testfile = new File(_testdir, "file.txt");
	private static final File _testsecondfile = new File(_testdir, "second.txt");

}