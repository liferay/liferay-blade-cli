package com.liferay.blade.cli;

import com.liferay.blade.cli.util.ExtensionConfigEntry;
import com.liferay.blade.cli.util.ExtensionConfigEntryTableAdapter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collection;

public class ListExtensionsCommand extends BaseCommand<ListExtensionsCommandArgs> {

	public ListExtensionsCommand() {
	}

	@Override
	public void execute() throws Exception {
		Path tempDirectory = null;

		Path tempOutputFile = null;

		try {
		tempDirectory = Files.createTempDirectory(null);

			tempOutputFile = tempDirectory.resolve("extensions.xml");

			String remotePathString = getArgs().getPath();

			if (remotePathString.startsWith("http") && Util.isValidURL(remotePathString)) {
				Util.downloadLink(remotePathString, tempOutputFile);
			} else {
				Path remotePath = Paths.get(remotePathString);

				if (Files.exists(remotePath) && !Files.isDirectory(remotePath)) {
					Files.copy(remotePath, tempOutputFile);
				}
			}

			if (Files.exists(tempOutputFile)) {
				Collection<ExtensionConfigEntry> collection = ExtensionConfigEntry.getEntries(tempOutputFile.toFile());

				printExtensions(collection);
			}
		}
		finally {
			try {
				if (tempOutputFile != null && Files.exists(tempOutputFile)) {
					Files.delete(tempOutputFile);
				}
			}
			catch (Exception e) {
			}

			try {
				if (tempDirectory != null && Files.exists(tempDirectory) && Files.isDirectory(tempDirectory)) {
					Files.delete(tempDirectory);
				}
			}
			catch (Exception e) {
			}
		}
	}

	@Override
	public Class<ListExtensionsCommandArgs> getArgsClass() {
		return ListExtensionsCommandArgs.class;
	}

	public void printExtensions(Collection<ExtensionConfigEntry> collection) {
		String output = ExtensionConfigEntryTableAdapter.get(collection);

		_blade.out(output);
	}

}