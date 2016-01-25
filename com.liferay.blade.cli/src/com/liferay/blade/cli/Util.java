package com.liferay.blade.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.nio.file.Files;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Gregory Amerson
 * @author David Truong
 */
public class Util {

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().equals("windows");
	}

	protected static File findParentFile(
		File dir, String[] fileNames, boolean checkParents) {

		if (dir == null) {
			return null;
		}

		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			if (file.exists()) {
				return dir;
			}
		}

		if (checkParents) {
			return findParentFile(dir.getParentFile(), fileNames, checkParents);
		}

		return null;
	}

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
			getWorkspaceDir(workDir), _GRADLE_PROPERTIES_FILE_NAME);

		return gradlePropertiesFile;
	}

	protected static File getGradleWrapper(File dir) {
		File gradleRoot = findParentFile(
			dir,
			new String[] {
				_GRADLEW_UNIX_FILE_NAME, _GRADLEW_WINDOWS_FILE_NAME },
			true);

		if (gradleRoot != null) {
			if (isWindows()) {
				return new File(gradleRoot, _GRADLEW_WINDOWS_FILE_NAME);
			}
			else {
				return new File(gradleRoot, _GRADLEW_UNIX_FILE_NAME);
			}
		}

		return null;
	}

	protected static File getWorkspaceDir(blade blade) {
		return getWorkspaceDir(blade.getBase());
	}

	protected static File getWorkspaceDir(File dir) {
		return findParentFile(
			dir,
			new String[] {
				_SETTINGS_GRADLE_FILE_NAME, _GRADLE_PROPERTIES_FILE_NAME
			},
			true);
	}

	protected static boolean isWorkspace(blade blade) {
		return isWorkspace(blade.getBase());
	}

	protected static boolean isWorkspace(File workDir) {
		File workspaceDir = getWorkspaceDir(workDir);

		File gradleFile = new File(workspaceDir, _SETTINGS_GRADLE_FILE_NAME);

		if (!gradleFile.exists()) {
			return false;
		}

		try {
			String script = read(gradleFile);

			Matcher matcher = Workspace.PATTERN_WORKSPACE_PLUGIN.matcher(
				script);

			if (matcher.find()) {
				return true;
			}
			else {
				//For workspace plugin < 1.0.5

				gradleFile = new File(workspaceDir, _BUILD_GRADLE_FILE_NAME);

				script = read(gradleFile);

				matcher = Workspace.PATTERN_WORKSPACE_PLUGIN.matcher(script);

				return matcher.find();
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	protected static String read(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}

	protected static void unzip(File srcFile, File destDir, String entryToStart)
		throws IOException {

		try (final ZipFile zip = new ZipFile(srcFile)) {
			final Enumeration<? extends ZipEntry> entries = zip.entries();

			boolean foundStartEntry = entryToStart == null;

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				String entryName = entry.getName();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entryName);
					continue;
				}

				if (entry.isDirectory() ||
					((entryToStart != null) &&
					 !entryName.startsWith(entryToStart))) {

					continue;
				}

				if (entryToStart != null) {
					entryName = entryName.replaceFirst(entryToStart, "");
				}

				final File f = new File(destDir, entryName);
				final File dir = f.getParentFile();

				if (!dir.exists() && !dir.mkdirs()) {
					final String msg = "Could not create dir: " + dir.getPath();
					throw new IOException(msg);
				}

				try (final InputStream in = zip.getInputStream(entry);
						final FileOutputStream out = new FileOutputStream(f)) {

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

	private static final String _BUILD_GRADLE_FILE_NAME = "build.gradle";

	private static final String _GRADLE_PROPERTIES_FILE_NAME =
		"gradle.properties";

	private static final String _GRADLEW_UNIX_FILE_NAME = "gradlew";

	private static final String _GRADLEW_WINDOWS_FILE_NAME = "gradlew.bat";

	private static final String _SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

}