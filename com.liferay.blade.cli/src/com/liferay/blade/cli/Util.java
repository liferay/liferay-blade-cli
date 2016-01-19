package com.liferay.blade.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Gregory Amerson
 */
public class Util {

	protected static Properties getGradleProperties(blade blade) {
		return getGradleProperties(blade.getBase());
	}

	protected static Properties getGradleProperties(File workDir) {
		File propertiesFile = getGradlePropertiesFile(workDir);

		try (InputStream inputStream = new FileInputStream(propertiesFile)) {
			Properties properties = new Properties();

			properties.load(inputStream);

			return properties;
		}
		catch (Exception e) {
			return null;
		}
	}

	protected static File getGradlePropertiesFile(blade blade) {
		return getGradlePropertiesFile(blade.getBase());
	}

	protected static File getGradlePropertiesFile(File workDir) {
		File gradlePropertiesFile = new File(
			workDir, _GRADLE_PROPERTIES_FILE_NAME);

		while (!gradlePropertiesFile.exists()) {
			workDir = workDir.getParentFile();

			if (workDir == null) {
				return null;
			}

			gradlePropertiesFile = new File(
				workDir, _GRADLE_PROPERTIES_FILE_NAME);
		}

		return gradlePropertiesFile;
	}

	protected static File getProjectDir(blade blade) {
		File gradlePropertiesFile = getGradlePropertiesFile(blade);

		if (gradlePropertiesFile == null) {
			return null;
		}

		return gradlePropertiesFile.getParentFile();
	}

	protected static boolean isWorkspace(blade blade) {
		return isWorkspace(blade.getBase());
	}

	protected static boolean isWorkspace(File workDir) {
		Properties properties = getGradleProperties(workDir);

		if ((properties == null) ||
			!properties.containsKey("liferay.workspace.home.dir")) {

			return false;
		}

		return true;
	}

	protected static void unzip(File srcFile, File destDir, String entryToStart)
		throws IOException {

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

	private static final String _GRADLE_PROPERTIES_FILE_NAME =
		"gradle.properties";

}