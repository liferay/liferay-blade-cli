package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Options;

@Arguments(arg = {"gogo-command", "args..."})
public interface ShellOptions extends Options {
}