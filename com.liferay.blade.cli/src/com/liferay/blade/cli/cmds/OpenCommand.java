package com.liferay.blade.cli.cmds;

import com.liferay.blade.cli.OpenOptions;
import com.liferay.blade.cli.blade;
import com.liferay.blade.cli.jmx.IDEConnector;

import java.io.File;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

public class OpenCommand {

	final private blade lfr;
	final private OpenOptions options;

	public OpenCommand(blade lfr, OpenOptions options) throws Exception {
		this.lfr = lfr;
		this.options = options;

		List<String> args = options._arguments();

		if (args.size() == 0) {

			// Default command

			printHelp();
		}
		else {
			execute();
		}
	}

	private void addError(String prefix, String msg) {
		lfr.addErrors(prefix, Collections.singleton(msg));
	}

	private void execute() throws Exception {
		File fileName = new File(options._arguments().get(0));

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
		options._command().help(f, this);
		lfr.out().println(f);
		f.close();
	}

}