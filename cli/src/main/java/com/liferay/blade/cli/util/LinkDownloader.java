/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blade.cli.util;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Map;

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

		_save(redirecter._httpURLConnection);
	}

	private String _getFileName(URL url) {
		Path path = Paths.get(url.getFile());

		return String.valueOf(path.getFileName());
	}

	private void _save(HttpURLConnection httpURLConnection) {
		Path savePath = _target;

		try {
			if (Files.isDirectory(_target)) {
				savePath = _target.resolve(_getFileName(httpURLConnection.getURL()));
			}

			Files.copy(httpURLConnection.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private String _link;
	private Path _target;

	private static class Redirecter {

		public Redirecter(String link) {
			try {
				_link = link;

				_url = new URL(link);

				_httpURLConnection = (HttpURLConnection)_url.openConnection();

				_headers = _httpURLConnection.getHeaderFields();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		public Boolean get() {
			for (String headerEntry : _headers.get(null)) {
				if (headerEntry.contains(" " + _HTTP_FOUND + " ") || headerEntry.contains(" " + _HTTP_MOVED + " ")) {
					return true;
				}
			}

			return false;
		}

		public void run() {
			try {
				while (get()) {
					List<String> headers = _headers.get("location");

					if (headers == null) {
						headers = _headers.get("Location");
					}

					_link = headers.get(0);

					_url = new URL(_link);

					_httpURLConnection = (HttpURLConnection)_url.openConnection();

					_headers = _httpURLConnection.getHeaderFields();
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private static final String _HTTP_FOUND = "302";

		private static final String _HTTP_MOVED = "301";

		private Map<String, List<String>> _headers;
		private HttpURLConnection _httpURLConnection;
		private String _link;
		private URL _url;

	}

}