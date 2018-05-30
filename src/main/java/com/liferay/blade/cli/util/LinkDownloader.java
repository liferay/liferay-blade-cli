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
		this.link = link;
		this.target = target;
	}

	private String getFileName(URL url) {
		Path path = Paths.get(url.getFile());

		return path.getFileName().toString();
	}

	@Override
	public void run() {
		Redirecter redirecter = new Redirecter(link);

		redirecter.run();
		save(redirecter.http);
	}

	private void save(HttpURLConnection http) {
		Path savePath = target;

		try {
			if (Files.isDirectory(target)) {
				savePath = target.resolve(getFileName(http.getURL()));
			}

			Files.copy(http.getInputStream(), savePath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String link;
	private Path target;

	private static class Redirecter implements Runnable, Supplier<Boolean> {

		public Redirecter(String link) {
			try {
				this.link = link;
				this.url = new URL(link);
				this.http = (HttpURLConnection) url.openConnection();
				this.header = http.getHeaderFields();
			} catch (Exception e) {
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
					link = header.get("Location").get(0);
					url = new URL(link);
					http = (HttpURLConnection) url.openConnection();
					header = http.getHeaderFields();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		Map<String, List<String>> header;
		HttpURLConnection http;
		String link;
		URL url;

		private static final String _HTTP_FOUND = "302";

		private static final String _HTTP_MOVED = "301";

	}
}
