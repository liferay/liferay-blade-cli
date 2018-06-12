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

package com.liferay.blade.cli.util;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Christopher Bryan Boyd
 */
public class LinkDownloader implements Runnable {

	public LinkDownloader(String link, Path target) {
		_link = link;
		_target = target;
	}

	@Override
	public void run() {
		Redirecter redirecter = new Redirecter(_link);

		redirecter.run();

		_save(redirecter.http);
	}

	private String _getFileName(URL url) {
		Path path = Paths.get(url.getFile());

		return String.valueOf(path.getFileName());
	}

	private void _save(HttpURLConnection http) {
		Path savePath = _target;

		try {
			if (Files.isDirectory(_target)) {
				savePath = _target.resolve(_getFileName(http.getURL()));
			}

			Files.copy(http.getInputStream(), savePath);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String _link;
	private Path _target;

	private static class Redirecter implements Runnable, Supplier<Boolean> {

		public Redirecter(String link) {
			try {
				this.link = link;

				url = new URL(link);

				http = (HttpURLConnection)url.openConnection();

				header = http.getHeaderFields();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Boolean get() {
			for (String headerEntry : header.get(null)) {
				if (headerEntry.contains(" " + _HTTP_FOUND + " ") || headerEntry.contains(" " + _HTTP_MOVED + " ")) {
					return true;
				}
			}

			return false;
		}

		@Override
		public void run() {
			try {
				while (get()) {
					List<String> headers = header.get("Location");

					link = headers.get(0);

					url = new URL(link);

					http = (HttpURLConnection)url.openConnection();

					header = http.getHeaderFields();
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Map<String, List<String>> header;
		public HttpURLConnection http;
		public String link;
		public URL url;

		private static final String _HTTP_FOUND = "302";

		private static final String _HTTP_MOVED = "301";

	}

}