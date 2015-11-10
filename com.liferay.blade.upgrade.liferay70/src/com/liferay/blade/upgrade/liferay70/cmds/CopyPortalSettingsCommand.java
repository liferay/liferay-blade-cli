package com.liferay.blade.upgrade.liferay70.cmds;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.apache.felix.service.command.CommandProcessor;
import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		CommandProcessor.COMMAND_SCOPE + "=blade",
		CommandProcessor.COMMAND_FUNCTION + "=copyPortalSettings"
	},
	service = Command.class
)
public class CopyPortalSettingsCommand implements Command {

	public static final String PARAM_SOURCE = "source";
	public static final String PARAM_DEST = "dest";

	private final String[] PROPERTIES_FILENAME_PATTERNS = {
		"portal-.*\\.properties",
		"system-ext\\.properties",
	};

	public Object copyPortalSettings(File sourcePortalDir, File destPortalDir)
			throws CommandException {

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

		if (errors.length() > 0) {
			throw new CommandException(errors.toString());
		}

		return null;
	}

	@Override
	public Object execute(Map<String, ?> parameters) throws CommandException {
		File src = (File) parameters.get(PARAM_SOURCE);
		File dest = (File) parameters.get(PARAM_DEST);

		return copyPortalSettings(src, dest);
	}

}