package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import org.apache.commons.lang3.StringUtils;

/**
 * @author David Truong
 */
public class GradleCommand {

	public static final String DESCRIPTION =
		"Execute gradle command using the gradle wrapper if detected";

	public GradleCommand(blade blade, GradleOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		String gradleCommand = StringUtils.join(_options._arguments(), " ");

		GradleExec gradleExec = new GradleExec(_blade);

		gradleExec.executeGradleCommand(gradleCommand);
	}

	@Arguments(arg = {"gradle-command", "args..."})
	@Description(DESCRIPTION)
	public interface GradleOptions extends Options {
	}

	private blade _blade;
	private GradleOptions _options;

}