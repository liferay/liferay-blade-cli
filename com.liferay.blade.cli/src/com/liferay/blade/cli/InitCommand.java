package com.liferay.blade.cli;

import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InitCommand {

	private final blade _blade;
	private final InitOptions _options;
	private final URL tagsApi = new URL("https://api.github.com/repos/david-truong/liferay-workspace/tags");

	public InitCommand(blade blade, InitOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() {
		final List<String> args = _options._arguments();

		final String name = args.size() > 0 ? args.get(0) : null;

		final File destDir =
			name != null ? new File(_blade.getBase(), name) : _blade.getBase();

		if (destDir.exists() && !destDir.isDirectory()) {
			addError(destDir.getAbsolutePath() + " is not a directory.");
			return;
		}

		if (destDir.exists() && destDir.list().length > 0) {
			addError(
				destDir.getAbsolutePath() +
				" contains files, please move them before continuing.");
			return;
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
			String firstEntryName = zip.entries().nextElement().getName();

			unzip(workspaceZip, destDir, firstEntryName);
		} catch (IOException e) {
			addError("Unable to unzip contents of workspace to dir: " + e.getMessage());
			return;
		}
	}

	void unzip(File srcFile, File destDir, String entryToStart) throws IOException {
	    try(final ZipFile zip = new ZipFile(srcFile)) {
	        final Enumeration<? extends ZipEntry> entries = zip.entries();

            boolean foundStartEntry = entryToStart == null;

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                if (!foundStartEntry) {
                    foundStartEntry = entryToStart.equals(entry.getName());
                    continue;
                }

                if (entry.isDirectory()) {
                    continue;
                }

                String entryName = null;

                if( entryToStart == null ) {
                    entryName = entry.getName();
                }
                else {
                    entryName = entry.getName().replaceFirst( entryToStart, "" );
                }

                final File f = new File(destDir, entryName);
                final File dir = f.getParentFile();

                if (!dir.exists() && !dir.mkdirs()) {
                    final String msg = "Could not create dir: " + dir.getPath();
                    throw new IOException(msg);
                }


                try(final InputStream in = zip.getInputStream(entry);
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
		Object json = new JSONCodec().dec().from(
				tagsApi.openStream()).get();

		if (json instanceof List<?>) {
			List<?> list = (List<?>) json;

			Object lastItem = list.get(list.size()-1);

			if (lastItem instanceof Map<?,?>) {
				Map<?,?> map = (Map<?, ?>) lastItem;

				Object name = map.get("name");
				Object zipUrl = map.get("zipball_url");

				File cache = _blade.getCacheDir();

				cache.mkdirs();

				File workspaceZip = new File(cache, name + ".zip");

				if (!workspaceZip.exists()) {
					IO.copy(
						new URL(zipUrl.toString()).openStream(),
						workspaceZip);
				}

				return workspaceZip;
			}
		}

		return null;
	}
	private void addError(String msg) {
		_blade.addErrors("init", Collections.singleton(msg));
	}
}