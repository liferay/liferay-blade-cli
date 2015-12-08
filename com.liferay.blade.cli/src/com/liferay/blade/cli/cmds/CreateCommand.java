
package com.liferay.blade.cli.cmds;

import aQute.bnd.osgi.Processor;
import aQute.lib.getopt.CommandLine;
import aQute.lib.getopt.Options;
import aQute.lib.justif.Justif;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.blade.api.ProjectTemplate;
import com.liferay.blade.cli.CreateOptions;
import com.liferay.blade.cli.blade;

import java.io.File;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class CreateCommand {

	final private blade _blade;
	final private Options _options;
	final private CreateOptions _createOptions;
	final private BundleContext _bundleContext =
		FrameworkUtil.getBundle(CreateCommand.class).getBundleContext();
	final Justif				justif		= new Justif(80, 30, 32, 70);

	public CreateCommand(blade blade, Options options) throws Exception {
		_blade = blade;
		_options = options;

		List<String> args = options._arguments();

		final Map<String, ServiceReference<ProjectTemplate>> templates =
				new HashMap<>();

		final Collection<ServiceReference<ProjectTemplate>> refs =
			_bundleContext.getServiceReferences(ProjectTemplate.class, null);

		for (ServiceReference<ProjectTemplate> ref : refs) {
			final String name = (String) ref.getProperty("name");
			templates.put(name, ref);
		}

		CommandLine cmdline = new CommandLine(_blade);

		_createOptions = cmdline.getOptions(CreateOptions.class, args);

		if (args.size() < 2) {
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
			cmdline.help(f, this, "create", CreateOptions.class);

			f.flush();
			justif.wrap(sb);
			_blade.out().print(sb.toString());

			_blade.out().print("Available project templates:\n\t");

			for (String name : templates.keySet()) {
				_blade.out().print(name + ", ");
			}

			_blade.out().println();
			return;
		}

		final String projectTemplateName = args.remove(0);
		final ServiceReference<ProjectTemplate> templateRef =
				templates.get(projectTemplateName);

		final ProjectTemplate template;
		if (templateRef != null) {
			template = _bundleContext.getService(templateRef);
		}
		else {
			template = null;
		}

		if (template == null) {
			_blade.error(
					"Unable to get project template " + projectTemplateName);
			return;
		}

		ProjectBuild build = _createOptions.build();

		if (build == null) {
			build = ProjectBuild.gradle;
		}

		File dir = _createOptions.dir();
		File base = _blade.getBase();
		String name = args.remove(0);
		File workDir = null;

		if (dir != null) {
			workDir = Processor.getFile(dir, name);
			name = workDir.getName();
			base = workDir.getParentFile();
		}
		else {
			workDir = Processor.getFile(base, name);
			name = workDir.getName();
			base = workDir.getParentFile();
		}

		final ServiceReference<Command> ref =
			_bundleContext.getServiceReferences(
				Command.class, "(osgi.command.function=createProject)").iterator().next();

		final Command command = _bundleContext.getService(ref);
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("workDir", workDir);
		parameters.put("projectTemplate", template);
		parameters.put("buildValue", build.toString());
		parameters.put("name", name);
		parameters.put("classname", _createOptions.classname());
		parameters.put("service", _createOptions.service());
		parameters.put("packageName", _createOptions.packagename());

		final Object errors = command.execute(parameters);

		if (errors != null) {
			_blade.error(errors.toString());

			if ("printHelp".equals(errors.toString())) {
				printHelp();
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
