package com.liferay.blade.cli;

import aQute.lib.getopt.Options;

import com.liferay.blade.cli.gradle.GradleTooling;

import java.io.File;

import java.nio.file.Path;

import java.util.Set;

/**
 * @author Gregory Amerson
 */
public class OutputsCommand {

	public OutputsCommand(blade blade, OutputsOptions options)
		throws Exception {

		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		final File base = _blade.getBase();

		final Set<File> outputs = GradleTooling.getOutputFiles(
			_blade.getCacheDir(), base);

		for (File output : outputs) {
			Path outputPath = output.toPath();

			if (_options.absolute()) {
				_blade.out().println(outputPath);
			}
			else {
				_blade.out().println(base.toPath().relativize(outputPath));
			}
		}
	}

	public interface OutputsOptions extends Options {

		public boolean absolute();

	}

	private blade _blade;
	private OutputsOptions _options;

}