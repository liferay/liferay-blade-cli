package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import com.liferay.blade.cli.jmx.IDEConnector;

import java.io.File;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class OpenCommand {

	final private blade _blade;
	final private OpenOptions _options;

	public OpenCommand(blade blade, OpenOptions options) throws Exception {
		_blade = blade;
		_options = options;
	}

	public void execute() throws Exception {
		List<String> args = _options._arguments();

		if (args.size() == 0) {

			// Default command

			printHelp();
		}
		else {
			doExecute();
		}
	}

	@Arguments(arg = "file or directory to open/import")
	@Description("Opens or imports a file or directory in Liferay IDE")
	public interface OpenOptions extends Options {

		@Description("The workspace to open or import this file or project")
		public String workspace();

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void doExecute() throws Exception {
		File fileName = new File(_options._arguments().get(0));

		if (!fileName.exists()) {
			addError("open", "Unable to find specified file " +
				fileName.getAbsolutePath());
			return;
		}

		IDEConnector connector = null;

		try {
			connector = new IDEConnector();
		}
		catch (Exception e) {

			// ignore

		}

		if (connector == null) {
			addError(
				"open", "Unable to connect to Eclipse/Liferay IDE instance.");
			return;
		}

		if (fileName.isDirectory()) {
			Object retval = connector.openDir(fileName);

			if (retval != null) {
				addError("open", retval.toString());
				return;
			}
		}
	}

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		_options._command().help(f, this);
		_blade.out().println(f);
		f.close();
	}

}