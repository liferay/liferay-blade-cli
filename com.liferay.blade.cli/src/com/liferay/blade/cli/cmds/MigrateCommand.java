package com.liferay.blade.cli.cmds;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.liferay.blade.cli.MigrateOptions;
import com.liferay.blade.cli.blade;
import com.liferay.blade.cli.util.ConsoleProgressMonitor;
import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;

public class MigrateCommand {

	final private blade lfr;
	final private MigrateOptions options;

	public MigrateCommand(blade lfr, MigrateOptions options)
		throws Exception {

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
		File projectDir = new File(options._arguments().get(0));

		if (!projectDir.exists()) {
			addError("migrate", "projectDir does not exist");
			return;
		}

		final BundleContext context = FrameworkUtil.getBundle(
			this.getClass()).getBundleContext();

		Format format = options.format();

		FileOutputStream fos = null;

		if (options._arguments().size() > 1) {
			File file = new File(options._arguments().get(1));

			if (!file.exists()) {
				file.createNewFile();
			}

			fos = new FileOutputStream(file);
		}

		ServiceReference<Migration> migrationSR = context.getServiceReference(Migration.class);
		Migration migrationService = context.getService(migrationSR);

		List<Problem> problems = migrationService.findProblems(projectDir, new ConsoleProgressMonitor());

		String formatValue = format != null ? format.toString() : "";

		if (options.detailed()) {
			migrationService.reportProblems(problems, Migration.DETAIL_LONG, formatValue, fos);
		}
		else {
			migrationService.reportProblems(problems, Migration.DETAIL_SHORT, formatValue, fos);
		}
	}

	private void printHelp() throws Exception {
		Formatter f = new Formatter();
		options._command().help(f, this);
		lfr.out().println(f);
		f.close();
	}

}