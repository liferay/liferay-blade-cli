package com.liferay.blade.cli;

import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import com.liferay.blade.api.ProjectBuild;

import java.io.File;

public interface CreateOptions extends Options {

	@Description("The build type of project to create.  "
			+ "Valid values are maven or gradle. Default: gradle")
	public ProjectBuild build();

	@Description("The directory where to create the new project.")
	public File dir();

}
