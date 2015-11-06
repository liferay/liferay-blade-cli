package com.liferay.blade.cli;

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.io.File;

import com.liferay.blade.cli.cmds.Build;

public interface CreateOptions extends Options {

	@Description("The build type of project to create.  "
			+ "Valid values are maven or gradle. Default: gradle")
	public Build build();

	@Description("The directory where to create the new project.")
	public File dir();

}
