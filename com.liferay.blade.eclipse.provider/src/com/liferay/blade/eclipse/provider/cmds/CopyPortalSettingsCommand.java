package com.liferay.blade.eclipse.provider.cmds;

import java.io.File;
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

	public void copyPortalSettings(File sourcePortalDir, File destPortalDir) {
		System.out.println(sourcePortalDir);
		System.out.println(destPortalDir);
		execute(null);
	}

	@Override
	public Object execute(Map<String, ?> parameters) {
		return null;
	}

}