package com.liferay.blade.cli.util;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LinkDownloader implements Runnable {

	public LinkDownloader(String link, Path target) {
		_link = link;
		_target = target;
	}

	private String getFileName(URL url) {
		Path path = Paths.get(url.getFile());

		return path.getFileName().toString();
	}

	@Override
	public void run() {
		Redirecter redirecter = new Redirecter(_link);

		redirecter.run();
		save(redirecter._http);
	}

	private void save(HttpURLConnection http) {
		Path savePath = _target;

		try {
			if (Files.isDirectory(_target)) {
				savePath = _target.resolve(getFileName(http.getURL()));
			}

			Files.copy(http.getInputStream(), savePath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final String _link;
	private final Path _target;

	private static class Redirecter implements Runnable, Supplier<Boolean> {

		public Redirecter(String link) {
			try {
				_link = link;
				_url = new URL(link);
				_http = (HttpURLConnection) _url.openConnection();
				_header = _http.getHeaderFields();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public Boolean get() {
			for (String headerEntry : _header.get(null)) {
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
					_link = _header.get("Location").get(0);
					_url = new URL(_link);
					_http = (HttpURLConnection) _url.openConnection();
					_header = _http.getHeaderFields();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		Map<String, List<String>> _header;
		HttpURLConnection _http;
		String _link;
		URL _url;

		private static final String _HTTP_FOUND = "302";

		private static final String _HTTP_MOVED = "301";

	}
}
