package com.liferay.blade.eclipse.provider.cmds;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

@Arguments(arg = {"name"})
public interface PortletTemplateOptions extends Options {
	@Description("If a class is generated in the project, " +
			"provide the name of the class to be generated." +
			" If not provided defaults to Project name.")
	public String classname();
}