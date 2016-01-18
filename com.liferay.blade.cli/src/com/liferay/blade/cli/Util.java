package com.liferay.blade.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Gregory Amerson
 */
public class Util {

	static void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
		try (final ZipFile zip = new ZipFile(srcFile)) {
			final Enumeration<? extends ZipEntry> entries = zip.entries();

			boolean foundStartEntry = entryToStart == null;

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entry.getName());
					continue;
				}

				if (entry.isDirectory() ||
						(entryToStart != null &&
							!entry.getName().startsWith(entryToStart))) {
					continue;
				}

				String entryName = null;

				if (entryToStart == null) {
					entryName = entry.getName();
				} else {
					entryName = entry.getName().replaceFirst(entryToStart, "");
				}

				final File f = new File(destDir, entryName);
				final File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					final String msg = "Could not create dir: " + dir.getPath();
					throw new IOException(msg);
				}

				try (final InputStream in = zip.getInputStream(entry);
						final FileOutputStream out = new FileOutputStream(f);) {

					final byte[] bytes = new byte[1024];
					int count = in.read(bytes);

					while (count != -1) {
						out.write(bytes, 0, count);
						count = in.read(bytes);
					}

					out.flush();
				}
			}
		}
	}

}
