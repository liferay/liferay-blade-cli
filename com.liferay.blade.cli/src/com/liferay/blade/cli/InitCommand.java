package com.liferay.blade.cli;

import aQute.lib.io.IO;

import com.liferay.blade.cli.aether.AetherClient;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class InitCommand {

	private final blade _blade;
	private final InitOptions _options;

	public InitCommand(blade blade, InitOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws IOException {
		final List<String> args = _options._arguments();

		final String name = args.size() > 0 ? args.get(0) : null;

		final File destDir =
			name != null ? new File(_blade.getBase(), name) : _blade.getBase();

		trace("Using destDir " + destDir);

		if (destDir.exists() && !destDir.isDirectory()) {
			addError(destDir.getAbsolutePath() + " is not a directory.");
			return;
		}

		if (destDir.exists()) {
			if (isPluginsSDK(destDir)) {
				trace("Found plugins-sdk, moving contents to new subdirectory "
						+ "and initing workspace.");

				moveContentsToDir(destDir, new File(destDir, "plugins-sdk"), "plugins-sdk");
			}
			else if (destDir.list().length > 0) {
				if (_options.force()) {
					trace("Files found, initing anyways.");
				}
				else {
					addError(
						destDir.getAbsolutePath() +
						" contains files, please move them before continuing or "
						+ "use -f (--force) option to init workspace anyways.");
					return;
				}
			}
		}

		if (!destDir.exists() && !destDir.mkdirs()) {
			addError("Unable to make directory at " + destDir.getAbsolutePath());
			return;
		}

		final File workspaceZip;

		try {
			workspaceZip = getWorkspaceZip();
		} catch (Exception e) {
			addError("Could not get workspace template: " + e.getMessage());
			return;
		}

		try(ZipFile zip = new ZipFile(workspaceZip)) {
			trace("Extracting workspace into destDir.");

			unzip(workspaceZip, destDir, "samples/");
		} catch (IOException e) {
			addError("Unable to unzip contents of workspace to dir: " + e.getMessage());
			return;
		}

		if(!new File(destDir, "gradlew").setExecutable(true)) {
			trace("Unable to make gradlew executable.");
		}
	}

	private void moveContentsToDir(File src, File dest, final String sdkDirName)
		throws IOException {

		Path tempDir = Files.createTempDirectory("temp-plugins-sdk");

		FileUtils.copyDirectory(src, tempDir.toFile(), new FileFilter() {
			public boolean accept(File pathname) {
				return (!pathname.getName().equals(sdkDirName) || !pathname.getName().startsWith("."));
			}
		}, true);

		String[] copied = tempDir.toFile().list();

		for (String name : copied) {
			IO.delete(new File(src, name));
		}

		FileUtils.moveDirectory(tempDir.toFile(), dest);
	}

	private boolean isPluginsSDK(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return false;
		}

		List<String> names = Arrays.asList(dir.list());

		return names != null &&
			names.contains("portlets") &&
			names.contains("hooks") &&
			names.contains("layouttpl") &&
			names.contains("themes") &&
			names.contains("build.properties") &&
			names.contains("build.xml") &&
			names.contains("build-common.xml") &&
			names.contains("build-common-plugin.xml");
	}

	void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
		try (final ZipFile zip = new ZipFile(srcFile)) {
			final Enumeration<? extends ZipEntry> entries = zip.entries();

			boolean foundStartEntry = entryToStart == null;

			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();

				if (!foundStartEntry) {
					foundStartEntry = entryToStart.equals(entry.getName());
					continue;
				}

				if (entry.isDirectory()
						|| !entry.getName().startsWith(entryToStart)) {
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

	File getWorkspaceZip() throws Exception {
		trace("Connecting to repository to find latest workspace template.");

		final File workspacePluginArtifact =
			new AetherClient().findLatestAvailableArtifact(
				"com.liferay:com.liferay.gradle.plugins.workspace:jar:sources");

		return workspacePluginArtifact;
	}
	private void addError(String msg) {
		_blade.addErrors("init", Collections.singleton(msg));
	}

	private void trace(String msg) {
		_blade.trace("%s: %s", "init", msg);
	}
}