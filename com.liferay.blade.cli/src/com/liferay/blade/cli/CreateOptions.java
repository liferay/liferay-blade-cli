package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.io.File;
@Arguments(arg = {"name"})

/**
 * @author Gregory Amerson
 */
public interface CreateOptions extends Options {

	@Description(
		"If a class is generated in the project, provide the name of the " +
			"class to be generated. If not provided defaults to Project name."
	)
	public String classname();

	@Description("The directory where to create the new project.")
	public File dir();

	@Description(
		"If a new jsp hook fragment needs to be created, provide the name of " +
			"the host bundle symbolic name."
	)
	public String hostbundlebsn();

	@Description(
		"If a new jsp hook fragment needs to be created, provide the name of " +
			"the host bundle version."
	)
	public String hostbundleversion();

	public String packagename();

	@Description(
		"If a new DS component needs to be created, provide the name of the " +
			"service to be implemented."
	)
	public String service();

	@Description(
		"The project template to use when creating the project. The following" +
			" templates are available: activator, jsphook, mvcportlet, " +
			"portlet, service, servicebuilder, servicewrapper"
	)
	public Template template();

}