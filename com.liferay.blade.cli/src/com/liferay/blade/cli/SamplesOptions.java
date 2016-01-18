package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import java.io.File;

/**
 * @author David Truong
 */
@Arguments(arg = {"[name]"})
public interface SamplesOptions extends Options {

	@Description("The directory where to create the new project.")
	public File dir();
}
