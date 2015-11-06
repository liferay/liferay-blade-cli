package com.liferay.blade.cli;

import com.liferay.blade.cli.cmds.Format;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

@Arguments(arg = {"projectDir", "[reportFile]"})
public interface MigrateOptions extends Options {

	@Description("Determines if the report format is short or long.")
	public boolean detailed();

	@Description("Defines the format of the output file. The following formats are supported, which should immediately follow this parameter: text, html, and xml.")
	public Format format();

}