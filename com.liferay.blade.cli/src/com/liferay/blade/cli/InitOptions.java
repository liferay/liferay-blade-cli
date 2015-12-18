package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Options;

@Arguments(arg = "[name]")
public interface InitOptions extends Options {

	public boolean force();

}
