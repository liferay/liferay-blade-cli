package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

@Arguments(arg = "file or directory to open/import")
@Description("Opens or imports a file or directory in Liferay IDE")
public interface OpenOptions extends Options {

	@Description("The workspace to open or import this file or project")
	public String workspace();

}