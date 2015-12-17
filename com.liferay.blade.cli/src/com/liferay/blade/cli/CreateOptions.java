package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.io.File;

@Arguments(arg = {"name"})
public interface CreateOptions extends Options {

	@Description("The directory where to create the new project.")
	public File dir();

	@Description("The project template to use when creating the project. ")
	public Template template();

	@Description("If a class is generated in the project, " +
			"provide the name of the class to be generated." +
			" If not provided defaults to Project name.")
	public String classname();

	@Description("If a new DS component needs to be created, " +
			"provide the name of the service to be implemented.")
	public String service();

	public String packagename();
}
