package com.liferay.blade.eclipse.provider.cmds;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.apache.felix.service.command.CommandProcessor;
import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.Command;

@Component(
	property = {
		CommandProcessor.COMMAND_SCOPE + "=blade",
		CommandProcessor.COMMAND_FUNCTION + "=copyPortalSettings"
	},
	service = Command.class
)
public class CopyPortalSettingsCommand implements Command {

	private final String[] PROPERTIES_FILENAME_PATTERNS = {
		"portal-.*\\.properties",
		"system-ext\\.properties",
	};

	public Object copyPortalSettings(File sourcePortalDir, File destPortalDir) {
		if (!sourcePortalDir.exists() || !destPortalDir.exists()) {
			return null;
		}

		final File[] propertiesFiles = sourcePortalDir.listFiles(
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					for (String pattern : PROPERTIES_FILENAME_PATTERNS) {
						if (name.matches(pattern)) {
							return true;
						}
					}

					return false;
				}
			});

		final StringBuilder errors = new StringBuilder();

		for (File propertiesFile : propertiesFiles ) {
			try {
				Files.copy(
					propertiesFile.toPath(),
					destPortalDir.toPath().resolve(propertiesFile.getName()),
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {
				errors.append(e.getMessage() + "\n");
			}
		}

		return errors.length() > 0 ? errors.toString() : null;
	}

	@Override
	public Object execute(Map<String, ?> parameters) {
		File src = (File) parameters.get("source");
		File dest = (File) parameters.get("dest");

		return copyPortalSettings(src, dest);
	}

}